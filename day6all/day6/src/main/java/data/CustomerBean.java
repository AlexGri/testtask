package data;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import javax.sql.*;

public class CustomerBean implements EntityBean
{
	private DataSource dataSource;
    private JobLocalHome jobHome;

	private String login;
	private String name;
	private String email;
	private String[] address = new String[2];

	public String getLogin () {
		return login;
	}

	public String getName () {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail () {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setAddress(String[] address){
		for (int i=0; i<Math.min(this.address.length,address.length); i++)
			this.address[i] = address[i];
	}

	public String[] getAddress() {
		String[] res = new String[address.length];
		System.arraycopy(this.address,0,res,0,res.length);
		return res;
	}

	// EJB methods start here

	public void ejbPostCreate (String login, String name, String email) {}

	public String ejbCreate (String login, String name, String email) throws CreateException {
		try {
            // workaround; should call ejbHome.findByPrimaryKey, but
            // ctx.getEJBHome() returns null...
			ejbFindByPrimaryKey(login);
			throw new CreateException("Duplicate customer name: "+login);
		}
		catch (FinderException ex) {}

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Customer (login,name,email) VALUES (?,?,?)");

			stmt.setString(1, login);
			stmt.setString(2, name);
			stmt.setString(3, email);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			error("Error creating customer "+login,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
		this.login = login;
		this.name = name;
		this.email = email;
		this.address[0] = "";
		this.address[1] = "";
		return login;
	}

	public String ejbFindByPrimaryKey(String login) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT login FROM Customer WHERE login = ?");

			stmt.setString(1, login);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new FinderException("Unknown customer: "+login);
			}
			return login;
		}
		catch (SQLException e) {
			error("Error in findByPrimaryKey for "+login,e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}

	public Collection ejbFindAll() throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT login FROM Customer ORDER BY login");

			rs = stmt.executeQuery();

			Collection col = new ArrayList();
			while (rs.next()) {
				col.add(rs.getString(1));
			}
			return col;
		}
		catch (SQLException e) {
			error("Error in findAll",e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}

	public void ejbLoad(){
		login = (String)ctx.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT login,name,email,address1,address2 FROM Customer WHERE login = ?");

			stmt.setString(1, login);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				error("No data found in ejbLoad for "+login,null);
			}
			this.login = rs.getString(1);
			this.name = rs.getString(2);
			this.email = rs.getString(3);
			this.address[0] = rs.getString(4);
			this.address[1] = rs.getString(5);
		}
		catch (SQLException e) {
			error("Error in ejbLoad for "+name,e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
	}

	public void ejbStore(){
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"UPDATE Customer SET name = ?, email = ?, address1 = ?, address2 = ? WHERE login = ?");

			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, address[0]);
			stmt.setString(4, address[1]);
			stmt.setString(5, login);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			error("Error in ejbStore for "+name,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
	}

	public void ejbPassivate(){
		login = null;
		name = null;
		email = null;
		address[0] = null;
		address[1] = null;
	}

	public void ejbActivate(){
	}

	public void ejbRemove(){
		login = (String)ctx.getPrimaryKey();

        // remove the customer's jobs.
        jobHome.deleteByCustomer(login);

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();

			stmt = con.prepareStatement(
			"DELETE FROM Customer WHERE login = ?");

			stmt.setString(1, login);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			error("Error removing customer "+login,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
		login = null;
		name = null;
		email = null;
		address[0] = null;
		address[1] = null;
	}

	private EntityContext ctx;

	public void setEntityContext(EntityContext ctx) {
		this.ctx = ctx;
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/Agency");
            jobHome = (JobLocalHome)ic.lookup("java:comp/env/ejb/JobLocal");
		}
		catch (NamingException ex) {
			error("Error looking up depended EJB or resource",ex);
			return;
		}
	}

	public void unsetEntityContext() {
		this.ctx = null;
		dataSource = null;
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

	private void error (String msg, Exception ex) {
		String s = "CustomerBean: "+msg + "\n" + ex;
		System.out.println(s);
		throw new EJBException(s,ex);
	}

}
