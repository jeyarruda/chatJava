package br.com.chatProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class User {
	private String nickname; 
	private String hostIp;
	private String serverIp;
	
	final int PORTsend = 6667; 
	final int PORTreceive = 6666;
	@SuppressWarnings("unused")
	private static ServerSocket serverSocket; 
	private static Socket clientSocket; 
	private static boolean logout = false ;
	
	public User (String nickname, String serverIp) throws IOException{
		this.nickname = nickname;
		this.serverIp = serverIp;
		serverSocket = new ServerSocket(PORTreceive);
	}
	
	public void logIn() throws Exception{
		System.out.println(nickname + " entrou no chat... ");
		new Thread(server).start();
		while(!logout) {
			String message = JOptionPane.showInputDialog("Digite sua mensagem ou exit() para sair: ");
			String msgKey = AES.encrypt(message, "abc");
			sendMessage(msgKey);
			if(message.equals("exit()")) {
				setLogout(true);
			}
		}
		
	}
	
	public void sendMessage(String message) throws Exception{
		clientSocket = new Socket(serverIp, PORTsend); 
		PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
		writer.write(nickname+" disse: "+message+"\n");
		writer.close();
	}
	
	
	public static void setLogout(boolean logoutBool) {
		logout = logoutBool;	
	}
	
	public String getNickname() {
		return nickname;
	}
	
	
	
	private static Runnable server = new Runnable() {

		@Override
		public void run() {
			while(true) {
				try {
					Socket socketListener = serverSocket.accept();
					InputStreamReader streamReader = new InputStreamReader(socketListener.getInputStream());
					BufferedReader reader = new BufferedReader(streamReader);
					String sentMessage = reader.readLine();
					String decryptedMessage = AES.decrypt(sentMessage,"def");
					System.out.println("[SERVIDOR CLIENTE] "+decryptedMessage);
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}			
		}
		
	};
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	
	
	public String getHostIp() {
		return hostIp;
	}
	
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	} 

}
