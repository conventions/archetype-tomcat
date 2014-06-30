/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.security;

import org.conventionsframework.qualifier.SecurityMethod;
import org.conventionsframework.security.BaseSecurityInterceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.List;

@SecurityMethod
@Interceptor
public class SecurityInterceptor extends BaseSecurityInterceptor {

    @Inject
    SecurityContextImpl securityContext;



    //need by glassfish which doesnt handle interceptor inheritance
    @Override
    @AroundInvoke
    public Object checkPermission(InvocationContext ic) throws Exception {
        return super.checkPermission(ic);
    }
    
    
    

    /**
     * this method is responsible for deciding if current user has permission to
     * execute a method
     *
     * @param rolesAllowed roles passed in the method
     * @return true if user has permission, false otherwise
     */
    @Override
    public boolean checkUserPermissions(String[] rolesAllowed) {
        if (!securityContext.loggedIn() || securityContext.getUser().getGroups() == null || securityContext.getUser().getGroups().isEmpty() || !userHasRoles()) {
            return false;
        }


        List<String> userRoles = securityContext.getUser().getUserRoles();

        for (String role : rolesAllowed) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }



    private boolean userHasRoles() {
        return !securityContext.getUser().getUserRoles().isEmpty();
    }
}
