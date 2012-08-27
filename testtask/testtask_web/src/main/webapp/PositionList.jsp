<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="org.nc.core.redistributable.javabean.PositionPojo"%>
<%@page import="org.nc.core.redistributable.javabean.EmployeePojo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
	<jsp:useBean id="pd" scope="request"
		class="org.nc.web.PersonnelDepartmentBean" />
	<jsp:useBean id="employee" scope="request"
		class="org.nc.core.redistributable.javabean.EmployeePojo" />
	<jsp:scriptlet>employee = (EmployeePojo)request.getAttribute("employee");</jsp:scriptlet>	
	<jsp:scriptlet>PositionPojo position = pd.getPosition(request.getParameter("id"));</jsp:scriptlet>

<form>
		<table>
			<tbody>
				<tr>
					<td>Firstname</td>
					<td><input name="firstname" value="<%=employee.getFirstname() %>"/></td>
				</tr>
				<tr>
					<td>Lastname</td>
					<td><input name="lastname" value="<%= employee.getLastname()%>"/></td>
				</tr>
				<tr>
					<td>Middlename</td>
					<td><input name="middlename" value="<%= employee.getMiddlename()%>"/></td>
				</tr>
				<tr>
					<td>Phones</td>
					<td><input name="phones" value="<%= employee.getPhones()%>"/></td>
				</tr>
				<tr>
					<td>Salary</td>
					<td><input name="salary" value="<%= employee.getSalary()%>"/></td>
				</tr>
				<tr>
					<td>Position</td>
					<td>
						<%= employee.getPosition().getPositionName()%>
						<input type="hidden" name="positionId" value="<%= employee.getPosition().getId()%>"/>
						<input type="submit" value="select"/>
					</td>
				</tr>
			</tbody>
		</table>
	</form>
</body>
</html>