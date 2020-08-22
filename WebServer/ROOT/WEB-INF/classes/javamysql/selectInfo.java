package javamysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class selectInfo {
	
	private static selectInfo instance = new selectInfo();

	public static selectInfo getInstance(){
		return instance;
	}
	
	public selectInfo(){
	};

	private String url = "jdbc:mysql://localhost:3306/kpuiot";
	private String id = "root";
	private String pw = "950817";
	private Connection con = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	private String sql = "";
	String result = "";
	int rsrs = 0;

	public String selectSafetyInfo(String equ_seq, String public_seq, String state){
		try{	
			// 변수명 선언
			String open_close_status = "";
			String gyro_status = "";
			String infra_status = "";
			String oscill_status = "";
			String open_status = "";			
	
			// method 
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, id, pw);

			// First Setting
			sql = "SELECT VALUE FROM open_close_status";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			// OPEN_CLOSE
			while( rs.next() ){
				open_close_status = rs.getString("VALUE");	
			};
			
			// Second Setting
                        sql = "SELECT STATUS FROM gyro_sensor";
                        pstmt = con.prepareStatement(sql);
                        rs = pstmt.executeQuery();

                        // GYRO
                        while( rs.next() ){
                                gyro_status = rs.getString("STATUS");
                        };

			// Third Setting
                        sql = "SELECT STATUS FROM infrared_sensor";
                        pstmt = con.prepareStatement(sql);
                        rs = pstmt.executeQuery();

                        // INFRARED
                        while( rs.next() ){
                       		infra_status = rs.getString("STATUS");
                        };

			// forth Setting
                        sql = "SELECT STATUS FROM oscillation_sensor";
                        pstmt = con.prepareStatement(sql);
                        rs = pstmt.executeQuery();

                        // OSCILLATION
                        while( rs.next() ){
                                oscill_status = rs.getString("STATUS");
                        };
			
			// Insert Setting
                        sql = "INSERT INTO status_change( equ_seq, public_seq, gyro_status, infrared_status, oscillation_status, oac_status, status, mot_time ) VALUES( ?, ?, ?, ?, ?, ?, ?, NOW())";
                        pstmt = con.prepareStatement(sql);
			pstmt.setString(1, equ_seq);
			pstmt.setString(2, public_seq);
			pstmt.setString(3, gyro_status);
			pstmt.setString(4, infra_status);
			pstmt.setString(5, oscill_status);
			pstmt.setString(6, open_close_status);
			pstmt.setString(7, state);
                        rsrs = pstmt.executeUpdate();
			
			// 금고에서 조회할 문 개폐여부
			sql = "SELECT STATUS FROM equ_info WHERE EQU_SEQ = (?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, equ_seq);
			rs = pstmt.executeQuery();

			while( rs.next() ){
				open_status = rs.getString("STATUS");
			};
                        // open_close_status
			result = open_status;
			

		}catch(Exception e){
			result = getPrintStackTrace(e);
		}finally{
			if(pstmt!=null){
				try{
					pstmt.close();
				}catch(SQLException ex){
					result = getPrintStackTrace(ex);
				}
			}
		return result;
		}
	};
	public static String getPrintStackTrace(Exception e) {

        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        return errors.toString();

    }
};
