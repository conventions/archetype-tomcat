/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.conventionsframework.model.VersionatedEntityLong;

@Entity
@SequenceGenerator(allocationSize = 10, name = "seq_role", sequenceName = "seq_role")
@NamedQuery(name="Role.findRoleNotInGroup",query="SELECT r from Role r where r.id NOT IN :roleIds")
@Table(name="role_")
public class Role extends VersionatedEntityLong {
    
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<Group> groups;


    public Role() {
    }
    
    public Role(String name) {
        this.name = name;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role name(String name){
        setName(name);
        return this;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
    
    public void addGroup(Group group){
        if(groups == null){
            groups = new ArrayList<Group>();
        }
        groups.add(group);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Role other = (Role) obj;
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
