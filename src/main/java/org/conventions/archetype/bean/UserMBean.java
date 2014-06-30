/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.States;
import org.conventions.archetype.model.User;
import org.conventions.archetype.service.GroupService;
import org.conventions.archetype.service.UserService;
import org.conventions.archetype.util.AppConstants;
import org.conventions.archetype.util.Pages;
import org.conventions.archetype.util.Utils;
import org.conventionsframework.bean.StateMBean;
import org.conventionsframework.bean.state.CrudState;
import org.conventionsframework.exception.BusinessException;
import org.conventionsframework.paginator.Paginator;
import org.conventionsframework.qualifier.*;
import org.conventionsframework.util.Constants;
import org.conventionsframework.util.MessagesController;
import org.conventionsframework.util.RedirectPage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author rmpestano
 */
@Named
@ViewAccessScoped
@PersistentClass(User.class)
@BeanStates({
        @BeanState(beanState = Constants.State.FIND_STATE, value = "user.find", outcome = Pages.FIND_USER, ajax = false),//dont need callback cause listUser uses StatePusher
        @BeanState(beanState = Constants.State.INSERT_STATE, value = "user.new", outcome = Pages.EDIT_USER,ajax = false,callback = "#{userMBean.setInsertState}"),
        @BeanState(beanState = Constants.State.UPDATE_STATE, value = "user.edit", outcome = Pages.EDIT_USER, ajax = false,callback = "#{userMBean.setUpdateState}", resetValues = true, addEntityIdParam = true),
        @BeanState(beanState = "manageGroup", value = "user.manageGroup", outcome = Pages.MANAGE_GROUP, callback = "#{userMBean.setGroupState}",addEntityIdParam = true)})//add viewParam to reload group view f:viewParam in page, see manageGroup.xhtml
public class UserMBean extends StateMBean<User> {

    private Group groupSelected;

    private String pass;

    private String newPass;

    private Long currentUserId;

    @Inject
    UserService userService;

    /**
     * @see org.conventions.archetype.configuration.AppConfigProvider
     */
    @Inject
    @Config
    Long myConfig;

    @Inject
    @Log
    transient Logger log;

    @Inject
    Utils utils;

    @Inject
    @LoggedIn
    User loggedUser;

    @Inject
    @Service(GroupService.class)//group service manages group table pagination @see GroupServiceImpl#configPagination
    private Paginator<Group> groupPaginator;//brings real pagination for group table


    @PostConstruct
    public void init() {
        log.info("myConfig:" + myConfig);
        setBaseService(userService);
        super.init();
        if(getSearchEntity().getGroup() == null){
            getSearchEntity().setGroup(new Group());
        }
        setCreateMessage(getResourceBundle().getString("user.create.message"));
        setUpdateMessage(getResourceBundle().getString("user.update.message"));
        setDeleteMessage(getResourceBundle().getString("user.delete.message"));
    }

    /**
     * preRenderView in manageGroups outcome
     */
    public void initManageGroupsPage() {
        if (!facesContext.get().isPostback()) {
            if (currentUserId == null){
                throw new BusinessException("Provide user id to manage groups",new RedirectPage("/user/userHome.faces"));
            }
            setBeanState(States.MANAGE_GROUP);
            //tell groupService which user it is working on @see GroupServiceImpl#configPagination
            groupPaginator.getFilter().put("currentUser", currentUserId);
        }
    }

    @Override
    public void delete() {
        /**
         * programatic permission check
         * only
         */
        if(securityContext.hasRole(AppConstants.Role.ADMIN)){
            super.delete();
        }else {
            throw new BusinessException(getResourceBundle().getString("security.role-allowed",AppConstants.Role.ADMIN));
        }

    }

    public void test() {
        userService.testPermission();
    }

