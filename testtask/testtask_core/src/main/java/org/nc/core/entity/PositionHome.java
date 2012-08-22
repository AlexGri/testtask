package org.nc.core.entity;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface PositionHome extends EJBLocalHome {

	Position create(String positionName) throws CreateException;

	Position findByPrimaryKey(Long id) throws FinderException;
	
	Position findByName(String name) throws FinderException;
	
	Collection<Position> findAll() throws FinderException;
}
