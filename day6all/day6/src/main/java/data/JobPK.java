package data;

import java.io.*;
import javax.ejb.*;

public class JobPK implements Serializable
{
	public String ref;
	public String customer;
	
	public JobPK() {
	}

	public JobPK(String ref, String customer) {
		this.ref = ref;
		this.customer = customer;
	}

	public String getRef() {
		return ref;
	}

	public String getCustomer() {
		return customer;
	}

	public boolean equals(Object obj) {
		if (obj instanceof JobPK) {
			JobPK pk = (JobPK)obj;
			return (pk.ref.equals(ref) && pk.customer.equals(customer)) ;
		}
		return false ;
	}

	public int hashCode() {
		return (ref.hashCode() ^ customer.hashCode());
	}

	public String toString() {
		return "JobPK: ref=\"" + ref + "\", customer=\"" + customer + "\"";
	}
}
