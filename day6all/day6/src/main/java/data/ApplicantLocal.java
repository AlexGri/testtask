package data;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;

public interface ApplicantLocal extends EJBLocalObject
{
	String getLogin();
	void setName(String name);
	String getName();
	void setEmail(String email);
	String getEmail();
	void setSummary(String summary);
	String getSummary();
	void setLocation(LocationLocal location);
	LocationLocal getLocation();
	Collection getSkills();
	void setSkills(Collection skills);
}
