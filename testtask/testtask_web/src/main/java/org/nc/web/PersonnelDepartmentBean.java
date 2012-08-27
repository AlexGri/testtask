package org.nc.web;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.nc.core.redistributable.javabean.EmployeePojo;
import org.nc.core.redistributable.remote.EmployeeController;
import org.nc.core.redistributable.remote.EmployeeControllerHome;

public class PersonnelDepartmentBean {
	private EmployeeController employeeController; 
	
	public PersonnelDepartmentBean()   throws NamingException, RemoteException, CreateException  {
		InitialContext ic = new InitialContext();
		Object lookupResult = ic.lookup("java:comp/env/web/EmployeeController");
		EmployeeControllerHome employeeControllerHome = 
				(EmployeeControllerHome) PortableRemoteObject.narrow(lookupResult, EmployeeControllerHome.class);
		employeeController = employeeControllerHome.create();
	}
	
	public Collection<EmployeePojo> getEmployeeList() throws RemoteException {
		return employeeController.getEmployeeList();
	}
	
	public EmployeePojo getEmployee(String id) throws RemoteException {
		return employeeController.getEmployee(convertId(id));
	}
	
	public EmployeePojo getPosition(String id) throws RemoteException {
		return employeeController.getEmployee(convertId(id));
	}
	
	private Long convertId(String id) {
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("wrong id type", e);
		}
	}
}
