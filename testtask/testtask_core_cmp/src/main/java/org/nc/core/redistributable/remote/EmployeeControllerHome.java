package org.nc.core.redistributable.remote;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface EmployeeControllerHome extends EJBHome {
	public EmployeeController create()  throws CreateException, RemoteException;
}
