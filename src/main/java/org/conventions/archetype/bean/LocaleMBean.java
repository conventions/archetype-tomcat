/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.bean;

import org.apache.myfaces.extensions.cdi.core.api.scope.conversation.ViewAccessScoped;
import org.conventionsframework.event.LocaleChangeEvent;
import org.conventionsframework.producer.ResourceBundleProvider;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 *
 * @author rmpestano Oct 2, 2011 11:48:37 AM
 */
@Named
@ViewAccessScoped
public class LocaleMBean implements Serializable{

    private String selectedLocale;
    @Inject
    private ResourceBundleProvider resourceBundleProvider;

    @PostConstruct
    public void initLocale () {
        selectedLocale = resourceBundleProvider.getCurrentLocale();
    }
    
    
    @Inject 
    private Event<LocaleChangeEvent> localeChangeEvent;
    

    public String getSelectedLocale() {
        return selectedLocale;
    }

    public void setSelectedLocale(String selectedLocale) {
        this.selectedLocale = selectedLocale;
    }

    public void changeLocale(){
        if(selectedLocale != null && !"".endsWith(selectedLocale)){
            localeChangeEvent.fire(new LocaleChangeEvent(selectedLocale));
//   OR     getResourceBundleProvider().setCurrentLocale(selectedLocale);
            
        }
    }
    
}
