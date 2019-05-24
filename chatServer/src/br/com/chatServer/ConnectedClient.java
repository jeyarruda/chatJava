package br.com.chatServer;

public class ConnectedClient {
	private String nickname;
	private String ip;
	
	
	public ConnectedClient(String nickname, String ip) {
		super();
		this.nickname = nickname;
		this.ip = ip;
	}
	
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	} 
	
	
}
