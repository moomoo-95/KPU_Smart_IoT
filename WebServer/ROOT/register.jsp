<%@ page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    Connection conn = null;
    PreparedStatement pstmt = null;
    String sql = null;
    String token = null;
    String requestMethod = null;
    
    request.setCharacterEncoding("utf-8");
    
    try{
        String url = "jdbc:mysql://localhost:3306/fcm";
        String dbId = "root";
        String dbPw = "950817";
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url, dbId, dbPw);
        out.println("MySQL 연결이 성공 하였습니다.");
    }catch(Exception e){
        out.println("MySQL 연결이 실패 하였습니다.");
        e.printStackTrace();
    }
    
    token = request.getParameter("Token");
    
    if( token.equals("") ){
        out.println("토큰값이 전달 되지 않았습니다.");
    }else{
        // 토큰값 전달시 쿼리문 입력할곳임
        sql = "INSERT INTO users(Token) VALUES(?)";
        pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1,token);
        
        pstmt.executeUpdate();//쿼리를 실행 하라는 명령어
    }
%>
