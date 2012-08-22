package org.nc.core.entity;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface EmployeeHome extends EJBLocalHome {
	Employee create(String firstName, String lastName) throws CreateException;
	
	Employee create(String firstname, String lastname, 
			String middlename, String phones, Double salary, Position position) throws CreateException;

	Employee findByPrimaryKey(Long id) throws FinderException;
	
	Collection<Employee> findByPartOfNameOrPosition(String name) throws FinderException;
	
	Collection<Employee> findAll() throws FinderException;
}
