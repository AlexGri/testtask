package org.nc.core.business;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.TreeSet;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

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
			error("Error getting Employee list", e);
		} finally {
			closeConnection(con, stmt, rs);
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
			error("Error getting Position list", e);
		} finally {
			closeConnection(con, stmt, rs);
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
			error("Error searching personnel department data", e);
		} finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}

	@Override
	public void ejbActivate() throws EJBException, RemoteException {}

	@Override
	public void ejbPassivate() throws EJBException, RemoteException {}

	@Override
	public void ejbRemove() throws EJBException, RemoteException {
		this.dataSource = null;
	}
	
	public void ejbCreate() throws EJBException, RemoteException {
		try {
	        InitialContext ic = new InitialContext();
	        dataSource = (DataSource)ic.lookup("java:PD-DS");
        }
        catch (NamingException ex) {
            error("Error connecting to PD:",ex);
        }
	}

	@Override
	public void setSessionContext(SessionContext arg0) throws EJBException,
			RemoteException {
		this.sessionContext = arg0;		
	}
	
	private void error (String msg, Exception ex) {
		String s = "EmployeeControllerBean: "+msg + "\n" + ex;
        System.out.println(s);
        throw new EJBException(s,ex);
	}
	
	private void closeConnection (Connection con, PreparedStatement stmt, ResultSet rslt) {
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
}
