package org.nc.core.entity;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public abstract class PositionBean implements EntityBean {
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private EntityContext entityContext;

	public abstract Long getId();
	public abstract void setId(Long id);
	
	public abstract String getPositionName();
	public abstract void setPositionName(String positionName);
	
	public Long ejbCreate(String positionName) throws CreateException {
		this.setPositionName(positionName);
		return null;
	}
	
	public void ejbPostCreate(String positionName){}
	
	@Override
	public void ejbActivate() {
	}
	
	@Override
	public void ejbPassivate() {
		this.setPositionName(null);
	}

	@Override
	public void ejbLoad() {
	}

	@Override
	public void ejbStore() {
	}
	
	@Override
	public void ejbRemove() {
	}

	@Override
	public void setEntityContext(EntityContext entityContext) {
		this.entityContext = entityContext;
	}

	@Override
	public void unsetEntityContext() {
		this.entityContext = null;
	}

	public String stringValue() {
		return "PositionBean [positionName=" + getPositionName() + "]";
	}
}
