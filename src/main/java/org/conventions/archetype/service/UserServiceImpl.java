/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.service;

import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;
import org.conventions.archetype.event.UpdateUserRoles;
import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.model.User;
import org.conventions.archetype.util.AppConstants;
import org.conventions.archetype.util.Utils;
import org.conventionsframework.model.SearchModel;
import org.conventionsframework.qualifier.LoggedIn;
import org.conventionsframework.qualifier.SecurityMethod;
import org.conventionsframework.service.impl.BaseServiceImpl;
import org.conventionsframework.util.Assert;
import org.conventionsframework.util.MessagesController;
import org.conventionsframework.util.ResourceBundle;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 *
 * @author rmpestano
 */
@Named("userService")
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Inject
    private ResourceBundle resourceBundle;
    
    @Inject 
    Utils utils;

    @Inject
    @LoggedIn
    User currentUser;

    @Inject
    Event<UpdateUserRoles> updateUserRolesEvent;


    @Override
    public Criteria configPagination(SearchModel<User> searchModel) {

        Map<String,Object> searchFilter = searchModel.getFilter();
        User searchEntity = searchModel.getEntity();
        if (searchFilter != null) {
            String group = (String) searchFilter.get("groups");
            if (group != null && !"all".equalsIgnoreCase(group)) {
                crud.join("groups", "groups");
                crud.eq("groups.name", group);
            }

        }

        if(searchEntity.getGroup().getName() != null){//defined in user search dialog
            crud.join("groups","groups");
            crud.ilike("groups.name",searchEntity.getGroup().getName(),MatchMode.ANYWHERE);
        }
        //you can return only your populated criteria(dc) 
        //but you can also pass your criteria to superclass(as below)
        //so the framework will add an ilike to string fields and eq to Integer/Long/Date/Calendar ones
        //if those fields are present in filters
        return super.configPagination(searchModel,crud.getCriteria(true));
    }


    private List<Group> fetchGroups(User user) {
        Query q = getEntityManager().createNamedQuery("Group.findByUser");
        q.setParameter("userId",user.getId());
        return q.getResultList();
    }

    @SecurityMethod(rolesAllowed = AppConstants.Role.OPERATOR, message = "Only operator can perform this task")
    public void testPermission(){
        MessagesController.addInfo(resourceBundle.getString("test.permission",currentUser.getName(),currentUser.getUserRoles()));
    }



    @Override
    @SecurityMethod(rolesAllowed= AppConstants.Role.ADMIN)
    @Transactional
    public void remove(User entity) {
        Assert.notTrue(entity.getGroups() != null && !entity.getGroups().isEmpty(),"be.user.remove");
        super.crud.delete(entity);
    }
    
    @Override
    @Transactional
    public void store(User entity) {
    	super.store(entity);
    }

    @Override
    public void beforeRemove(User entity) {
        //override to perform logic before removing an entity
        super.beforeRemove(entity);
    }


    @Override
    public void afterRemove(User entity) {
        //override to perform logic after removing an entity
        super.afterRemove(entity);
    }

    @Override
    public void beforeStore(User entity) {
        //override to perform logic before storing an entity
        Assert.notTrue(isExistingUser(entity),"be.user.existing");
        if(entity.getPassword() != null){
            entity.setPassword(utils.encrypt(entity.getPassword()));//could be in @PrePersist/Update
        }
    }


    @Override
    public void afterStore(User entity) {
        updateUserRolesEvent.fire(new UpdateUserRoles(entity.getUserRoles()));
    }

    private boolean isExistingUser(User user) {
        if(user == null){
            return false;
        }
        Criteria criteria = getCriteria();
        //used to ignore user id which we are editing
        if (user.getId() != null) {
            criteria.add(Restrictions.ne("id", user.getId()));
        }

        if (!"".endsWith(user.getName())) {
            criteria.add(Restrictions.ilike("name", user.getName(), MatchMode.EXACT));
            return (crud.criteria(criteria).count() > 0);
        }
        return false;
    }

    @Override
    public User findUser(String username, String pass) throws NoResultException{
        Query q = getEntityManager().createNamedQuery("User.findByNameAndPass");
        q.setParameter("name", username);
        q.setParameter("pass", pass);
        return (User) q.getSingleResult();
    }

    public List<User> findUserByRole(Role role){
        return  crud.join("groups","groups", JoinType.LEFT_OUTER_JOIN).
                join("groups.roles","roles", JoinType.LEFT_OUTER_JOIN).
                eq("roles.name",role.getName()).list();
    }


}
