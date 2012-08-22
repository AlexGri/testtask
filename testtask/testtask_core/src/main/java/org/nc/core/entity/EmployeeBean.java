package org.nc.core.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.nc.core.utils.CommonUtils;

public class EmployeeBean implements EntityBean {
	private static final long serialVersionUID = 1L;
        
    private EntityContext entityContext;
    private DataSource dataSource;
    private PositionHome positionHome;
    
    private Long id;
    private String firstname;
    private String lastname;
    private String middlename;
    private String phones;
    private Double salary;
    private Position position;
    
    public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Long getId() {
		return id;
	}
	
	public Long ejbCreate(String firstname, String lastname,
        String middlename, String phones, Double salary, Position position)
        throws CreateException {
		
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		Long id = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO employee (firstname, lastname, middlename, phones, salary, position) " +
			" VALUES (?, ?, ?, ?, ?, ?)", java.sql.Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, firstname);
			stmt.setString(2, lastname);
			stmt.setString(3, middlename);
			stmt.setString(4, phones);
			stmt.setDouble(5, salary);
			stmt.setLong(6, (Long)position.getPrimaryKey());
			stmt.executeUpdate();
			resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				id = resultSet.getLong(1);
			} else {
				throw new CreateException("Creation did not insert any row");
			}
			
		}
		catch (SQLException e) {
			CommonUtils.error("Error creating Employee ", e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, resultSet);
		}
		
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setMiddlename(middlename);
        this.setPhones(phones);
        this.setSalary(salary);
        this.setPosition(position);
        this.id = id;
        return id;
    } 
	
	public void ejbPostCreate(String firstname, String lastname,
	        String middlename, String phones, Double salary, Position position) {}
	
	public Long ejbCreate(String firstname, String lastname) throws CreateException {
	       return ejbCreate(firstname, lastname, null, null, null, null);
	}
	
	public void ejbPostCreate(String firstname, String lastname) {}
	
	public Long ejbFindByPrimaryKey(Long id) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT id FROM employee WHERE id = ?");

			stmt.setLong(1, id);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new ObjectNotFoundException("Unknown employee");
			}
			return id;
		}
		catch (SQLException e) {
			CommonUtils.error("Error in findByPrimaryKey for "+id,e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return null;
	}
	
	public Collection<Long> ejbFindByPartOfNameOrPosition(String name) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String searchValue = StringUtils.isBlank(name) ? "%" : "%"+name.trim() +"%";
		
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT empl.id FROM employee as empl " +
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

			Collection<Long> col = new TreeSet<Long>();
			while (rs.next()) {
				col.add(rs.getLong(1));
			}

			return col;
		} catch (SQLException e) {
			CommonUtils.error("Error searching personnel department data", e);
		} finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return Collections.emptySet();
	}
	
	public Collection<Long> ejbFindAll() throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT id FROM employee");
			rs = stmt.executeQuery();

			Collection<Long> col = new TreeSet<Long>();
			while (rs.next()) {
				col.add(rs.getLong(1));
			}
			return col;
		}
		catch (SQLException e) {
			CommonUtils.error("Error in ejbFindAll ",e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return Collections.emptySet();
	}
        
	@Override
	public void ejbActivate() {}
	
	@Override
	public void ejbPassivate() {
		this.id = null;
		this.firstname = null;
		this.lastname = null;
		this.middlename = null;
		this.phones = null;
		this.salary = null;
		this.position = null;
	}

	@Override
	public void ejbLoad() {
		Long id = (Long)this.entityContext.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT firstname, lastname, " +
					" middlename, phones, salary, position FROM employee WHERE id = ?");

			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				CommonUtils.error("No data found in ejbLoad for " + id, null);
			}
			
			this.id = id;
			this.firstname = rs.getString(1);
			this.lastname = rs.getString(2);
			this.middlename = rs.getString(3);
			this.phones = rs.getString(4);
			this.salary = rs.getDouble(5);
			Long positionFK = rs.getLong(6); 
			if (positionFK == null)
				this.position = null;
			else {
				try {
					this.position = positionHome.findByPrimaryKey(positionFK);
				} catch (FinderException e) {
					e.printStackTrace();
				}
			}

		}
		catch (SQLException e) {
			CommonUtils.error("Error in ejbLoad for "+id,e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
	}

	@Override
	public void ejbRemove() {
		Long id = (Long)this.entityContext.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("DELETE FROM employee WHERE id = ?");

			stmt.setLong(1, id);
			stmt.executeUpdate();

		}
		catch (SQLException e) {
			CommonUtils.error("Error removing employee "+id,e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
	}

	@Override
	public void ejbStore() {
		Long id = (Long)this.entityContext.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("UPDATE employee SET firstname = ?, lastname = ?, " +
					" middlename = ?, phones = ?, salary = ?, position = ? WHERE id = ?");

			stmt.setString(1, firstname);
			stmt.setString(2, lastname);
			stmt.setString(3, middlename);
			stmt.setString(4, phones);
			stmt.setDouble(5, salary);
			stmt.setLong(6, (Long)position.getPrimaryKey());
			stmt.setLong(7, id);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			CommonUtils.error("Error in ejbStore for "+id,e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
	}

	@Override
	public void setEntityContext(EntityContext entityContext) {
		this.entityContext = entityContext;
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			dataSource = (DataSource)ic.lookup("java:comp/env/jdbc/PersonnelDepartmentDS");
			positionHome = (PositionHome)ic.lookup("java:comp/env/ejb/Position");
		}
		catch (NamingException ex) {
			CommonUtils.error("Error looking up depended datasource ",ex);
			return;
		}
	}

	@Override
	public void unsetEntityContext() {
		this.entityContext = null;
		this.dataSource = null;
		this.positionHome = null;
	}

	public String stringValue() {
		return "EmployeeBean [firstname=" + firstname + ", lastname="
				+ lastname + ", middlename=" + middlename + ", phones="
				+ phones + ", salary=" + salary + ", position=" + position.stringValue()
				+ "]";
	}
}
