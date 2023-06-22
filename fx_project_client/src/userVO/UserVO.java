package userVO;

public class UserVO {

	private String nick;
	private int win;
	private int lose;
	private String now;
	
	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLose() {
		return lose;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public UserVO() {};
	
	public UserVO(String nick, int win, int lose, String now) {
		super();
		this.nick = nick;
		this.win = win;
		this.lose = lose;
		this.now = now;
	}
	
	public UserVO(String nick, int win, int lose) {
		super();
		this.nick = nick;
		this.win = win;
		this.lose = lose;
	}
	
	/**
	 * @return the now
	 */
	public String getNow() {
		return now;
	}

	/**
	 * @param now the now to set
	 */
	public void setNow(String now) {
		this.now = now;
	}

	public UserVO(String now) {
		this.now = now;
	}
	
}
