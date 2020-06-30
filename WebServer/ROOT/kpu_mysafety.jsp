<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String type = "mySafety";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("mySafety") ) {
		String returns = connectDB.mySafety(id);
		out.println(returns);
	};
%>
