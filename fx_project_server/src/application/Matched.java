package application;

import java.util.Hashtable;
import java.util.Vector;

public class Matched extends Vector<GamingUser> implements Runnable {

	public boolean nowGaming = false;
	ServerController sc;
	
	public Matched(ServerController sc) {
		this.sc = sc;
	}
	
	@Override
	public void run() {
		this.nowGaming = true;
	}
	
	
	public void gameEnd() {
		this.removeAllElements();
		sc.removeEmptyRoom();
	}

	@Override
	public String toString() {
		String result = ""; 
		for(GamingUser account : this ) {
			result += account.loginAccount.getNick() + " : ";
		}
		return result;
	}
	
	
	
	
	
	
	
	
}
