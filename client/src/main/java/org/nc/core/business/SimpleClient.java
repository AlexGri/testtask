package org.nc.core.business;

import agency.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;
import javax.rmi.*;

import org.nc.core.entity.*;

import java.util.*;

public class SimpleClient 
{
	private static String myJndi = "ejb/EmployeeController";

    public static void main(String[] args) {
		if (args.length == 1)
    		myJndi = args[0];
    	else if (args.length > 1) {
    		System.err.println("Usage: SimpleClient [ AgencyJNDI ]");
    		System.exit(1);
    	}
    	
    	try {
    		Hashtable<String, String> env = new Hashtable<String, String>();
			env.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory");
			env.put("java.naming.provider.url","localhost:1099");
	        InitialContext ic = new InitialContext(env);
	        Object lookup = ic.lookup(myJndi);
	        
	        EmployeeControllerHome home = (EmployeeControllerHome)PortableRemoteObject.narrow(lookup, EmployeeControllerHome.class);
	        
	        EmployeeController employeeController = home.create();
	        System.out.println("position list: ");
	        print(employeeController.getPositionList());
	        System.out.println("\n\nemployee list:");
	        print(employeeController.getEmployeeList());
	        System.out.println("\n\nfound list list:");
	        print(employeeController.findAllOccurences("ров"));
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
    
    public static void print(Collection<String> strings) {
    	for (String string : strings) {    		
    		System.out.println("\n\t" + string);
    	}
    }

}


