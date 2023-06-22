package user.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * database 반복 작업 수행 class
 *
 */

public abstract class DBUtil {
	
	private static Connection conn;
	
	public static Connection getConnection() {
		
		if(conn == null) {
			
			try {
				conn = DriverManager.getConnection(
						"jdbc:mysql://10.100.205.175/project",
						"root",
						"12345"
				);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}		
		return conn;
	}
	
	// 사용된 자원 해제
	public static void close(AutoCloseable... closers) {
		
		for(AutoCloseable closer : closers) {
			if(closer != null) {
				try {
					closer.close();
				} catch (Exception e) {}
			}
		}
	}
	
}
