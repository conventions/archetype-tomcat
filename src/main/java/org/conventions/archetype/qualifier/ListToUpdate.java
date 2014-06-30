package org.conventions.archetype.qualifier;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier
@Retention(RUNTIME)
@Target({FIELD,PARAMETER,METHOD})
public @interface ListToUpdate {
  
  ListType value();


    public enum ListType {

        USER, ROLE, GROUP

    }
}
