package org.nc.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;

public class EmployeeViewParameterFilter implements Filter {

	private PersonnelDepartmentBean personnelDepartmentBean;

	@Override
	public void destroy() {
		personnelDepartmentBean = null;
	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		try {
			personnelDepartmentBean = new PersonnelDepartmentBean();
		} catch (Exception e) {
			throw new ServletException("error during initialization of " + this.getClass(), e.getCause());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String employeeString = request.getParameter("employeeId");
		
		Long employeeId = StringUtils.isBlank(employeeString) ? null : Long.valueOf(employeeString);
		if (employeeId == null) {
			employeeId = (Long)request.getAttribute("createdEmployeeId");
		}
		
		request.setAttribute("employee", personnelDepartmentBean.getEmployee(employeeId));
		chain.doFilter(request, response);
	}

}
