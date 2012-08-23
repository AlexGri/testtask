package org.nc.core.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.EJBException;

public class CommonUtils {
	private CommonUtils() {
		throw new RuntimeException("CommonUtils should never be initialized");
	}
	
	public static void closeConnection (Connection con, PreparedStatement stmt, ResultSet rslt) {
		if (rslt != null) {
			try {
				rslt.close();
			}
			catch (SQLException e) {}
		}
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {}
		}
		if (con != null) {
			try {
				con.close();
			}
			catch (SQLException e) {}
		}
	}
	
	public static void error (String msg, Exception ex) {
		String s = msg + "\n" + ex;
		System.out.println(s);
		throw new EJBException(s,ex);
	}
}
