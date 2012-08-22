package org.nc.core.entity;

import javax.ejb.EJBLocalObject;

public interface Position extends EJBLocalObject {
	public String getPositionName();

	public void setPositionName(String positionName);
	
	@Override
	public String toString();
}
