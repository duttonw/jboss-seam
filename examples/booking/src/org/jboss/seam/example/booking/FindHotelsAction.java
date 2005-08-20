//$Id$
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("findHotels")
@LocalBinding(jndiBinding="findHotels")
@Interceptor(SeamInterceptor.class)
@LoggedIn
public class FindHotelsAction implements FindHotels, Serializable
{
   private static final Logger log = Logger.getLogger(FindHotels.class);
   
   @PersistenceContext
   private EntityManager em;
   
   private String searchString;
   private List<Hotel> hotels;
   
   @Out(required=false)
   private Hotel hotel;
   private int selectedHotel;

   @Begin
   public String find()
   {
      hotels = em.createQuery("from Hotel where city like :search or zip like :search or address like :search")
            .setParameter("search", '%' + searchString.replace('*', '%') + '%')
            .setMaxResults(50)
            .getResultList();
      
      log.info(hotels.size() + " hotels found");
      
      return "success";
   }
   
   @End
   public String selectHotel()
   {
      hotel = hotels.get(selectedHotel);
      return "success";
   }
   
   public List getHotels()
   {
      return hotels;
   }

   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }
   
   public int getSelectedHotel()
   {
      return selectedHotel;
   }

   public void setSelectedHotel(int selectedHotel)
   {
      this.selectedHotel = selectedHotel;
   }

   @Destroy @Remove
   public void destroy() {}
}
