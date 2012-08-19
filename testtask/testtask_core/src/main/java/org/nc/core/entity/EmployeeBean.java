package org.nc.core.entity;

import java.rmi.RemoteException;
import javax.ejb.CreateException;

import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

public abstract class EmployeeBean implements EntityBean {
	private static final long serialVersionUID = 1L;
        
        private EntityContext entityContext;

        public abstract Long getId();

        public abstract String getFirstname();

        public abstract String getLastname();

        public abstract String getMiddlename();

        public abstract String getPhones();

        public abstract Double getSalary();

        public abstract void setId(Long id);

        public abstract void setFirstname(String firstname);

        public abstract void setLastname(String lastname);

        public abstract void setMiddlename(String middlename);

        public abstract void setPhones(String phones);

        public abstract void setSalary(Double salary);
        
        public String ejbCreate(Long id, String firstname, String lastname,
            String middlename, String phones, Double salary)
            throws CreateException {
            this.setId(id);
            this.setFirstname(firstname);
            this.setLastname(lastname);
            this.setMiddlename(middlename);
            this.setPhones(phones);
            this.setSalary(salary);
            return null;
        }
        
	@Override
	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbLoad() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbRemove() throws RemoveException, EJBException,
			RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbStore() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEntityContext(EntityContext arg0) throws EJBException,
			RemoteException {
            this.entityContext = arg0;
	}

	@Override
	public void unsetEntityContext() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

}
