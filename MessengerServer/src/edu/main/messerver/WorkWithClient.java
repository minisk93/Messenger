package edu.main.messerver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import edu.data.chat.MessageData;
import edu.data.chat.My_Encryption;

public class WorkWithClient implements Runnable 
{
	private Socket incoming_socket;
	private boolean working = true;
	private ObjectOutputStream oos;
	private MessageData md;
	private Socket to_send_socket;
	private Connection con;
	private String check_name;
	private String check_pass;
	private PreparedStatement pst;
	private String response;
	private String name_checked;
	private String pass_checked;
	private int port;
	private Statement st;
	private String[] columns;
	private ArrayList<MessageData> list;
	private int position;
	private String reciever;
	private String sender;
	private String message;
	private ArrayList<String> sendList;
	private ArrayList<Integer> ids;
	private String mode;
	private int id;
	private String ip;
	private String sql11;
	private ArrayList<String> companions;
	private String history;
	
	public WorkWithClient(Socket incoming_socket) 
	{
		this.incoming_socket = incoming_socket;
	}

	@Override
	public void run() 
	{
		try 
		{
			DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
			con = DriverManager.getConnection("jdbc:sqlserver://192.168.0.100;databaseName=Chat_DB;user=minisk;password=negstcerb54321;");
			System.out.println("Connection to Chat_DB: succesfull");
		
		} 
		catch (SQLException e1) 
		{
			e1.printStackTrace();
		}
		
		while(working)
		{
			
			try 
			{
				try 
				{
					md = (MessageData)My_Encryption.decrypt(incoming_socket.getInputStream());
				} 
				catch (ClassNotFoundException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			} 
			catch (IllegalBlockSizeException e) 
			{
				e.printStackTrace();
			} 
			catch (BadPaddingException e) 
			{
				e.printStackTrace();
			}
				
			int action = md.getAction();
				
			try
			{
			switch(action)
				{
					case MessageData.REGISTRATION:	check_name = md.getName();
													
													String sql = "SELECT name AS 'name' "
																+ "FROM People "
																+ "WHERE name LIKE '"+check_name+"'";
													
													System.out.println(sql);
													
													pst = con.prepareStatement(sql);
													ResultSet rs = pst.executeQuery();
													
													name_checked = null;
													
													while(rs.next())
													{
														name_checked = rs.getString("name");
													}
													
													if(name_checked == null)
													{
														String sql_port = "SELECT MAX(port) AS port "
																			+ "FROM People";
														
														pst = con.prepareStatement(sql_port);
														rs = pst.executeQuery();
														
														while(rs.next())
														{
															port = rs.getInt("port")+1;
														}
														
														pst = con.prepareStatement("INSERT INTO People VALUES(?,?,?,?,?,?)");
														pst.setString(1,check_name);
														pst.setInt(2,md.getPhone());
														pst.setString(3,md.getEmail());
														pst.setString(4,md.getIp());
														pst.setInt(5,port);
														pst.setString(6,md.getPassword());
															
														pst.executeUpdate();
														
														response = "Вы зарегистрированы";
														MainClass.people_online.add(check_name);
														position = MainClass.people_online.indexOf(check_name);
														
														System.out.println("Аккаунт добавлен");
														System.out
																.println(check_name+" added , position "+position);
														
													}
													
													else if(name_checked!=null)
													{
														response = "Такое имя уже существует";
													}
													
													to_send_socket = new Socket(md.getIp(),1516);
													oos = new ObjectOutputStream(new BufferedOutputStream(to_send_socket.getOutputStream()));
													md = new MessageData(MessageData.REGISTRATION,response,port);
													oos.writeObject(md);
													oos.flush();
													working = false;
													
													break;
									
					case MessageData.ENTER:		String update_ip = md.getIp();
												check_name = md.getName();
												check_pass = md.getPassword();
												port = md.getPort();
												id=0;
												
												String sql_enter = "SELECT name AS 'name',user_password AS 'password',ID_user AS 'id' "
														+ "FROM People "
														+ "WHERE name LIKE '"+check_name+"' AND user_password LIKE '"+check_pass+"'";
											
												System.out.println(sql_enter);
												
												pst = con.prepareStatement(sql_enter);
												rs = pst.executeQuery();
												
												name_checked = null;
												pass_checked = null;
												
												while(rs.next())
												{
													name_checked = rs.getString("name");
													pass_checked = rs.getString("password");
													id = rs.getInt("id");
												}
												
												if(name_checked == null && pass_checked==null)
												{
													response = "Неверное имя или пароль";
												}
												else if(name_checked != null && pass_checked!=null)
												{
													response = "Все верно";
													
													String update_sql = "UPDATE People "
																		+ "SET ip = ? "
																		+ "WHERE ID_user = ?";
													
													pst = con.prepareStatement(update_sql);
													pst.setString(1,update_ip);
													pst.setInt(2,id);
													
													pst.executeUpdate();
												
													MainClass.people_online.add(name_checked);
													position = MainClass.people_online.indexOf(name_checked);
													System.out
													.println(name_checked+" added , position "+position);
												}
												
												to_send_socket = new Socket(update_ip,port);
												
												md = new MessageData();
												md.setAction(MessageData.ENTER);
												md.setMessage(response);
												
												My_Encryption.encrypt(md,to_send_socket.getOutputStream());
												
												working = false;
						
												break;
												
					case MessageData.ALL_PEOPLE_LIST:
						
						String name = md.getIp();
						String to_update = md.getName();
						mode = md.getMessage();
						
						String sql_list_all = "SELECT * FROM People ";
						st = con.createStatement();
						rs = st.executeQuery(sql_list_all);
						
						ResultSetMetaData mdata = rs.getMetaData();
						int colCount = mdata.getColumnCount();
						
						columns = new String[colCount];
						
						for(int i=0;i<colCount;i++)
						{
							columns[i] = mdata.getColumnLabel(i+1);
						}
						
						list  = new ArrayList<>();
						
						while(rs.next())
						{
							String[] p = new String[colCount]; 
							for(int i=0;i<colCount;i++)
							{
								p[i] = rs.getString(i+1);
							}
							MessageData md2 = new MessageData(Integer.parseInt(p[0]),p[1],Integer.parseInt(p[2]),p[3],p[4],Integer.parseInt(p[5]));
							list.add(md2);
							
						}
						
						md = new MessageData();
						md.setAction(MessageData.ALL_PEOPLE_LIST);
						md.setList(list);
						md.setName(to_update);
						md.setMessage(mode);
						
						String sql1 = "SELECT port AS 'port' "
									+ " FROM People"
									+ " WHERE name LIKE '"+name+"'";
						
						st = con.createStatement();
						rs = st.executeQuery(sql1);
						
						int port1 = 0;
						
						while(rs.next())
						{
							port1 = rs.getInt("port");
						}
						
						Socket s2 = new Socket(incoming_socket.getInetAddress().getHostAddress(),port1);
						
						My_Encryption.encrypt(md,s2.getOutputStream());
						System.out.println("sended");
						working = false;
						
						break;
				
					case MessageData.ONLINE_PEOPLE_LIST:
						
						System.out.println("encrypted");
						String name1 = md.getIp();
						String to_update_online = md.getName();
						mode = md.getMessage();
						
						md = new MessageData();
						
						ArrayList<MessageData> onlines = new ArrayList<>();
						
						if(MainClass.people_online.size()==0)
						{
							md.setName("No people online");
							onlines.add(md);
						}
						else
						{
							for(int i=0;i<MainClass.people_online.size();i++)
							{
								MessageData md = new MessageData();
								md.setName(MainClass.people_online.get(i));
								onlines.add(md);
								
							}
						}
						
						md.setAction(MessageData.ALL_PEOPLE_LIST);
						md.setList(onlines);
						md.setName(to_update_online);
						md.setMessage(mode);
						
						String sql2 = "SELECT port AS 'port' "
								+ " FROM People"
								+ " WHERE name LIKE '"+name1+"'";
					
						st = con.createStatement();
						rs = st.executeQuery(sql2);
					
						int port2 = 0;
					
						while(rs.next())
						{
							port2 = rs.getInt("port");
						}
					
						Socket s3 = new Socket(incoming_socket.getInetAddress().getHostAddress(),port2);
						
						My_Encryption.encrypt(md, s3.getOutputStream());
						working = false;
						
						break;
						
					case MessageData.EXIT: String delete = md.getName();
					System.out.println("Клиент "+delete+" отключился");
					MainClass.people_online.remove(delete);
					working = false;
					break;
				
					case MessageData.DIALOGS_LIST:// history dialogs list
						System.out.println("entered");
						sender = md.getName();
						
						String sql_id1 = " SELECT ID_user as 'id' ,port AS 'port' ,ip AS 'ip' "
										+ " FROM People "
										+ " WHERE name LIKE '"+sender+"'";
						
						st = con.createStatement();
						rs = st.executeQuery(sql_id1);
						
						id = -1;
						
						while(rs.next())
						{
							id = rs.getInt("id");
							port = rs.getInt("port");
							ip = rs.getString("ip");
						}
						
						String sql_dialogs = " SELECT name AS 'name' "
											+ " FROM History INNER JOIN People on History.interlocutor_1 = People.ID_user "
											+ " WHERE (History.interlocutor_1 = "+id+" or History.interlocutor_2 = "+id+") AND name NOT LIKE '"+sender+"' "
											+ " UNION "
											+ " SELECT name AS 'name' "
											+ " FROM History INNER JOIN People on History.interlocutor_2 = People.ID_user "
											+ " WHERE (History.interlocutor_1 = "+id+" or History.interlocutor_2 = "+id+") AND name NOT LIKE '"+sender+"' ";
						
						companions = new ArrayList<>();
						
						st = con.createStatement();
						rs = st.executeQuery(sql_dialogs);
						
						while(rs.next())
						{
							companions.add(rs.getString("name"));
						}
						
						md = new MessageData();
						md.setSend_list(companions);
						md.setAction(MessageData.DIALOGS_LIST);
						
						Socket s = new Socket(ip,port);
						
						My_Encryption.encrypt(md, s.getOutputStream());
						
						working = false;
						
						break;
						
					case MessageData.HISTORY://history return
						
						reciever = md.getReciever();
						sender = md.getName();
						
						sql11 = " SELECT ID_user AS 'id' "
								+ " FROM People "
								+ " WHERE name LIKE '"+reciever+"' OR name LIKE '"+sender+"'";
						
						st = con.createStatement();
						rs = st.executeQuery(sql11);
						
						ids = new ArrayList<>();
						
						while(rs.next())
						{
							ids.add(rs.getInt("id"));
						}
						
						sql11 = " SELECT messages AS 'history' "
								+ " FROM History "
								+ " WHERE (interlocutor_1 = "+ids.get(0)+" OR interlocutor_1 = "+ids.get(1)+") AND (interlocutor_2 = "+ids.get(0)+" OR interlocutor_2 = "+ids.get(1)+")";
						
						st = con.createStatement();
						rs = st.executeQuery(sql11);
						
						while(rs.next())
						{
							history = rs.getString("history");
						}
						
						sql11 = " SELECT ip AS 'ip',port AS 'port' "
								+ " FROM People "
								+ " WHERE name LIKE '"+sender+"'";
						
						st = con.createStatement();
						rs = st.executeQuery(sql11);
						
						while(rs.next())
						{
							ip = rs.getString("ip");
							port = rs.getInt("port");
						}
						
						to_send_socket = new Socket(ip,port);
						
						md = new MessageData();
						md.setAction(MessageData.HISTORY);
						md.setReciever(reciever);
						md.setMessage(history);
						
						My_Encryption.encrypt(md,to_send_socket.getOutputStream());
						
						working = false;
						break;
					
					
					
					case MessageData.ENCRYPTED_MULTIPLE:
						
						sender = md.getName();
						sendList = md.getSend_list();
						message = md.getMessage();
						
						String sql_send_list;
						
						for(int i=0;i<sendList.size();i++)
						{
							sql_send_list = "SELECT ip AS 'ip',port AS 'port'"
									+ " FROM People"
									+ " WHERE name LIKE '"+sendList.get(i)+"'";
						
							st = con.createStatement();
							rs = st.executeQuery(sql_send_list);
							
							String reciever_ip = null;
							int reciever_port = 0;
							
							while(rs.next())
							{
								reciever_ip = rs.getString("ip");
								reciever_port = rs.getInt("port");
							}
							
							to_send_socket = new Socket(reciever_ip,reciever_port);
							
							md = new MessageData();
							md.setMessage(message);
							md.setName(sender);
							md.setAction(MessageData.MESSAGE_RECIEVED);
							
							My_Encryption.encrypt(md,to_send_socket.getOutputStream());
							
						}
						working = false;
						break;
					
					case MessageData.ENCRYPTED_SINGLE:
						System.out.println("entered");
						sender = md.getName();
						reciever = md.getReciever();
						message = md.getMessage();
						
						System.out.println(sender+"\n"+reciever);
						
						String sql_reciever = "SELECT ip AS 'ip',port AS 'port'"
											+ " FROM People"
											+ " WHERE name LIKE '"+reciever+"'";
						
						st = con.createStatement();
						rs = st.executeQuery(sql_reciever);
						
						String reciever_ip = null;
						int reciever_port = 0;
						
						while(rs.next())
						{
							reciever_ip = rs.getString("ip");
							reciever_port = rs.getInt("port");
						}
						
						System.out.println(reciever_ip+" "+reciever_port);
						
						String sql_id = " SELECT ID_user as 'id' "
										+ "FROM People "
										+ "WHERE name = '"+sender+"' "+"or name = '"+reciever+"'";
						
						st = con.createStatement();
						rs = st.executeQuery(sql_id);
						
						ArrayList<Integer> ids = new ArrayList<>();
						
						while(rs.next())
						{
							ids.add(rs.getInt("id"));
						}
						
						int history_id = -1;
						
						String sql_history = " SELECT id AS 'id' "
											+ "FROM History "
											+ "WHERE (interlocutor_1 = "+ids.get(0)+" OR interlocutor_1 = "+ids.get(1)+")"
											+" AND (interlocutor_2 = "+ids.get(0)+" OR interlocutor_2 = "+ids.get(1)+")";
						
						st = con.createStatement();
						rs = st.executeQuery(sql_history);
						
						while(rs.next())
						{
							history_id = rs.getInt("id");
						}
						
						if(history_id == -1)
						{
							pst = con.prepareStatement(" INSERT INTO History "
														+ "VALUES(?,?,?) ");
							
							pst.setInt(1,ids.get(0));
							pst.setInt(2,ids.get(1));
							pst.setString(3,message);
							
							pst.executeUpdate();
							
						}
						else
						{
							String sql_update = " UPDATE History "
										+ " SET messages+=?"
										+ " WHERE id = ?";
							
							pst = con.prepareStatement(sql_update);
							pst.setString(1,message);
							pst.setInt(2,history_id);
							
							pst.executeUpdate();
						
						}
						
						to_send_socket = new Socket(reciever_ip,reciever_port);
						
						md = new MessageData();
						md.setMessage(message);
						md.setName(sender);
						md.setAction(MessageData.MESSAGE_RECIEVED);
						
						My_Encryption.encrypt(md, to_send_socket.getOutputStream());
						working = false;
						break;
						
				}
			
			
			}
		
			catch (IOException e) 
			{
				e.printStackTrace();
			} 
			catch (SQLException e)//troubles with database connection 
			{
				e.printStackTrace();
			}
		}
	}

}
