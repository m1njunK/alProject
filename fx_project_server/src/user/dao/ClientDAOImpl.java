package user.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import user.utils.DBUtil;
import user.vo.BackUpClientVO;
import user.vo.ClientVO;


/**
                 ====  mySql =======
			< table명 :   ClientsTable  >

	속성명  id                   순서대로
          pw                  순서대로	
          nick                 순서대로
         (wins)                 순서대로
         (loses)                  순서대로
         number                순서대로
                ====  mySql =======
 * */
public class ClientDAOImpl implements ClientDAO {

	Connection conn;
	Statement stmt;
	PreparedStatement pstmt;
	ResultSet rs;
	//  abstract class DBUtil 	
	//       --> connection Mysql계정 우리껄로 수정 
	
	public ClientDAOImpl() {
		conn = DBUtil.getConnection();
	}
	
	@Override
	public ClientVO join(ClientVO client) {
		// 회원가입
		// 회원정보를 저장하고 있는 client를 넘겨받아서
		// 회원정보를 테이블에 추가하고 <<등록된 회원 정보를 반환>>
		
		String sql = "INSERT INTO ClientsTable VALUES(?,?,?,0,0,NULL)";
		try {
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, client.getId());
			pstmt.setString(2, client.getPw());
			pstmt.setString(3, client.getId());
			
			//pstmt.setLong(4, System.currentTimeMillis());
			// 4 , 5 , 6번 필드 : int    승  패  number
			int result = pstmt.executeUpdate();
			
			if(result == 1) {
				sql = "SELECT * FROM ClientsTable WHERE id = '" +client.getId()+"'";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				
				if(rs.next()) {
					client.setId(rs.getString(1));
					client.setPw(rs.getString(2));
					client.setNick(rs.getString(3));
					client.setWins(rs.getInt(4));
					client.setLoses(rs.getInt(5));
					client.setcNum(rs.getInt(6));
					return client;
					//가입완료 
				}
			}
			
		} catch (SQLException e) {
			
		}finally {
			DBUtil.close(rs,stmt,pstmt);
		}
		return null;
	}

	@Override // mId , mPw가 일치하는 사용자 검색
	public ClientVO selectClient(String id, String pw) {
		ClientVO client = null;
		String sql = "SELECT * FROM ClientsTable WHERE id = ? AND pw = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
		
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// id   pw   nick   win    lose    num  
				client = new ClientVO(
						rs.getString("id"),			// 1
						rs.getString("pw"),		// 2
						rs.getString("nick"),		// 3
						rs.getInt(4),		// 4
						rs.getInt(5),
						rs.getInt(6)
						
				);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(rs,pstmt);
		}
		
		return client;
	}

	@Override
	public ClientVO selectClient(int cNum) {
		
		ClientVO client = null;
		
		String sql = "SELECT * FROM ClientsTable WHERE number = " + cNum;
		System.out.println(sql);
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		
								// table  str,str,str,int,int,int
			if(rs.next()) {
				client = new ClientVO(rs.getString(1),
						rs.getString(2),
						rs.getString(3),
						rs.getInt(4),
						rs.getInt(5),
						rs.getInt(6));
			}
			
		} catch (SQLException e) {
			
		}finally {
			DBUtil.close(rs,stmt);
		}
		
		return client;
	}

	@Override
	public boolean selectClient(String id) {
		boolean isChecked = true;
		// 트루 
		try {
			pstmt = conn.prepareStatement("SELECT * FROM ClientsTable WHERE id = ?");
			pstmt.setString(1, id);
			// "SELECT * FROM tbl_member WHERE mId = mId;
			rs = pstmt.executeQuery();
			if(rs.next()) {
				isChecked = false;
			}
		// 이미 있으면 false - > 생성 불가  	
		} catch (SQLException e) {
			e.printStackTrace();
			isChecked = false;
		}finally {
			
			DBUtil.close(rs,pstmt);
			
			/*
			try {
				if(rs != null) rs.close();
			} catch (SQLException e) {}
			
			try {
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {}
			*/
			
		}
		
		return isChecked;
	}

	@Override
	public Vector<ClientVO> select() {
		Vector<ClientVO> list = new Vector<>();
		// 번호 -> id 순서로 정렬하도록 수정함 
		String sql = "SELECT * FROM ClientsTable ORDER BY number";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				
				ClientVO client = new ClientVO();
				String id = rs.getString(1);
				String pw = rs.getString(2);
				String nick = rs.getString(3);
				int win = rs.getInt(4);
				int lose = rs.getInt(5);
				int cNum = rs.getInt(6);
				
//				member.setmNum(mNum);
//				member.setmName(mName);
//				member.setmId(mId);
//				member.setmPw(mPw);
//				member.setReg(reg);
//				list.add(member);
				
			}
			
		} catch (SQLException e) {}
		finally {
			DBUtil.close(rs,stmt);
		}
		
		return list;
	}

	@Override
	public int update(ClientVO client) {
		int result = 0;
		
		String sql = "UPDATE ClientsTable SET wins=?, loses=? WHERE id = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, client.getWins());
			pstmt.setInt(2, client.getLoses());
			pstmt.setString(3, client.getId());
			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(pstmt);
		}
		
		return result;
	}

	@Override
	public int delete(int cNum) {
		
		// 탈퇴 요청한 회원 번호 mNum
		// 탈퇴 요청한 회원 정보를 검색해서 back_up_member 테이블에 등록 시킨 후
		// tbl_member 활성 회원 테이블에서 삭제
		
		int result = 0;
		
		ClientVO deleteClient = selectClient(cNum);
													//   총   7개 
		String sql = "INSERT INTO backupClient VALUES(?,?,?,?,?,?,now());";
		
		try {
			conn.setAutoCommit(false); 
			//백업 테이블 추가와 기존 테이블에서 삭제를 한가지 트랜잭션으로 묶어야 
			//중복수행 오류 발생하지 않음
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, deleteClient.getId());
			pstmt.setString(2, deleteClient.getPw());
			pstmt.setString(3, deleteClient.getNick());
			pstmt.setInt(4, deleteClient.getWins());
			pstmt.setInt(5, deleteClient.getLoses());
			pstmt.setInt(6,deleteClient.getcNum());
			
			
			result = pstmt.executeUpdate();
			
			if(result == 1) {
				
				sql = "DELETE FROM ClientsTable WHERE mNum = "+cNum;
				stmt = conn.createStatement();
				result = stmt.executeUpdate(sql);
				conn.commit();
				// 실행 성공 
			}else {
				conn.rollback();
			}
			
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			DBUtil.close(pstmt,stmt);
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {e.printStackTrace();}
		}
		
		return result;
	}

	@Override
	public ArrayList<BackUpClientVO> deleteMember() {

		ArrayList<BackUpClientVO> deletes = new ArrayList<>();
		
		String sql = "SELECT * FROM back_up_member ORDER BY deleteDate DESC";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

//			while(rs.next()) {
//				BackUpClientVO vo = new BackUpClientVO(
//						rs.getInt(1),			// mNum
//						rs.getString(2),		// mName
//						rs.getString(3),		// mId
//						rs.getString(4),		// mPw 	
//						rs.getLong(5),			// reg
//						rs.getTimestamp(6)		// deleteDate
//						);
//				deletes.add(vo);
//			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(rs,stmt);
		}
		
		
		return deletes;
	}

	public ClientVO nickSearch(String nick) {
		ClientVO client = null;

		String sql = "SELECT * FROM ClientsTable WHERE nick = " +"'"+nick+"'";
		System.out.println(sql);

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			// table str,str,str,int,int,int
			if (rs.next()) {
				client = new ClientVO(
						rs.getString(1), 
						rs.getString(2), 
						rs.getString(3), 
						rs.getInt(4), 
						rs.getInt(5),
						rs.getInt(6));
				}

		} catch (SQLException e) {

		} finally {
			DBUtil.close(rs, stmt);
		}

		return client;
	}
	

	

}
