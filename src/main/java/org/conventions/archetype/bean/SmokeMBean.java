package org.conventions.archetype.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;

import javax.inject.Named;
import java.io.Serializable;

/**
 * Created by rmpestano on 6/3/14.
 *
 * used for smokeTests
 */
@Named
@ViewAccessScoped
public class SmokeMBean implements Serializable{

    private Boolean dbStatus;
    private Boolean restEndpointStatus;


    public void testBd(){
        //todo
    }

    public void testRestEndpoint(){
        //todo
    }

    public Boolean getRestEndpointStatus() {
        return restEndpointStatus;
    }

    public void setRestEndpointStatus(Boolean restEndpointStatus) {
        this.restEndpointStatus = restEndpointStatus;
    }

    public Boolean getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(Boolean dbStatus) {
        this.dbStatus = dbStatus;
    }
}
