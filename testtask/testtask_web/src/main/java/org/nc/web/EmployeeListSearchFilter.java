package org.nc.web;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.nc.core.redistributable.javabean.EmployeePojo;

public class EmployeeListSearchFilter implements Filter {

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
			throw new ServletException("error during initialization of filter " + this.getClass(), e.getCause());
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String parameterName = "searchValue";
		Set<String> params = request.getParameterMap().keySet();
		Collection<EmployeePojo> employees = Collections.emptyList();
		if (params.contains(parameterName)) {
			String searchValue = request.getParameter(parameterName);
			employees = personnelDepartmentBean.findEmployee(searchValue);
		} else {
			employees = personnelDepartmentBean.getEmployeeList();
		}
		request.setAttribute("employees", employees);
		chain.doFilter(request, response);
	}
}
