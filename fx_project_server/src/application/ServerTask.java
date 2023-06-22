package application;

/**
 * daoImpl -> true false 체크 
 * 
 * */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import user.vo.ClientVO;

public class ServerTask implements Runnable {
	Codes codes;
	// 현재 task에 연결된 client socket 정보
	static Socket client;

	// 유일 인스턴스 서버컨트롤러 :: methods: serverCurrent,
	ServerController sc;
	// 필드: rooms userWaiting userAll
	// IO Stream------------------
	PrintWriter serverOutput;
	BufferedReader serverInput;
	GamingUser gamingUser;
	// ObjectOutputStream 추가
	ObjectOutputStream userVoOutPut;
	ClientVO loginAccount = null;
	// 정지플래그
	boolean isRun = true;

	// ---------------------constructor

	// 넘겨 받을 재원은 client 소켓과 server의 필드 이용 가능
	// 1:1 소켓스트림 생성

	// 작업(송수신)이 생성된 후 역할을 할 수 있도록 재원 초기화 하는 구간
	public ServerTask(Socket client, ServerController sc) {
		this.client = client;
		this.sc = sc;
		try {
			serverOutput = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			serverInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			// userVOoutPut = new ObjectOutputStream();
			userVoOutPut = new ObjectOutputStream(new FileOutputStream(new File("seri.ser")));
		} catch (IOException e) {
			sc.serverCurrent("연결X" + e.getMessage());
		}
		// 1:1 소켓 생성 완료

		// 수신 시작. 이때 클라이언트 화면은 Main에 존재함.
	} // Constructor End

	// 작업을 정의한다. ( 1.생성자 함수로 스트림 ON
	// 2. 메시지 수신을 한다. )

	// 조건부 반복문을 담고 있다.
	@Override
	public void run() { // run Pool.submit(ServerTask.this) -> 실행되는 메소드

		sc.serverCurrent("serverTask run ON, 수신 중");
		codes = new Codes();

		// System.out.println("readLine완료");
		// 메시지는 코드와 함께 작성되어 있다. 
		// userMsg 전송 방식: 
		//(ToServer(String msg) == Code code = new Code(), println(code.코드 + contendts))

		while (true) {
			try {

				String userMsg = serverInput.readLine();
				sc.serverCurrent(userMsg);
				if(userMsg == null) {
					sc.serverCurrent("널널널");
					break;
				}
				String[] code = userMsg.split("\\|");
				System.out.println("스플릿");
				// code[0] = code
				String content = code[1];
				
				if (code[0].equals(codes.join)) {
					join(content);
				} else if (code[0].equals(codes.log)) {
					login(content);
				} else if (code[0].equals(codes.matching)) {
		               sc.serverCurrent("지금 유저가 매칭함");
		               loginAccount.nowGaming(true);
		               sendListToLoginUser();
		               GamingUser callUser = new GamingUser(this.loginAccount, this.serverOutput);
		               sc.serverCurrent(callUser.toString() + "게임 객체가 완성되었습니다.");
		               match(callUser);
	            }else if (code[0].equals(codes.waitChat)) {
					waitChat("waitChat"+content); 
				} else if (code[0].equals(codes.idCheck)) {
					idCheck(content);
				} else if (code[0].equals(codes.logOut)) {
					logOut(content); 
					sendListToLoginUser();
				} else if (code[0].equals(codes.roomChat)) {
					if(content.startsWith("/전적")) {
						sc.serverCurrent("실행은됐나?");
						String id = content.replace("/전적", "").trim();
						sc.serverCurrent("id : " + id);
						ClientVO vo = sc.dao.nickSearch(id);
						if(vo != null) {
							sc.serverCurrent(vo.getNick());
							roomOwnChat("'"+vo.getNick()+"'님의 전적은 ["+vo.getWins()+"승"+vo.getLoses() +"패]입니다.");
						} else {
							gamingUser.ServerOutput.println("roomChat"+id+"는 존재하지 않아...");
						}
					} else {
						roomChat("roomChat" + this.loginAccount.getNick() + " : " + content); // 끗
					}
				} else if (code[0].equals(codes.win)) {
					win(content);
					sendListToLoginUser();
				} else if (code[0].equals(codes.lose)) {
					lose(content); 
					sendListToLoginUser();
				} else if (code[0].contains("moveBlack")) {
					String xy[] = content.split(",");
					moveBlack(xy[0],xy[1],xy[2]); // 끗
				} else if (code[0].equals("moveWhite")) {
					String xy[] = content.split(",");
					moveWhite(xy[0],xy[1],xy[2]); // 끗
				} else if (code[0].equals(codes.turnEnd)) {
					turnEnd(); // 끗
				} else if (code[0].equals("closeRoom")) {
					closeRoom();
					sendListToLoginUser();
				} else if (code[0].equals("please")) {
					sendListToLoginUser();
				} else if (code[0].equals("giveUp")) {
					if(findUser(gamingUser).get(0) != gamingUser) {
						findUser(gamingUser).get(0).ServerOutput.println("giveUpWin");
					}else {
						findUser(gamingUser).get(1).ServerOutput.println("giveUpWin");
					}
					lose(content);
					sendListToLoginUser();
				} else {
					sc.serverCurrent("ㅎㅇ!");
				}
			} catch (IOException e) {
				sc.serverCurrent(client.getInetAddress().toString() + " : 연결해제");
				break;
			}
		} // end while
		if (client != null && !client.isClosed()) {
			try {
				client.close();
			} catch (Exception e) {
				sc.serverCurrent("clientClose Exception");
			}
		}
	} // run()

//	private void allUserList() {
//		
//		for(ClientVO c : sc.loginAccountList) {
//			if(c != this.loginAccount) {
//				serverOutput.println("addList,"+c.commaString());
//			}
//		}
//		
//	}
//	
//	private void reUserList() {
//		
//		for(ClientVO c : sc.loginAccountList) {
//			serverOutput.println("addList,"+c.commaString());
//		}
//		
//		for(PrintWriter p : sc.toLoginUser.values()) {
//			if(p != this.serverOutput) {
//				p.println("removeUser|"+loginAccount.getNick());
//				p.println("addList,"+loginAccount.commaString());
//			}
//		}
//		
//	}
	
