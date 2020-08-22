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

	public String selectSafetyInfo(){
		try{	
			// 변수명 선언
			String open_close_status = "";
			String gyro_status = "";
			String infra_status = "";
			String oscill_status = "";
			
			// method 
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, id, pw);
			sql = "SELECT STATUS FROM open_close_status";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			// result
			while( rs.next() ){
				open_close_status = rs.getString("STATUS");	
			};
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
