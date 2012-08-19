package headfirst;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

public interface Advice extends EJBObject {
	public String getMessage() throws RemoteException;
}
