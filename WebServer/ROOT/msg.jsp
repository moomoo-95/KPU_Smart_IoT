<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<title>FCM Push Example</title>
</head>
<body>
<h2>Push 알림 메시지 입력</h2>
 
<form action="push_notification.jsp" method="post">
    <textarea name="message" rows="4" cols="50" placeholder="메세지를 입력하세요"></textarea><br>
    <input type="submit" name="submit" value="Send" id="submitButton">
</form>
</body>
</html>
