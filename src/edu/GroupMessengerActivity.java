package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {
	static final String TAG = GroupMessengerActivity.class.getSimpleName();
	static final int serverPort = 10000;
	static final String sequencer = "11108";
	public static int[] socketConnections = {11108, 11112, 11116, 11120, 11124};
	String portStr; 
	HashMap<Integer, Message> waitingMessages = new HashMap<Integer, Message>();
	public int messageSequenceNumber = 0;
	private int expectedSequenceNumber = 0;
	private int highestSequenceNumberReceived = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);

		/*
		 * TODO: Use the TextView to display your messages. Though there is no grading component
		 * on how you display the messages, if you implement it, it'll make your debugging easier.
		 */
		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());

		/*
		 * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
		 * OnPTestClickListener demonstrates how to access a ContentProvider.
		 */
		findViewById(R.id.button1).setOnClickListener(new OnPTestClickListener(tv, getContentResolver()));

		/* Calculate the port number that this AVD listens on. */
		portStr = getPortStr();

		/* Start the ServerThread that listens to all the requests */
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			new ServerThread().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);	
		} catch (IOException e) {
			Log.e(TAG, "Can't create a ServerSocket");
		}

		/*
		 * TODO: You need to register and implement an OnClickListener for the "Send" button.
		 * In your implementation you need to get the message from the input box (EditText)
		 * and send it to other AVDs in a total-causal order.
		 */
		final EditText editText = (EditText) findViewById(R.id.editText1);
		findViewById(R.id.button4).setOnClickListener(new OnSendClickListener(editText, portStr, socketConnections));
	}

	private class ServerThread extends AsyncTask<ServerSocket, Message, Void> {
		@Override
		protected Void doInBackground(ServerSocket... params) {
			ServerSocket serverSocket = params[0];
			Socket socket = null;
			ObjectInputStream reader = null;

			/* Keep listening on the received socket for incoming messages */
			while(true){
				try {
					socket = serverSocket.accept();
					reader = new ObjectInputStream(socket.getInputStream());
					Message receivedMessage = (Message) reader.readObject();
					if (receivedMessage != null){
						if ((receivedMessage.messageType.equalsIgnoreCase("fromClient")) &&	portStr.equals(sequencer)){
							//waitingMessages.put(receivedMessage.UID, receivedMessage);
							addSequenceNumberAndMulticast(receivedMessage);
						}
						else if (receivedMessage.messageType.equalsIgnoreCase("fromSequencer")){
							isMessagePublishable(receivedMessage);
							//publishProgress(receivedMessage);
						}
					}
				} catch (IOException e) {
					Log.e(TAG, "ServerSocket do in bakground failed : IO Exception");
				} catch (ClassNotFoundException e) {
					Log.e(TAG, "ServerSocket do in bakground failed : Class Not Found Exception");
				} finally {
					if (reader != null){
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					if (socket != null){
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		private void isMessagePublishable(Message receivedMessage) {
			// If the sequence number in the message is what I am expecting then 
			// check if this number is lesser than the highestSequenceNumberReceived
			// then starting from the current sequence number till the highestSequenceNumberReceived
			// fetch the messages and pass them to publish progress
			if (expectedSequenceNumber == receivedMessage.sequenceNumber){
				publishProgress(receivedMessage);
				//lastProcessedSequenceNumber = receivedMessage.sequenceNumber;
				expectedSequenceNumber++;
				
				if(expectedSequenceNumber < highestSequenceNumberReceived){
					int i = expectedSequenceNumber; 
					while( i <= highestSequenceNumberReceived){
						if(waitingMessages.containsKey(expectedSequenceNumber)){
							Message message = waitingMessages.get(i);
							publishProgress(message);
							expectedSequenceNumber++;
							waitingMessages.remove(i);
							i++;
						}
						else{
							break;
						}
					}
				}
			}else{
			// else add this message to waitingMessages and update the highestSequenceNumberReceived
				waitingMessages.put(receivedMessage.sequenceNumber, receivedMessage);
				if (receivedMessage.sequenceNumber > highestSequenceNumberReceived){
					highestSequenceNumberReceived = receivedMessage.sequenceNumber;
				}
			}			
		}

		protected void onProgressUpdate(Message... messages) {
			Message receivedMessage = messages[0];
			TextView localTextView = (TextView) findViewById(R.id.textView1);
			MessageProcessor messageProcessor = new MessageProcessor(getContentResolver(), localTextView);
			messageProcessor.processMessage(receivedMessage);
		}

		private void addSequenceNumberAndMulticast(Message receivedMessage) {
			receivedMessage.setMessageSequenceNumber(messageSequenceNumber);
			receivedMessage.setMessageType("fromSequencer");
			MultiCaster multiCaster = new MultiCaster(socketConnections, receivedMessage);
			Thread multiCasterThread = new Thread(multiCaster);
			multiCasterThread.start();
			messageSequenceNumber++;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}

	private String getPortStr() {
		TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
		return myPort;
	}
}

