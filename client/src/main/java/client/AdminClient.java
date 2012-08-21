package client;

import agency.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;
import javax.rmi.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

public class AdminClient extends JPanel implements ActionListener, ListSelectionListener
{
	public static void main(String[] args) {
		if (args.length!=0 && args.length!=1) {
			System.err.println("Usage: AdminClient AgencyJNDI");
			System.exit(1);
		}
		final JFrame window = new JFrame("Agency Table Browser");
		try {
			final AdminClient client = new AdminClient(args.length==1?args[0]:null);
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
	
	private JList applicants;
	private JList customers;
	private DefaultListModel skills = new DefaultListModel();
	private JList skillList = new JList(skills);
	private DefaultListModel locations = new DefaultListModel();
	private JList locationList = new JList(locations);

	private FixedField descLocation = new FixedField(16);
	private FixedField locationDescription = new FixedField(16);
	private FixedField newLocation = new FixedField(16);
	private FixedField newLocationDescription = new FixedField(16);
	private JButton updateLocationButton = new JButton("Update");
	private JButton addLocationButton = new JButton("Add");
	private JButton removeLocationButton = new JButton("Remove");

	private FixedField descSkill = new FixedField(16);
	private FixedField skillDescription = new FixedField(16);
	private FixedField newSkill = new FixedField(16);
	private FixedField newSkillDescription = new FixedField(16);
	private JButton updateSkillButton = new JButton("Update");
	private JButton addSkillButton = new JButton("Add");
	private JButton removeSkillButton = new JButton("Remove");

	public AdminClient(String agencyJNDI) {
		if (agencyJNDI != null)
			this.agencyJNDI = agencyJNDI;
		try
		{
			setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			agency = getAgency(this.agencyJNDI);
			loadTables(agency);
			add(topPanel());
			add(Box.createVerticalStrut(5));
			add(midPanel());
			add(Box.createVerticalStrut(5));
			add(bottomPanel());

			locationList.addListSelectionListener(this);
			skillList.addListSelectionListener(this);
			
			updateLocationButton.addActionListener(this);
			addLocationButton.addActionListener(this);
			removeLocationButton.addActionListener(this);
			updateSkillButton.addActionListener(this);
			addSkillButton.addActionListener(this);
			removeSkillButton.addActionListener(this);

			descLocation.setEnabled(false);
			descSkill.setEnabled(false);
			updateLocationButton.setEnabled(false);
			updateSkillButton.setEnabled(false);
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

	private JPanel boxPanel(String name) {
		JPanel p = new JPanel();
		Border border = BorderFactory.createEtchedBorder();
		border = BorderFactory.createTitledBorder(border,name);
		p.setLayout (new BoxLayout(p,BoxLayout.Y_AXIS));
		p.setBorder(border);
		return p;
	}

	private JPanel panelRow(JComponent l, JComponent r) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add (l);
		if (r != null)
			p.add (r);
		return p;
	}

	private JPanel topPanel() {
		JPanel c = boxPanel("Customers");
		customers.setVisibleRowCount(4);
		c.add (new JScrollPane(customers));

		JPanel a = boxPanel("Applicants");
		applicants.setVisibleRowCount(4);
		a.add (new JScrollPane(applicants));

		JPanel p = new JPanel(new GridLayout(1,2));
		p.add (c);
		p.add (a);
		return p;
	}

	private JPanel midPanel() {
		locationList.setVisibleRowCount(4);
		JPanel l = boxPanel("Locations");
		l.add (new JScrollPane(locationList));
		l.add (removeLocationButton);
		
		JPanel m = boxPanel("Select Location");
		m.add (descLocation);
		m.add (new FixedLabel("Description:"));
		m.add (locationDescription);
		m.add (updateLocationButton);

		JPanel r = boxPanel("New location");
		m.add (new FixedLabel("Name:"));
		r.add (newLocation);
		r.add (new FixedLabel("Description:"));
		r.add (newLocationDescription);
		r.add (addLocationButton);

		JPanel all = new JPanel(new GridLayout(1,3));
		all.add(l);
		all.add(m);
		all.add(r);
		return all;
	}

	private JPanel bottomPanel() {
		skillList.setVisibleRowCount(4);		
		JPanel l = boxPanel("Skills");
		l.add (new JScrollPane(skillList));
		l.add (removeSkillButton);
		
		JPanel m = boxPanel("Select Skill");
		m.add (descSkill);
		m.add (new FixedLabel("Description:"));
		m.add (skillDescription);
		m.add (updateSkillButton);

		JPanel r = boxPanel("New skill");
		r.add (new FixedLabel("Name:"));
		r.add (newSkill);
		r.add (new FixedLabel("Description:"));
		r.add (newSkillDescription);
		r.add (addSkillButton);

		JPanel all = new JPanel(new GridLayout(1,3));
		all.add(l);
		all.add(m);
		all.add(r);
		return all;
	}
   
	public void actionPerformed(ActionEvent ev) {
		try {
			Object o = ev.getSource();
			if (o==updateLocationButton) {
				doUpdateLocation(agency);
			}
			else if (o==addLocationButton) {
				doAddLocation(agency);
			}
			else if (o==removeLocationButton) {
				doRemoveLocation(agency);
			}
			else if (o==updateSkillButton) {
				doUpdateSkill(agency);
			}
			else if (o==addSkillButton) {
				doAddSkill(agency);
			}
			else if (o==removeSkillButton) {
				doRemoveSkill(agency);
			}
			else
				warning("Unknown event","Unknown event: "+ev);
			validate();
		}
		catch (NotFoundException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (DuplicateException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (RemoteException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
	}

	public void valueChanged(ListSelectionEvent ev) {
		if (!ev.getValueIsAdjusting() || ev.getFirstIndex()==-1)
			return;
		try {			
			Object o = ev.getSource();
			if (o==locationList) {
				doLocationDescription(agency);			
			}
			else if (o==skillList) {
				doSkillDescription(agency); 		
			}
			else
				warning("Unknown list event","Unknown event: "+ev);
		}
		catch (NotFoundException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (RemoteException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
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

	private boolean isNull (String s) {
		return s==null || s.length()==0;
	}
	
	private boolean isNull (JTextComponent c) {
		return isNull(c.getText());
	}

	private static class FixedField extends JTextField
	{
		public FixedField (int n) { super(n); }
		public Dimension getMinimumSize() { return getPreferredSize(); }
		public Dimension getMaximumSize() { return getPreferredSize(); }
		public float getAlignmentX() { return 0;}
		public float getAlignmentY() { return 0;}
	}

	private static final JLabel sample = new JLabel("abcdefghijklmn");
	
	private static class FixedLabel extends JLabel
	{
		private Dimension size;
		public FixedLabel (String s) {
			super(s);
			size = super.getPreferredSize();
			size.width = sample.getPreferredSize().width;
		}
		public Dimension getPreferredSize() { return size; }
		public Dimension getMinimumSize() { return size; }
		public Dimension getMaximumSize() { return size; }
		public float getAlignmentX() { return 0;}
		public float getAlignmentY() { return 0;}
	}

	// EJB stuff from here
			
	private Agency getAgency(String agencyJNDI)
		throws NamingException, RemoteException, CreateException, ClassCastException {
		InitialContext ic = new InitialContext();
		Object lookup = ic.lookup(agencyJNDI);
		AgencyHome home = (AgencyHome)PortableRemoteObject.narrow(lookup, AgencyHome.class);
		return home.create();
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

	private void loadTables(Agency agency) throws RemoteException {
		customers = new JList(agency.findAllCustomers().toArray());
		applicants = new JList(agency.findAllApplicants().toArray());
		locations.clear();
		Iterator it = agency.getLocations().iterator();
		while (it.hasNext())
			locations.addElement(it.next());
		skills.clear();
		it = agency.getSkills().iterator();
		while (it.hasNext())
			skills.addElement(it.next());

	}

	private void doLocationDescription(Agency agency) throws RemoteException, NotFoundException {
		Object[] location = (locationList.getSelectedValues());
		if (location.length != 1)
		{
			descLocation.setText("");
			locationDescription.setText("");
			locationDescription.setEnabled(false);
		}
		else
		{
			descLocation.setText((String)location[0]);
			locationDescription.setText(agency.getLocationDescription((String)location[0]));
			locationDescription.setEnabled(true);
		}
		updateLocationButton.setEnabled(location.length==1);
	}

	private void doUpdateLocation(Agency agency) throws RemoteException, NotFoundException {
		Object[] location = (locationList.getSelectedValues());
		if (location.length != 1)
			warning ("No Location","You must select a single Location to update the description");
		agency.updateLocation((String)location[0],locationDescription.getText());
	}

	private void doAddLocation(Agency agency) throws RemoteException, DuplicateException {
		if (isNull(newLocation))
			warning ("No Location","You must enter a name for the new Location");
		String s = newLocation.getText();
		
		if (!locations.contains(s)) {
			agency.addLocation(s,newLocationDescription.getText());
			locations.addElement(s);
			locationList.invalidate();
			newLocation.setText("");
			newLocationDescription.setText("");
		}
		else
			warning ("Duplicate Location",s+" already defined");
	}

	private void doRemoveLocation(Agency agency) throws RemoteException, NotFoundException {
		Object[] location = (locationList.getSelectedValues());
		if (location.length == 0)
			warning ("No Location","You must select a Location to remove from the Locations list");
		for (int i=0; i<location.length; i++) {
			if (locations.contains(location[i])) {
				agency.removeLocation((String)location[i]);
				locations.removeElement(location[i]);
			}
		}
		descLocation.setText("");
		locationDescription.setText("");
		locationList.invalidate();
	}
	
	private void doSkillDescription(Agency agency) throws RemoteException, NotFoundException {
		Object[] skill = (skillList.getSelectedValues());
		if (skill.length != 1)
		{
			descSkill.setText("");
			skillDescription.setText("");
			skillDescription.setEnabled(false);
		}
		else
		{
			descSkill.setText((String)skill[0]);
			skillDescription.setText(agency.getSkillDescription((String)skill[0]));
			skillDescription.setEnabled(true);
		}
		updateSkillButton.setEnabled(skill.length==1);
	}

	private void doUpdateSkill(Agency agency) throws RemoteException, NotFoundException {
		Object[] skill = (skillList.getSelectedValues());
		if (skill.length != 1)
			warning ("No Location","You must select a single Location to update the description");
		agency.updateSkill((String)skill[0],skillDescription.getText());
	}

	private void doAddSkill(Agency agency) throws RemoteException, DuplicateException {
		if (isNull(newSkill))
			warning ("No skill","You must enter a name for the new skill");
		String s = newSkill.getText();
		
		if (!skills.contains(s)) {
			agency.addSkill(s,newSkillDescription.getText());
			skills.addElement(s);
			skillList.invalidate();
			newSkill.setText("");
			newSkillDescription.setText("");
		}
		else
			warning ("Duplicate skill",s+" already defined");
	}

	private void doRemoveSkill(Agency agency) throws RemoteException, NotFoundException {
		Object[] skill = (skillList.getSelectedValues());
		if (skill.length == 0)
			warning ("No skill","You must select a skill to remove from the skills list");
		for (int i=0; i<skill.length; i++) {
			if (skills.contains(skill[i])) {
				agency.removeSkill((String)skill[i]);
				skills.removeElement(skill[i]);
			}
		}
		descSkill.setText("");
		skillDescription.setText("");
		skillList.invalidate();
	}

}


