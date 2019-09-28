package com.example.androidchatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static final int SocketServerPORT = 8080;

	TextView infoIp, infoPort, chatMsg;

	String msgLog = "";

	List<ChatClient> userList;

	ServerSocket serverSocket;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoIp = (TextView) findViewById(R.id.infoip);
		infoPort = (TextView) findViewById(R.id.infoport);
		chatMsg = (TextView) findViewById(R.id.chatmsg);

		infoIp.setText(getIpAddress());

		userList = new ArrayList<ChatClient>();

		new ChatServer();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class ChatServer extends Thread {

		public ChatServer() {
			start();
		}

		@Override
		public void run() {
			Socket socket = null;

			try {
				serverSocket = new ServerSocket(SocketServerPORT);
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						infoPort.setText("I'm waiting here: "
								+ serverSocket.getLocalPort());
					}
				});

				while (true) {
					socket = serverSocket.accept();
					ChatClient client = new ChatClient(socket);
					userList.add(client);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

	}

	private class ChatClient extends Thread {

		Socket socket;
		String name;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;

		ChatClient(Socket socket) throws IOException {
			this.socket = socket;
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			start();
		}

		@Override
		public void run() {

			try {

				//sendMsg("Welcome!");
				// name = dataInputStream.readUTF();

				msgLog += name + " connected@" + socket.getInetAddress() + ":"
						+ socket.getPort() + "\n";
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						chatMsg.setText(msgLog);
					}
				});

				broadcastMsg(name + " join our chat.\n");

				while (true) {
					if (dataInputStream.available() > 0) {
						String newMsg = dataInputStream.readUTF();

						msgLog += name + ": " + newMsg;
						MainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								chatMsg.setText(msgLog);
							}
						});

						broadcastMsg(name + ": " + newMsg);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (dataInputStream != null) {
					try {
						dataInputStream.close();
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

				userList.remove(this);
				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MainActivity.this, name + " removed.",
								Toast.LENGTH_LONG).show();

						msgLog += "-- " + name + " leaved\n";
						MainActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								chatMsg.setText(msgLog);
							}
						});

						broadcastMsg("-- " + name + " leaved\n");
					}
				});
			}

		}

		private void sendMsg(String msg)  {
			try {
				dataOutputStream.write(msg.getBytes());
				dataOutputStream.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

	}

	private void broadcastMsg(String msg) {
		for (ChatClient client : userList) {
			client.sendMsg(msg);
			msgLog += "- send to " + client.name + "\n";
		}

		MainActivity.this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				chatMsg.setText(msgLog);
			}
		});
	}

	private String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
						.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
						.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "SiteLocalAddress: "
								+ inetAddress.getHostAddress() + "\n";
					}

				}

			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}

		return ip;
	}
}