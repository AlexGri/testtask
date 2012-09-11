<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<%@ include file="/head.jsp" %>
	<body>
		<%@ include file="/navigation.jsp" %>
		<div>
			<form action="employeeList" method="post" >
				<input name="searchValue" value="<%= request.getParameter("searchValue") == null ? "" : request.getParameter("searchValue") %>" /> 
				<input type="submit" value="Искать"/>
			</form>
		</div>
		<table>
			<thead>
				<tr>
					<td>Фамилия</td>
					<td>Имя</td>					
					<td>Отчество</td>
					<td>Телефоны</td>
					<td>Оклад</td>
					<td>Должность</td>
					<td>Доступные действия</td>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${employees}" var="employee">
					<tr>
						<td>${employee.lastname}</td>
						<td>${employee.firstname}</td>						
						<td>${employee.middlename}</td>
						<td>${employee.phones}</td>
						<td>${employee.salary}</td>
						<td>${employee.position.positionName}</td>
						<td>
							<div>
								<form action="employeeEdit" method="post">
									<input type="hidden" name="employeeId" value="${employee.id}" /> 
									<input type="submit" value="Редактировать" />
								</form>
							</div>
						</td>
						<td>
							<form action="employeeDelete" method="post">
								<input type="hidden" name="employeeId" value="${employee.id}"/> 
								<input type="image" src="../img/delete.gif" alt="Удалить"/>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</body>
</html>