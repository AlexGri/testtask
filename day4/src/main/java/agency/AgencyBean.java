package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.* ;
import javax.sql.*;

public class AgencyBean implements SessionBean
{
    private DataSource dataSource;
    private String name = "";

	public String getAgencyName() {
		return name;
	}

	public Collection findAllApplicants() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT login FROM Applicant");

            rs = stmt.executeQuery();

            Collection col = new TreeSet();
            while (rs.next()) {
                col.add(rs.getString(1));
            }

            return col;
        }
        catch (SQLException e) {
            error("Error getting Applicant list",e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
        return null;
    }
	
	public void createApplicant(String login, String name, String email) throws DuplicateException, CreateException{
		Collection apps = findAllApplicants();
		Iterator it = apps.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			if (s.equalsIgnoreCase(login))
				throw (new DuplicateException(s+" applicant already defined"));
		}
        Connection con = null;
        PreparedStatement stmt = null;
        try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Applicant (login,name,email) VALUES (?, ?, ?)");

			stmt.setString(1, login);
			stmt.setString(2, name);
			stmt.setString(3, email);

			stmt.executeUpdate();
        }
        catch (SQLException e) {
            error("Error creating Applicant "+login,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}
	

	public void deleteApplicant (String login) throws NotFoundException{
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
            "DELETE FROM ApplicantSkill WHERE applicant = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "DELETE FROM Applicant WHERE login = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
            con.commit();
        }
        catch (SQLException e) {
        	if (con != null) {
        		try {
	        		con.rollback();
        		}
        		catch (SQLException ex) {}
        	}
            error("Error deleteing Applicant "+login,e);
        }
        finally {
            closeConnection(con, stmt, null);
        }
	}

	public Collection findAllCustomers() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT login FROM Customer");

            rs = stmt.executeQuery();

            Collection col = new TreeSet();
            while (rs.next()) {
                col.add(rs.getString(1));
            }

            return col;
        }
        catch (SQLException e) {
            error("Error getting Customer list",e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
        return null;
	}
	

	public void createCustomer(String login, String name, String email) throws DuplicateException, CreateException{
		Collection cust = findAllCustomers();
		Iterator it = cust.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			if (s.equalsIgnoreCase(login))
				throw (new DuplicateException(s+" customer already defined"));
		}
        Connection con = null;
        PreparedStatement stmt = null;
        try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Customer (login,name,email) VALUES (?, ?, ?)");

			stmt.setString(1, login);
			stmt.setString(2, name);
			stmt.setString(3, email);

			stmt.executeUpdate();
        }
        catch (SQLException e) {
            error("Error creating Customer "+login,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}
	

	public void deleteCustomer (String login) throws NotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
            "DELETE FROM InvoiceItem WHERE customer = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "DELETE FROM Invoice WHERE customer = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "DELETE FROM JobSkill WHERE customer = ?");
            stmt.setString(1, login);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "DELETE FROM Job WHERE customer = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "DELETE FROM Customer WHERE login = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
            con.commit();
        }
        catch (SQLException e) {
        	if (con != null) {
        		try {
	        		con.rollback();
        		}
        		catch (SQLException ex) {}
        	}
            error("Error deleting Customer "+login,e);
        }
        finally {
            closeConnection(con, stmt, null);
        }
	}
	
	public Collection getLocations() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT name FROM Location");

            rs = stmt.executeQuery();

            Collection col = new TreeSet();
            while (rs.next()) {
                col.add(rs.getString(1));
            }

            return col;
        }
        catch (SQLException e) {
            error("Error getting Location list",e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
        return null;
	}
	
	public void addLocation(String name) throws DuplicateException {
		Collection locations = getLocations();
		Iterator it = locations.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			if (s.equalsIgnoreCase(name))
				throw (new DuplicateException(s+" location already defined"));
		}
		
        Connection con = null;
        PreparedStatement stmt = null;
        try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Location (name) VALUES (?)");

			stmt.setString(1, name);

			stmt.executeUpdate();
		}
        catch (SQLException e) {
            error("Error creating Location "+name,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}
	
	public void removeLocation(String code) throws NotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
            "UPDATE Job SET location = '' WHERE location = ?");

            stmt.setString(1, code);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "UPDATE Applicant SET location = '' WHERE location = ?");

            stmt.setString(1, code);
            stmt.executeUpdate();
            
            stmt = con.prepareStatement(
            "DELETE FROM Location WHERE name = ?");

            stmt.setString(1, code);
            stmt.executeUpdate();
            
            con.commit();
        }
        catch (SQLException e) {
     		try {
        		con.rollback();
    		}
        	catch (SQLException ex) {}
            error("Error removing Location "+code,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}
	
	public Collection getSkills() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT name FROM Skill");

            rs = stmt.executeQuery();

            Collection col = new TreeSet();
            while (rs.next()) {
                col.add(rs.getString(1));
            }

            return col;
        }
        catch (SQLException e) {
            error("Error getting Skill list: ",e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
        return null;
	}

	public void addSkill (String name) throws DuplicateException {
		Collection skills = getSkills();
		Iterator it = skills.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			if (s.equalsIgnoreCase(name))
				throw (new DuplicateException(s+" skill already defined"));
		}
		
        Connection con = null;
        PreparedStatement stmt = null;
        try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Skill (name) VALUES (?)");

			stmt.setString(1, name);
			stmt.executeUpdate();
		}
        catch (SQLException e) {
            error("Error creating skill "+name,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}
	
	public void removeSkill (String name) throws NotFoundException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);
            stmt = con.prepareStatement(
            "DELETE FROM JobSkill WHERE skill = ?");

            stmt.setString(1, name);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "DELETE FROM ApplicantSkill WHERE skill = ?");

            stmt.setString(1, name);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "DELETE FROM Skill WHERE name = ?");

            stmt.setString(1, name);
            stmt.executeUpdate();

            con.commit();
        }
        catch (SQLException e) {
    		try {
        		con.rollback();
    		}
       		catch (SQLException ex) {}
            error("Error deleting skill "+name,e);
        }
        finally {
            closeConnection(con, stmt, null);
        }
	}

	public List select(String table) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT * FROM "+table);

            rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();

            List ans = new ArrayList();
            String[] hdr = new String[numCols];
            
            // get column header info
            for (int i=1; i <= numCols; i++)
                hdr[i-1] = rsmd.getColumnLabel(i);	
            ans.add(hdr);

            while (rs.next()) {
            	String[] row = new String[numCols];
          		for (int i=1; i <= numCols; i++)
					row[i-1] = rs.getString(i);
				ans.add(row);
            }

            return ans;
        }
        catch (SQLException e) {
            error("Error getting table rows",e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
        return null;
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

	// EJB methods start here

	private void error (String msg, Exception ex) {
		String s = "AgencyBean: "+msg + "\n" + ex;
        System.out.println(s);
        throw new EJBException(s,ex);
	}
	
	public void ejbCreate () throws CreateException {
        try {
	        InitialContext ic = new InitialContext();
	        dataSource = (DataSource)ic.lookup("java:agencyDS");
	        name = (String)ic.lookup("java:comp/env/AgencyName");
        }
        catch (NamingException ex) {
            error("Error connecting to Agency:",ex);
        }
	}

    public void ejbActivate(){
    }

    public void ejbPassivate(){
    }

    public void ejbRemove(){
    	dataSource = null;
    }

    private SessionContext ctx;
    
    public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
    }	
}
