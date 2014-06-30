/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.bean;

import org.conventions.archetype.event.UpdateList;
import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.qualifier.ListToUpdate;
import org.conventionsframework.crud.Crud;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rmpestano
 */
@Named
@ApplicationScoped
public class ComboMBean implements Serializable {

    private List<SelectItem> groupList;
    private List<SelectItem> roleList;
    
    @Inject
    Crud<Group> groupCrud;
    
    @Inject
    Crud<Role> roleCrud;

    
    public List<SelectItem> getGroupList() {
        if (groupList == null) {
            groupList = new ArrayList<SelectItem>();
            groupList.add(new SelectItem(null, "All"));
            List<Group> allGroups = groupCrud.listAll();
            for (Group group : allGroups) {
                SelectItem item = new SelectItem(group.getName(), group.getName());
                groupList.add(item);
            }
        }
        return groupList;
    }
    
    public List<SelectItem> getRoleList() {
         if (roleList == null) {
            roleList = new ArrayList<SelectItem>();
            roleList.add(new SelectItem(null, "All"));
            List<Role> allGroups = roleCrud.listAll();

            for (Role role : allGroups) {
                SelectItem item = new SelectItem(role.getName(), role.getName());
                roleList.add(item);
            }
        }
        return roleList;
    }
    
    public void updateGroupList(@Observes @ListToUpdate(ListToUpdate.ListType.GROUP)UpdateList updateList){
        groupList = null;//force update when a new group is added or removed @see GroupServiceImpl#afterStore
    }

    public void updateRoleList(){
        roleList = null;//force update
    }
}
