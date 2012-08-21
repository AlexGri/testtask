package client;

import agency.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;
import javax.rmi.*;
import java.util.*;

public class SimpleClient 
{
	private static String agencyJNDI = "ejb/Agency";

    public static void main(String[] args) {
		if (args.length == 1)
    		agencyJNDI = args[0];
    	else if (args.length > 1) {
    		System.err.println("Usage: SimpleClient [ AgencyJNDI ]");
    		System.exit(1);
    	}
    	
    	try {
    		Hashtable<String, String> env = new Hashtable<String, String>();
			env.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
			env.put("java.naming.provider.url","localhost:1099");
	        InitialContext ic = new InitialContext(env);
	        Object lookup = ic.lookup(agencyJNDI);
	        
	        AgencyHome home = (AgencyHome)PortableRemoteObject.narrow(lookup, AgencyHome.class);
	        
	        Agency agency = home.create();

	        System.out.println("Welcome to: "+agency.getAgencyName());

	        System.out.println("Customer list: "+agency.getAgencyName());
	        Collection customers = agency.findAllCustomers();
	        Iterator it = customers.iterator();
	        while (it.hasNext())
	        {
	        	String name = (String)it.next();
	        	System.out.println(name);
	        }
    	}
    	catch (NamingException ex) {
 	    	System.err.println(ex);
    	}
    	catch (ClassCastException ex) {
 	    	System.err.println(ex);
    	}
    	catch (CreateException ex) {
 	    	System.err.println(ex);
    	}
    	catch (RemoteException ex) {
 	    	System.err.println(ex);
    	}
    }

}


