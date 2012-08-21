package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.* ;
import javax.sql.*;

import data.*;

public class AdvertiseBean implements SessionBean
{
	private JobLocalHome jobHome;
	private CustomerLocalHome customerHome;
	private CustomerLocal customer;

	public void updateDetails (String name, String email, String[] address) {
		customer.setName(name);
		customer.setEmail(email);
		customer.setAddress(address);
	}
	
	public String getLogin() {
		return customer.getLogin();
	}

	public String getName() {
		return customer.getName();
	}

	public String getEmail() {
		return customer.getEmail();
	}

	public String[] getAddress() {
		return customer.getAddress();
	}

	public String[] getJobs() {
		try {
			Collection col = jobHome.findByCustomer(customer.getLogin());
			String[] res = new String[col.size()];
			Iterator it = col.iterator();
			int i = 0;
			while (it.hasNext()) {
				JobLocal job = (JobLocal)it.next();
				res[i++] = job.getRef();
			}
			return (res);
		}
		catch (FinderException ex) {
			error("Cannot find jobs for customer: "+customer.getLogin(),ex);
		}
		return null;
	}

	public void createJob (String ref) throws DuplicateException, CreateException {
		try {
			jobHome.create(ref,customer.getLogin());
		}
		catch (CreateException ex) {
			error("Cannot find job: "+ref,ex);
		}
	}

	public void deleteJob (String ref) throws NotFoundException {
		try {
			jobHome.remove(new JobPK(ref,customer.getLogin()));
		}
		catch (RemoveException ex) {
			error("Cannot remove job: "+ref,ex);
		}
	}

	private void error (String msg, Exception ex) {
		String s = "AdvertiseBean: "+msg + "\n" + ex;
		System.out.println(s);
		throw new EJBException(s, ex);
	}

	// EJB methods start here

	public void ejbCreate (String login) throws CreateException {
		try {
			customer = customerHome.findByPrimaryKey(login);
		}
		catch (FinderException ex) {
			error("Cannot find customer: "+login,ex);
		}
	}

	public void ejbActivate(){
	}

	public void ejbPassivate(){
	}

	public void ejbRemove(){
	}

	private SessionContext ctx;
	
	public void setSessionContext(SessionContext ctx) {
		this.ctx = ctx;
		InitialContext ic=null;
		try {
			ic = new InitialContext();
			customerHome = (CustomerLocalHome)ic.lookup("java:comp/env/ejb/CustomerLocal");
			jobHome = (JobLocalHome)ic.lookup("java:comp/env/ejb/JobLocal");
		}
		catch (NamingException ex) {
			error("Error looking up depended EJB or resource",ex);
		}
	}
}

