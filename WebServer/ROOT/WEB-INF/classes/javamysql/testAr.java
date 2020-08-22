package javamysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class testAr {
    // 싱글톤 패턴으로 사용 하기위 한 코드들
    private static testAr instance = new testAr();

    public static testAr getInstance() {
        return instance;
    }

    public testAr() {
    }
    private String jdbcUrl = "jdbc:mysql://localhost:3306/kpuiot";
    private String dbId = "root";
    private String dbPw = "950817";
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;
    private String sql = "";
    private String sub_sql = "";
    private String sub_sql_query = "";
    String returns = "";
    int rsrs = 0;
    
    // 금고 서보모터(개폐여부)
    public String safetyOpen(String open_close, String user, String equ, String state) {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
    		System.out.println(open_close);
		String eopen_close = "1";
		String euser = "2";
		String eequ = "3";
		String estate = "4";
                // 장비명, 값
    		sql = "INSERT INTO open_close_status( VALUE, PUBLIC_SEQ, EQU_SEQ, STATUS, MOT_TIME , OPEN_TIME , CLOSE_TIME ) VALUES( ?, ?, ?, ?, NOW(), NOW(), NOW() )";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, open_close);
		pstmt.setString(2, user);
		pstmt.setString(3, equ);
		pstmt.setString(4, state);
    		rsrs = pstmt.executeUpdate();
    			
                // 조회값 여부
                if ( rsrs != 0) {
                    returns = "success";
                } else {
                    returns = "fail";
                }
    	} catch (Exception e) {
    			returns = getPrintStackTrace(e);
    	} finally {
    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				returns = getPrintStackTrace(ex);
    			}
    		}
            if (conn != null) {
    		    try {
    		    	conn.close();
    		    } catch (SQLException ex) {
    		    	returns = getPrintStackTrace(ex);
    		    }
            }
	return returns;
    	}
    }
    
    // 금고 진동 센서
    public String safetyOscill(String oscillation, String user, String equ, String state) {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
    		String eoscillation = "1";
		String euser = "2";
		String eequ = "3";
		String estate = "4";
                // 장비명, 값
    		sql = "INSERT INTO oscillation_sensor( VALUE, PUBLIC_SEQ, EQU_SEQ, STATUS, MOD_TIME) VALUES(?, ?, ?, ?, NOW())";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, oscillation);
		pstmt.setString(2, user);
		pstmt.setString(3, equ);
		pstmt.setString(4, state);
    		rsrs = pstmt.executeUpdate();
    			
                // 조회값 여부
                if ( rsrs != 0) {
                    returns = "success";
                } else {
                    returns = "fail";
                }
    	} catch (Exception e) {
    			returns = getPrintStackTrace(e);
    	} finally {
    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				returns = getPrintStackTrace(ex);
    			}
    		}
            if (conn != null) {
    		    try {
    		    	conn.close();
    		    } catch (SQLException ex) {
    		    	returns = getPrintStackTrace(ex);
    		    }
            }
	return returns;
    	}
    }
    
    // 금고 자이로 센서
    public String safetyGyro(String gyro_x, String gyro_y, String gyro_z, String user, String equ, String state) {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
    		String x_value = "";
		String y_value = "";
		String z_value = "";
	
		// DB에 저장되어 있는 마지막 x값 조회	
		sql = "SELECT X_INDEX_VALUE AS PRE_X_IDX_VAL FROM gyro_sensor ORDER BY MOD_TIME DESC LIMIT 1";
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while( rs.next()){
		     x_value = rs.getString("PRE_X_IDX_VAL");
		};
		
		// DB에 저장되어 있는 마지막 Y값 조회
		sql = "SELECT Y_INDEX_VALUE AS PRE_Y_IDX_VAL FROM gyro_sensor ORDER BY MOD_TIME DESC LIMIT 1";
                pstmt = conn.prepareStatement(sql);
                rs = pstmt.executeQuery();
                while( rs.next()){
	             y_value = rs.getString("PRE_Y_IDX_VAL");
                };

		// DB에 저장되어 있는 마지막 Z값 조회
		sql = "SELECT Z_INDEX_VALUE AS PRE_Z_IDX_VAL FROM gyro_sensor ORDER BY MOD_TIME DESC LIMIT 1";
		pstmt = conn.prepareStatement(sql);
		rs = pstmt.executeQuery();
		while( rs.next()){
	 	     z_value = rs.getString("PRE_Z_IDX_VAL");
		};
		
		// 자로형 변환
		int int_x = Integer.parseInt(x_value);	
		int int_y = Integer.parseInt(y_value);
		int int_z = Integer.parseInt(z_value);
		int input_x = Integer.parseInt(gyro_x);
		int input_y = Integer.parseInt(gyro_y);
		int input_z = Integer.parseInt(gyro_z);
		
		// gyro_x 
		if( Math.abs( input_x - int_x ) >= 3000 ){

			state = "2";
		}else if( Math.abs( input_x - int_x ) >=1000 ){

			state = "1";
		}else if( Math.abs( input_y - int_y ) >= 3000 ){

                        state = "2";
                }else if( Math.abs( input_y - int_y ) >=1000 ){

                        state = "1";
                }else if( Math.abs( input_z - int_z ) >= 3000 ){

                        state = "2";
                }else if( Math.abs( input_z - int_z ) >=1000 ){

                        state = "1";
                };
		
		// 장비명, 값
                sql = "INSERT INTO gyro_sensor( X_INDEX_VALUE, Y_INDEX_VALUE, Z_INDEX_VALUE, PUBLIC_SEQ, EQU_SEQ, STATUS, MOD_TIME) VALUES(?, ?, ?, ?, ?, ?, NOW())";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, gyro_x);
                pstmt.setString(2, gyro_y);
                pstmt.setString(3, gyro_z);
                pstmt.setString(4, user);
                pstmt.setString(5, equ);
                pstmt.setString(6, state);
                rsrs = pstmt.executeUpdate();

		returns = x_value; 
    	} catch (Exception e) {
    		returns = getPrintStackTrace(e);
    	} finally {
    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				returns = getPrintStackTrace(ex);
    			}
    		}
            if (conn != null) {
    		    try {
    		    	conn.close();
    		    } catch (SQLException ex) {
    		    	returns = getPrintStackTrace(ex);
    		    }
            }
	return returns;
    	}
    }
    
    // 금고 적외선 센서
    public String safetyInfra(String infra, String user, String equ, String state) {
    	try {
    		Class.forName("com.mysql.jdbc.Driver");
    		conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
    			
                // 장비명, 값
    		sql = "INSERT INTO infrared_sensor( VALUE, PUBLIC_SEQ, EQU_SEQ, STATUS, MOD_TIME) VALUES(?, ?, ?, ?, NOW())";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, infra);
		pstmt.setString(2, user);
		pstmt.setString(3, equ);
		pstmt.setString(4, state);
    		rsrs = pstmt.executeUpdate();
    			
                // 조회값 여부
                if ( rsrs != 0) {
                    returns = "HelloDaddy";
                } else {
                    returns = "fail";
                }
    	} catch (Exception e) {
    			returns = getPrintStackTrace(e);
    	} finally {
    		if (pstmt != null) {
    			try {
    				pstmt.close();
    			} catch (SQLException ex) {
    				returns = getPrintStackTrace(ex);
    			}
    		}
            if (conn != null) {
    		    try {
    		    	conn.close();
    		    } catch (SQLException ex) {
    		    	returns = getPrintStackTrace(ex);
    		    }
            }
	return returns;
    	}
    }
    
    public static String getPrintStackTrace(Exception e) {
         
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
         
        return errors.toString();
         
    }
};





