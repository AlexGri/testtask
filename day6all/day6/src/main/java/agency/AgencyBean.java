package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.* ;
import javax.sql.*;

import data.*;

public class AgencyBean implements SessionBean
{
	private DataSource dataSource;
	private CustomerLocalHome customerHome;
	private JobLocalHome jobHome;
	private LocationLocalHome locationHome;
	private SkillLocalHome skillHome;
	private String agencyName = "";

	public String getAgencyName() {
		return agencyName;
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
            stmt = con.prepareStatement(
            "DELETE FROM ApplicantSkill WHERE applicant = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();

            stmt = con.prepareStatement(
            "DELETE FROM Applicant WHERE login = ?");

            stmt.setString(1, login);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            error("Error deleting Applicant "+login,e);
        }
        finally {
            closeConnection(con, stmt, null);
        }
	}

	public Collection findAllCustomers() {
		try {
			Collection res = new ArrayList();
			Collection col = customerHome.findAll();
			Iterator it = col.iterator();
			while (it.hasNext())
			{
				CustomerLocal customer = (CustomerLocal)it.next();
				res.add(customer.getLogin());
			}
			return res;
		}
		catch (FinderException e) {
			error("Error getting Customer list",e);
		}
		return null;
	}


	public void createCustomer(String login, String name, String email) throws DuplicateException, CreateException{
		try {
			CustomerLocal customer = customerHome.create(login,name,email);
		}
		catch (CreateException e) {
			error("Error adding Customer "+login,e);
		}
	}

	public void deleteCustomer (String login) throws NotFoundException {
		try {
			customerHome.remove(login);
		}
		catch (RemoveException e) {
			error("Error removing customer "+login,e);
		}
	}

	public Collection getLocations() {
		try {
			Collection res = new ArrayList();
			Collection col = locationHome.findAll();
			Iterator it = col.iterator();
			while (it.hasNext())
			{
				LocationLocal location = (LocationLocal)it.next();
				res.add(location.getName());
			}
			return res;
		}
		catch (FinderException e) {
			error("Error getting Location list",e);
		}
		return null;
	}

	public String getLocationDescription(String name) throws NotFoundException {
		try {
			LocationLocal location = locationHome.findByPrimaryKey(name);
			return location.getDescription();
		}
		catch (FinderException e) {
			error("Error finding Location description for "+name,e);
		}
		return null;
	}

	public void updateLocation(String name, String description) throws NotFoundException {
		try {
			LocationLocal location = locationHome.findByPrimaryKey(name);
			location.setDescription(description);
		}
		catch (FinderException e) {
			error("Error updating Location description for "+name,e);
		}
	}

	public void addLocation(String name, String description) throws DuplicateException {
		try {
			LocationLocal location = locationHome.create(name,description);
		}
		catch (CreateException e) {
			error("Error adding Location "+name,e);
		}
	}

	public void removeLocation(String name) throws NotFoundException {
		try {
			LocationLocal location = locationHome.findByPrimaryKey(name);
			location.remove();
		}
		catch (FinderException e) {
			error("Remove error finding Location "+name,e);
		}
		catch (RemoveException e) {
			error("Error removing Location "+name,e);
		}
	}

	public Collection getSkills() {
		try {
			Collection res = new ArrayList();
			Collection col = skillHome.findAll();
			Iterator it = col.iterator();
			while (it.hasNext())
			{
				SkillLocal skill = (SkillLocal)it.next();
				res.add(skill.getName());
			}
			return res;
		}
		catch (FinderException e) {
			error("Error getting Skill list",e);
		}
		return null;
	}

	public String getSkillDescription(String name) throws NotFoundException {
		try {
			SkillLocal skill = skillHome.findByPrimaryKey(name);
			return skill.getDescription();
		}
		catch (FinderException e) {
			error("Error finding skill description for "+name,e);
		}
		return null;
	}

	public void updateSkill(String name, String description) throws NotFoundException {
		try {
			SkillLocal skill = skillHome.findByPrimaryKey(name);
			skill.setDescription(description);
		}
		catch (FinderException e) {
			error("Error updating skill description for "+name,e);
		}
	}

	public void addSkill(String name, String description) throws DuplicateException {
		try {
			SkillLocal skill = skillHome.create(name,description);
		}
		catch (CreateException e) {
			error("Error adding skill "+name,e);
		}
	}

	public void removeSkill(String name) throws NotFoundException {
		try {
			SkillLocal skill = skillHome.findByPrimaryKey(name);
			skill.remove();
		}
		catch (FinderException e) {
			error("Remove error finding skill "+name,e);
		}
		catch (RemoveException e) {
			error("Error removing skill "+name,e);
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
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/Agency");
			agencyName = (String)ic.lookup("java:comp/env/AgencyName");
			customerHome = (CustomerLocalHome)ic.lookup("java:comp/env/ejb/CustomerLocal");
			jobHome = (JobLocalHome)ic.lookup("java:comp/env/ejb/JobLocal");
			locationHome = (LocationLocalHome)ic.lookup("java:comp/env/ejb/LocationLocal");
			skillHome = (SkillLocalHome)ic.lookup("java:comp/env/ejb/SkillLocal");
		}
		catch (NamingException ex) {
			error("Error looking up depended EJB or resource",ex);
			return;
		}
	}
}
