/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.service.RoleService;
import org.conventionsframework.bean.BaseMBean;
import org.conventionsframework.qualifier.PersistentClass;
import org.conventionsframework.qualifier.Service;

import javax.annotation.PostConstruct;
import javax.inject.Named;

/**
 *
 * @author rmpestano
 */

@Named
@ViewAccessScoped
@PersistentClass(Role.class)
@Service(RoleService.class)
public class RoleMBean extends BaseMBean<Role> {

    @PostConstruct
    public void initBean(){
        setCreateMessage(getResourceBundle().getString("role.create.message"));
        setUpdateMessage(getResourceBundle().getString("role.update.message"));
        setDeleteMessage(getResourceBundle().getString("role.delete.message"));
    }

    @Override
    public void delete(Role entity) {
       //reatach entity to hibernate session, needed cause we are using a stateless/Non EJB service
        super.delete((Role) getBaseService().crud().load(entity.getId()));
    }

}
