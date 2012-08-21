package org.nc.core.entity;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface PositionHome extends EJBLocalHome {

	Position create(String positionName) throws CreateException;

	Position findByPrimaryKey(Long pk) throws FinderException;
}
