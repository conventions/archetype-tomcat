/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.conventions.archetype.db;

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
@RequestScoped
@Specializes
public class EntityManagerProducer extends DefaultEntityManagerProducer  {

	private EntityManager entityManager;
	
	 
	public  EntityManagerProducer(){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("archetypePU");
		entityManager = emf.createEntityManager();
		
	}

	/**
	 * The producer is required so CODI can handlle @Transactional methods and
	 * also conventions needs for entity manager injection in BaseService
	 */
	@Produces
	@RequestScoped
	public EntityManager getEntityManager() {
		return entityManager;
	}

   
   
	public void dispose(@Disposes EntityManager entityManager){
		if(entityManager.isOpen()){
			entityManager.close();
		}
	}
}
