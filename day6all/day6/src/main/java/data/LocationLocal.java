package data;

import java.rmi.*;
import javax.ejb.*;

public interface LocationLocal extends EJBLocalObject
{
	String getName ();
	String getDescription ();
	void setDescription (String description);
}
