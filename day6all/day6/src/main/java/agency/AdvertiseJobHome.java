package agency;

import java.rmi.*;
import javax.ejb.*;

public interface AdvertiseJobHome extends EJBHome
{
	AdvertiseJob create (String ref, String customer) throws RemoteException, CreateException;
}
