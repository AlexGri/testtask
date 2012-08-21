package data;

import java.rmi.*;
import java.util.*;
import javax.ejb.*;

public interface CustomerLocalHome extends EJBLocalHome
{
	CustomerLocal create (String login, String name, String email) throws CreateException;
	CustomerLocal findByPrimaryKey(String login) throws FinderException;
	Collection findAll() throws FinderException;
}
