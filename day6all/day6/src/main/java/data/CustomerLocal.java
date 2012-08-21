package data;

import java.rmi.*;
import javax.ejb.*;

public interface CustomerLocal extends EJBLocalObject
{
	String getLogin();
	void setName(String name);
	String getName();
	void setEmail(String email);
	String getEmail();
	void setAddress(String[] address);
	String[] getAddress();
}
