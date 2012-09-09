package org.nc.web;

import java.io.IOException;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EmployeeSave
 */
public class EmployeeDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = null;
		String paramName = "employeeId";
		try {
			Set<String> params = request.getParameterMap().keySet();
			if (params.contains(paramName)) {
				PersonnelDepartmentBean pd = new PersonnelDepartmentBean();
				pd.deleteEmployee(request.getParameter(paramName));
			} 
			requestDispatcher = request.getRequestDispatcher("/employeeList");
		} catch (Exception e) {
			requestDispatcher = request.getRequestDispatcher("/error");
		} finally {
			requestDispatcher.forward(request, response);
		}		
	}

}
