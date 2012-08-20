package agency;

import java.rmi.*;
import javax.ejb.*;

public interface AgencyHome extends EJBHome
{
	Agency create () throws RemoteException, CreateException;
}
