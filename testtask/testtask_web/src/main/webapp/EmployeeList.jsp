<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
	xmlns:c="http://java.sun.com/jsp/jstl/core">
	<jsp:directive.page contentType="text/html; charset=UTF-8"
		pageEncoding="UTF-8" session="false" />
	<jsp:output doctype-root-element="html"
		doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
		doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
		omit-xml-declaration="true" />
	<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>PD</title>
</head>
<body>
	<!--<jsp:useBean id="pd" scope="request"
		class="org.nc.web.PersonnelDepartmentBean" />

	--><div>
		<form action="employeeList" method="post" >
			<input name="searchValue" /> 
			<input type="submit" value="Find"/>
		</form>
	</div>
	<table>
		<THEAD>
			<TR>
				<TD>ID</TD>
				<TD>Firstname</TD>
				<TD>Lastname</TD>
				<TD>Middlename</TD>
				<TD>Phones</TD>
				<TD>Salary</TD>
				<TD>Position</TD>
				<TD>Action</TD>
			</TR>
		</THEAD>
		<TBODY>

			<c:forEach items="${employees}" var="employee">
			<TR>
				<TD>${employee.id}</TD>
				<TD>${employee.firstname}</TD>
				<TD>${employee.lastname}</TD>
				<TD>${employee.middlename}</TD>
				<TD>${employee.phones}</TD>
				<TD>${employee.salary}</TD>
				<TD>${employee.position.positionName}</TD>
				<TD>
					<form action="employeeView" method="post">
							<input type="hidden" name="id" value="${employee.id}" /> 
							<input type="submit" value="view" />
					</form>
					<form action="employeeEdit" method="post">
							<input type="hidden" name="employeeId" value="${employee.id}" /> 
							<input type="submit" value="edit" />
					</form>
					<form action="employeeDelete" method="post">
							<input type="hidden" name="employeeId" value="${employee.id}"/> 
							<input type="submit" value="delete"/>
					</form>
				</TD>
			</TR>
			</c:forEach>
			
		</TBODY>
	</table>

</body>
	</html>
</jsp:root>