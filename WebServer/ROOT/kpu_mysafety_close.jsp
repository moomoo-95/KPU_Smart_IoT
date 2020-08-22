<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="javamysql.ConnectDB"%>
<%
	request.setCharacterEncoding("UTF-8");
	String id = request.getParameter("id");
        String name = request.getParameter("name");
	String type = request.getParameter("type");
	ConnectDB connectDB = ConnectDB.getInstance();
        String returns = "";
	if( type.equals("mySafety") ) {
		returns = connectDB.mySafety_close(id, name, 1);
	}
        else if( type.equals("openmulty") ) {
                returns = connectDB.mySafety_close(id, name, 2);
        }
        else if( type.equals("opencheck") ) {
                returns = connectDB.mySafety_close(id, name, 3);
        }
        else if(type.equals("checkSafety") ) {
                returns = connectDB.mySafety_close(id, name, 4);
        }


        out.println(returns);

%>
