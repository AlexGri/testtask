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

import org.apache.commons.lang3.StringUtils;

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
			String salaryString = request.getParameter("salary");
			Double salary = StringUtils.isBlank(salaryString) ? null : Double.valueOf(salaryString);
			
			String employeeString = request.getParameter("employeeId");
			Long employeeId = StringUtils.isBlank(employeeString) ? null : Long.valueOf(employeeString);
			if (employeeId != null) {
				pd.updateEmployee(employeeId, firstname, lastname, middlename, phones, salary);					
			} else {
				String positionString = request.getParameter("positionId");
				Long positionId =  StringUtils.isBlank(positionString) ? null : Long.valueOf(positionString);
				Long createdEmployeeId = pd.createEmployee(firstname, lastname, middlename, phones, salary, positionId);
				request.setAttribute("createdEmployeeId", createdEmployeeId);
			}
			requestDispatcher = request.getRequestDispatcher("/employee/employeeView");
		} catch (Exception e) {
			e.printStackTrace();
			requestDispatcher = request.getRequestDispatcher("/error");
		} finally {
			requestDispatcher.forward(request, response);
		}
		
	}

}
