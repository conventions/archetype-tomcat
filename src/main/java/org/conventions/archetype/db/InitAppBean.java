/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.db;

import org.conventions.archetype.model.Group;
import org.conventions.archetype.model.Role;
import org.conventions.archetype.model.User;
import org.conventions.archetype.qualifier.DateFormat;
import org.conventions.archetype.util.Utils;
import org.conventionsframework.qualifier.Config;
import org.conventionsframework.qualifier.Log;
import org.conventionsframework.qualifier.Service;
import org.conventionsframework.service.BaseService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

/**
 *
 * @author rmpestano Oct 2, 2011 11:48:37 AM
 */
@Named
@ApplicationScoped
public class InitAppBean implements Serializable{
    
    @Inject
    @Service
    BaseService<User> userService;

    @Inject
    @Log
    transient Logger log;
    
    @Inject 
    Utils utils;

    @Inject
    @Config
    Double myDoubleConfig;

    @Inject
    @DateFormat(format = "dd/MM/yyyy hh:mm:ss")
    SimpleDateFormat dateFormat;


    @PostConstruct
    public void init() {
        /**
         * @see org.conventions.archetype.configuration.AppConfigProvider
         */
        log.info("my double config:" + myDoubleConfig);
        log.info("init app mbean");
        if(userService.crud().countAll() == 0){
            final Group groupManager = new Group("Manager");
            final Group groupDev = new Group("Devs");
            final Group groupDevOps = new Group("DevOps");
            final Group groupOperations = new Group("Operations");
            
            Role roleOp = new Role("operator");
            Role roleSystem = new Role("system engineer");
            Role roleArch = new Role("architect"); 
            
            groupDevOps.addRole(roleSystem);
            groupDevOps.addRole(roleArch);
            
            groupOperations.addRole(roleOp);
            groupOperations.addRole(roleSystem);
            
            Role roleAdmin = new Role("administrator");
            
            groupManager.addRole(roleAdmin);
            
            Role roleDev = new Role("developer");
            
            groupDev.addRole(roleDev);
            groupDev.addRole(roleArch);
             
            User userAdmin = new User("admin");
            userAdmin.setPassword(utils.encrypt("admin"));
            userAdmin.addGroup(groupManager);
            userAdmin.addGroup(groupDevOps);
            userAdmin.addGroup(groupOperations);
            userAdmin.addGroup(groupDev);
            userService.store(userAdmin);
            
            User op = new User("operator");
            op.setPassword(utils.encrypt("operator"));
            op.addGroup(groupOperations);
            userService.store(op);
            
            User devOps = new User("devops");
            devOps.setPassword(utils.encrypt("devops"));
            devOps.addGroup(groupDevOps);
            userService.store(devOps);
            
            User developer = new User("developer");
            developer.setPassword(utils.encrypt("developer"));
            developer.addGroup(groupDev);
            userService.store(developer);
            
            User guest = new User("guest");
            guest.setPassword(utils.encrypt("guest"));
            userService.store(guest);
        }
    }

    @Named("timeNow")
    @Produces
    public String dateNow(){
        return dateFormat.format(Calendar.getInstance().getTime());
    }

}
