package agency;

import java.rmi.*;
import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import javax.sql.*;
import data.*;

public class AdvertiseJobBean implements SessionBean {
    private JobLocalHome jobHome;
    private SkillLocalHome skillHome;
    private LocationLocalHome locationHome;
    private CustomerLocalHome customerHome;
    private JobLocal job;

    public void updateDetails(String description, String locationName, String[] skillNames) {
        if (skillNames == null) {
            skillNames = new String[0];
        }
        List skillList;
        try {
            skillList = skillHome.lookup(Arrays.asList(skillNames));
        } catch (FinderException ex) {
            error("Invalid skill", ex); // throws an exception
            return;
        }
        LocationLocal location = null;
        if (locationName != null) {
            try {
                location = locationHome.findByPrimaryKey(locationName);
            } catch (FinderException ex) {
                error("Invalid location", ex); // throws an exception
                return;
            }
        }
        job.setDescription(description);
        job.setLocation(location);
        job.setSkills(skillList);
    }

	public String getRef() {
        return job.getRef();
    }

	public String getCustomer() {
        return job.getCustomer();
    }

    public String getDescription() {
        return job.getDescription();
    }

    public String getLocation() {
        LocationLocal location = job.getLocation();
        return (location != null) ? location.getName() : null;
    }

    public String[] getSkills() {
        Collection col = job.getSkills();
        String[] res = new String[col.size()];
        int i = 0;
        for (Iterator iter = col.iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            SkillLocal skill = (SkillLocal)o;
            res[i++] = skill.getName();
        }
        return res;
    }

    private void error(String msg, Exception ex) {
        String s = "AdvertiseJobBean: " + msg + "\n" + ex;
        System.out.println(s);
        throw new EJBException(s, ex);
    }

    // EJB methods start here
    public void ejbCreate(String ref, String customerLogin) throws CreateException {
        try {
            CustomerLocal customer = customerHome.findByPrimaryKey(customerLogin);
            job = jobHome.findByPrimaryKey(new JobPK(ref, customer.getLogin()));
        }
        catch (FinderException ex) {
            error("Cannot find Customer or Job: " + ref + "," + customerLogin, ex);
        }
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() {
        job = null;
        jobHome = null;
    }

    private SessionContext ctx;

    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
        InitialContext ic = null;
        try {
            ic = new InitialContext();
            jobHome = (JobLocalHome)ic.lookup("java:comp/env/ejb/JobLocal");
            skillHome = (SkillLocalHome)ic.lookup("java:comp/env/ejb/SkillLocal");
            locationHome = (LocationLocalHome)ic.lookup("java:comp/env/ejb/LocationLocal");
            customerHome = (CustomerLocalHome)ic.lookup("java:comp/env/ejb/CustomerLocal");
        }
        catch (NamingException ex) {
            error("Error looking up depended EJB or resource", ex);
        }
    }
}
