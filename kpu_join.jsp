<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String name = request.getParameter("name");
	String id = request.getParameter("id");
	String pwd = request.getParameter("pwd");
	String email = request.getParameter("email");
	String birth = request.getParameter("birth");
	String type = "join";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("join") ) {
		String returns = connectDB.joindb(name, id, pwd, email, birth);
		out.println(returns);
	};
%>
