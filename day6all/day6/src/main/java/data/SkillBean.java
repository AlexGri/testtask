package data;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import javax.sql.*;

public class SkillBean implements EntityBean
{
	private DataSource dataSource;
	private String name;
	private String description;

	public String getName () {
		return name;
	}
	
	public String getDescription () {
		return description;
	}
	
	public void setDescription (String description) {
		this.description = description;
	}

	// EJB methods start here

	public void ejbPostCreate (String name, String description) {}

	public String ejbCreate (String name, String description) throws CreateException {
		try {
            // workaround; should call ejbHome.findByPrimaryKey, but
            // ctx.getEJBHome() returns null...
			ejbFindByPrimaryKey(name);
			throw new CreateException("Duplicate skill name: "+name);
		}
		catch (FinderException ex) {}
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Skill (name,description) VALUES (?,?)");

			stmt.setString(1, name);
			stmt.setString(2, description);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			error("Error creating Skill "+name,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
		this.name = name;
		this.description = description;
		return name;
	}
	
	public String ejbFindByPrimaryKey(String name) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT name FROM Skill WHERE name = ?");

			stmt.setString(1, name);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new FinderException("Unknown Skill: "+name);
			}
			return name;
		}
		catch (SQLException e) {
			error("Error in findByPrimaryKey for "+name,e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}

    public List ejbHomeLookup(List names) throws FinderException {
        List res = new ArrayList();
        for(Iterator iter = names.iterator(); iter.hasNext(); ) {
            String name = (String)iter.next();
			SkillLocal skill = ((SkillLocalHome)ctx.getEJBLocalHome()).findByPrimaryKey(name);
            if (skill == null) {
                throw new FinderException("Could not find skill named " + name);
			}
            res.add(skill);
        }
        return res;
    }
	
	public Collection ejbFindAll() throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT name FROM Skill ORDER BY name");

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
		name = (String)ctx.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT name,description FROM Skill WHERE name = ?");

			stmt.setString(1, name);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				error("No data found in ejbLoad for "+name,null);
			}
			this.name = rs.getString(1);
			this.description = rs.getString(2);
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
			"UPDATE Skill SET description = ? WHERE name = ?");

			stmt.setString(1, description);
			stmt.setString(2, name);
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
		name = null;
		description = null;
	}

	public void ejbActivate(){
	}

	public void ejbRemove(){
		name = (String)ctx.getPrimaryKey();
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
		  
			stmt = con.prepareStatement(
			"DELETE FROM Skill WHERE name = ?");

			stmt.setString(1, name);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			error("Error removing location "+name,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
		name = null;
		description = null;
	}

	private EntityContext ctx;
	
	public void setEntityContext(EntityContext ctx) {
		this.ctx = ctx;
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/Agency");
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
		String s = "SkillBean: "+msg + "\n" + ex;
		System.out.println(s);
		throw new EJBException(s,ex);
	}
	
}
