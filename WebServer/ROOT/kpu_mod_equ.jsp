<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String name = request.getParameter("name");
	String birth = request.getParameter("birth");
	String type = "findId";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("findId") ) {
		String returns = connectDB.findId(name, birth);
		out.println(returns);
	};
%>
