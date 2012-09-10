package org.nc.web;

import java.io.IOException;
import java.util.Set;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EmployeeSave
 */
public class SaveEmployeePrim extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher requestDispatcher = null;
		PersonnelDepartmentBean pd = null;
		try {
			pd = new PersonnelDepartmentBean();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CreateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");
			String middlename = request.getParameter("middlename");
			String phones = request.getParameter("phones");
			Double salary = Double.valueOf(request.getParameter("salary"));
			
			Set<String> params = request.getParameterMap().keySet();
			if (params.contains("employeeId")) {
				Long employeeId = Long.valueOf(request.getParameter("employeeId"));
				pd.updateEmployee(employeeId, firstname, lastname, middlename, phones, salary);					
			} else {				
				Long positionId =  Long.valueOf(request.getParameter("positionId"));
				pd.createEmployee(firstname, lastname, middlename, phones, salary, positionId);
			}
			requestDispatcher = request.getRequestDispatcher("/employee/employeeView");
		} catch (Exception e) {
			requestDispatcher = request.getRequestDispatcher("/error");
		} finally {
			requestDispatcher.forward(request, response);
		}
		
	}

}
