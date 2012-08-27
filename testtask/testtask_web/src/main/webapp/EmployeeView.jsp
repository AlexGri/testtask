<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
<jsp:directive.page import="org.nc.core.redistributable.javabean.EmployeePojo"/>
	<jsp:directive.page contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8" session="false" />
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
	<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert title here</title>
</head>
<body>
	<jsp:useBean id="pd" scope="request"
		class="org.nc.web.PersonnelDepartmentBean" />
	<jsp:scriptlet>EmployeePojo employee = pd.getEmployee(request.getParameter("id"));</jsp:scriptlet>
	<table>
		<TBODY>
			<TR>
				<TD>Firstname</TD>
				<TD>
					<jsp:scriptlet>out.print(employee.getFirstname());</jsp:scriptlet>
				</TD>
			</TR>
			<TR>
				<TD>Lastname</TD>
				<TD>
					<jsp:scriptlet>out.print(employee.getLastname());</jsp:scriptlet>
				</TD>
			</TR>
			<TR>
				<TD>Middlename</TD>
				<TD>
					<jsp:scriptlet>out.print(employee.getMiddlename());</jsp:scriptlet>
				</TD>
			</TR>
			<TR>
				<TD>Phones</TD>
				<TD>
					<jsp:scriptlet>out.print(employee.getPhones());</jsp:scriptlet>
				</TD>
			</TR>
			<TR>
				<TD>Salary</TD>
				<TD>
					<jsp:scriptlet>out.print(employee.getSalary());</jsp:scriptlet>
				</TD>
			</TR>
			<TR>
				<TD>Position</TD>
				<TD>
					<jsp:scriptlet>out.print(employee.getPosition().getPositionName());</jsp:scriptlet>
				</TD>
			</TR>
		</TBODY>
	</table>
</body>
	</html>
</jsp:root>