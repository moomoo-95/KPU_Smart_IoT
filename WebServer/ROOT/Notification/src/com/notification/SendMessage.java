package com.notification;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

@WebServlet("/SendMessage")
public class SendMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Connection con = null;
	public static Statement stmt = null;
	ResultSet rs = null;
	
	ArrayList<String> token = new ArrayList<String>();    			//token in arraylist
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);    //message unique ID
    boolean SHOW_ON_IDLE = false;    				//App activity
    int LIVE_TIME = 1;    					//message time
    int RETRY = 2;    						//retry chance
    
    String simpleApiKey = "AAAAoRyP5IM:APA91bEPW2rFGrifP6Jl2geKUFkKDGD7tt6nyOLr2uFJKLIDJC_NLtDpBRI0I16-YMV7nY9F2GS34QJkjKkgzPpKrvtOPx1IRYYykkAOqXMiiw91UVLUp-9YBU6yHirWlpRUZ6oIeCPO";
    String gcmURL = "https://android.googleapis.com/fcm/send";
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String msg = request.getParameter("message");
		System.out.println(msg);
		if(msg==null || msg.equals("")) msg="";
		
		msg = new String(msg.getBytes("UTF-8"), "UTF-8");   //message broken prevent
		 
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306","root","950817");
			stmt = con.createStatement();
			System.out.println("DB connection successed");
			stmt.executeQuery("use kpuiot");
			
			String sql = "select * from equ_info where equ_seq=18";
			rs = stmt.executeQuery(sql);
			
	        while(rs.next()){
	            token.add(rs.getString("user_seq"));
	            System.out.println(rs.getString("user_seq"));
	        }
	        con.close();
	        
	        Sender sender = new Sender(simpleApiKey);
	        Message message = new Message.Builder()
	        .collapseKey(MESSAGE_ID)
	        .delayWhileIdle(SHOW_ON_IDLE)
	        .timeToLive(LIVE_TIME)
	        .addData("message",msg)
	        .build();
	        MulticastResult result1 = sender.send(message,token,RETRY);
	        if (result1 != null) {
	            List<Result> resultList = result1.getResults();
	            for (Result result : resultList) {
	                System.out.println(result.getErrorCodeName()); 
	            }
	        }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("DB disconnection");
			e.printStackTrace();
		}
	}

}