	private void closeRoom() {
		serverOutput.println("closeRoom");
		sc.rooms.remove(findUser(gamingUser));
		sc.toLoginUser.put(loginAccount.getNick(), serverOutput);
		loginAccount.nowGaming(false);
		gamingUser = null;
	};
	
	private void turnEnd() {
		String who = null;
		for(GamingUser gamingUser : findUser(this.gamingUser)) {
			if(gamingUser != this.gamingUser) {
				who = gamingUser.toString();
				gamingUser.ServerOutput.println("isYourTurn");
			}
		}
		for(GamingUser gamingUser : findUser(this.gamingUser)) {
			gamingUser.ServerOutput.println("roomChat"+who+"님의 차례입니다.");
		}
	}
	
	private void moveBlack(String stoneNum, String x, String y) {
		for(GamingUser gamingUser : findUser(this.gamingUser)) {
			if(gamingUser != this.gamingUser) {
				if(gamingUser != null) {
					gamingUser.ServerOutput.println("moveBlack," + stoneNum + "," + x + "," + y);
				}
			}
		 }
	}
	
	private void moveWhite(String stoneNum,String x, String y) {
		for(GamingUser gamingUser : findUser(this.gamingUser)) {
			if(gamingUser != this.gamingUser) {
				if(findUser(gamingUser).nowGaming) {
					gamingUser.ServerOutput.println("moveWhite," + stoneNum + "," + x + "," + y);
				}
			}
		 }
	}
	
	 public void lose(String content) {
		 loginAccount.nowGaming(false);
		      this.loginAccount.setLoses
		      (this.loginAccount.getLoses()+1);
//		      double rate = (double)this.loginAccount.getWins()
//		            /(this.loginAccount.getWins()+this.loginAccount.getLoses());
		      sc.dao.update(loginAccount);
		      sc.serverCurrent(this.loginAccount.getNick() + "님의 전적 업데이트 완료" + String.valueOf(this.loginAccount.getLoses()));
		      serverOutput.println("lose");
		      sc.toLoginUser.put(loginAccount.getNick(), serverOutput);
//		      return 100*rate;
	 }


