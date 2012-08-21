package client;

import agency.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;
import javax.rmi.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class AllClients extends JPanel
{
    public static void main(String[] args) {
    	if (args.length!=0 && args.length!=4) {
    		System.err.println("Usage: AllClients AgencyJNDI RegisterJNDI AdvertiseJNDI AdvertiseJobJNDI");
    		System.exit(1);
    	}
    	final JFrame window = new JFrame("Agency Clients");
    	try {
	        final AllClients client = new AllClients(args);
	    	window.addWindowListener(new WindowAdapter() {
	    		public void windowClosing( WindowEvent ev) {
	    			client.shutdown();
	    			window.dispose();
					System.exit(0);
	    		}
	    	});
	    	String title = client.getAgencyName();
	    	window.setTitle(title);
	    	Container cp = window.getContentPane();
	    	cp.add(heading(title),BorderLayout.NORTH);
	    	cp.add(client,BorderLayout.CENTER);
	   		window.pack();
	    	window.setVisible(true);
    	}
    	catch (Exception ex) {
 	    	System.err.println(ex);
	    	window.dispose();
	    	System.exit(1);
    	}
    }

    private static JLabel heading(String s) {
    	JLabel label = new JLabel(s,SwingConstants.CENTER);
    	Font old = label.getFont();
    	Font f = new Font(old.getName(),old.getStyle()|Font.BOLD|Font.ITALIC,(int)(old.getSize()*1.20));
    	label.setFont(f);
    	return label;
    }

	private RegisterClient register;
	private AdvertiseClient advertise;
	private AdminClient admin;
	private TableClient table;

    public AllClients(String[] args) {
    	String[] jndi = args;
    	if (jndi==null || jndi.length<4)
    		jndi = new String[4];
    	try {
    		setLayout(new GridLayout(1,1));
	    	JTabbedPane tp = new JTabbedPane();
	    	tp.add ("Register", register = new RegisterClient(jndi[0],jndi[1]));
	    	tp.add ("Advertise", advertise = new AdvertiseClient(jndi[0],jndi[2],jndi[3]));
	    	tp.add ("Admin", admin = new AdminClient(jndi[0]));
	    	tp.add ("Tables", table = new TableClient(jndi[0]));
	    	add(tp);
    	}
    	catch (RuntimeException ex) {
    		shutdown();
    		throw ex;
    	}
    }

    public void shutdown() {
    	if (register != null)
    		register.shutdown();
    	if (advertise != null)
    		advertise.shutdown();
    	if (admin != null)
    		admin.shutdown();
    	if (table != null)
    		table.shutdown();
    }

    public String getAgencyName() {
    	return admin.getAgencyName();
    }


}


