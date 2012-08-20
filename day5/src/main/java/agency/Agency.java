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
	String getLocationDescription(String name) throws RemoteException, NotFoundException;
	void addLocation(String name, String description) throws RemoteException, DuplicateException;
	void updateLocation(String name, String description) throws RemoteException, NotFoundException;
	void removeLocation(String code) throws RemoteException, NotFoundException;
	
	Collection getSkills() throws RemoteException;
	String getSkillDescription(String name) throws RemoteException, NotFoundException;
	void addSkill(String name, String description) throws RemoteException, DuplicateException;
	void updateSkill(String name, String description) throws RemoteException, NotFoundException;
	void removeSkill(String name) throws RemoteException, NotFoundException;

	List select(String table) throws RemoteException;

}
