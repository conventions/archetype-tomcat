/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.db;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.conventionsframework.producer.DefaultEntityManagerProducer;

/**
 *
 * @author rmpestano
 */
@ApplicationScoped
@Specializes
public class EntityManagerProducer extends DefaultEntityManagerProducer  {

	private EntityManagerFactory emf;
	
	 
	public  EntityManagerProducer(){
	      emf = Persistence.createEntityManagerFactory("archetypePU");
		
	}

	/**
	 * The producer is required so CODI can handlle @Transactional methods and
	 * also conventions needs for entity manager injection in BaseService
	 */
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return emf.createEntityManager();
	}

   
   
	public void dispose(@Disposes EntityManager entityManager){
		if(entityManager.isOpen()){
			entityManager.close();
		}
	}
}
