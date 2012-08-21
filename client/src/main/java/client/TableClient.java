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

public class TableClient extends JPanel implements ActionListener
{
	public static void main(String[] args) {
		if (args.length!=0 && args.length!=1) {
			System.err.println("Usage: TableClient [ AgencyJNDI ]");
			System.exit(1);
		}
		final JFrame window = new JFrame("Agency Table Browser");
		try {
			final TableClient client = new TableClient(args.length==1?args[0]:null);
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

	private String agencyJNDI = "java:comp/env/ejb/Agency";
	
	private Agency agency;
	
	private String[] tableNames = {
		"Applicant", "ApplicantSkill",
		"Customer", "Job", "JobSkill",
		"Location", "Matched", "Skill"
	};
	private JComboBox tables = new JComboBox(tableNames);
	private JButton selectButton = new JButton("Select");

	private AgencyTableModel model = new AgencyTableModel();
	JTable main = new JTable(model);
	
	public TableClient(String agencyJNDI) {
		if (agencyJNDI != null)
			this.agencyJNDI = agencyJNDI;
		try
		{
			setLayout(new BorderLayout());
			agency = getAgency(this.agencyJNDI);
			Box b = Box.createHorizontalBox();
			b.add (new JLabel("Table"));
			b.add (tables);
			b.add (selectButton);
			add (b, BorderLayout.NORTH);
			add (new JScrollPane(main), BorderLayout.CENTER);
			selectButton.addActionListener(this);
			tables.addActionListener(this);
		}
		catch (NamingException ex) {
			abort("getAgency",ex.toString());
		}
		catch (CreateException ex) {
			abort("getAgency",ex.toString());
		}
		catch (RemoteException ex) {
			abort("getAgency",ex.toString());
		}
		catch (ClassCastException ex) {
			abort("getAgency",ex.toString());
		}
	}

	public void actionPerformed(ActionEvent ev) {
		try {
			Object o = ev.getSource();
			if (o==selectButton || o==tables) {
				model.doSelect(agency,(String)tables.getSelectedItem());
			}
			else
				warning("Unknown event","Unknown event: "+ev);
		}
		catch (RemoteException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
	}

	private void abort (String title, String message) {
		error (title, message);
		shutdown ();
		throw new RuntimeException(message);
	}
	
	private void error (String title, String message) {
		JOptionPane.showMessageDialog(this,message,title,JOptionPane.ERROR_MESSAGE);
	}

	private void warning (String title, String message) {
		JOptionPane.showMessageDialog(this,message,title,JOptionPane.WARNING_MESSAGE);
	}

	private void info (String title, String message) {
		JOptionPane.showMessageDialog(this,message,title,JOptionPane.INFORMATION_MESSAGE);
	}

	// EJB stuff from here
		
	private Agency getAgency(String agencyJNDI)
		throws NamingException, RemoteException, CreateException, ClassCastException {
		InitialContext ic = new InitialContext();
		Object lookup = ic.lookup(agencyJNDI);
		AgencyHome home = (AgencyHome)PortableRemoteObject.narrow(lookup, AgencyHome.class);
		return home.create();
	}

	public void shutdown() {
		try {
			if (agency != null)
				agency.remove();
		}
		catch (RemoveException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (RemoteException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
	}

	public String getAgencyName() {
		try {
			return agency.getAgencyName();
		}
		catch (RemoteException ex) {
			error("getAgencyName",ex.toString());
			ex.printStackTrace();
		}
		return "Agency";		
	}

	private class AgencyTableModel extends AbstractTableModel {
		java.util.List query;	// list of String[], first row = column names

		public String getColumnName(int col) {
			if (query==null || query.size()==0)
				return "Error";
			return (((String[])query.get(0))[col]);
		}

		public int getColumnCount() {
			if (query==null || query.size()==0)
				return 0;
			return (((String[])query.get(0)).length);
		}

		public int getRowCount() {
			if (query==null || query.size()==0)
				return 0;
			return query.size()-1;
		}

		public Object getValueAt(int row, int col)
		{
			if (query==null || query.size()<=1)
				return "Error";
			return (((String[])query.get(row+1))[col]);
		}
		
		public void doSelect(Agency agency, String table) throws RemoteException {
			query = agency.select(table);
			fireTableChanged(null);
		}
	}

}


