<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String email = request.getParameter("email");
	String type = "findPasswd";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("findPasswd") ) {
		String returns = connectDB.findPasswd(id, email);
		out.println(returns);
	};
%>
