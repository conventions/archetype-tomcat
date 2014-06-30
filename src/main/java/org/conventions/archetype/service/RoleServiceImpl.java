/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.service;

import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.myfaces.extensions.cdi.jpa.api.Transactional;
import org.conventions.archetype.event.UpdateList;
import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.qualifier.ListToUpdate;
import org.conventions.archetype.util.AppConstants;
import org.conventionsframework.model.SearchModel;
import org.conventionsframework.service.impl.BaseServiceImpl;
import org.conventionsframework.util.Assert;
import org.conventionsframework.util.ResourceBundle;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author rmpestano
 *
 * NON EJB Service
 */
public class RoleServiceImpl extends BaseServiceImpl<Role> implements RoleService {

    @Inject
    @ListToUpdate(ListToUpdate.ListType.ROLE)
    Event<UpdateList> updateRoleList;

    @Inject
    ResourceBundle resourceBundle;
    
    @Override
    public Criteria configPagination(SearchModel<Role> searchModel) {
        
        Criteria criteria = getCriteria();
        Map<String,Object> searchFilter = searchModel.getFilter();
        Role searchEntity = searchModel.getEntity();
            if(searchEntity != null && searchEntity.getId() != null){
            criteria.add(Restrictions.eq("id", searchEntity.getId()));
            return criteria;
        }
        List<Long> groupRoles = (List<Long>) searchFilter.get("groupRoles");
        if(groupRoles != null && !groupRoles.isEmpty()){
            //open roleModal listing only with not used groups
            //PS: not used cause roleAssociation is done with picklist now not with list of values anymore
            criteria.add(Restrictions.not(Restrictions.in("id", groupRoles)));
        }
        return super.configPagination(searchModel, criteria);
    }
    
    @Override
    @Transactional
    public void store(Role entity) {
    	super.store(entity);
    }
    
    @Override
    @Transactional
    public void remove(Role entity) {
    	super.remove(entity);
    }

    @Override
    public void afterStore(Role entity) {
        updateRoleList.fire(new UpdateList());
    }

    @Override
    public void afterRemove(Role entity) {
        updateRoleList.fire(new UpdateList());
    }

    @Override
    public void beforeRemove(Role entity) {
        Assert.notTrue(entity.getName().equals(AppConstants.Role.ADMIN),"role.be.admin");
        Assert.notTrue(roleIsAssociatedWithGroups(entity),"role.be.groups");
    }

    private boolean roleIsAssociatedWithGroups(Role entity) {
        Query q = getEntityManager().createNamedQuery("Group.findGroupsWithrole");
        q.setParameter("roleId",entity.getId());
        List<Group> groupsFound = q.getResultList();
        return !groupsFound.isEmpty();
    }

}
