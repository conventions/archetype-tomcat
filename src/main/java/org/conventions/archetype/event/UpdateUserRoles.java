package org.conventions.archetype.event;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rmpestano on 12/30/13.
 */
public class UpdateUserRoles implements Serializable{

    private List<String> userRoles;

    public UpdateUserRoles(List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }
}
