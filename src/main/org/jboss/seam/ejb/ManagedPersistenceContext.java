//$Id$
package org.jboss.seam.ejb;

import java.io.Serializable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Scope(ScopeType.CONVERSATION)
public class ManagedPersistenceContext implements Serializable
{
   private static final Logger log = Logger.getLogger(ManagedPersistenceContext.class);
   
   private EntityManager entityManager;
   private String persistenceUnitName;

   @Create
   public void create(Component component)
   {
      persistenceUnitName = component.getName();
      try
      {
         entityManager = getEntityManagerFactory(persistenceUnitName)
               .createEntityManager(PersistenceContextType.EXTENDED);
      }
      catch (NamingException ne)
      {
         throw new IllegalArgumentException("EntityManagerFactory not found", ne);
      }
      
      log.info("created seam managed persistence context for persistence unit: "+ persistenceUnitName);
   }
   
   @Unwrap
   public EntityManager getEntityManager()
   {
      return entityManager;
   }
   
   @Destroy
   public void destroy()
   {
      log.info("destroying seam managed persistence context for persistence unit: " + persistenceUnitName);
      entityManager.close();
   }
   
   private EntityManagerFactory getEntityManagerFactory(String persistenceUnit)
         throws NamingException
   {
      return (EntityManagerFactory) new InitialContext()
            .lookup("java:/EntityManagerFactories/" + persistenceUnit);
   }
   
   public String toString()
   {
      return "ManagedPersistenceContext(" + persistenceUnitName + ")";
   }
}
