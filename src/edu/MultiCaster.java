package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class MultiCaster implements Runnable{
	int[] socketConnections;
	Message receivedMessage;
	public static String TAG = "Multicaster";
	
	MultiCaster(int[] socketConnections, Message receivedMessage){
		this.socketConnections = socketConnections;
		this.receivedMessage = receivedMessage;
	}
	
	@Override
	public void run() {
		for (int port : socketConnections){
			try{
				Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), port);
				ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
				writer.writeObject(receivedMessage);
				writer.flush();
				socket.close();
			} catch (UnknownHostException e) {
				Log.e(TAG, "Multicaster UnknownHostException : Multicasting Message");
			} catch (IOException e) {
				Log.e(TAG, "Multicaster socket IOException : Multicasting Message - " + port);
			}
		}
	}

}