	public void win(String content) {
		loginAccount.nowGaming(false);
		this.loginAccount.setWins
	      (this.loginAccount.getWins()+1);
//	      double rate = (double)this.loginAccount.getWins()
//	            /(this.loginAccount.getWins()+this.loginAccount.getLoses());
	      
	      sc.dao.update(loginAccount);
	      sc.serverCurrent(this.loginAccount.getNick() + "님의 전적 업데이트 완료" + String.valueOf(this.loginAccount.getWins()));
	      serverOutput.println("win");
	      sc.toLoginUser.put(loginAccount.getNick(), serverOutput);
	      
	      
//	      return 100*rate;
	}

	private void roomChat(String content) {
		// find(this.loginAccount).get(0).ServerOutput.println(content);// << 리턴이
		// Matched 타입 Matched는 Vector
		for (GamingUser gamingUser : findUser(this.gamingUser)) {
			gamingUser.ServerOutput.println(content);
		}
		
	}

//	@SuppressWarnings("unlikely-arg-type")
	private Matched findUser(GamingUser loginAccount) {
		for (Matched room : sc.rooms) {
			if (room.contains(loginAccount)) {
				return room;
			}
		}
		return null;
	}

	@SuppressWarnings("unlikely-arg-type")
	private void logOut(String content) {
		String temp = loginAccount.getNick();
	
		sc.loginAccountList.remove(loginAccount);
		sc.toLoginUser.remove(loginAccount);
		sc.serverCurrent(loginAccount +"가 로그아웃");
		
		loginAccount = null;
		
		sc.serverCurrent(sc.loginAccountList.toString());
		
		serverOutput.println("logOut");
		
		for(PrintWriter p : sc.toLoginUser.values()) {
			p.println("removeUser|"+temp);
		}
		
	}

	private void waitChat(String content) {

		String chatContents = content;
		sc.serverCurrent(loginAccount.getNick() + "가 채팅함:" + content);
		for (PrintWriter sockets : sc.toLoginUser.values()) {
			sockets.println(loginAccount.getNick() + ": " + chatContents);
		}

	}

	private void idCheck(String id) {
		sc.serverCurrent(id);
		if (!sc.dao.selectClient(id)) {
			// 아래는 Id값 중복으로 인해 생성 불가 상황을 말함
			serverOutput.println("아이디중복");
		} else {
			serverOutput.println("아이디사용가능");
		}
	}

	private void join(String arguments) { // arguments = id + pw
		sc.serverCurrent(arguments);
		String[] id1pw2 = arguments.split(",");

		String Id = id1pw2[0];
		String pw = id1pw2[1];

		if (!sc.dao.selectClient(Id)) {
			// 아래는 Id값 중복으로 인해 생성 불가 상황을 말함
			System.out.println("join, selectClient 아이디 중복");
			serverOutput.println("아이디중복");
		} else {
			ClientVO newclient = sc.dao.join(new ClientVO(Id, pw));
			sc.clientsVoVector.add(newclient);
			serverOutput.println("회원가입완료");
		}
	}

