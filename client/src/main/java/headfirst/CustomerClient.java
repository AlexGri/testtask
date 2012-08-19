package headfirst;

import javax.naming.*;

import java.rmi.*;
import java.util.Hashtable;

import javax.rmi.*;
import javax.ejb.*;

public class CustomerClient {

     public static void main(String[] args) {
         new CustomerClient().go();
     }

    public void go() {
      try {
    	  Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory"); 
			//env.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces"); 
			env.put("java.naming.provider.url","localhost:1099");
			
			Context ic = new InitialContext(env	);
         System.out.println("got a context");

         Object o = ic.lookup("CustomerBean");
         System.out.println("got an object" + o);
        

         
         CustomerHome home = (CustomerHome) PortableRemoteObject.narrow(o, CustomerHome.class);
        System.out.println("did the narrow");
         
          Customer customer1 = home.create("Bates", "Bert", "42 Foo St.", "421");
          System.out.println("did the create");

          System.out.println(customer1.getLastName());
           } catch (Exception ex) {
              ex.printStackTrace();
          }
      }
}