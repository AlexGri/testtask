package org.nc.core.entity;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public abstract class EmployeeBean implements EntityBean {
	private static final long serialVersionUID = 1L;
        
    @SuppressWarnings("unused")
	private EntityContext entityContext;
    
    public abstract String getFirstname();

	public abstract void setFirstname(String firstname);

	public abstract String getLastname();

	public abstract void setLastname(String lastname);

	public abstract String getMiddlename();

	public abstract void setMiddlename(String middlename);

	public abstract String getPhones();

	public abstract void setPhones(String phones);

	public abstract Double getSalary();

	public abstract void setSalary(Double salary);

	public abstract Position getPosition();

	public abstract void setPosition(Position position);

	public abstract Long getId();
	public abstract void setId(Long id);
	
	public Long ejbCreate(String firstname, String lastname,
        String middlename, String phones, Double salary, Position position)
        throws CreateException {
		
        this.setFirstname(firstname);
        this.setLastname(lastname);
        this.setMiddlename(middlename);
        this.setPhones(phones);
        this.setSalary(salary);
        this.setPosition(position);
        return null;
    } 
	
	public void ejbPostCreate(String firstname, String lastname,
	        String middlename, String phones, Double salary, Position position) {}
	
	public Long ejbCreate(String firstname, String lastname) throws CreateException {
	       return ejbCreate(firstname, lastname, null, null, null, null);
	}
	
	public void ejbPostCreate(String firstname, String lastname) {}
        
	@Override
	public void ejbActivate() {}
	
	@Override
	public void ejbPassivate() {
		this.setFirstname(null);
		this.setLastname(null);
		this.setMiddlename(null);
		this.setPhones(null);
		this.setSalary(null);
		this.setPosition(null);
	}

	@Override
	public void ejbLoad() {
	}

	@Override
	public void ejbRemove() {
	}

	@Override
	public void ejbStore() {
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
		return "EmployeeBean [firstname=" + getFirstname() + ", lastname="
				+ getLastname() + ", middlename=" + getMiddlename() + ", phones="
				+ getPhones() + ", salary=" + getSalary() + ", position=" + getPosition().stringValue()
				+ "]";
	}
}
