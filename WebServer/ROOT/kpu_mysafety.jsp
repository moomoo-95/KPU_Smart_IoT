<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
        String name = request.getParameter("name");
	String type = request.getParameter("type");
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("mySafety") ) {
		String returns = connectDB.mySafety(id, name, 1);
		out.println(returns);
	}
        else if( type.equals("nowSafety") ) {
                String returns = connectDB.mySafety(id, name, 2);
                out.println(returns);
        }

%>
