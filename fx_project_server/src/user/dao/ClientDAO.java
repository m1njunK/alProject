package user.dao;

import java.util.ArrayList;
import java.util.Vector;

import user.vo.BackUpClientVO;
import user.vo.ClientVO;

public interface ClientDAO {
	
	// 회원가입
	// 회원정보를 저장하고 있는 client를 넘겨받아서
	// 회원정보를 테이블에 추가하고 <<등록된 회원 정보를 반환>>
	ClientVO join(ClientVO account);
	
	// 회원 검색
	// mId , mPw가 일치하는 사용자 검색
	ClientVO selectClient(String Id, String Pw);
	
	// 번호로 로 검색하기
	ClientVO selectClient(int cNum);
	
	// 아이디가 기존에 존재하는지 확인
	boolean selectClient(String Id);
	
	// 회원 목록 검색
	Vector<ClientVO> select();
	
	// 회원정보 수정
	int update(ClientVO account);
	
	// 회원 탈퇴 - 회원 정보 삭제
	int delete(int cNum);
	
	// 탈퇴한 회원 목록 
	ArrayList<BackUpClientVO> deleteMember();
	
	ClientVO nickSearch(String nick);
}

















