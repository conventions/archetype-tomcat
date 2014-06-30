/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.model;

import org.conventionsframework.model.VersionatedEntityLong;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@NamedQuery(name = "User.findByNameAndPass", query = "select u from User u left join fetch u.groups g where u.name = :name and u.password = :pass")
public class User extends VersionatedEntityLong {

	@NotNull
	private String name;

	@NotNull
	private String password;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<Group> groups;

    @Transient
    private Group group;//used to search users by group

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public User password(String pass){
        setPassword(pass);
        return this;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public User name(String name){
        setName(name);
        return this;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void addGroup(Group group) {
		if (getGroups() == null) {
			setGroups(new ArrayList<Group>());
		}
		getGroups().add(group);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final User other = (User) obj;
		if ((this.getId() == null) ? (other.getId() != null) : !this.getId()
				.equals(other.getId())) {
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

	@Transient
	public int getNumGroups() {
		return groups != null ? groups.size() : 0;
	}

    @Transient
    public List<String> getUserRoles() {
        List<String> userRoles = new ArrayList<>();
        if(groups != null){
            for (Group group : groups) {
                for (Role role : group.getRoles()) {
                    userRoles.add(role.getName());

                }
            }
        }
        return userRoles;
    }
}
