<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
	String servo = request.getParameter("servo");
	String vib = request.getParameter("vib");
	String gyro_x = request.getParameter("gyro_x");
	String gyro_y = request.getParameter("gyro_y");
	String gyro_z = request.getParameter("gyro_z");
	String pir = request.getParameter("PIR");
	String type = "getMain";
	ConnectDB connectDB = ConnectDB.getInstance();
	if( type.equals("getMain") ) {
		String returns = connectDB.getMain(id, servo, vib, gyro_x, gyro_y, gyro_z, pir);
		out.println(returns);
	};
%>
