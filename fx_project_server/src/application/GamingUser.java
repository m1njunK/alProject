package application;

import java.io.PrintWriter;
import java.util.Hashtable;

import user.vo.ClientVO;

public class GamingUser  {

	public ClientVO loginAccount; 
	
	public PrintWriter ServerOutput;
	
	public boolean host; 
	
	
	public GamingUser(ClientVO loginAccount, PrintWriter serverOutput) {
		this.loginAccount = loginAccount;
		this.ServerOutput = serverOutput;
	}

	@Override
	public String toString() {
		return this.loginAccount.getNick();
	}

	//
	public boolean isHost() {
		return host;
	}

	
	public void setHost(boolean host) {
		this.host = host;
	} 
	//
	 
	
	
	
	
	
	
	
	
}
