<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String equ_name = request.getParameter("equ_name");
	String status = request.getParameter("status");
	String type = "delEqu";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("delEqu") ) {
		String returns = connectDB.delEqu(equ_name, status);
		out.println(returns);
	};
%>
