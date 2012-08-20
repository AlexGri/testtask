package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.* ;
import javax.sql.*;

public class AdvertiseBean implements SessionBean
{
    private DataSource dataSource;

	private String login;
	private String name;
	private String email;
	private String[] address;
	private Collection jobs = new TreeSet();

	public void updateDetails (String name, String email, String[] address) {
		String[] dbAddress = {"",""};
		for (int i=0; i<Math.min(dbAddress.length,address.length); i++)
			dbAddress[i] = address[i];

		Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "UPDATE Customer SET name = ?, email = ?, address1 = ?, address2 = ? WHERE login = ?");

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, dbAddress[0]);
            stmt.setString(4, dbAddress[1]);
            stmt.setString(5, login);
            stmt.executeUpdate();

            this.name = name;
            this.email = email;
            this.address = dbAddress;
        }
        catch (SQLException e) {
        	try
        	{
	        	if (con != null)
		        	con.rollback();
        	}
        	catch (SQLException ex) {}
            error("Error updating "+login,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}

	public String getLogin() {
		return login;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String[] getAddress() {
		return address;
	}

	public String[] getJobs() {
		return ((String[])jobs.toArray(new String[jobs.size()]));
	}

	private void loadJobList () {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT ref FROM Job WHERE customer = ?");

            stmt.setString(1, login);
            rs = stmt.executeQuery();

			jobs.clear();
            while (rs.next()) {
                jobs.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            error("Error loading jobs for "+login,e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
	}

	public void createJob (String ref) throws DuplicateException, CreateException {
		Iterator it = jobs.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			if (s.equalsIgnoreCase(ref))
				throw (new DuplicateException(s+": job already defined"));
		}
        Connection con = null;
        PreparedStatement stmt = null;
        try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Job (ref,customer) VALUES (?, ?)");

			stmt.setString(1, ref);
			stmt.setString(2, login);

			stmt.executeUpdate();
			loadJobList();
        }
        catch (SQLException e) {
            error("Error creating Job "+login+":"+ref,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}

	public void deleteJob (String ref) throws NotFoundException {
		boolean found = false;
		Iterator it = jobs.iterator();
		while (it.hasNext()) {
			String s = (String)it.next();
			if (s.equalsIgnoreCase(ref)) {
				found = true;
				break;
			}
		}
		if (!found)
			throw (new NotFoundException(ref+": job not found"));
 		Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "DELETE FROM JobSkill WHERE job = ? AND customer = ?");

            stmt.setString(1, ref);
            stmt.setString(2, login);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "DELETE FROM Job WHERE ref = ? AND customer = ?");

            stmt.setString(1, ref);
            stmt.setString(2, login);
            stmt.executeUpdate();

            loadJobList();
        }
        catch (SQLException e) {
            error("Error deleting job "+ref+" for "+login,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}

	private void error (String msg, Exception ex) {
		String s = "AdvertiseBean: "+msg + "\n" + ex;
        System.out.println(s);
        throw new EJBException(s, ex);
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

	public void ejbCreate (String login) throws CreateException {
		this.login = login;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT name,email,address1,address2 FROM Customer WHERE login = ?");

            stmt.setString(1, login);
            rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString(1);
                email = rs.getString(2);
                address = new String[2];
                address[0] = rs.getString(3);
                address[1] = rs.getString(4);
            }
            else
            	throw new CreateException ("Create failed to find databse record for "+login);

            loadJobList();
        }
        catch (SQLException e) {
            error("Error creating "+login,e);
		}
        finally {
            closeConnection(con, stmt, rs);
        }
	}

    public void ejbActivate(){
    }

    public void ejbPassivate(){
    }

    public void ejbRemove(){
    }

    private SessionContext ctx;

    public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
        try {
	        InitialContext ic = new InitialContext();
	        dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/Agency");
        }
        catch (NamingException ex) {
            error("Error connecting to java:comp/env/jdbc/Agency:",ex);
        }
    }
}

