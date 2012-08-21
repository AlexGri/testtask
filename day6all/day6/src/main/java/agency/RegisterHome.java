package agency;

import java.rmi.*;
import javax.ejb.*;

public interface RegisterHome extends EJBHome
{
	Register create (String login) throws RemoteException, CreateException;
}
