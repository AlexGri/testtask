package data;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import javax.sql.*;

public class JobBean implements EntityBean
{
	private DataSource dataSource;
	private SkillLocalHome skillHome;
	private LocationLocalHome locationHome;
	private CustomerLocalHome customerHome;

	private String ref;
	private String customer;
	private String description;
	private LocationLocal location;

    private CustomerLocal customerObj; // derived

	// vector attribute; list of SkillLocal ref's.
    private List skills;

	public String getRef () {
		return ref;
	}

	public String getCustomer () {
		return customer;
	}

	public CustomerLocal getCustomerObj () {
		return customerObj;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocationLocal getLocation () {
		return location;
	}

	public void setLocation(LocationLocal location) {
		this.location = location;
	}

    /** returns (copy of) skills */
	public Collection getSkills() {
        return new ArrayList(skills);
    }

	public void setSkills(Collection skills) {
		// just validate that the collection holds references to SkillLocal's
		for(Iterator iter = getSkills().iterator(); iter.hasNext(); ) {
            SkillLocal skill = (SkillLocal)iter.next();
        }

		// replace the list of skills with that defined.
		this.skills = new ArrayList(skills);
	}

	// EJB methods start here

	public void ejbPostCreate (String ref, String customer) {}

	public JobPK ejbCreate (String ref, String customer) throws CreateException {

		// validate customer login is valid.
		try {
            customerObj = customerHome.findByPrimaryKey(customer);
        } catch(FinderException ex) {
            error("Invalid customer.", ex);
        }

		JobPK key = new JobPK(ref,customer);
		try {
            // workaround; should call ejbHome.findByPrimaryKey, but
            // ctx.getEJBHome() returns null...
			/*
            JobLocalHome jobHome = (JobLocalHome)ctx.getEJBHome();
			System.out.println("jobHome = " + jobHome + ", key = " + key);
			*/
			ejbFindByPrimaryKey(key);
			throw new CreateException("Duplicate job name: "+key);
		}
		catch (FinderException ex) {}
		
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO Job (ref,customer) VALUES (?,?)");

			stmt.setString(1, ref);
			stmt.setString(2, customerObj.getLogin());
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			error("Error creating job "+key,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
		this.ref = ref;
		this.customer = customer;
		this.description = description;
		this.location = null;

        this.skills = new ArrayList();
		return key;
	}
	
	public JobPK ejbFindByPrimaryKey(JobPK key) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT ref FROM Job WHERE ref = ? AND customer = ?");

			stmt.setString(1, key.getRef());
			stmt.setString(2, key.getCustomer());
			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new FinderException("Unknown job: "+key);
			}
			return key;
		}
		catch (SQLException e) {
			error("Error in findByPrimaryKey for "+key,e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}
	
	public Collection ejbFindByCustomer(String customer) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT ref, customer FROM Job WHERE customer = ? ORDER BY ref");

			stmt.setString(1, customer);
			rs = stmt.executeQuery();

			Collection col = new ArrayList();
			while (rs.next()) {
				String nextRef = rs.getString(1);
				String nextCustomer = rs.getString(2);
				// validate customer exists
                CustomerLocal nextCustomerObj = customerHome.findByPrimaryKey(nextCustomer);
				col.add(new JobPK(nextRef,nextCustomerObj.getLogin()));
			}
			return col;
		}
		catch (SQLException e) {
			error("Error in findByCustomer: "+customer,e);
		}
		catch (FinderException e) {
			error("Error in findByCustomer, invalid customer: "+customer,e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}

	public Collection ejbFindByLocation(String location) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT ref, customer FROM Job WHERE location = ? ORDER BY customer, ref");

			stmt.setString(1, location);
			rs = stmt.executeQuery();

			Collection col = new ArrayList();
			while (rs.next()) {
				String ref = rs.getString(1);
				String customer = rs.getString(2);
				// validate customer exists
                CustomerLocal customerObj = customerHome.findByPrimaryKey(customer);
				col.add(new JobPK(ref,customer));
			}
			return col;
		}
		catch (SQLException e) {
			error("Error in findByLocation: "+location,e);
		}
		catch (FinderException e) {
			error("Error in findByLocation, invalid customer: "+customer,e);
		}
		finally {
			closeConnection(con, stmt, rs);
		}
		return null;
	}

	public void ejbHomeDeleteByCustomer(String customer) {

		Connection con = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt1 = null;
		try {
			con = dataSource.getConnection();

			stmt1 = con.prepareStatement(
			"DELETE FROM JobSkill WHERE customer = ?");
			stmt2 = con.prepareStatement(
			"DELETE FROM Job WHERE customer = ?");

			stmt1.setString(1, customer);
			stmt2.setString(1, customer);

			stmt1.executeUpdate();
			stmt2.executeUpdate();
		}
		catch (SQLException e) {
			error("Error removing all jobs for "+customer,e);
		}
		finally {
			closeConnection(con, stmt1, null);
			closeConnection(con, stmt2, null);
		}
	}

	public void ejbLoad(){
		JobPK key= (JobPK)ctx.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"SELECT description,location FROM Job WHERE ref = ? AND customer = ?");

			stmt.setString(1, key.getRef());
			stmt.setString(2, key.getCustomer());
			rs = stmt.executeQuery();

			if (!rs.next()) {
				error("No data found in ejbLoad for "+key,null);
			}
			this.ref = key.getRef();
			this.customer = key.getCustomer();
            this.customerObj = customerHome.findByPrimaryKey(this.customer); // derived
			this.description = rs.getString(1);
            String locationName = rs.getString(2);
			this.location = (locationName != null)?locationHome.findByPrimaryKey(locationName):null;

			// load skills
   			stmt = con.prepareStatement(
			"SELECT job, customer, skill FROM JobSkill WHERE job = ? AND customer = ? ORDER BY skill");

			stmt.setString(1, ref);
			stmt.setString(2, customerObj.getLogin());
			rs = stmt.executeQuery();

			List skillNameList = new ArrayList();
			while (rs.next()) {
				skillNameList.add(rs.getString(3));
			}

			this.skills = skillHome.lookup(skillNameList);
		}
		catch (SQLException e) {
			error("Error in ejbLoad for "+key,e);
		}
		catch (FinderException e) {
			error("Error in ejbLoad (invalid customer or location) for "+key,e);
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
			"UPDATE Job SET description = ?, location = ? WHERE ref = ? AND customer = ?");

			stmt.setString(1, description);
			if (location != null) {
				stmt.setString(2, location.getName());
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR);
        	}
			stmt.setString(3, ref);
			stmt.setString(4, customerObj.getLogin());
			stmt.executeUpdate();

			// delete all skills
			stmt = con.prepareStatement(
			"DELETE FROM JobSkill WHERE job = ? and customer = ?");
	
			stmt.setString(1, ref);
			stmt.setString(2, customerObj.getLogin());
			stmt.executeUpdate();
	
			// add back in all skills
			for(Iterator iter = getSkills().iterator(); iter.hasNext(); ) {
	            SkillLocal skill = (SkillLocal)iter.next();
	
				stmt = con.prepareStatement(
				"INSERT INTO JobSkill (job,customer,skill) VALUES (?,?,?)");
	
				stmt.setString(1, ref);
				stmt.setString(2, customerObj.getLogin());
				stmt.setString(3, skill.getName());
				stmt.executeUpdate();
	        }
		}
		catch (SQLException e) {
			error("Error in ejbStore for "+ref+","+customer,e);
		}
		finally {
			closeConnection(con, stmt, null);
		}
	}

	public void ejbPassivate(){
		ref = null;
		customer = null;
		customerObj = null;
		description = null;
		location = null;
	}

	public void ejbActivate(){
	}

	public void ejbRemove(){
		JobPK key = (JobPK)ctx.getPrimaryKey();
		
		Connection con = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		try {
			con = dataSource.getConnection();

			stmt1 = con.prepareStatement(
			"DELETE FROM JobSkill WHERE job = ? and customer = ?");

			stmt1.setString(1, ref);
			stmt1.setString(2, customerObj.getLogin());

			stmt2 = con.prepareStatement(
			"DELETE FROM Job WHERE ref = ? and customer = ?");

			stmt2.setString(1, ref);
			stmt2.setString(2, customerObj.getLogin());

			stmt1.executeUpdate();
			stmt2.executeUpdate();
		}
		catch (SQLException e) {
			error("Error removing job "+key,e);
		}
		finally {
			closeConnection(con, stmt1, null);
			closeConnection(con, stmt2, null);
		}
		ref = null;
		customer = null;
		customerObj = null;
		description = null;
		location = null;
	}

	private EntityContext ctx;
	
	public void setEntityContext(EntityContext ctx) {
		this.ctx = ctx;
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/Agency");
			skillHome = (SkillLocalHome)ic.lookup("java:comp/env/ejb/SkillLocal");
			locationHome = (LocationLocalHome)ic.lookup("java:comp/env/ejb/LocationLocal");
			customerHome = (CustomerLocalHome)ic.lookup("java:comp/env/ejb/CustomerLocal");
		}
		catch (NamingException ex) {
			error("Error looking up depended EJB or resource",ex);
			return;
		}
	}

	public void unsetEntityContext() {
		this.ctx = null;
		dataSource = null;
        skillHome = null;
        locationHome = null;
        customerHome = null;
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
		String s = "JobBean: "+msg + "\n" + ex;
		System.out.println(s);
		throw new EJBException(s,ex);
	}
}
