package data;

import java.rmi.*;
import java.util.*;
import javax.ejb.*;

public interface SkillLocalHome extends EJBLocalHome
{
	SkillLocal create (String name, String Description) throws CreateException;
	SkillLocal findByPrimaryKey(String name) throws FinderException;
    List lookup(List names) throws FinderException;
	Collection findAll() throws FinderException;
}
