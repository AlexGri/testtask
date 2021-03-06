package org.nc.core.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.nc.core.entity.Employee;
import org.nc.core.entity.EmployeeHome;
import org.nc.core.entity.Position;
import org.nc.core.entity.PositionHome;
import org.nc.core.redistributable.javabean.EmployeePojo;
import org.nc.core.redistributable.javabean.PositionPojo;
import org.nc.core.utils.CommonUtils;

public class EmployeeControllerBean implements SessionBean {
	private static final long serialVersionUID = 1L;
	
	private SessionContext sessionContext;
	private PositionHome positionHome;
	private EmployeeHome employeeHome;
	
	/*public void createPosition(String name) {
		try {
			positionHome.create(name);
		} catch (CreateException e) {
			CommonUtils.error("error while creating new position", e);
		}
	}
	
	public void updatePosition(String oldName, String newName) {
		try {
			Position position = positionHome.findByName(oldName);
			position.setPositionName(newName);
		} catch (FinderException e) {
			CommonUtils.error("error while updating position", e);
		}
	}*/
	
	public Collection<EmployeePojo> getEmployeeList() {
		try {
			Collection<Employee> employees = employeeHome.findAll();
			Collection<EmployeePojo> result = new ArrayList<EmployeePojo>(employees.size());
			for (Employee employee : employees) {
				result.add(CommonUtils.createEmployeePojo(employee));
			}
			return result;
		} catch (FinderException e) {
			CommonUtils.error("error occured during getting employee list", e);
		}
		
		return Collections.emptyList();
	}
	
	public Collection<PositionPojo> getPositionList() {
		try {
			Collection<Position> positions = positionHome.findAll();
			Collection<PositionPojo> result = new ArrayList<PositionPojo>(positions.size());
			for (Position position : positions)
				result.add(CommonUtils.createPositionPojo(position));
			return result;
		} catch (FinderException e) {
			CommonUtils.error("error occured during getting position list", e);
		}
		
		return Collections.emptyList();
	}
	
	public Collection<EmployeePojo> findAllOccurences(String value) {
		String searchValue = StringUtils.isBlank(value) ? "%" : "%"+value.trim() +"%";
		try {
			Collection<Employee> employees = employeeHome.findByPartOfNameOrPosition(searchValue);
			Collection<EmployeePojo> result = new ArrayList<EmployeePojo>(employees.size());
			for (Employee employee : employees) {
				result.add(CommonUtils.createEmployeePojo(employee));
			}
			return result;
		} catch (FinderException e1) {
			CommonUtils.error("error occured during searching", e1);
		}
		return Collections.emptyList();
	}
	
	public EmployeePojo getEmployee(Long id) {
		try {
			return CommonUtils.createEmployeePojo(employeeHome.findByPrimaryKey(id));
		} catch (FinderException e) {
			CommonUtils.error("error occured during searching", e);
		}
		return null;
	}
	
	public PositionPojo getPosition(Long id) {
		try {
			return CommonUtils.createPositionPojo(positionHome.findByPrimaryKey(id));
		} catch (FinderException e) {
			CommonUtils.error("error occured during searching", e);
		}
		return null;
	}
	
	public void deleteEmployee(Long id) {
		try {
			employeeHome.remove(id);
		} catch (EJBException e) {
			CommonUtils.error("An error occured during deleting employee = " + id, e);
		} catch (RemoveException e) {
			CommonUtils.error("could not delete employee with id = " + id, e);
		}
	}
	
	public Long createEmployee(String firstname, String lastname) {
		try {
		return employeeHome.create(firstname, lastname).getId();
	} catch (CreateException e) {
		CommonUtils.error("could not create employee", e);
	}
		return null;
	}
	
	public Long createEmployee(String firstname, String lastname, 
			String middlename, String phones, Double salary, Long positionId) {
		try {
			Employee employee = employeeHome.create(firstname, lastname, middlename, phones, salary);
			Position position = null;
			if (positionId != null){
				try {
					position = positionHome.findByPrimaryKey(positionId);
				} catch (FinderException e) {
					CommonUtils.error("could not find position with id = " + positionId, e);
				}
			}
			if (position != null)
				employee.setPosition(position);
			return employee.getId();
		} catch (CreateException e) {
			CommonUtils.error("could not create employee", e);
		}
		return null;
	}
	
	public void updateEmployee(Long id, String firstname, String lastname, 
			String middlename, String phones, Double salary, Long positionId) {
		Employee employee = null;
		try {
			employee = employeeHome.findByPrimaryKey(id);
		} catch (FinderException e1) {
			CommonUtils.error("error occured during searching", e1);
		}
		
		employee.setFirstname(firstname);
		employee.setLastname(lastname);
		employee.setMiddlename(middlename);
		employee.setPhones(phones);
		employee.setSalary(salary);
		
		Position currentPosition = employee.getPosition();
		
		if (currentPosition == null || currentPosition.getId() != positionId) {
			Position position = null;
			if (positionId != null){
				try {
					position = positionHome.findByPrimaryKey(positionId);
				} catch (FinderException e) {
					CommonUtils.error("could not find position with id = " + positionId, e);
				}
			}
			employee.setPosition(position);
		}
	}

	@Override
	public void ejbActivate() {}

	@Override
	public void ejbPassivate() {}

	@Override
	public void ejbRemove() {
		this.positionHome = null;
		this.employeeHome = null;
	}
	
	public void ejbCreate() throws CreateException {
		try {
	        InitialContext ic = new InitialContext();
	        positionHome = (PositionHome)ic.lookup("java:comp/env/ejb/Position");
	        employeeHome = (EmployeeHome)ic.lookup("java:comp/env/ejb/Employee");
        }
        catch (NamingException ex) {
            CommonUtils.error("Error connecting to PD:",ex);
        }
	}

	@Override
	public void setSessionContext(SessionContext arg0) {
		this.sessionContext = arg0;		
	}
}