	///////////////////////////////////
	@SuppressWarnings("unlikely-arg-type")
	private void match(GamingUser callUser) {
		// sc.serverCurrent("매치 함수가 작동합니다.");
		System.out.println("매치요청 메소드 on");

		// 일단 대기 화면을 띄움
		// serverOutput.println("match");

		// sc.serverCurrent("Gaming user 생성 완료");

		Matched targetRoom = null;
		// sc.serverCurrent("check");
		sc.serverCurrent("현재 " + sc.rooms.size() + "개의 방이 있습니다");

		// 방이 없어서 만들었다. 아래는 웨이팅 상황
		if (sc.rooms.isEmpty()) {
			
			targetRoom = new Matched(sc);
			sc.serverCurrent("방이 0개라 방 생성 완료");
			targetRoom.add(callUser);
			sc.rooms.add(targetRoom);
			sc.serverCurrent(targetRoom.toString() + "최초 방의 주인");
			sc.serverCurrent("등록 완료!! 지금까지의 방 개수:" + sc.rooms.size());
			sc.toLoginUser.remove(callUser);
			serverOutput.println("gameWait");
			gamingUser = callUser;
			
			return;
		}

		// 아래는 즉시 게임이 시작되는 경우다.
		//serverOutput.println("go");
		for (Matched room : sc.rooms) {
			System.out.println(room + "순회중 . . . ");
			if (room.size() == 1) {
				targetRoom = room;
				targetRoom.add(callUser);
				
				sc.serverCurrent("1명 방 발견해서 들어갔다. 등록은 안 됨");
				sc.serverCurrent("방엔 누가 있어?" + targetRoom.toString());
				sc.serverCurrent("지금까지의 방 개수:" + sc.rooms.size());
				sc.toLoginUser.remove(callUser);
				
				targetRoom.get(0).ServerOutput.println("good");
				targetRoom.get(1).ServerOutput.println("good");
				gamingUser = callUser;
				
				startGame(targetRoom);
				targetRoom.run();
				
				return;
			}
		}

		// 1명인 방을 찾지 못해 방을 만들었다. 아래는 웨이팅 상황
		targetRoom = new Matched(sc);
		targetRoom.add(callUser);
		sc.rooms.add(targetRoom);
		sc.serverCurrent("1명 방이 없어서 만들고 들어갔다.");
		sc.serverCurrent("방엔 누가 있어?" + targetRoom.toString());
		sc.serverCurrent("지금까지의 방 개수:" + sc.rooms.size());
		sc.toLoginUser.remove(callUser);
		serverOutput.println("gameWait");
		gamingUser = callUser;
		return;

	}

	private void startGame(Matched room) {
		// 게임시작,  먼저 들어온 유저(방만든놈이)가 선
		GamingUser black = room.get(0);
		GamingUser white = room.get(1);
		
		black.ServerOutput.println(black.loginAccount.getNick()+"선공");
		white.ServerOutput.println(white.loginAccount.getNick()+"후공");
		
	}
	
	private void login(String con) {

		sc.serverCurrent(con);
		String[] arguments = con.split(",");
		String id = arguments[0];
		String pw = arguments[1];

		// id와 비밀번호가 일치하는지

		// return타입 : ClientVO
		if (sc.dao.selectClient(id, pw) != null) {
			System.out.println("true -> 로그인 성공");
			ClientVO vo = sc.dao.selectClient(id, pw);
			loginAccount = vo;
			sc.loginAccountList.add(this.loginAccount);
			sc.serverCurrent(sc.loginAccountList.size() + "명째 로그인 했습니다.");
			System.out.println(loginAccount.getNick());

			// <key:닉네임과, value : Client향 Socket>
			//sc.loginAccountList.put(loginAccount.getNick(), serverOutput); // 전체
			sc.toLoginUser.put(loginAccount.getNick(), serverOutput); // 게임중X
			// <key:닉네임과, value : Client향 Socket>

			System.out.println(codes.log);
			serverOutput.println(codes.log); // 로그인 성공을 알림 "log"
			for(PrintWriter printUserInfo : sc.toLoginUser.values()) {
//				printUserInfo.println("addList,"+loginAccount.commaString());
				printUserInfo.println("waitChat"+loginAccount.getNick()+"님이 접속하였습니다!");
			}
			sendListToLoginUser();
			//}
		} else {
			serverOutput.println("로그인오류");
			sc.serverCurrent("나 여깄어!");
		}
	}
	
	private void roomOwnChat(String content) {
		// find(this.loginAccount).get(0).ServerOutput.println(content);// << 리턴이
		// Matched 타입 Matched는 Vector
		for (GamingUser gamingUser : findUser(this.gamingUser)) {
			if(gamingUser == this.gamingUser)
			gamingUser.ServerOutput.println("roomChat"+content);
		}

	}
	
	//호출시점:   로그인, win , lose , 매칭 취소()
	private void sendListToLoginUser() {
		for(PrintWriter toLoginUser : sc.toLoginUser.values()) { // 에게 
			for(ClientVO login : sc.loginAccountList) { // 의 정보를 
				toLoginUser.println("removeUser|"+login.getNick());
				toLoginUser.println("addList," +login.commaString()) ;
			}
		}
	}

}
