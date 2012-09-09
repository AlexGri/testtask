package org.nc.web;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.nc.core.redistributable.javabean.EmployeePojo;
import org.nc.core.redistributable.javabean.PositionPojo;

public class EmployeeEditParameterFilter implements Filter {
	
	private PersonnelDepartmentBean personnelDepartmentBean;

	@Override
	public void destroy() {
		personnelDepartmentBean = null;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Set<String> params = request.getParameterMap().keySet();
		EmployeePojo employeePojo = new EmployeePojo();
		PositionPojo positionPojo = new PositionPojo();
		if (params.contains("employeeId")) {
			employeePojo = personnelDepartmentBean.getEmployee(request.getParameter("employeeId"));
			if (params.contains("positionId"))
				positionPojo = personnelDepartmentBean.getPosition(request.getParameter("positionId"));
			else if (employeePojo.getPosition() != null)
				positionPojo = employeePojo.getPosition();
		}	
		request.setAttribute("empl", employeePojo);
		request.setAttribute("pos", positionPojo);
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			personnelDepartmentBean = new PersonnelDepartmentBean();
		} catch (Exception e) {
			throw new ServletException("error during initialization of " + this.getClass(), e.getCause());
		}
	}
	

}
