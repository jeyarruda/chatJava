package br.com.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PublicServer {
	private ServerSocket serverSocket; 
	final int PORTreceive = 6667;
	final int PORTsend = 6666;
	private ArrayList <ConnectedClient> clientList = new ArrayList<>();
	
	public PublicServer() throws IOException {
		System.out.println("Iniciando servidor");
		serverSocket = new ServerSocket(PORTreceive); 
	}
	
	public void runServer() {
		while(true) {
			try {
				Socket socketListener = serverSocket.accept();
				String ipClient = socketListener.getInetAddress().toString().substring(1);
				int cont = 0;
				for(ConnectedClient client:clientList) {
					if(!client.getIp().equals(ipClient)) {
						cont++;
					}
				}
				
				InputStreamReader streamReader = new InputStreamReader(socketListener.getInputStream());
				BufferedReader reader = new BufferedReader(streamReader);
				String sentMessage = reader.readLine();
				String [] worda = sentMessage.split(" ");
				String nick = worda[0];
				if(cont == clientList.size()) {
					String [] words = sentMessage.split(" ");
					String nome = "@"+words[0];
					
					ConnectedClient user = new ConnectedClient(nome, ipClient);
					clientList.add(user);
					System.out.println("Lista de usuários: ");
					for(ConnectedClient client:clientList) {
						System.out.println(client.getIp() + " - " +client.getNickname());
					}
				}
				String [] pureMessage = sentMessage.split(sentMessage.split(" ")[0]+" disse: "); 
				//LOG MESSAGE ENCRYPTED
				System.out.println("[LOG] "+sentMessage);
				if (pureMessage.length > 0) {
					if(pureMessage[1].equals("exit()")) {
						for(int i = 0; i < clientList.size(); i++) {
							if(clientList.get(i).getNickname().equals("@"+sentMessage.split(" ")[0])) {
								clientList.remove(i);
							}
						}
					}
					String finalMessage = AES.decrypt(pureMessage[1], "abc");
					verifyAndSend(finalMessage, nick);
				}
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}		
	}
	
	
	@SuppressWarnings("resource")
	public void verifyAndSend(String message, String sender) throws UnknownHostException, IOException {
		if(message.equals("list()")) {
			String finalMessage = "Lista de usuários online:  ";
			for(ConnectedClient client:clientList) {
				finalMessage+=client.getNickname()+"    ";
				Socket userSocket = new Socket(client.getIp(), PORTsend);
				PrintWriter writer = new PrintWriter(userSocket.getOutputStream());
				writer.write(finalMessage);
				writer.close();
			}
		}else {
			int msgSended = 0;
			for(ConnectedClient client:clientList) {
				if(message.contains(client.getNickname())) {
					msgSended++; 
					Socket userSocket = new Socket(client.getIp(), PORTsend);
					PrintWriter writer = new PrintWriter(userSocket.getOutputStream());
					
					
					String messageFinalUncrypted = sender+" disse para você: "+message; 
					String messageFinalCrypted = AES.encrypt(messageFinalUncrypted, "def");
							
							
					writer.write(messageFinalCrypted);
					writer.close();
				}
			}
			if(msgSended == 0) {
				for(ConnectedClient client:clientList) {
					if(!message.equals("exit()")) {
						Socket userSocket = new Socket(client.getIp(), PORTsend);
						PrintWriter writer = new PrintWriter(userSocket.getOutputStream());
						String messageFinalUncrypted = (sender+" disse para todos: "+message);
						String messageFinalCrypted = AES.encrypt(messageFinalUncrypted, "def");
						writer.write(messageFinalCrypted);
						writer.close();
					}else {
						Socket userSocket = new Socket(client.getIp(), PORTsend);
						PrintWriter writer = new PrintWriter(userSocket.getOutputStream());
						writer.write(AES.encrypt((sender + " saiu do chat ..."), "def"));
						writer.close();
					}
				}
			}
		}
	}
}
