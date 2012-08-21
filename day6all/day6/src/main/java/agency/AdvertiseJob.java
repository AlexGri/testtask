package agency;

import java.rmi.*;
import javax.ejb.*;

public interface AdvertiseJob extends EJBObject
{
	void updateDetails (String description, String location, String[] skills) throws RemoteException;
	String getRef() throws RemoteException;
	String getCustomer() throws RemoteException;
	String getDescription() throws RemoteException;
	String getLocation() throws RemoteException;
	String[] getSkills() throws RemoteException;
}
