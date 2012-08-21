package client;

import agency.*;
import javax.ejb.*;
import javax.naming.*;
import java.rmi.*;
import javax.rmi.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class AdvertiseClient extends JPanel implements ActionListener
{
	public static void main(String[] args) {
		if (args.length!=0 && args.length!=3) {
			System.err.println("Usage: AdvertiseClient AgencyJNDI AdvertiseJNDI AdvertiseJobJNDI");
			System.exit(1);
		}
		final JFrame window = new JFrame("Agency Advertise Job");
		try {
			final AdvertiseClient client = new AdvertiseClient(args.length==3?args[0]:null, args.length==3?args[1]:null, args.length==3?args[2]:null);
			window.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent ev) {
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
	private String advertiseJNDI = "java:comp/env/ejb/Advertise";
	private String advertiseJobJNDI = "java:comp/env/ejb/AdvertiseJob";
	
	private Agency agency;
	private Advertise advertise;
	private AdvertiseJob advertiseJob;

	public AdvertiseClient(String agencyJNDI, String advertiseJNDI, String advertiseJobJNDI) {
		if (agencyJNDI != null)
			this.agencyJNDI = agencyJNDI;
		if (advertiseJNDI != null)
			this.advertiseJNDI = advertiseJNDI;
		if (advertiseJobJNDI != null)
			this.advertiseJobJNDI = advertiseJobJNDI;
		try
		{
			setLayout(new BorderLayout());
			agency = getAgency(this.agencyJNDI);
			loadTables(agency);
			Box b = Box.createHorizontalBox();
			b.add (nwPanel());
			b.add (nePanel());
			add (b, BorderLayout.NORTH);
			add (mainPanel(), BorderLayout.CENTER);
			add (jobPanel(), BorderLayout.SOUTH);
			loginButton.addActionListener(this);
			logoffButton.addActionListener(this);
			registerButton.addActionListener(this);
			updateButton.addActionListener(this);
			deleteButton.addActionListener(this);
			selectJobButton.addActionListener(this);
			allJobs.addActionListener(this);
			createJobButton.addActionListener(this);
			addSkillButton.addActionListener(this);
			removeSkillButton.addActionListener(this);
			updateJobButton.addActionListener(this);
			deleteJobButton.addActionListener(this);
			currentJob.setEnabled(false);
			enableForm(false);
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

	private FixedField login = new FixedField(16);

	private FixedField addLogin = new FixedField(16);
	private FixedField addName = new FixedField(16);
	private FixedField addEmail = new FixedField(16);

	private FixedField name = new FixedField(16);
	private FixedField email = new FixedField(16);
	private FixedField address1 = new FixedField(16);
	private FixedField address2 = new FixedField(16);
	private FixedField newJob = new FixedField(16);
	private DefaultComboBoxModel jobs = new DefaultComboBoxModel();
	private JComboBox allJobs = new JComboBox();
	private FixedField currentJob = new FixedField(16);
	private JTextArea description = new JTextArea(4,32);
	private JComboBox location;
	private JList allSkills;
	private DefaultListModel skills = new DefaultListModel();
	private JList actualSkills = new JList(skills);
	
	private JButton loginButton = new JButton("Login");
	private JButton logoffButton = new JButton("Logoff");
	private JButton updateButton = new JButton("Update");
	private JButton deleteButton = new JButton("Delete Customer");
	private JButton registerButton = new JButton("Register");
	
	private JButton selectJobButton = new JButton("Select Job");
	private JButton createJobButton = new JButton("Create Job");
	private JButton addSkillButton = new JButton("Add skill");
	private JButton removeSkillButton = new JButton("Remove skill");
	private JButton updateJobButton = new JButton("Update Job");
	private JButton deleteJobButton = new JButton("Delete Job");

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
	
	private JPanel nwPanel() {
		JPanel panel = boxPanel("Login");
		panel.add (panelRow(new FixedLabel("Login name:"),login));
		panel.add (Box.createVerticalGlue());
		panel.add(panelRow(loginButton,logoffButton));
		return panel;
	}

	private JPanel nePanel() {
		JPanel panel = boxPanel("Register");
		panel.add (panelRow(new FixedLabel("Login name:"),addLogin));
		panel.add (panelRow(new FixedLabel("Full name:"),addName));
		panel.add (panelRow(new FixedLabel("Email address:"),addEmail));
		panel.add (Box.createVerticalGlue());
		panel.add(panelRow(registerButton,null));
		return panel;
	}

	private JPanel mainPanel() {
		JPanel panel = boxPanel("Customer details");
		Box b = Box.createHorizontalBox();
		Box left = Box.createVerticalBox();
		left.add (panelRow(new FixedLabel("Full name:"),name));
		left.add (panelRow(new FixedLabel("Email address:"),email));
		left.add (panelRow(new FixedLabel("Address 1:"),address1));
		left.add (panelRow(new FixedLabel("Address 2:"),address2));
		left.add (Box.createVerticalGlue());
		left.add(panelRow(updateButton,deleteButton));
		
		Box right = Box.createVerticalBox();
		right.add (panelRow(new FixedLabel("Jobs:"),allJobs));
		right.add(panelRow(selectJobButton,null));
		right.add (Box.createVerticalGlue());
		right.add (panelRow(new FixedLabel("New Job Ref:"),newJob));
		right.add(panelRow(createJobButton,null));

		b.add (left);
		b.add (Box.createHorizontalGlue());
		b.add (right);
		panel.add(b);
		return panel;
	}

	private JPanel jobPanel() {
		actualSkills.setVisibleRowCount(4);

		JPanel panel = boxPanel("Job details");
		Box left = Box.createVerticalBox();
		left.add (panelRow(new FixedLabel("Reference:"),currentJob));
		left.add (panelRow(new FixedLabel("Location:"),location));
		left.add(panelRow(new FixedLabel("Skills:"),new JScrollPane(actualSkills)));
		left.add(panelRow(new FixedLabel(""),removeSkillButton));
		
		Box right = Box.createVerticalBox();
		right.add (panelRow(new FixedLabel("Description:"),new JScrollPane(description)));
		right.add(panelRow(new FixedLabel("Available skills:"),new JScrollPane(allSkills)));
		right.add(panelRow(new FixedLabel(""),addSkillButton));
		
		Box t = Box.createHorizontalBox();
		t.add (left);
		t.add (Box.createHorizontalGlue());
		t.add (right);
		panel.add(t);

		panel.add (Box.createVerticalGlue());
		panel.add(panelRow(updateJobButton,deleteJobButton));
	
		return panel;
	}

	public void actionPerformed(ActionEvent ev) {
		try {
			Object o = ev.getSource();
			if (o == loginButton)
				doLogin();
			else if (o == logoffButton)
				doLogoff();
			else if (o == updateButton)
				doUpdate();
			else if (o == registerButton)
				doRegister();
			else if (o == deleteButton)
				doDelete();
			else if (o == selectJobButton || o == allJobs)
				doJobSelect();
			else if (o == createJobButton)
				doJobCreate();
			else if (o == updateJobButton)
				doJobUpdate();
			else if (o == deleteJobButton)
				doJobDelete();
			else if (o == addSkillButton)
				doAddSkill();
			else if (o == removeSkillButton)
				doRemoveSkill();
			else
				warning("Unknown event","Unknown event: "+ev);
		}
		catch (NamingException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (CreateException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (NotFoundException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (DuplicateException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (RemoveException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (RemoteException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
		catch (ClassCastException ex) {
			error("actionPerformed",ex.toString());
			ex.printStackTrace();
		}
	}

	public void shutdown() {
		try {
			if (advertiseJob != null)
				advertiseJob.remove();
			if (advertise != null)
				advertise.remove();
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

	private void enableForm(boolean on)
	{
		loginButton.setEnabled(!on);
		logoffButton.setEnabled(on);
		updateButton.setEnabled(on);
		deleteButton.setEnabled(on);
		allJobs.setEnabled(on);
		selectJobButton.setEnabled(on);
		createJobButton.setEnabled(on);
		login.setEnabled(!on);
		enableJobForm(false);
	}

	private void enableJobForm(boolean on)
	{
		updateJobButton.setEnabled(on);
		addSkillButton.setEnabled(on);
		removeSkillButton.setEnabled(on);
		deleteJobButton.setEnabled(on);
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

	private boolean isNull (String s) {
		return s==null || s.length()==0;
	}
	
	private boolean isNull (JTextComponent c) {
		return isNull(c.getText());
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
		location = new JComboBox(agency.getLocations().toArray());
		allSkills = new JList(agency.getSkills().toArray());
		allSkills.setVisibleRowCount(4);
	}

	private void resetDetails() throws RemoveException, RemoteException {
		if (advertise != null)
		{
			advertise.remove();
			advertise = null;
		}
		enableForm (false);
		login.setText("");
		addLogin.setText("");
		addName.setText("");
		addEmail.setText("");
		name.setText("");
		email.setText("");
		address1.setText("");
		address2.setText("");
		jobs = new DefaultComboBoxModel();
		allJobs.setModel(jobs);
		resetJobDetails();
		login.setEnabled(true);
	}
	
	private void resetJobDetails() throws RemoveException, RemoteException {
		enableJobForm (false);
		currentJob.setText("");
		description.setText("");
		location.setSelectedIndex(0);
		skills.clear();
	}
	
	private AdvertiseHome advHome;

	private void doLogin()
		throws NamingException, RemoteException, RemoveException, CreateException, ClassCastException {
		String name = login.getText();
		if (isNull(name)) {
			error ("Login","No login name specified");
			return;
		}
		resetDetails();
		login.setText(name);
		if (advHome == null) {
			InitialContext ic = new InitialContext();
			Object lookup = ic.lookup(advertiseJNDI);
			advHome = (AdvertiseHome)PortableRemoteObject.narrow(lookup, AdvertiseHome.class);
		}
		
		advertise = advHome.create(name);
		loadDetails (advertise, agency);
		enableForm(true);
		if (!isNull(currentJob))
			enableJobForm(true);
	}

	private void doLogoff() throws RemoveException, RemoteException {
		resetDetails();
		enableForm(false);
	}

	private void loadDetails (Advertise advertise, Agency agency) throws RemoteException {
		name.setText(advertise.getName());
		email.setText(advertise.getEmail());
		String[] address = advertise.getAddress();
		address1.setText(address[0]);
		address2.setText(address[1]);
		loadJobs(advertise);
	}

	private void loadJobs (Advertise advertise) throws RemoteException {
		jobs = new DefaultComboBoxModel(advertise.getJobs());
		allJobs.setModel(jobs);
	}
	
	private void doRegister()
		throws NamingException, RemoveException, RemoteException, CreateException, ClassCastException {
		if (isNull(addLogin) || isNull(addName) || isNull(addEmail)) {
			error("Null field","You must provide a login, full name and email address");
			return;
		}
		try {
			agency.createCustomer(addLogin.getText(),addName.getText(),addEmail.getText());
			String name = addLogin.getText();
			resetDetails();
			login.setText(name);
			doLogin();
		}
		catch (DuplicateException ex) {
			error ("Duplicate Login","Login name already in use - try another");
		}
	}
	
	private void doUpdate() throws RemoteException {
		if (advertise == null) {
			error ("No customer", "You must login or register before updating your details");
			return;
		}
		String[] address = new String[2];
		address[0] = address1.getText();
		address[1] = address2.getText();
		advertise.updateDetails (name.getText(),email.getText(),address);
		info ("Customer update Complete","Customer database updated successfully");
	}
	
	private void doDelete() throws RemoveException, RemoteException {
		if (advertise == null) {
			error ("No customer", "You must login before deleting your entry");
			return;
		}
		try {
			agency.deleteCustomer(login.getText());
			resetDetails();
			info ("Delete Complete","Customer entry deleted successfully");
		}
		catch (NotFoundException ex) {
			error ("Missing Login","Internal error - customer entry not found");
		}
	}

	private void doJobSelect() throws RemoteException, NamingException, CreateException {
		if (advertise == null) {
			error ("No customer", "You must login or register before selecting a job");
			return;
		}
		if (jobs.getSize() == 0) {
			error ("No Job", "You have no jobs - try creating one");
			return;
		}
		loadJobDetails(advertise,(String)jobs.getSelectedItem(), login.getText());
		enableJobForm(true);
	}
	
	private void doJobCreate() throws RemoteException, DuplicateException, NamingException, CreateException {
		if (advertise == null) {
			error ("No customer", "You must login or register before updating your details");
			return;
		}
		if (isNull(newJob)) {
			error ("No Job", "You must enter a job name in the new job field");
			return;
		}
		String job = newJob.getText();
		advertise.createJob(job);
		loadJobs(advertise);
		loadJobDetails(advertise,job,login.getText());
		enableJobForm(true);
	}

	private AdvertiseJobHome advJobHome;
	
	private void loadJobDetails(Advertise advertise, String ref, String customer) throws RemoteException, NamingException, CreateException {
		if (advJobHome == null) {
			InitialContext ic = new InitialContext();
			Object lookup = ic.lookup(advertiseJobJNDI);
			advJobHome = (AdvertiseJobHome)PortableRemoteObject.narrow(lookup, AdvertiseJobHome.class);
		}
		if (ref==null || customer==null)			// check for race condition
			return;
		advertiseJob = advJobHome.create(ref, customer);
		allJobs.setSelectedItem(ref);
		newJob.setText("");
		description.setText(advertiseJob.getDescription()); 
		currentJob.setText(ref);
		location.setSelectedItem(advertiseJob.getLocation());
		String[] skillList = advertiseJob.getSkills();
		skills.clear();
		for (int i=0; i<skillList.length; i++)
			skills.addElement(skillList[i]);
	}
 
	private void doJobUpdate() throws RemoteException, NotFoundException {
		if (advertiseJob == null) {
			error ("No Job", "You must select or create a job before updating the details");
			return;
		}
		String[] skillList = new String[skills.size()];
		skills.copyInto (skillList); 
		advertiseJob.updateDetails (description.getText(),(String)location.getSelectedItem(),skillList);
		info ("Job update Complete","Job database updated successfully");
	}

	private void doJobDelete() throws RemoveException, RemoteException, NotFoundException {
		if (advertiseJob == null) {
			error ("No Job", "You must select a job before you can delete it");
			return;
		}
		try {
			advertise.deleteJob(currentJob.getText());
			resetJobDetails();
			loadJobs(advertise);
			info ("Delete Complete","Customer entry deleted successfully");
		}
		catch (NotFoundException ex) {
			error ("Missing Login","Internal error - customer entry not found");
		}
	}

	private void doAddSkill() throws RemoteException {
		if (advertiseJob == null) {
			error ("No job", "You must create or select a job before modifying skills");
			return;
		}
		Object[] skill = allSkills.getSelectedValues();
		if (skill.length == 0)
			warning ("No skill","You must select a skill to add from the available skills list");
		
		for (int i=0; i<skill.length; i++) {
			if (!skills.contains(skill[i]))
				skills.addElement(skill[i]);
			else
				warning ("Duplicate skill",skill[i]+" already selected");
		}
		actualSkills.invalidate();
		validate();
	}

	private void doRemoveSkill() throws RemoteException {
		if (advertiseJob == null) {
			error ("No job", "You must create or select a job before modifying skills");
			return;
		}
		Object[] skill = (actualSkills.getSelectedValues());
		if (skill.length == 0)
			warning ("No skill","You must select a skill to remove from the skills list");
		for (int i=0; i<skill.length; i++) {
			if (skills.contains(skill[i]))
				skills.removeElement(skill[i]);
		}
	}

}


