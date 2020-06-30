<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String equ_name = request.getParameter("equ_name");
	String status = request.getParameter("status");
	String type = "addEqu";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("addEqu") ) {
		String returns = connectDB.addEqu(id, equ_name, status);
		out.println(returns);
	};
%>
