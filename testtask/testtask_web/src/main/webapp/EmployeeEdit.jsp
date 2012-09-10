<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="org.nc.core.redistributable.javabean.PositionPojo"%>
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
		<jsp:useBean id="employee" scope="request"
			class="org.nc.core.redistributable.javabean.EmployeePojo" />
		<jsp:useBean id="position" scope="request"
			class="org.nc.core.redistributable.javabean.PositionPojo" />
		<jsp:scriptlet>employee = (EmployeePojo)request.getAttribute("empl");</jsp:scriptlet>
		<jsp:scriptlet>position = (PositionPojo)request.getAttribute("pos");</jsp:scriptlet>
		<%@ include file="/navigation.jsp" %>
		<div >
			<form action="saveEmployeePrim" method="post">
				<c:if test="<%= employee.getId() != null%>">
					<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/>
				</c:if>
				<table>
					<tbody>
						<tr>
							<td>Firstname</td>
							<td><input name="firstname" value="<%=employee.getFirstname() == null ? "" : employee.getFirstname()%>"/></td>
						</tr>
						<tr>
							<td>Lastname</td>
							<td><input name="lastname" value="<%= employee.getLastname() == null ? "" : employee.getLastname()%>"/></td>
						</tr>
						<tr>
							<td>Middlename</td>
							<td><input name="middlename" value="<%= employee.getMiddlename() == null ? "" : employee.getMiddlename()%>"/></td>
						</tr>
						<tr>
							<td>Phones</td>
							<td><input name="phones" value="<%= employee.getPhones() == null ? "" : employee.getPhones()%>"/></td>
						</tr>
						<tr>
							<td>Salary</td>
							<td><input name="salary" value="<%= employee.getSalary() == null ? "" : employee.getSalary()%>"/></td>
						</tr>
					</tbody>
				</table>
				<div class="inl">
					<input type="submit" value="<%= employee.getId() != null ? "save employee" : "create employee" %>"/>
				</div>
			</form>
		</div>
		<div>
		<c:if test="<%= employee.getId() != null%>">
			<form action="employeeDelete" method="post">
				<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/> 
				<input type="submit" value="delete employee"/>
			</form>
		</c:if>
		</div>
		<br/>
		<p>Связанные сущности:</p>
		<table class="brd">
			<tr>
				<td>Position : </td>
				<td><%= position.getPositionName() == null ? "" : position.getPositionName()%></td>
				<c:if test="<%= position.getId() != null%>">
					<td>
						<form action="saveEmployeeEnt" method="post">
							<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/>
							<input type="image" src="../img/delete.gif" alt="delete employee"/>
						</form>						
					</td>
				</c:if>
			</tr>
		</table>
		<c:if test="<%= employee.getId() != null%>">
			<form action="positionList" method="post">
				<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/>
				<input type="hidden" name="positionId" value="<%= position.getId()%>"/>
				<input type="submit" value="choose position"/>
			</form>
		</c:if>
	</body>
</html>