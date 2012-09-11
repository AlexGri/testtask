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
				<input type="hidden" name="employeeId" value="<%= employee.getId() == null ? "" : employee.getId() %>"/>
				<table>
					<tbody>
						<tr>
							<td>Фамилия</td>
							<td><input name="lastname" value="<%= employee.getLastname() == null ? "" : employee.getLastname()%>"/></td>
						</tr>
						<tr>
							<td>Имя</td>
							<td><input name="firstname" value="<%=employee.getFirstname() == null ? "" : employee.getFirstname()%>"/></td>
						</tr>
						<tr>
							<td>Отчество</td>
							<td><input name="middlename" value="<%= employee.getMiddlename() == null ? "" : employee.getMiddlename()%>"/></td>
						</tr>
						<tr>
							<td>Телефоны</td>
							<td><input name="phones" value="<%= employee.getPhones() == null ? "" : employee.getPhones()%>"/></td>
						</tr>
						<tr>
							<td>Оклад</td>
							<td><input name="salary" value="<%= employee.getSalary() == null ? "" : employee.getSalary()%>"/></td>
						</tr>
					</tbody>
				</table>
				<div class="inl">
					<input type="submit" value="<%= employee.getId() != null ? "Сохранить" : "Создать" %>"/>
				</div>
			</form>
		</div>
		<div>
		<c:if test="<%= employee.getId() != null%>">
			<form action="employeeDelete" method="post">
				<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/> 
				<input type="submit" value="Удалить"/>
			</form>
		</c:if>
		</div>
		<br/>
		<p>Связанные таблицы:</p>
		<table class="brd">
			<tr>
				<td>Должность : </td>
				<td><%= position.getPositionName() == null ? "" : position.getPositionName()%></td>
				<c:if test="<%= position.getId() != null%>">
					<td>
						<form action="saveEmployeeEnt" method="post">
							<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/>
							<input type="image" src="../img/delete.gif" alt="Удалить"/>
						</form>						
					</td>
				</c:if>
			</tr>
		</table>
		<c:if test="<%= employee.getId() != null%>">
			<form action="positionList" method="post">
				<input type="hidden" name="employeeId" value="<%= employee.getId()%>"/>
				<input type="hidden" name="positionId" value="<%= position.getId()%>"/>
				<input type="submit" value="Выбрать должность"/>
			</form>
		</c:if>
	</body>
</html>