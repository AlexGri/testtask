package org.nc.core.entity;

import javax.ejb.EJBLocalObject;

public interface Employee extends EJBLocalObject {
	public String getFirstname();

	public void setFirstname(String firstname);

	public String getLastname();

	public void setLastname(String lastname);

	public String getMiddlename();

	public void setMiddlename(String middlename);

	public String getPhones();

	public void setPhones(String phones);

	public Double getSalary();

	public void setSalary(Double salary);
	
	public Position getPosition();

	public void setPosition(Position position);

	public Long getId();
	
	public String toString();
}
