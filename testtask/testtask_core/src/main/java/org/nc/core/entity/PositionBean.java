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

import org.nc.core.utils.CommonUtils;

public class PositionBean implements EntityBean {
	private static final long serialVersionUID = 1L;
	
	private String positionName;
	private EntityContext entityContext;
	private DataSource dataSource;

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	
	public Long ejbCreate(String positionName) throws CreateException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		Long id = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement(
			"INSERT INTO position (position_name) VALUES (?)", java.sql.Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, positionName);
			stmt.executeUpdate();
			resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				id = resultSet.getLong(1);
			} else {
				throw new CreateException("Creation doen not take any effect: "+positionName);
			}
			
		}
		catch (SQLException e) {
			CommonUtils.error("Error creating Position "+positionName, e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, resultSet);
		}
		this.positionName = positionName;
		return id;
	}
	
	public void ejbPostCreate(String positionName){}

	public Long ejbFindByPrimaryKey(Long id) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT id FROM position WHERE id = ?");

			stmt.setLong(1, id);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new ObjectNotFoundException("Unknown position");
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
	
	public Long ejbFindByName(String name) throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT id FROM position WHERE position_name = ?");

			stmt.setString(1, name);
			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new ObjectNotFoundException("Unknown position");
			}
			return rs.getLong(1);
		}
		catch (SQLException e) {
			CommonUtils.error("Error in ejbFindByName for "+name,e);
		}
		finally {
			CommonUtils.closeConnection(con, stmt, rs);
		}
		return null;
	}
	
	public Collection<Long> ejbFindAll() throws FinderException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT id FROM position");
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
	public void ejbActivate() {
	}
	
	@Override
	public void ejbPassivate() {
		this.positionName = null;
	}

	@Override
	public void ejbLoad() {
		Long id = (Long)this.entityContext.getPrimaryKey();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stmt = con.prepareStatement("SELECT id, position_name FROM position WHERE id = ?");

			stmt.setLong(1, id);
			rs = stmt.executeQuery();
			
			if (!rs.next()) {
				CommonUtils.error("No data found in ejbLoad for " + id, null);
			}
			
			this.positionName = rs.getString(2);

		}
		catch (SQLException e) {
			CommonUtils.error("Error in ejbLoad for "+id,e);
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
			stmt = con.prepareStatement("UPDATE position SET position_name = ? WHERE id = ?");

			stmt.setString(1, this.positionName);
			stmt.setLong(2, id);
			stmt.executeUpdate();
		}
		catch (SQLException e) {
			CommonUtils.error("Error in ejbStore for "+positionName,e);
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
			stmt = con.prepareStatement("DELETE FROM position WHERE id = ?");

			stmt.setLong(1, id);
			stmt.executeUpdate();

		}
		catch (SQLException e) {
			CommonUtils.error("Error removing position "+this.getPositionName(),e);
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
	}

	@Override
	public String toString() {
		return "PositionBean [positionName=" + positionName + "]";
	}
}
