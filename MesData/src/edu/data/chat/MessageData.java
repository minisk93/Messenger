package edu.data.chat;
import java.io.Serializable;
import java.util.ArrayList;

import javax.crypto.SealedObject;

public class MessageData implements Serializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ip;
	private String message;
	public static final int MESSAGE_RECIEVED = 1;
	public static final int REGISTRATION = 2;
	public static final int ENTER = 3;
	public static final int ALL_PEOPLE_LIST = 4;
	public static final int ONLINE_PEOPLE_LIST = 5;
	public static final int ENCRYPTED_SINGLE = 6;
	public static final int ENCRYPTED_MULTIPLE = 7;
	public static final int DECRYPTED_SINGLE = 8;
	public static final int DECRYPTED_MULTIPLE = 9;
	public static final int READY_FOR_CIPHER = 10;
	public static final int DIALOGS_LIST = 11;
	public static final int HISTORY = 12;
	public static final int EXIT = 13;
	private String name;
	private String password;
	private int phone;
	private String email;
	private int port;
	private int action;
	private ArrayList<MessageData> list;
	private ArrayList<String> send_list;
	private String reciever;
	private int id;
	private int[] ids;
	private SealedObject so;
	private MessageData md;
	
	public MessageData() 
	{
		
	}
	
	public MessageData(String ip, String message) 
	{
		this.ip = ip;
		this.message = message;
	}
	
	public MessageData(int action,String message,int port) 
	{
		this.action = action;
		this.message = message;
		this.port = port;
	}

	public MessageData(int action,String name, String password) 
	{
		this.action = action;
		this.name = name;
		this.password = password;
	}
	
	public MessageData(String name, int phone, String email, String ip, int port,
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
	
	public MessageData(int id,String name, int phone, String email, String ip, int port)
	{
		this.id = id;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.ip = ip;
		this.port = port;
	}
	
	
	
	public MessageData getMd() {
		return md;
	}

	public void setMd(MessageData md) {
		this.md = md;
	}

	public SealedObject getSo() {
		return so;
	}

	public void setSo(SealedObject so) {
		this.so = so;
	}

	public ArrayList<String> getSend_list() {
		return send_list;
	}

	public void setSend_list(ArrayList<String> send_list) {
		this.send_list = send_list;
	}

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String reciever) {
		this.reciever = reciever;
	}

	public int[] getIds() 
	{
		return ids;
	}

	public void setIds(int[] ids) 
	{
		this.ids = ids;
	}

	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	public ArrayList<MessageData> getList() 
	{
		return list;
	}

	public void setList(ArrayList<MessageData> list) 
	{
		this.list = list;
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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password) 
	{
		this.password = password;
	}

	public String getIp() 
	{
		return ip;
	}

	public void setIp(String ip) 
	{
		this.ip = ip;
	}

	public String getMessage() 
	{
		return message;
	}

	public void setMessage(String message) 
	{
		this.message = message;
	}
}
