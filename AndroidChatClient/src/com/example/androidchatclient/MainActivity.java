package com.example.androidchatclient;

import java.io.IOException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static final int SocketServerPORT = 8080;

	LinearLayout loginPanel, chatPanel;

	EditText editTextUserName, editTextAddress;
	Button buttonConnect;
	TextView chatMsg, textPort;

	EditText editTextSay;
	Button buttonSend;
	Button buttonDisconnect;

	String msgLog = "";

	ChatClient client = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		loginPanel = (LinearLayout) findViewById(R.id.loginpanel);
		chatPanel = (LinearLayout) findViewById(R.id.chatpanel);

		editTextUserName = (EditText) findViewById(R.id.username);
		editTextAddress = (EditText) findViewById(R.id.address);
		textPort = (TextView) findViewById(R.id.port);
		textPort.setText("port: " + SocketServerPORT);
		buttonConnect = (Button) findViewById(R.id.connect);
		buttonDisconnect = (Button) findViewById(R.id.disconnect);

		chatMsg = (TextView) findViewById(R.id.chatmsg);

		buttonConnect.setOnClickListener(buttonConnectOnClickListener);
		buttonDisconnect.setOnClickListener(buttonDisconnectOnClickListener);

		editTextSay = (EditText) findViewById(R.id.say);
		buttonSend = (Button) findViewById(R.id.send);

		buttonSend.setOnClickListener(buttonSendOnClickListener);
	}

	OnClickListener buttonDisconnectOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (client == null) {
				return;
			}
			client.disconnect();
		}

	};

	OnClickListener buttonSendOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (editTextSay.getText().toString().equals("")) {
				return;
			}

			if (client == null) {
				return;
			}

			try {
				client.sendMsg(editTextSay.getText().toString() + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	};

	OnClickListener buttonConnectOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String textUserName = editTextUserName.getText().toString();
			if (textUserName.equals("")) {
				Toast.makeText(MainActivity.this, "Enter User Name",
						Toast.LENGTH_LONG).show();
				return;
			}

			String textAddress = editTextAddress.getText().toString();
			if (textAddress.equals("")) {
				Toast.makeText(MainActivity.this, "Enter Addresse",
						Toast.LENGTH_LONG).show();
				return;
			}

			msgLog = "";
			chatMsg.setText(msgLog);
			loginPanel.setVisibility(View.GONE);
			chatPanel.setVisibility(View.VISIBLE);

			try {
				client = new ChatClient(MainActivity.this, textUserName,
						"10.0.2.2", SocketServerPORT);
				client.start();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	};

	void showMessage(final String msg) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
					chatMsg.setText(chatMsg.getText()+msg);
			}
		});
	}

}
