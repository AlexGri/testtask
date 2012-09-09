package org.nc.web;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import org.nc.core.redistributable.javabean.EmployeePojo;
import org.nc.core.redistributable.javabean.PositionPojo;
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
	
	public Collection<EmployeePojo> findEmployee(String value) throws RemoteException {
		return employeeController.findAllOccurences(value);
	}
	
	public Collection<EmployeePojo> getEmployeeList() throws RemoteException {
		return employeeController.getEmployeeList();
	}
	
	public Collection<PositionPojo> getPositionList() throws RemoteException {
		return employeeController.getPositionList();
	}
	
	public EmployeePojo getEmployee(String id) throws RemoteException {
		return employeeController.getEmployee(convertId(id));
	}
	
	public PositionPojo getPosition(String id) throws RemoteException {
		return employeeController.getPosition(convertId(id));
	}
	
	public Long convertId(String id) {
		try {
			return Long.valueOf(id);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("wrong id type", e);
		}
	}
	
	public void deleteEmployee(String id) throws RemoteException {
		employeeController.deleteEmployee(convertId(id));
	}
	
	public void saveEmployee(Long id, String firstname, String lastname, 
			String middlename, String phones, Double salary, Long positionId) throws RemoteException {
		employeeController.updateEmployee(id, firstname, lastname, middlename, phones, salary, positionId);
	}
	
	public void updateEmployee(Long id, String firstname, String lastname, 
			String middlename, String phones, Double salary) throws RemoteException {
		EmployeePojo employeePojo = employeeController.getEmployee(id);
		employeeController.updateEmployee(id, firstname, lastname, middlename, phones, salary, employeePojo.getPosition().getId());
	}
	
	public void updateEmployeePosition(Long id, Long positionId) throws RemoteException {
		EmployeePojo employeePojo = employeeController.getEmployee(id);
		String firstname = employeePojo.getFirstname();
		String lastname = employeePojo.getLastname();
		String middlename = employeePojo.getMiddlename();
		String phones = employeePojo.getPhones();
		Double salary = employeePojo.getSalary();		
		employeeController.updateEmployee(id, firstname, lastname, middlename, phones, salary, positionId);
	}
	
	public void createEmployee(String firstname, String lastname, 
			String middlename, String phones, Double salary, Long positionId) throws RemoteException {
		employeeController.createEmployee(firstname, lastname, middlename, phones, salary, positionId);
	}
}
