package edu.example.mymessenger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import edu.data.chat.MessageData;
import edu.data.chat.My_Encryption;

public class SendRequest implements Runnable 
{
	private Socket s;
	private MessageData md;
	
	public SendRequest(MessageData md) 
	{
		this.md = md;
	}

	@Override
	public void run() 
	{
		try 
		{
			s = new Socket("192.168.0.100",1515);
			My_Encryption.encrypt(md, s.getOutputStream());
		
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}


	}

}
