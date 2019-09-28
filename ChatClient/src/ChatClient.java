import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient extends Thread {

	Socket socket = null;
	DataOutputStream dataOutputStream = null;
	DataInputStream dataInputStream = null;

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		new ChatClient("Vasya", "localhost", 8080);

	}

	String name;
	String dstAddress;
	int dstPort;


	boolean goOut = false;

	ChatClient(String name, String address, int port)
			throws UnknownHostException, IOException {
		this.name = name;
		dstAddress = address;
		dstPort = port;
		socket = new Socket(dstAddress, dstPort);
		dataOutputStream = new DataOutputStream(socket.getOutputStream());
		dataInputStream = new DataInputStream(socket.getInputStream());
		start();
	}

	private void sendMsg(String msg) throws IOException {
		dataOutputStream.writeUTF(msg);
		dataOutputStream.flush();
	}

	@Override
	public void run() {

		Scanner in = new Scanner(System.in);
		try {

			sendMsg(name);

			while (!goOut) {
				if (dataInputStream.available() > 0) {
					String msg = dataInputStream.readUTF();
					System.out.println(msg);
					
				}
				System.out.println("Enter^");
				sendMsg(in.nextLine());
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

}
