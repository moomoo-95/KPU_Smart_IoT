<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = requset.getParameter("id");
	String passwd = request.getParameter("passwd");
	String email = request.getParameter("email");
	String type = "modUsr";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("modUsr") ) {
		String returns = connectDB.modUsr(id, passwd, email);
		out.println(returns);
	};
%>
