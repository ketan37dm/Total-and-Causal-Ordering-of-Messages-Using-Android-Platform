package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.UUID;
import java.util.Vector;

public class Message implements Serializable{
	String UID;
	String senderAvd;
	int avdId;
	String messageText;
	String messageType;
	int[] vector = new int[5];
	int sequenceNumber;
	
	Message(String senderAvd, int avdId, String text, String messageType){
		this.UID = UUID.randomUUID().toString();
		this.senderAvd = senderAvd;
		this.avdId = avdId;
		this.messageText = text;
		this.messageType = messageType;  // Can have 2 values : null OR "fromSequencer"
		this.sequenceNumber = -1;
	}
	
	public Message getMessage(){
		return this;
	}
	
	public String getMessageText(){
		return this.messageText;
	}
	
	public void setMessageText(String text){
		this.messageText = text;
	}
	
	public void setMessageType(String messageType){
		this.messageType = messageType;
	}
	
	public void setMessageSequenceNumber(int number){
		this.sequenceNumber = number;
	}
}
