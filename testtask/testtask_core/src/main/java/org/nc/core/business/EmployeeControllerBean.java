package org.nc.core.business;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;

public class EmployeeControllerBean implements SessionBean {
	private static final long serialVersionUID = 1L;
	
	private DataSource dataSource;
	
	public Collection<Object> getEmployeeList() {
		
	}
	
	public Collection<Object> getPositionList() {
		
	}
	
	public Collection<Object> findAllOccurences(String value) {
		
	}

	@Override
	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSessionContext(SessionContext arg0) throws EJBException,
			RemoteException {
		// TODO Auto-generated method stub
		
	}

}
