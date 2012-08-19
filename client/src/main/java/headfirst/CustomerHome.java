package headfirst;

import javax.ejb.*;
import java.rmi.RemoteException;
import java.util.Collection;

public interface CustomerHome extends EJBHome {
   public Customer create(String last, String first, String addr, String ID) throws CreateException, RemoteException;
   public Customer findByPrimaryKey(String key) throws FinderException, RemoteException;
   //public Collection findByCreditLimit(double greaterThanAmt) throws FinderException, RemoteException;
}
