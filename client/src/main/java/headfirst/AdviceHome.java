package headfirst;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface AdviceHome extends EJBHome {
	public Advice create() throws CreateException, RemoteException;
}
