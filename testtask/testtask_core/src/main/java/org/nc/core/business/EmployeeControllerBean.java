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
			stmt = con.prepareStatement("SELECT employee FROM Employee employee");

			rs = stmt.executeQuery();

			Collection<Object> col = new TreeSet<Object>();
			while (rs.next()) {
				col.add(rs.getString(1));
			}

			return col;
		} catch (SQLException e) {
			error("Error getting Employee list", e);
		} finally {
			closeConnection(con);
			closeConnection(stmt);
			closeConnection(rs);
		}
		return null;
	}
	
	/*public Collection<Object> getPositionList() {
		
	}
	
	public Collection<Object> findAllOccurences(String value) {
		
	}*/

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
	
	private void closeConnection (AutoCloseable closeable) {
		if (closeable != null) {
            try {
            	closeable.close();
            }
            catch (Exception e) {}
        }
	}

}
