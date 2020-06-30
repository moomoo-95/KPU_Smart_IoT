package javamysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ConnectDB {
    // 싱글톤 패턴으로 사용 하기위 한 코드들
    private static ConnectDB instance = new ConnectDB();

    public static ConnectDB getInstance() {
        return instance;
    }

    public ConnectDB() {

    }

    private String jdbcUrl = "jdbc:mysql://localhost:3306/kpuiot";
    private String dbId = "root";
    private String dbPw = "950817";
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private PreparedStatement pstmt2 = null;
    private ResultSet rs = null;
    private String sql = "";
    private String sql2 = "";
    String returns = "";
    String returns2 = "";

    // 데이터베이스와 통신하기 위한 코드가 들어있는 메서드
    public String joindb(String name, String id, String pwd, String email, String birth) {
   	 try {
   	         String key = "kpuiot";
		 Class.forName("com.mysql.jdbc.Driver");
	 	 conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
		 sql = "select ID from user_info where ID=?";
		 pstmt = conn.prepareStatement(sql);
            	 pstmt.setString(1, id);
            	 rs = pstmt.executeQuery();
		 // 입력한 아이디가 없을경우
          	 if ( !rs.next() ) { //회원가입 진행
                 
                	 sql2 = "insert into user_info ( USER_NAME, ID, PASSWORD, EMAIL, BIRTH_DAY,  MOD_DT ) values(?,?,HEX(AES_ENCRYPT(?, ?)),?,?,NOW())";
          	     	 pstmt2 = conn.prepareStatement(sql2);
             		 pstmt2.setString(1, name);
              		 pstmt2.setString(2, id);
              		 pstmt2.setString(3, pwd);
			 pstmt2.setString(4, key);
             	 	 pstmt2.setString(5, email);
		         pstmt2.setString(6, birth);
               		 pstmt2.executeUpdate();
		         returns = "ok";
		// 입력한 아이디가 있는경우
		}else { 
			returns = "id"; // 입력한 ID값 반환
		}
       } catch (Exception e) {

            e.printStackTrace();
       } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
           if (rs != null)try {rs.close();} catch (SQLException ex) {}
       }

     return returns;
    }

    // 로그인 Method
    public String login(String id, String pwd) {
        try {
	    String key = "kpuiot";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            sql = " SELECT *FROM user_info WHERE ID = ? AND PASSWORD = HEX(AES_ENCRYPT(?,?)) "; // 아이디와 비밀번호가 일치할 때, Count = 1
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
	    pstmt.setString(3, key);
            rs = pstmt.executeQuery();
            // 아이디, 비밀번호 검증
	    if ( !rs.next() ){ // 일치하는 아이디 및 비밀번호 미존재

                returns = "fail";
            } else {

                returns = id;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 아이디 찾기 Method
    public String findId(String name, String birth) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 이름과 생년월일로 아이디 찾기
            sql = " SELECT ID FROM user_info WHERE USER_NAME = ? AND BIRTH_DAY = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, birth);
            rs = pstmt.executeQuery();

            // 이름과 생년월일로 검증
            if ( rs.next()) { // 일치하는 아이디 및 비밀번호 미 존재
		
                returns = rs.getString("ID");
            } else {

                returns = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 비밀번호 찾기 Method
    public String findPasswd(String id, String email) {
        try {
            String key = "kpuiot"; // 해쉬 키값
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 아이디와 이메일로 비밀번호 찾기
            sql = " SELECT AES_DECRYPT(UNHEX(PASSWORD), ?) as PASSWORD FROM user_info WHERE ID = ? AND EMAIL = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, key);
            pstmt.setString(2, id);
	    pstmt.setString(3, email);
            rs = pstmt.executeQuery();

            // 아이디, 이메일 검증
            if ( rs.next()) { // 일치하는 아이디 및 비밀번호 미존재

                returns = rs.getString("PASSWORD");
            } else {

                returns = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 메인페이지 정보 호출
    public String getMain(String id) {
        try {

            String str; 
	    int i = 0;
	    String flag = "&";
	    String flag2 = "%Y.%m.%d";
	    Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 로그인 ID값의 USER_SEQ와 장비의 USER_SEQ가 같은 값의 장비만 조회
            sql = "select equ_name, DATE_FORMAT(reg_dt, ?) as reg_dt from equ_info ei inner join user_info ui on ui.user_seq = ei.user_seq where ui.user_seq = (select user_seq from user_info where id = ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, flag2);
   	    pstmt.setString(2, id);
            rs = pstmt.executeQuery();
            // 장비 조회
            if ( rs.next() ) {
		    returns2 = rs.getString("reg_dt");
		    str = flag.concat(returns2);
		    returns = rs.getString("equ_name");
		    returns = returns.concat(str);
            // 조회된 장비가 없어서 미 조회
            } else {

                returns = "empty";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 마이페이지 금고 정보 조회
    public String mySafety(String id) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 로그인 ID의 금고명, 금고등록시간,
            sql = "SELECT ei.EQU_NAME, ei.REG_DT, ocs.status, ifs.status, gs.status FROM equ_info ei INNER JOIN user_info ui on ui.user_seq = ei.user_seq INNER JOIN open_close_status ocs on ocs.equ_seq = ui.equ_seq INNER JOIN infrared_sensor ifs on ifs.equ_seq = ocs.equ_seq INNER JOIN gyro_sensor gs on gs.equ_seq = ifs.equ_seq WHERE ui.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            // 조회값 여부
            if ( rs.next()) {

                //TODO 값 조회해봐야암
                returns = rs.getString("PASSWORD");
            } else {

                returns = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 마이페이지 사용자 정보 수정
    public String modUsr(String id, String email, String passwd) {
        try {
            String key = "kpuiot";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 필수값 = 이메일, 패스워드,
            sql = "UPDATE user_info SET password = HEX(AES_ENCRYPT( ? , key)) , email= ? WHERE id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, passwd);
            pstmt.setString(2, email);
            pstmt.setString(3, id);
            rs = pstmt.executeQuery();
            // 조회값 여부
            if ( rs.next()) {

                returns = "success";
            } else {

                returns = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 마이페이지 금고 추가
    public String addEqu(String id, String equ_name, String status) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 장비명, 상태
            sql = "INSERT INTO equ_info(USER_SEQ, EQU_NAME, STATUS, REG_DT) VALUES((select user_seq from user_info where id = ? ), ? , ? ,NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, equ_name);
            pstmt.setString(3, status);
            rs = pstmt.executeQuery();
            // 조회값 여부
            if ( rs.next()) {

                returns = "success";
            } else {

                returns = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }

    // 마이페이지 금고 삭제
    public String addEqu(String equ_name, String status) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
            // 선택 금고 삭제
            sql = "DELETE from equ_info where equ_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, equ_name);
            rs = pstmt.executeQuery();
            // 삭제값 여부
            if ( rs.next()) {

                returns = "success";
            } else {

                returns = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {if (pstmt != null)try {pstmt.close();} catch (SQLException ex) {}
            if (conn != null)try {conn.close();} catch (SQLException ex) {}
            if (pstmt2 != null)try {pstmt2.close();} catch (SQLException ex) {}
            if (rs != null)try {rs.close();} catch (SQLException ex) {}
        }
        return returns;
    }
}
