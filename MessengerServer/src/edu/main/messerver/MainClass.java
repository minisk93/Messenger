package edu.main.messerver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class MainClass 
{
	public static ArrayList<String> people_online = new ArrayList<>();
	private static ServerSocket ss;
	private static Socket s;
	
	public static void main(String[] args) 
	{
		try 
		{
			 ss = new ServerSocket(1515);
			
			 while(true)
			 {
				 s = ss.accept();
				 Thread t = new Thread(new WorkWithClient(s));
				 t.start();
			 }
			
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}

}
