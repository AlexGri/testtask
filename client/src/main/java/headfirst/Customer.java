package headfirst;

import javax.ejb.*;
import java.rmi.RemoteException;

public interface Customer extends EJBObject { 

    public String getLastName() throws RemoteException;
    public void setLastName(String lastName) throws RemoteException;  

    public String getFirstName() throws RemoteException;
    public void setFirstName(String firstName) throws RemoteException;

    public String getAddress() throws RemoteException;
    public void setAddress(String address) throws RemoteException;
  
    public double getCreditLimit() throws RemoteException;
    public void setCreditLimit(double amt) throws RemoteException;

}