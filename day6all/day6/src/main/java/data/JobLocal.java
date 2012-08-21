package data;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;

public interface JobLocal extends EJBLocalObject
{
	String getRef();
	String getCustomer();
    CustomerLocal getCustomerObj(); // derived

	void setDescription(String description);
	String getDescription();

	void setLocation(LocationLocal location);
	LocationLocal getLocation();

	Collection getSkills();
	void setSkills(Collection skills);
}
