package com.example.androidchatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient extends Thread {
	
	MainActivity activity;

	Socket socket = null;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;


	String name;
	String dstAddress;
	int dstPort;


	boolean goOut = false;

	ChatClient(MainActivity activity, String name, String address, int port)
			throws UnknownHostException, IOException {
		this.activity = activity;
		this.name = name;
		dstAddress = address;
		dstPort = port;
		
	}

	void sendMsg(String msg) throws IOException {
		dataOutputStream.writeUTF(msg);
		dataOutputStream.flush();
	}

	@Override
	public void run() {
		
		
		try {

			socket = new Socket(dstAddress, dstPort);
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			dataInputStream = new DataInputStream(socket.getInputStream());
			
			sendMsg(name);

			while (!goOut) {
				if (dataInputStream.available() > 0) {
					String msg = dataInputStream.readUTF();
				    activity.showMessage(msg); 	
				}
			}
			

		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
			final String eString = e.toString();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
	
	public void disconnect()
	{
		goOut = true;
	}

}
