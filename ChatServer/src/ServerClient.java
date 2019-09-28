import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerClient extends Thread {

		Socket socket;
		String name;
		DataInputStream dataInputStream = null;
		DataOutputStream dataOutputStream = null;
		ChatServer server;

		ServerClient(ChatServer server, Socket socket) throws IOException {
			this.socket = socket;
			this.server = server;
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			start();
		}

		@Override
		public void run() {

			try {

				sendMsg("Welcome!");
				name = dataInputStream.readUTF();

				System.out.println(name + " connected@"
						+ socket.getInetAddress() + ":" + socket.getPort()
						+ "\n");

				server.broadcastMsg(name + " join our chat.\n");

				while (true) {
					if (dataInputStream.available() > 0) {
						String newMsg = dataInputStream.readUTF();

						System.out.println(name + ": " + newMsg);

						server.broadcastMsg(name + ": " + newMsg);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}server.remove(this);

		}

	   void sendMsg(String msg) {
			try {
				dataOutputStream.writeUTF(msg);
				dataOutputStream.flush();
			} catch (IOException e) {
				server.remove(this);
			}

		}

	}