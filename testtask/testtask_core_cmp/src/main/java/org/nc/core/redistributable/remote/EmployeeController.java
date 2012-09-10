package org.nc.core.redistributable.remote;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBObject;

import org.nc.core.redistributable.javabean.EmployeePojo;
import org.nc.core.redistributable.javabean.PositionPojo;

public interface EmployeeController extends EJBObject {
	public Collection<EmployeePojo> getEmployeeList() throws RemoteException;
	
	public Collection<PositionPojo> getPositionList() throws RemoteException;
	
	public Collection<EmployeePojo> findAllOccurences(String value) throws RemoteException;
	
	public EmployeePojo getEmployee(Long id) throws RemoteException;
	
	public PositionPojo getPosition(Long id) throws RemoteException;
	
	public void deleteEmployee(Long id) throws RemoteException;
	
	public Long createEmployee(String firstname, String lastname, 
			String middlename, String phones, Double salary, Long positionId) throws RemoteException;
	
	public Long createEmployee(String firstname, String lastname) throws RemoteException;
	
	public void updateEmployee(Long id, String firstname, String lastname, 
			String middlename, String phones, Double salary, Long positionId) throws RemoteException;
	
}
