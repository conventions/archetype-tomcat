package org.conventions.archetype.configuration;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.conventionsframework.producer.ConfigurationProvider;

/**
 * Created by rmpestano on 12/27/13.
 *
 * gather app configuration
 */
@ApplicationScoped
public class AppConfigProvider  {

    @Inject
    ConfigurationProvider configurationProvider;//can also be extended

    @PostConstruct
    public void init(){

        addConfig("myConfig", 1L);
        addConfig("myDoubleConfig",2.5);
        /**
         * to get custom config use:
         * @Inject
         * @Config
         * Long myConfig;
         */
    }

    
    
    public void addConfig(String key, Object value){
        configurationProvider.addConfigEntry(key, value);
    }

}
