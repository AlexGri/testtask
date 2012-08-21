package data;

import java.rmi.*;
import java.util.*;
import javax.ejb.*;

public interface ApplicantLocalHome extends EJBLocalHome
{
	ApplicantLocal create (String login, String name, String email) throws CreateException;
	ApplicantLocal findByPrimaryKey(String login) throws FinderException;
	Collection findByLocation(String location) throws FinderException;
	Collection findAll() throws FinderException;
}
