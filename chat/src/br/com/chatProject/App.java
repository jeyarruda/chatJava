package br.com.chatProject;

import java.io.IOException;

import javax.swing.JOptionPane;

@SuppressWarnings("unused")
public class App {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		String username = JOptionPane.showInputDialog("Informe seu apelido: ");
		User user = new User(username, "192.168.0.22");
		
		user.logIn();
	}

}
