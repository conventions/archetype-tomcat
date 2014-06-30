/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.service;

import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.Role;
import org.conventionsframework.service.BaseService;

import java.util.List;

/**
 *
 * @author rmpestano
 */
public interface GroupService extends BaseService<Group> {


    public List<Role> findAvailableRoles(Group group);
}
