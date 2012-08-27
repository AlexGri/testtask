package org.nc.core.redistributable.javabean;

import java.io.Serializable;

public class PositionPojo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id = null;
	private String positionName = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	
	public PositionPojo() {
		super();
	}

	public PositionPojo(Long id, String positionName) {
		this();
		this.id = id;
		this.positionName = positionName;
	}
}
