package edu.data.chat;

import java.io.Serializable;

public class Contact implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private String name;
	private int phone;
	private String email;
	private String ip;
	private int port;
	private String password;
	private int action;
	
	public Contact(String name, int phone, String email, String ip, int port,
			String password,int action) 
	{
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.ip = ip;
		this.port = port;
		this.password = password;
		this.action = action;
	}
	
	public Contact()
	{
		
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public int getPhone() 
	{
		return phone;
	}

	public void setPhone(int phone) 
	{
		this.phone = phone;
	}

	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String email) 
	{
		this.email = email;
	}

	public String getIp() 
	{
		return ip;
	}

	public void setIp(String ip) 
	{
		this.ip = ip;
	}

	public int getPort() 
	{
		return port;
	}

	public void setPort(int port) 
	{
		this.port = port;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	public int getAction() 
	{
		return action;
	}

	public void setAction(int action) 
	{
		this.action = action;
	}

	
}
