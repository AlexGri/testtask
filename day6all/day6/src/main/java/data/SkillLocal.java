package data;

import java.rmi.*;
import javax.ejb.*;

public interface SkillLocal extends EJBLocalObject
{
	String getName ();
	String getDescription ();
	void setDescription (String description);
}
