<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String pwd = request.getParameter("pwd");
	String type = "login";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("login") ) {
		String returns = connectDB.login(id, pwd);
		out.println(returns);
	};
%>
