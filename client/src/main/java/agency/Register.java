package agency;

import java.rmi.*;
import javax.ejb.*;

public interface Register extends EJBObject
{
	void updateDetails (String name, String email, String location, String summary, String[] skills) throws RemoteException;
	String getLogin() throws RemoteException;
	String getName() throws RemoteException;
	String getEmail() throws RemoteException;
	String getLocation() throws RemoteException;
	String getSummary() throws RemoteException;
	String[] getSkills() throws RemoteException;
}
