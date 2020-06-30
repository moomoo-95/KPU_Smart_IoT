<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String type = "getMain";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("getMain") ) {
		String returns = connectDB.getMain(id);
		out.println(returns);
	};
%>
