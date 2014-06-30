/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.conventionsframework.model.VersionatedEntityLong;

import javax.persistence.*;
import java.util.*;

@Entity
@SequenceGenerator(allocationSize = 10, name = "seq_group", sequenceName = "seq_group")
@Table(name="group_")
@NamedQueries({
        @NamedQuery(name = "Group.findByUser", query = "select g from Group g left join fetch g.users u where u.id = :userId"),
        @NamedQuery(name = "Group.findGroupsWithrole", query = "select g from Group g left join g.roles r where r.id = :roleId"),

})
public class Group extends VersionatedEntityLong{
    
    private String name;
    @ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Role> roles;
    @JsonIgnore
    @ManyToMany(mappedBy = "groups")
    private List<User> users;


    public Group() {
    }
    
    public Group(String name) {
        this.name = name;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public List<Role> getRolesAsList() {
        if(roles != null){
            return new ArrayList<>(roles);
        }
        return null;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    

    public void addRole(Role role){
        if(getRoles() == null){
            setRoles(new HashSet<Role>());
        }
        getRoles().add(role);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if ((this.getId() == null) ? (other.getId() != null) : !this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
}
