<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.testAr"%>
<%@ page import="javamysql.selectInfo"%>
<%
	request.setCharacterEncoding("UTF-8");
	String open_close = request.getParameter("motor");
	String oscillation = request.getParameter("oscill");
	String gyro_x = request.getParameter("gyro_x");
	String gyro_y = request.getParameter("gyro_y");
	String gyro_z = request.getParameter("gyro_z");
	String infra = request.getParameter("infra");
	String user = "28";
	String equ = "18";
	String state = "0";
	
	testAr connectDBA = testAr.getInstance();
        String oac = connectDBA.safetyOpen( open_close, user, equ, state );
        String oscill = connectDBA.safetyOscill( oscillation, user, equ, state );
        String gyro = connectDBA.safetyGyro( gyro_x, gyro_y, gyro_z, user, equ, state );
        String infr = connectDBA.safetyInfra( infra, user, equ, state );

	selectInfo connectDB = selectInfo.getInstance();
	String result = connectDB.selectSafetyInfo(equ, user, state);

	out.println(result);
	out.println("TEXTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT");
%>
