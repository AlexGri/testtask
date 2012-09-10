<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%@ include file="/head.jsp" %>
<body>

<%@ include file="/navigation.jsp" %>
	<!--<jsp:useBean id="pd" scope="request"
		class="org.nc.web.PersonnelDepartmentBean" />

	--><div>
		<form action="employeeList" method="post" >
			<input name="searchValue" /> 
			<input type="submit" value="Find"/>
		</form>
	</div>
	<table>
		<thead>
			<tr>
				<td>ID</td>
				<td>Firstname</td>
				<td>Lastname</td>
				<td>Middlename</td>
				<td>Phones</td>
				<td>Salary</td>
				<td>Position</td>
				<td>Action</td>
			</tr>
		</thead>
		<tbody>

			<c:forEach items="${employees}" var="employee">
			<tr>
				<td>${employee.id}</td>
				<td>${employee.firstname}</td>
				<td>${employee.lastname}</td>
				<td>${employee.middlename}</td>
				<td>${employee.phones}</td>
				<td>${employee.salary}</td>
				<td>${employee.position.positionName}</td>
				<td>
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
				</td>
			</tr>
			</c:forEach>
			
		</tbody>
	</table>

</body>
	</html>