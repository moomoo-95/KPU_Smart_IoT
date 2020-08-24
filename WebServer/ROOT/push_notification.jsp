<%@page import="java.net.URLEncoder"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.google.android.gcm.server.*"%>
<%@page import="java.io.IOException"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%!
    public static String getPrintStackTrace(Exception e) {

        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        return errors.toString();

    }
    public class FCMSender extends Sender{
        public FCMSender(String key) {
            super(key);
        }
    @Override
    protected HttpURLConnection getConnection(String url) throws IOException {
        String fcmUrl = "https://fcm.googleapis.com/fcm/send";
        return (HttpURLConnection) new URL(fcmUrl).openConnection();
    }
}
%> 
<%
    ArrayList<String> token = new ArrayList<String>();    //token값을 ArrayList에 저장
    String MESSAGE_ID = String.valueOf(Math.random() % 100 + 1);    //메시지 고유 ID
    boolean SHOW_ON_IDLE = false;    //옙 활성화 상태일때 보여줄것인지
    int LIVE_TIME = 1;    //옙 비활성화 상태일때 FCM가 메시지를 유효화하는 시간
    int RETRY = 2;    //메시지 전송실패시 재시도 횟수
 
    
    String simpleApiKey = "AAAAoRyP5IM:APA91bEPW2rFGrifP6Jl2geKUFkKDGD7tt6nyOLr2uFJKLIDJC_NLtDpBRI0I16-YMV7nY9F2GS34QJkjKkgzPpKrvtOPx1IRYYykkAOqXMiiw91UVLUp-9YBU6yHirWlpRUZ6oIeCPO";
 // String simpleApiKey = "BKNwzgZq5jyt72T4sYVQwtGCpLezm1nKcnZ2QrbkPKHNCYYMUGq6PDwnrvR0UyqOc3J6oeIibAmPcCFd0Q00gro";
    String gcmURL = "https://android.googleapis.com/fcm/send";
    Connection conn = null; 
    Statement stmt = null; 
    ResultSet rs = null;
    
    String msg = request.getParameter("message");;

    if(msg==null || msg.equals("")){
        msg="";
    }

    try {
        String url = "jdbc:mysql://localhost:3306/kpuiot";
        String dbId = "root";
        String dbPw = "950817";
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url, dbId, dbPw);
        stmt = conn.createStatement();
        String sql = "SELECT gyro_status, oscillation_status, status, flag FROM status_change WHERE (public_seq=28 and equ_seq=18) and (gyro_status<>0 or oscillation_status <> 0) ORDER BY mot_time DESC LIMIT 1";
        rs = stmt.executeQuery(sql);
        String check = "";
        String flag = "";
        String gyro = "";
        String vib = "";
        //모든 등록ID를 리스트로 묶음
        if(rs.next()){
            check = rs.getString("status");
            flag = rs.getString("flag");
            gyro = rs.getString("gyro_status");
            vib = rs.getString("oscillation_status");
            
        }
        if ( check.compareTo("0") == 0 && flag.compareTo("0")==0){
            sql = "UPDATE status_change SET flag='1' WHERE (public_seq=28 and equ_seq=18) and (gyro_status<>0 or oscillation_status <> 0) ORDER BY mot_time DESC LIMIT 1";
            stmt.executeUpdate(sql);

            sql = "use fcm";
            rs = stmt.executeQuery(sql);
            sql = "select token from users";
            rs = stmt.executeQuery(sql);

            //모든 등록ID를 리스트로 묶음
            while(rs.next()){
                token.add(rs.getString("Token"));
            }
            conn.close();

            if(gyro.compareTo("0") != 0 && vib.compareTo("0") != 0){ msg = "금고의 움직임과 충격이 감지되었습니다."; }
            else if(gyro.compareTo("0") != 0) { msg = "금고의 움직임이 감지되었습니다."; }
            else { msg = "금고의 충격이 감지되었습니다."; }
            msg = new String(msg.getBytes("UTF-8"), "UTF-8");   //메시지 한글깨짐 처리

            //Sender sender = new Sender(simpleApiKey);
            Sender sender = new FCMSender(simpleApiKey);
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
                    out.println(result.getErrorCodeName());
                }
            }
            out.println("success");
      
 
        }
    }catch (Exception e) {
        out.println("failed");
        //e.printStackTrace();
        out.println(getPrintStackTrace(e));
    }
    out.println("finish");
%>
