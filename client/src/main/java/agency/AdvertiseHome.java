package agency;

import java.rmi.*;
import javax.ejb.*;

public interface AdvertiseHome extends EJBHome
{
	Advertise create (String login) throws RemoteException, CreateException;
}
