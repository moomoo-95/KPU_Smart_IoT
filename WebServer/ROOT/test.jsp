<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<%@ page import = "java.sql.*" %>

<%

    Connection conn = null;

    try{

        String url = "jdbc:mysql://localhost:3306/LHS_connSample";//생성한 데이타베이스 이름을 넣는다.

        String id = "root"; // db에 접속하는 계정

        String pw = "950817"; // db에 접속하는 계정의 비밀번호
 
        Class.forName("com.mysql.jdbc.Driver");
	out.println("2");
        conn=DriverManager.getConnection(url, id, pw);

        out.println("success");  // mysql에 연결되면 성공!!

    }catch(Exception e){

        out.println("fail"); // mysql에 연결 실패!!

    }

%>
