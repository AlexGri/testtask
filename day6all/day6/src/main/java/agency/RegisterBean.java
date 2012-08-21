package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.* ;
import javax.sql.*;

public class RegisterBean implements SessionBean
{
    private DataSource dataSource;

	private String login;
	private String name;
	private String email;
	private String summary;
	private String location;
	private Collection skills;

	public void updateDetails (String name, String email, String location, String summary, String[] skills) {
        if (skills == null) {
            skills = new String[0];
        }
		Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "UPDATE Applicant SET name = ?, email = ?, location = ?, summary = ? WHERE login = ?");

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, location);
            stmt.setString(4, summary);
            stmt.setString(5, login);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "DELETE FROM ApplicantSkill WHERE applicant = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "INSERT INTO ApplicantSkill (applicant, skill) VALUES (?, ?)");

            for (int i=0; i<skills.length; i++)
            {
	            stmt.setString(1, login);
	            stmt.setString(2, skills[i]);
	            stmt.executeUpdate();
            }

            this.name = name;
            this.email = email;
            this.summary = summary;
            this.location = location;
            this.skills.clear();
            for (int i=0; i<skills.length; i++)
            	this.skills.add(skills[i]);
        }
        catch (SQLException e) {
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

	public String getLocation() {
		return location;
	}

	public String getSummary() {
		return summary;
	}

	public String[] getSkills() {
		return ((String[])skills.toArray(new String[skills.size()]));
	}

	private void error (String msg, Exception ex) {
		String s = "RegisterBean: "+msg + "\n" + ex;
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

	public void ejbCreate (String login) throws CreateException {
		this.login = login;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            stmt = con.prepareStatement(
            "SELECT name,email,summary,location FROM Applicant WHERE login = ?");

            stmt.setString(1, login);
            rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString(1);
                email = rs.getString(2);
                summary = rs.getString(3);
                location = rs.getString(4);
            }
            else
            	throw new CreateException ("Create failed to find databse record for "+login);

            stmt = con.prepareStatement(
            "SELECT skill FROM ApplicantSkill WHERE applicant = ?");

            stmt.setString(1, login);
            rs = stmt.executeQuery();

			skills = new TreeSet();
            while (rs.next()) {
                skills.add(rs.getString(1));
            }
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