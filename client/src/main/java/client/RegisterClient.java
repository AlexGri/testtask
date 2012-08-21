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

public class RegisterClient extends JPanel implements ActionListener
{
    public static void main(String[] args) {
    	if (args.length!=0 && args.length!=2) {
    		System.err.println("Usage: RegisterClient AgencyJNDI RegisterJNDI");
    		System.exit(1);
    	}
   		final JFrame window = new JFrame("Agency Register Applicant");
   		try {
	        final RegisterClient client = new RegisterClient(args.length==2?args[0]:null, args.length==2?args[1]:null);
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
	private String registerJNDI = "java:comp/env/ejb/Register";
	
    private Agency agency;
    private Register register;

    public RegisterClient(String agencyJNDI, String registerJNDI) {
    	if (agencyJNDI != null)
    		this.agencyJNDI = agencyJNDI;
    	if (registerJNDI != null)
	    	this.registerJNDI = registerJNDI;
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
	    	loginButton.addActionListener(this);
	    	logoffButton.addActionListener(this);
	    	registerButton.addActionListener(this);
	    	updateButton.addActionListener(this);
	    	addSkillButton.addActionListener(this);
	    	removeSkillButton.addActionListener(this);
	    	deleteButton.addActionListener(this);
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
    private JTextArea summary = new JTextArea(4,32);
    private JComboBox location;
    private JList allSkills;
    private DefaultListModel skills = new DefaultListModel();
    private JList actualSkills = new JList(skills);
    
    private JButton loginButton = new JButton("Login");
    private JButton logoffButton = new JButton("Logoff");
    private JButton updateButton = new JButton("Update");
    private JButton registerButton = new JButton("Register");
    private JButton addSkillButton = new JButton("Add");
    private JButton removeSkillButton = new JButton("Remove");
    private JButton deleteButton = new JButton("Delete Registration");

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
    	actualSkills.setVisibleRowCount(4);
    	
    	JPanel panel = boxPanel("Applicant details");
	    Box left = Box.createVerticalBox();
    	left.add (panelRow(new FixedLabel("Full name:"),name));
    	left.add (panelRow(new FixedLabel("Email address:"),email));
    	left.add (panelRow(new FixedLabel("Location:"),location));
	   	left.add(panelRow(new FixedLabel("Skills:"),new JScrollPane(actualSkills)));
    	left.add(panelRow(new FixedLabel(""),removeSkillButton));
    	
	    Box right = Box.createVerticalBox();
    	right.add (panelRow(new FixedLabel("Summary:"),new JScrollPane(summary)));
    	right.add(panelRow(new FixedLabel("Available skills:"),new JScrollPane(allSkills)));
    	right.add(panelRow(new FixedLabel(""),addSkillButton));
    	
	    Box t = Box.createHorizontalBox();
    	t.add (left);
    	t.add (Box.createHorizontalGlue());
    	t.add (right);
    	panel.add(t);

    	panel.add (Box.createVerticalGlue());
    	panel.add(panelRow(updateButton,deleteButton));
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
			else if (o == addSkillButton)
				doAddSkill();
			else if (o == removeSkillButton)
				doRemoveSkill();
			else if (o == deleteButton)
				doDelete();
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
	    	if (register != null)
	    		register.remove();
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
    	addSkillButton.setEnabled(on);
    	removeSkillButton.setEnabled(on);
    	deleteButton.setEnabled(on);
    	login.setEnabled(!on);
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
		if (register != null)
		{
			register.remove();
			register = null;
		}
		enableForm (false);
		login.setText("");
		name.setText("");
		email.setText("");
		summary.setText("");
		location.setSelectedIndex(0);
		skills.clear();
    }
    
	private RegisterHome regHome;

    private void doLogin()
    	throws NamingException, RemoteException, RemoveException, CreateException, ClassCastException {
    	String name = login.getText();
    	if (isNull(name)) {
    		error ("Login","No login name specified");
    		return;
    	}
    	resetDetails();
    	login.setText(name);
		if (regHome == null) {
    		InitialContext ic = new InitialContext();
	        Object lookup = ic.lookup(registerJNDI);
	        regHome = (RegisterHome)PortableRemoteObject.narrow(lookup, RegisterHome.class);
		}
		
    	register = regHome.create(name);
    	loadDetails (register);
    	enableForm(true);
    }

    private void doLogoff() throws RemoveException, RemoteException {
    	resetDetails();
    	enableForm(false);
    }

    private void loadDetails (Register register) throws RemoteException {
    	name.setText(register.getName());
    	email.setText(register.getEmail());
    	summary.setText(register.getSummary());
    	String[] skillList = register.getSkills();
    	location.setSelectedItem(register.getLocation());
    	skills.clear();
    	for (int i=0; i<skillList.length; i++)
    		skills.addElement(skillList[i]);
    }
    
    private void doRegister()
       	throws NamingException, RemoveException, RemoteException, CreateException, ClassCastException {
    	if (isNull(addLogin) || isNull(addName) || isNull(addEmail)) {
    		error("Null field","You must provide a login, full name and email address");
    		return;
    	}
    	resetDetails();
    	try {
	    	agency.createApplicant(addLogin.getText(),addName.getText(),addEmail.getText());
	    	login.setText(addLogin.getText());
	    	doLogin();
    	}
    	catch (DuplicateException ex) {
    		error ("Duplicate Login","Login name already in use - try another");
    	}
    }
    
    private void doUpdate() throws RemoteException {
    	if (register == null) {
    		error ("No applicant", "You must login or register before updating your details");
    		return;
    	}
    	String[] skillList = new String[skills.size()];
    	skills.copyInto (skillList); 
		register.updateDetails (name.getText(),email.getText(),(String)location.getSelectedItem(),summary.getText(),skillList);
		info ("Update Complete","Database updated successfully");
    }
    
    private void doDelete() throws RemoveException, RemoteException {
    	if (register == null) {
    		error ("No applicant", "You must login before deleting your entry");
    		return;
    	}
    	try {
	    	agency.deleteApplicant(login.getText());
	    	resetDetails();
			info ("Delete Complete","Applicant entry deleted successfully");
    	}
    	catch (NotFoundException ex) {
    		error ("Missing Login","Internal error - applcation entry not found");
    	}
    }
    
    private void doAddSkill() throws RemoteException {
    	if (register == null) {
    		error ("No applicant", "You must login or register before modifying skills");
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
    	//actualSkills.invalidate();
    	//validate();
    }

    private void doRemoveSkill() throws RemoteException {
    	if (register == null) {
    		error ("No applicant", "You must login or register before modifying skills");
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


