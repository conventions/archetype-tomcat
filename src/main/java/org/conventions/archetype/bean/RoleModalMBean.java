/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.WindowScoped;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.service.RoleService;
import org.conventionsframework.bean.ModalMBean;
import org.conventionsframework.qualifier.Service;
import org.primefaces.event.CloseEvent;

import javax.inject.Named;
import java.io.Serializable;

 /**
 * 
 * @author rmpestano
 * provides role list of values 
 */
@WindowScoped
@Named(value = "roleModalMBean")
@Service(RoleService.class)//injects RoleService in the superClass. Only works for non EJB services, for user service it will not work cause its an EJB
public class RoleModalMBean extends ModalMBean<Role> implements Serializable{

    private Role[] selectedRoles;
    
    public RoleModalMBean() {
    }

    public Role[] getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(Role[] selectedRoles) {
        this.selectedRoles = selectedRoles;
    }


    @Override
    public Object modalCallback() {
       return selectedRoles;
    }
  
    public void clearSelection(CloseEvent event){
        this.selectedRoles = null;
    }
    
    /**
     * pass parameter to service so only roles which 
     * are no associated with user will be displayed in the popup
     */
    @Override     
    public void onOpen() {
        getPaginator().setFilter(getParameters());//substitute paginator filter with parammeters filter @see RoleServiceImpl#
    }
    
    
}