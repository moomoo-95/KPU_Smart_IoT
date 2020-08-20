<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.testAr"%>
<%
	request.setCharacterEncoding("UTF-8");
	String open_close = request.getParameter("motor");
	String oscillation = request.getParameter("oscill");
	String gyro_x = request.getParameter("gyro_x");
	String gyro_y = request.getParameter("gyro_y");
	String gyro_z = request.getParameter("gyro_z");
	String infra = request.getParameter("infra");
	String user = request.getParameter("user");
	String equ = request.getParameter("equ");
	String state = request.getParameter("state");

	testAr connectDB = testAr.getInstance();

	String open_return = connectDB.safetyOpen(open_close,user,equ,state);
	String oscill_return = connectDB.safetyOscill(oscillation,user,equ,state);
	String gyro_return = connectDB.safetyGyro(gyro_x, gyro_y, gyro_z,user,equ,state);
	String infra_return = connectDB.safetyInfra(infra,user,equ,state);

        out.println("<p>"+open_return+"</p>");
	out.println("<p>"+oscill_return+"</p>");
	out.println("<p>"+gyro_return+"</p>");
	out.println("<p>"+infra_return+"</p>");
%>
<%="open_close : "+open_return%>
<%="oscillation : "+oscill_return%>
<%="gyro : "+gyro_return%>
<%="infra : "+infra_return%>
