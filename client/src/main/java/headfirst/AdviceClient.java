package headfirst;
import javax.naming.*;
import java.rmi.*;
import java.util.Hashtable;

import javax.rmi.*;
import headfirst.*;
import javax.ejb.*;

// not all of these imports are used in this code... 
// but in a *real* client you'd probably need at least
// java.rmi.RemoteException and javax.ejb.CreateException

public class AdviceClient {

	public static void main(String[] args) {
		new AdviceClient().go();
	}

	public void go() {
		try {
			Hashtable env = new Hashtable();
			env.put("java.naming.factory.initial","org.jnp.interfaces.NamingContextFactory"); 
			//env.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces"); 
			env.put("java.naming.provider.url","localhost:1099");
			
			Context ic = new InitialContext(env	);
                        

			Object o = ic.lookup("AdviceBean");  // replace with YOUR JNDI name for the bean

			AdviceHome home = (AdviceHome) PortableRemoteObject.narrow(o, AdviceHome.class);

			Advice advisor = home.create();

			System.out.println(advisor.getMessage());
			System.out.println(advisor.getMessage());
			System.out.println(advisor.getMessage());

		} catch (Exception ex) {
				ex.printStackTrace();
		}
	}
}