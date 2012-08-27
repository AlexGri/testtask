package org.nc.core.redistributable.javabean;

import java.io.Serializable;

public class EmployeePojo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id = null;
	private String firstname = null;
	private String lastname = null;
	private String middlename = null;
	private String phones = null;
	private Double salary = null;
	private PositionPojo position = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public PositionPojo getPosition() {
		return position;
	}

	public void setPosition(PositionPojo position) {
		this.position = position;
	}

	public EmployeePojo() {
		super();
	}

	public EmployeePojo(Long id, String firstname, String lastname,
			String middlename, String phones, Double salary,
			PositionPojo position) {
		this();
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.middlename = middlename;
		this.phones = phones;
		this.salary = salary;
		this.position = position;
	}
	
	
}
