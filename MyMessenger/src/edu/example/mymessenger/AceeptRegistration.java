package edu.example.mymessenger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.data.chat.MessageData;
import android.os.Message;

public class AceeptRegistration implements Runnable 
{
	private ServerSocket ss;
	private Socket s;
	private Message msg;
	private String message;
	private ObjectInputStream ois;
	private MessageData md;
	private boolean waiting=true;
	
	@Override
	public void run() 
	{
		try 
		{
			ss = new ServerSocket(1516);
			
			while(waiting)
			{
				s = ss.accept();
			
				ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
				md = (MessageData)ois.readObject();
			
				message = md.getMessage();
				
				if(message.equals("Вы зарегистрированы"))
				{
					MainActivity.port = md.getPort();
					msg = MainActivity.handler.obtainMessage(MessageData.REGISTRATION,0,0,message);
					MainActivity.handler.sendMessage(msg);
				
					waiting = false;
				}
				else
				{
					MainActivity.port = md.getPort();
					msg = MainActivity.handler.obtainMessage(MessageData.REGISTRATION,0,0,message);
					MainActivity.handler.sendMessage(msg);
				}
			}
		
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
