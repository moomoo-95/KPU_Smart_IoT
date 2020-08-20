<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String type = "modUsr";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("modUsr") ) {
		String returns = connectDB.modUsr(id);
		out.println(returns);
	};
%>
