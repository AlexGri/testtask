package org.nc.core.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.EJBException;

import org.nc.core.entity.Employee;
import org.nc.core.entity.Position;
import org.nc.core.redistributable.javabean.EmployeePojo;
import org.nc.core.redistributable.javabean.PositionPojo;

public class CommonUtils {
	private CommonUtils() {
		throw new RuntimeException("CommonUtils should never be initialized");
	}
	
	public static void closeConnection (Connection con, PreparedStatement stmt, ResultSet rslt) {
		if (rslt != null) {
			try {
				rslt.close();
			}
			catch (SQLException e) {}
		}
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {}
		}
		if (con != null) {
			try {
				con.close();
			}
			catch (SQLException e) {}
		}
	}
	
	public static void error (String msg, Exception ex) {
		String s = msg + "\n" + ex;
		System.out.println(s);
		throw new EJBException(s,ex);
	}
	
	public static EmployeePojo createEmployeePojo(Employee employee) {
		if (employee == null)
			return null;
		else
			return new EmployeePojo(employee.getId(), employee.getFirstname(), 
					employee.getLastname(), employee.getMiddlename(), employee.getPhones(),
					employee.getSalary(), createPositionPojo(employee.getPosition()));
	}
	
	public static PositionPojo createPositionPojo(Position position) {
		if (position == null)
			return null;
		else
			return new PositionPojo(position.getId(), position.getPositionName());
	}
}
