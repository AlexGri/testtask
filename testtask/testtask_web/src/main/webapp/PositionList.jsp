<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="org.nc.core.redistributable.javabean.PositionPojo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<%@ include file="/head.jsp" %>
	<body>
		<jsp:useBean id="pd" scope="request"
			class="org.nc.web.PersonnelDepartmentBean" />
		<jsp:scriptlet>String employeeId = request.getParameter("employeeId");</jsp:scriptlet>
		<jsp:scriptlet>boolean selectMode = employeeId != null && !employeeId.isEmpty();</jsp:scriptlet>
		<%@ include file="/navigation.jsp" %>
		<table>
			<thead>
				<tr>
					<th>Наименование должности</th>
					<c:if test="<%=selectMode%>">
						<th>Доступное действие</th>
					</c:if>
				
				</tr>
			</thead>
			<tbody>
				<c:forEach var="pos" items="${pd.positionList}">
					<tr>
						<td>${pos.positionName}</td>
						<c:if test="<%=selectMode%>">
							<td>
								<form action="saveEmployeeEnt" method="post">
									<input type="hidden" name="positionId" value="${pos.id}"/>
									<input type="hidden" name="employeeId" value="<%= employeeId%>"/>
									<input type="submit" value="Выбрать" />
								</form>
							</td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<c:if test="<%=selectMode%>">
			<form action="employeeEdit" method="post">
				<input type="hidden" name="employeeId" value="<%= employeeId%>"/>
				<input type="submit" value="Отменить" />
			</form>
		</c:if>
	</body>
</html>