package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;
import android.widget.TextView;

public class MessageProcessor {
	ContentResolver contentResolver;
	TextView textView;
	private static String uriString = "content://edu.buffalo.cse.cse486586.groupmessenger.provider/" + "ds_development";
	private static Uri contentUri = Uri.parse(uriString);

	public MessageProcessor(ContentResolver contentResolver, TextView textView) {
		this.contentResolver = contentResolver;
		this.textView = textView;
	}

	public void processMessage(Message receivedMessage) {
		ContentValues values = new ContentValues();
		values.put("key", ((Integer)receivedMessage.sequenceNumber).toString());
		values.put("value", receivedMessage.messageText);
		contentResolver.insert(contentUri, values);
		textView.append(receivedMessage.sequenceNumber + receivedMessage.getMessageText());
		Log.e("MessageProcessor", "inserting key : " + receivedMessage.sequenceNumber + " value : " + receivedMessage.messageText);
	}
}
