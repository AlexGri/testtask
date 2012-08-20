package org.nc.core.business;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBObject;

public interface EmployeeController extends EJBObject {
	public Collection<Object> getEmployeeList() throws RemoteException;
	
	public Collection<Object> getPositionList() throws RemoteException;
	
	public Collection<Object> findAllOccurences(String value) throws RemoteException;
}
