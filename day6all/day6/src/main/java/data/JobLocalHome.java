package data;

import java.rmi.*;
import java.util.*;
import javax.ejb.*;

public interface JobLocalHome extends EJBLocalHome
{
	JobLocal create (String ref, String customer) throws CreateException;
	JobLocal findByPrimaryKey(JobPK key) throws FinderException;
	Collection findByCustomer(String customer) throws FinderException;
	Collection findByLocation(String location) throws FinderException;
	void deleteByCustomer(String customer);
}
