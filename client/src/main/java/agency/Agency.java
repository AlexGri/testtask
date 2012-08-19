package agency;

import java.rmi.*;
import java.util.*;
import javax.ejb.*;

public interface Agency extends EJBObject
{
	String getAgencyName() throws RemoteException;
	
	Collection findAllApplicants() throws RemoteException;
	void createApplicant(String login, String name, String email) throws RemoteException, DuplicateException, CreateException;
	void deleteApplicant (String login) throws RemoteException, NotFoundException;

	Collection findAllCustomers() throws RemoteException;
	void createCustomer(String login, String name, String email) throws RemoteException, DuplicateException, CreateException;
	void deleteCustomer (String login) throws RemoteException, NotFoundException;

	Collection getLocations() throws RemoteException;
	void addLocation(String name) throws RemoteException, DuplicateException;
	void removeLocation(String code) throws RemoteException, NotFoundException;
	
	Collection getSkills() throws RemoteException;
	void addSkill(String name) throws RemoteException, DuplicateException;
	void removeSkill(String name) throws RemoteException, NotFoundException;

	List select(String table) throws RemoteException;

}
