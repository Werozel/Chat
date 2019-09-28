import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


 class ChatServer extends Thread {

		

		final int SocketServerPORT = 8080;

		String msgLog = "";

		ArrayList<ServerClient> userList;

		ServerSocket serverSocket;
		
		public ChatServer() {
			start();
		}

		public static void main(String args[]) {
			new ChatServer();
		}
		
		@Override
		public void run() {
			
			userList = new ArrayList<ServerClient>();
			Socket socket = null;

			try {
				serverSocket = new ServerSocket(SocketServerPORT);

				System.out.println("I'm waiting here:\n" + getIpAddress() + "Port: " + 
						+ serverSocket.getLocalPort());

				while (true) {
					socket = serverSocket.accept();
					ServerClient client = new ServerClient(this, socket);
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
		
		void remove(ServerClient client)
		{

			System.out.println(client.name + " leaved\n");
			userList.remove(client);
			broadcastMsg(client.name + " leaved\n");
		}

	   void broadcastMsg(String msg) {
			String msgLog = "";
			for (ServerClient client : userList) {
				client.sendMsg(msg);
				msgLog += "- send to " + client.name + "\n";
			}
			System.out.println(msgLog);
		}
		
	    String getIpAddress() {
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
							ip += "IP: "
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