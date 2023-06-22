package user.vo;

public class ClientVO {
	
	/**
	 * 인자2 생성자 -> 닉네임 설정 후 인자3 생성자 
	 * */
	// 유저 번호
	private int cNum;
	
	private String nick; // 닉네임 : 가입 후 만들기 
	// 회원 아이디
	private String Id;   //아이디 (가입시)
	// 회원 비밀번호
	private String Pw;	// pw (가입시)
	
	private int wins = 0 ; 
	private int loses = 0 ; 
	
	private String nowGaming = " 대기실";
	
	public void nowGaming(boolean isGaming) {
		if(isGaming == true) { this.nowGaming = " 게임 중";}
		else this.nowGaming = " 대기실";
	}
	
	public ClientVO() {}
	// 회원가입 단계 생성자 
	public ClientVO(String id, String pw) { 
		Id = id;
		Pw = pw;
	}

	// 회원가입 단계에서 닉네임 추가  , id, pw는 
	public ClientVO(String id, String pw,String nick) {
		this.Id = id;
		this.Pw = pw;
		this.nick = nick;
	}
		
	public ClientVO(String nick, String id, String pw, int wins, int loses, int cNum) {
		this.cNum = cNum;
		this.nick = nick;
		this.Id = id;
		this.Pw = pw;
		this.wins = wins;
		this.loses = loses;
	}
	
	public String commaString() {
		return "          " +this.nick+","+this.wins +","+this.loses+",       "+this.nowGaming;
				
	}

	
	
	///// --------------gt st  
	public int getcNum() {
		return cNum;
	}
	public void setcNum(int cNum) {
		this.cNum = cNum;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getPw() {
		return Pw;
	}
	public void setPw(String pw) {
		Pw = pw;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public int getLoses() {
		return loses;
	}
	public void setLoses(int loses) {
		this.loses = loses;
	}
	// 회원정보의 mId field와 mPw field가 일치하면 동일한 객체로 
	// 인식할 수 있도록 재정의
	@Override
	public boolean equals(Object o) {
		if(o instanceof ClientVO) {
			ClientVO c = (ClientVO)o;
			if(this.Id.equals(c.getId())  && this.Pw.equals(c.getPw())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public String toString() {
		return "ClientVO [cNum=" + cNum + ", nick=" + nick + ", Id=" + Id + ", Pw=" + Pw + ", wins=" + wins + ", loses="
				+ loses + "]";
		}
	}

// 자신이 가지고 있는  long type의 값을  2020-10-23 12:11:21 형식의 문자열이 반환 하도록 변경
// SimpleDateFormat 객체 활용
//public String getReg() {
//	SimpleDateFormat sdf 
//		= new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//	String time = sdf.format(new Date(this.reg));
//	return time;
//}






//// long 타입에 시간 반환
//public long getRealReg() {
//	return this.reg;
//}

// System의 현재시간을 m/s으로 전달 받아 저장
//public void setReg(long reg) {
//	this.reg = reg;
//}
	
