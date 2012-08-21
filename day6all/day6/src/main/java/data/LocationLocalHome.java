package data;

import java.rmi.*;
import java.util.*;
import javax.ejb.*;

public interface LocationLocalHome extends EJBLocalHome
{
	LocationLocal create (String name, String description) throws CreateException;
	LocationLocal findByPrimaryKey(String name) throws FinderException;
	Collection findAll() throws FinderException;
}