    /**
     * preRenderView in addUser outcome
     */
    public void initAddUserPage() {
        if (!facesContext.get().isPostback()) {
            if (currentUserId == null || currentUserId == 0) {
                setBeanState(CrudState.INSERT);
            } else {//edit user
                User userEdit = getUserService().crud().load(currentUserId);
                if (!userEdit.getId().equals(loggedUser.getId())) {
                    throw new BusinessException(getResourceBundle().getString("be.user.edit-not-allowed"), new RedirectPage(Pages.FIND_USER));
                }
                setEntity(userEdit);
                setBeanState(CrudState.UPDATE);
            }
        }

    }

    public UserService getUserService() {
        return (UserService) super.getBaseService();
    }


    public String manageGroups() {
        checkUserPassword();
        return Pages.MANAGE_GROUP + "?id=" + getEntity().getId() + "&" + Constants.FACES_REDIRECT;
    }

    public boolean isManageGroupState() {
        return States.MANAGE_GROUP.equals(getBeanState());
    }

    public Paginator<Group> getGroupPaginator() {
        return groupPaginator;
    }


    @Override
    public void afterModalResponse() {
        /*modalResponse is populated via CDI event by modalMBeans @see GroupModalMBean#modalCallback()
         */
        Group[] selectedGroups = (Group[]) getModalResponse();
        if (getEntity().getGroups() == null) {
            getEntity().setGroups(new ArrayList<Group>());
        }

        if (selectedGroups != null && selectedGroups.length > 0) {
            boolean groupAdded = false;
            for (Group group : selectedGroups) {
                if (!getEntity().getGroups().contains(group)) {
                    //attach deatached entity
                    getEntity().getGroups().add(getUserService().getEntityManager().find(Group.class, group.getId()));
                    groupAdded = true;
                }
            }
            if (groupAdded) {
                getEntity().setPassword(pass);
                getUserService().store(getEntity());
                MessagesController.addInfo(getResourceBundle().getString("user.groupAdded"));
            }
        }
    }

    public void preLoadDialog() {
        //gather user groups to ignore them in groups popup
        List<Long> myGroups = new ArrayList<Long>();
        if (getEntity().getGroups() != null) {
            for (Group group : getEntity().getGroups()) {
                myGroups.add(group.getId());
            }
        }
        getPaginator().getFilter().put("userGroups", myGroups);
        super.initModal(getPaginator().getFilter()); //pass the map as parameter so GroupModalMBean can access @see GroupModalMBean#onOpen()
    }

    public void removeGroup() {
        if (getEntity().getGroups() != null && getEntity().getGroups().contains(groupSelected)) {
            getEntity().getGroups().remove(groupSelected);
            MessagesController.addInfo(getResourceBundle().getString("group.info.removed", groupSelected.getName(), getResourceBundle().getString("save")));
        }
    }

    public String saveUser() {
        getEntity().setPassword(this.pass);
        super.save();
        return (getBeanState().equals(CrudState.UPDATE) ? Pages.EDIT_USER : Pages.FIND_USER) + Constants.FACES_REDIRECT;
    }


    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Group getGroupSelected() {
        return groupSelected;
    }

    public void setGroupSelected(Group groupSelected) {
        this.groupSelected = groupSelected;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    public void changePassword() {
        checkUserPassword();
        getEntity().setPassword(newPass);
        super.save(getResourceBundle().getString("user.changePass.success"));
    }

    public String getSearchCriteria(){
        StringBuilder sb = new StringBuilder(21);

        if(getSearchModel().getFilter().containsKey("name")){
            sb.append("username:"+getSearchModel().getFilter().get("name")+",");
        }
        if(getSearchEntity().getGroup().getName() != null){
            sb.append("group:"+getSearchEntity().getGroup()+",");
        }

        int commaIndex = sb.lastIndexOf(",");

        if (commaIndex != -1) {
            sb.deleteCharAt(commaIndex);
        }

        if (sb.toString().trim().isEmpty()) {
            return null;
        }

        return sb.toString();
    }

    @Override
    public void resetSearch() {
        super.resetSearch();
        getSearchEntity().setGroup(new Group());
    }

    private void checkUserPassword() {
        /** TODO decript
         if (!utils.decript(getEntity().getPassword()).equals(pass)) {
         throw new BusinessException(getResourceBundle().getString("user.changePass.incorrect"));
         }  */
    }
}
