package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class OnSendClickListener implements OnClickListener {
	EditText editText;
	String avdName, portStr;
	int avdId;
	int[] vector;
	public int[] socketConnections;
	public static String TAG = "GroupMessenger";

	OnSendClickListener(EditText editText, String portStr, int[] socketConnections){
		this.editText = editText;
		this.portStr = portStr;
		this.socketConnections = socketConnections;
	}

	@Override
	public void onClick(View tv) {
		/* Use the portStr to obtain current avd information */
		setCurrentAvdDetails(portStr);
		
		/* Create a new Message object for multi-casting */
        Message newMessage = new Message(avdName, avdId, null, "fromClient");

        /* extract message from the editText field and set it up in current message object */
        setTextForNewMessage(newMessage);

		/* Create Client AsyncTask for multicasting the newMessage */
		new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, newMessage);
	}

	private void setTextForNewMessage(Message newMessage) {
		String msg = editText.getText().toString() + "\n";
        editText.setText(""); // This is one way to reset the input box.
        newMessage.setMessageText(msg);
	}

	public class ClientTask extends AsyncTask<Message, Void, Void> {
		@Override
		protected Void doInBackground(Message... params) {
			//for (int port : socketConnections){
				int port = socketConnections[0];
				try{
					Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), port);
					ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
					writer.writeObject(params[0]);
					writer.flush();
					socket.close();
				} catch (UnknownHostException e) {
					Log.e(TAG, "ClientTask UnknownHostException");
				} catch (IOException e) {
					Log.e(TAG, "ClientTask socket IOException");
				}
			//}
			return null;
		}
	}

	private void setCurrentAvdDetails(String portStr) {
		switch (Integer.parseInt(portStr)){
		case 11108:
			avdName = "avd0";
			avdId = 0;
			break;
		case 11112:
			avdName = "avd1";
			avdId = 1;
			break;
		case 11116:
			avdName = "avd2";
			avdId = 2;
			break;
		case 11120:
			avdName = "avd3";
			avdId = 3;
			break;
		case 11124:
			avdName = "avd4";
			avdId = 4;
			break;
		}
		System.out.println("Current avdName : " + avdName + "Current avdId : " + avdId);
	}

}
