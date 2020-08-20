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
    String returns = "";
    int rsrs;
    
    // 금고 서보모터(개폐여부)
    public String safetyOpen(String open_close, String user, String equ, String state) {
    	try {
    			Class.forName("com.mysql.jdbc.Driver");
    			conn = DriverManager.getConnection(jdbcUrl, dbId, dbPw);
    			
                // 장비명, 값
    			sql = "INSERT INTO open_close_status(VALUE, PUBLIC_SEQ, EQU_SEQ, STATUS, MOT_TIME , OPEN_TIME , CLOSE_TIME ) VALUES(?, ?, ?, ?, NOW(), NOW(), NOW())";
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
    			
                // 장비명, 값
    			sql = "INSERT INTO oscillation_sensor(VALUE, PUBLIC_SEQ, EQU_SEQ, STATUS, MOD_TIME) VALUES(?, ?, ?, ?, NOW())";
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
    
    public static String getPrintStackTrace(Exception e) {
         
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
         
        return errors.toString();
         
    }
};





