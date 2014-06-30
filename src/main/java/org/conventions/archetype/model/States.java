/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.model;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import org.conventionsframework.bean.state.State;


 public enum States implements State{
        
        MANAGE_ROLE("manageRole"),
        MANAGE_GROUP("manageGroup");
          

        private final String stateName;

        States(String stateName) {
            this.stateName = stateName;
        }

        @Override
        public String getStateName() {
            return this.stateName;
        }
        
        @Produces
        @Named
        public State manageRole(){
            return States.MANAGE_ROLE;
        }
        
        @Produces
        @Named
        public State manageGroup(){
            return States.MANAGE_GROUP;
        }
        
    }
