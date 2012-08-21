package org.nc.core.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.nc.core.utils.CommonUtils;

public class EmployeeControllerBean implements SessionBean {
	private static final long serialVersionUID = 1L;
	
	private DataSource dataSource;
	private SessionContext sessionContext;
	
	public Collection<Object> getEmployeeList() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT lastname FROM employee employee");

			rs = stmt.executeQuery();

			Collection<Object> col = new TreeSet<Object>();
			while (rs.next()) {
				col.add(rs.getString(1));
			}
 
			return col;
		} catch (SQLException e) {
			CommonUtils.error("Error getting Employee list", e);
		} finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return null;
	}
	
	public Collection<Object> getPositionList() {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT position_name FROM position position");

			rs = stmt.executeQuery();

			Collection<Object> col = new TreeSet<Object>();
			while (rs.next()) {
				col.add(rs.getString(1));
			}

			return col;
		} catch (SQLException e) {
			CommonUtils.error("Error getting Position list", e);
		} finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return null;
	}
	
	public Collection<Object> findAllOccurences(String value) {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String searchValue = StringUtils.isBlank(value) ? "%" : "%"+value.trim() +"%";
		
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT empl.lastname FROM employee as empl " +
					" left join position pos on pos.id = empl.position " +
					" where empl.firstname like ?" +
					" or empl.lastname like ? " + 
					" or empl.middlename like ? " + 
					" or pos.position_name like ? "
					);
			
			stmt.setString(1, searchValue);
			stmt.setString(2, searchValue);
			stmt.setString(3, searchValue);
			stmt.setString(4, searchValue);

			rs = stmt.executeQuery();

			Collection<Object> col = new TreeSet<Object>();
			while (rs.next()) {
				col.add(rs.getString(1));
			}

			return col;
		} catch (SQLException e) {
			CommonUtils.error("Error searching personnel department data", e);
		} finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return null;
	}

	@Override
	public void ejbActivate() {}

	@Override
	public void ejbPassivate() {}

	@Override
	public void ejbRemove() {
		this.dataSource = null;
	}
	
	public void ejbCreate() throws CreateException {
		try {
	        InitialContext ic = new InitialContext();
	        dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/PersonnelDepartmentDS");
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
