package org.nc.core.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.nc.core.entity.Employee;
import org.nc.core.entity.EmployeeHome;
import org.nc.core.entity.Position;
import org.nc.core.entity.PositionHome;
import org.nc.core.utils.CommonUtils;

public class EmployeeControllerBean implements SessionBean {
	private static final long serialVersionUID = 1L;
	
	private DataSource dataSource;
	private SessionContext sessionContext;
	private PositionHome positionHome;
	private EmployeeHome employeeHome;
	
	public void createPosition(String name) {
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
	}
	
	public Collection<String> getEmployeeList() {
		try {
			Collection<Employee> employees = employeeHome.findAll();
			Collection<String> result = new ArrayList<String>(employees.size());
			for (Employee employee : employees) {
				result.add(employee.stringValue());
			}
			return result;
		} catch (FinderException e) {
			CommonUtils.error("error occured during getting employee list", e);
		}
		
		return Collections.emptyList();
	}
	
	public Collection<String> getPositionList() {
		try {
			Collection<Position> positions = positionHome.findAll();
			Collection<String> result = new ArrayList<String>(positions.size());
			for (Position position : positions)
				result.add(position.stringValue());
			return result;
		} catch (FinderException e) {
			CommonUtils.error("error occured during getting position list", e);
		}
		
		return Collections.emptyList();
	}
	
	public Collection<String> findAllOccurences(String value) {	
		try {
			Collection<Employee> employees = employeeHome.findByPartOfNameOrPosition(value);
			Collection<String> result = new ArrayList<String>(employees.size());
			for (Employee employee : employees) {
				result.add(employee.stringValue());
			}
			return result;
		} catch (FinderException e1) {
			CommonUtils.error("error occured during searching", e1);
		}
		return Collections.emptyList();
	}

	@Override
	public void ejbActivate() {}

	@Override
	public void ejbPassivate() {}

	@Override
	public void ejbRemove() {
		this.dataSource = null;
		this.positionHome = null;
	}
	
	public void ejbCreate() throws CreateException {
		try {
	        InitialContext ic = new InitialContext();
	        dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/PersonnelDepartmentDS");
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
