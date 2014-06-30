/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.service.GroupService;
import org.conventionsframework.bean.BaseMBean;
import org.conventionsframework.qualifier.PersistentClass;
import org.conventionsframework.qualifier.Service;
import org.primefaces.model.DualListModel;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author rmpestano
 */
@Named
@ViewAccessScoped
@PersistentClass(Group.class)
@Service(GroupService.class)
public class GroupMBean extends BaseMBean<Group> implements Serializable{

    private Role roleSelected;
    private DualListModel<Role> groupRoles;

    @PostConstruct
    public void initBean() {
        setCreateMessage(getResourceBundle().getString("group.create.message"));
        setUpdateMessage(getResourceBundle().getString("group.update.message"));
        setDeleteMessage(getResourceBundle().getString("group.delete.message"));
    }

    public void preLoadDialog() {
        //gather user roles to ignore them in role popup
        List<Long> myRoles = new ArrayList<Long>();
        for (Role role : getEntity().getRoles()) {
            myRoles.add(role.getId());
        }
        getPaginator().getFilter().put("groupRoles", myRoles);
        super.initModal(getPaginator().getFilter()); //pass the map as parameter to GroupModalMBean
    }

    public GroupService getGroupService(){
        return (GroupService) getBaseService();
    }
    public Role getRoleSelected() {
        return roleSelected;
    }

    public void setRoleSelected(Role roleSelected) {
        this.roleSelected = roleSelected;
    }

    
    @Override
    public void save() {
        Set<Role> roles = new HashSet<>();
        for (Iterator<Role> i = groupRoles.getTarget().iterator(); i.hasNext(); ) {
            roles.add(getBaseService().getEntityManager().find(Role.class, i.next().getId()));
        }
        getEntity().setRoles(roles);
        super.save();
    }

    /**
     *
     * invoked by add/new button
     */
    @Override
    public String afterPrepareInsert() {
        List<Role> source = new ArrayList<Role>();
        List<Role> target = new ArrayList<Role>();
        source.addAll(getGroupService().findAvailableRoles(getEntity()));
        groupRoles = new DualListModel(source, target);
        return null;//keep in the same outcome
    }

    
    
    /**
     *
     * invoked by edit button
     */
    @Override
    public String afterPrepareUpdate() {
        List<Role> source = new ArrayList<Role>();
        List<Role> target = new ArrayList<Role>();
        target.addAll(getEntity().getRoles());
        source.addAll(getGroupService().findAvailableRoles(getEntity()));
        groupRoles = new DualListModel(source, target);
        return null;//keep in the same outcome
    }

    public DualListModel getGroupRoles() {
        return groupRoles;
    }

    public void setGroupRoles(DualListModel groupRoles) {
        this.groupRoles = groupRoles;
    }

    /**
     * called after clear button(which only clears bean entity)
     */
    @Override
    public void clearMBean() {
        this.afterPrepareInsert();
    }
    
    
}
