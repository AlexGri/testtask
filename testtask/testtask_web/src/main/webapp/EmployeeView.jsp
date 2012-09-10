<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="org.nc.core.redistributable.javabean.EmployeePojo"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/head.jsp" %>
<body>
	<jsp:useBean id="pd" scope="request"
		class="org.nc.web.PersonnelDepartmentBean" />
	<jsp:scriptlet>EmployeePojo employee = pd.getEmployee(request.getParameter("employeeId"));</jsp:scriptlet>
<%@ include file="/navigation.jsp" %>
	<table>
		<tbody>
			<tr>
				<td>Firstname</td>
				<td><%=employee.getFirstname()%></td>
			</tr>
			<tr>
				<td>Lastname</td>
				<td><%=employee.getLastname()%></td>
			</tr>
			<tr>
				<td>Middlename</td>
				<td><%=employee.getMiddlename()%></td>
			</tr>
			<tr>
				<td>Phones</td>
				<td><%=employee.getPhones()%></td>
			</tr>
			<tr>
				<td>Salary</td>
				<td><%=employee.getSalary()%></td>
			</tr>
			<tr>
				<td>Position</td>
				<td><%=employee.getPosition() == null ? "" : employee.getPosition().getPositionName()%></td>
			</tr>
		</tbody>
	</table>
	<form action="employeeEdit" method="post">
		<input type="hidden" name="employeeId" value="<%= employee.getId()%>" /> 
		<input type="submit" value="edit" />
	</form>
</body>
</html>