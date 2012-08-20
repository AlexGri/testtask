package agency;

import java.rmi.*;
import javax.ejb.*;

public interface Advertise extends EJBObject
{
	void updateDetails (String name, String email, String[] Address) throws RemoteException;
	String getName() throws RemoteException;
	String getEmail() throws RemoteException;
	String[] getAddress() throws RemoteException;
	String[] getJobs() throws RemoteException;

	void createJob (String ref) throws RemoteException, DuplicateException, CreateException;
	void deleteJob (String ref) throws RemoteException, NotFoundException;
}
