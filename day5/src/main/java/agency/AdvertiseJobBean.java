package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.* ;
import javax.sql.*;

public class AdvertiseJobBean implements SessionBean
{
    private DataSource dataSource;

	private String ref;
	private String customer;
	private String description;
	private String location;
	private Collection skills;

	public void updateDetails (String description, String location, String[] skills) {
        if (skills == null) {
            skills = new String[0];
        }
		Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "UPDATE JOB SET description = ?, location = ? WHERE ref = ? AND customer = ?");

            stmt.setString(1, description);
            stmt.setString(2, location);
            stmt.setString(3, ref);
            stmt.setString(4, customer);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "DELETE FROM JobSkill WHERE job = ? AND customer = ?");

            stmt.setString(1, ref);
            stmt.setString(2, customer);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "INSERT INTO JobSkill (job, customer, skill) VALUES (?, ?, ?)");

            for (int i=0; i<skills.length; i++)
            {
	            stmt.setString(1, ref);
	            stmt.setString(2, customer);
	            stmt.setString(3, skills[i]);
	            stmt.executeUpdate();
            }

            this.description = description;
            this.location = location;
            this.skills.clear();
            for (int i=0; i<skills.length; i++)
            	this.skills.add(skills[i]);
        }
        catch (SQLException e) {
            error("Error updating job "+ref+" for "+customer,e);
		}
        finally {
            closeConnection(con, stmt, null);
        }
	}

    public String getRef() {
        return ref;
    }

    public String getCustomer() {
        return customer;
    }

	public String getDescription() {
		return description;
	}

	public String getLocation() {
		return location;
	}

	public String[] getSkills() {
		return ((String[])skills.toArray(new String[skills.size()]));
	}

	private void error (String msg, Exception ex) {
		String s = "JobManagerBean: "+msg + "\n" + ex;
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

	// EJB methods start here

	public void ejbCreate (String ref, String customer) throws CreateException {
		this.ref = ref;
		this.customer = customer;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT description,location FROM Job WHERE ref = ? AND customer = ?");

            stmt.setString(1, ref);
            stmt.setString(2, customer);
            rs = stmt.executeQuery();

            if (rs.next()) {
                description = rs.getString(1);
                location = rs.getString(2);
                skills = new TreeSet();
            }
            else
            	throw new CreateException ("Failed to find database job record for "+ref+":"+customer);

            stmt = con.prepareStatement(
            "SELECT skill FROM JobSkill WHERE job = ? AND customer = ?");

            stmt.setString(1, ref);
            stmt.setString(2, customer);
            rs = stmt.executeQuery();

            while (rs.next()) {
                skills.add(rs.getString(1));
            }
        }
        catch (SQLException e) {
            error("Error creating job "+ref+":"+customer,e);
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

