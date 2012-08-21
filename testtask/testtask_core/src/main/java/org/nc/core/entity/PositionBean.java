package org.nc.core.entity;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

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
			"INSERT INTO position (position_name) VALUES (?)");

			stmt.setString(1, positionName);
			stmt.executeUpdate();
			resultSet = stmt.getGeneratedKeys();
			if (resultSet.next()) {
				id = resultSet.getLong(1);
			} else {
				throw new CreateException("Could not create new position : "+positionName);
			}
			
		}
		catch (SQLException e) {
			error("Error creating Location "+name,e);
		}
		finally {
			closeConnection(con, stmt, resultSet);
		}
		this.positionName = positionName;
		return positionName;
	}
	
	public void ejbPostCreate(String positionName){}

	public Long findByPrimaryKey(Long pk) throws FinderException {
		
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
		// TODO Auto-generated method stub
	}

	@Override
	public void ejbStore() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void ejbRemove() {
		// TODO Auto-generated method stub
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
			error("Error looking up depended EJB or resource",ex);
			return;
		}
	}

	@Override
	public void unsetEntityContext() {
		this.entityContext = null;
		this.dataSource = null;
	}

}
