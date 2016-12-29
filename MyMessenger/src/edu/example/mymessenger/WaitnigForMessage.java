package edu.example.mymessenger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import edu.data.chat.MessageData;
import edu.data.chat.My_Encryption;
import android.os.Message;

public class WaitnigForMessage implements Runnable 
{
	private Message msg;
	private String ip;
	private Socket s;
	private ServerSocket ss;
	private String message;
	private int port;
	private MessageData md;
	
	public WaitnigForMessage(String ip,int port) 
	{
		this.ip = ip;
		this.port = port;
	}
	
	public WaitnigForMessage(MessageData md,int port) 
	{
		this.md = md;
		this.port = port;
	}

	@Override
	public void run() 
	{
		try{
			ss = new ServerSocket(port);
			
			while(true)
			{
				s = ss.accept();
			
				try 
				{
					md = (MessageData)My_Encryption.decrypt(s.getInputStream());
				} 
				catch (IllegalBlockSizeException e) 
				{
					e.printStackTrace();
				} 
				catch (BadPaddingException e) 
				{
					e.printStackTrace();
				}
				
				switch(md.getAction())
				{
					
					case MessageData.MESSAGE_RECIEVED:			message = md.getMessage();
																msg = MainActivity.handler.obtainMessage(MessageData.MESSAGE_RECIEVED,0,0,message);
																MainActivity.handler.sendMessage(msg);
																break;
					case MessageData.ENTER:
						message = md.getMessage();
						msg = MainActivity.handler.obtainMessage(MessageData.ENTER,0,0,message);
						MainActivity.handler.sendMessage(msg);
						break;
						
					
					case MessageData.ALL_PEOPLE_LIST:
						msg = MainActivity.handler.obtainMessage(MessageData.ALL_PEOPLE_LIST,0,0,md);
						MainActivity.handler.sendMessage(msg);
						break;
						
					case MessageData.DIALOGS_LIST://dilogs list
						msg = MainActivity.handler.obtainMessage(MessageData.DIALOGS_LIST,0,0,md);
						MainActivity.handler.sendMessage(msg);
						break;
						
					case MessageData.HISTORY://history
						msg = MainActivity.handler.obtainMessage(MessageData.HISTORY,0,0,md);
						MainActivity.handler.sendMessage(msg);
						break;
					
				}
			}
		}
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}

	}

}
