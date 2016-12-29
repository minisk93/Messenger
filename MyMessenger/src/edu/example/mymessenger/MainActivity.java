package edu.example.mymessenger;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import edu.data.chat.MessageData;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 

public class MainActivity extends Activity 
{
	private FragmentTransaction ft;
	private MenuFragment menu;
	private String text;
	public static boolean is_cipher = false;
	public static Handler handler;
	private String ip;
	private AlertDialog.Builder adb;
	private AlertDialog al;
	private LayoutInflater inflater;
	private View enter_view;
	private View registr_view;
	private Button btn_registr;
	private EditText et_name;
	private EditText et_password;
	private EditText et_name_reg;
	private EditText et_phone_reg;
	private EditText et_email_reg;
	private EditText et_password_reg;
	private EditText et_confirm_reg;
	private static MessageData md;
	private SharedPreferences sp;
	private String name_to_save;
	private String pass_to_save;
	public static int port;
	public static String my_name;
	public static ArrayAdapter<String> adapter;
	private Thread thread;
	public static ArrayList<String>dialogs;
	
    protected void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
        enter_view = inflater.inflate(R.layout.enter_chat_dialog,null);
        et_name = (EditText)enter_view.findViewById(R.id.et_name);
        et_password = (EditText)enter_view.findViewById(R.id.et_password);
        btn_registr = (Button)enter_view.findViewById(R.id.btn_messages);
        
        sp = getSharedPreferences("MyChatData",MODE_PRIVATE);
        my_name = sp.getString("myChatName","NAN");
        
        ip = getIPAddress(true);
		
        Toast.makeText(this,MainActivity.my_name,Toast.LENGTH_SHORT).show();
        
        btn_registr.setOnClickListener(new OnClickListener() 
        {
        	
			@Override
			public void onClick(View v) 
			{
				al.dismiss();
				inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				registr_view = inflater.inflate(R.layout.registr_dialog,null);
				et_name_reg = (EditText)registr_view.findViewById(R.id.et_message);
				et_phone_reg = (EditText)registr_view.findViewById(R.id.editText2);
				et_email_reg = (EditText)registr_view.findViewById(R.id.et_bypatron);
				
				et_password_reg = (EditText)registr_view.findViewById(R.id.editText6);
				et_confirm_reg = (EditText)registr_view.findViewById(R.id.editText7);
				
				 Thread t = new Thread(new AceeptRegistration());
				 t.start();
			
				adb = new AlertDialog.Builder(MainActivity.this);
		        adb.setTitle("Регистрация");
		        adb.setView(registr_view);
		        adb.setCancelable(false);
		        adb.setPositiveButton("Подтвердить",new DialogInterface.OnClickListener() 
		        {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						
						
						String name = et_name_reg.getText().toString();
						String phone = et_phone_reg.getText().toString();
						String email = et_email_reg.getText().toString();
						String password = et_password_reg.getText().toString();
						String confirm = et_confirm_reg.getText().toString();
						String ip = getIPAddress(true);
					
						if(!password.equals(confirm))
						{
							Toast.makeText(MainActivity.this,"Пароли не совпадают",Toast.LENGTH_LONG).show();
							
						}
						
						else if(isEmpty(et_name_reg) ||  isEmpty(et_password_reg) || isEmpty(et_confirm_reg))
						{
							Toast.makeText(MainActivity.this,"Не все обязательные поля заполнены!",Toast.LENGTH_LONG).show();
							
						}
					
						else if(!isEmpty(et_name_reg) &&  !isEmpty(et_password_reg) && !isEmpty(et_confirm_reg))
						{
							name_to_save = name;
							pass_to_save = password;
							my_name = name;
							
							md = new MessageData();
							md.setName(name);
							md.setPhone(Integer.parseInt(phone));
							md.setEmail(email);
							md.setIp(ip);
							md.setPassword(password);
							md.setAction(MessageData.REGISTRATION);
							
							Thread t2 = new Thread(new SendRequest(md));
							t2.start();
						}
						
					}
				});
		        
		        adb.setNegativeButton("Отмена",new DialogInterface.OnClickListener() 
		        {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				});
		        
		        al = adb.create();
		        al.show();
			}
		});
        
        adb = new AlertDialog.Builder(this);
        adb.setTitle("Вход");
        adb.setView(enter_view);
        adb.setCancelable(false);
        adb.setPositiveButton("Войти",new DialogInterface.OnClickListener() 
        {
			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				String name = et_name.getText().toString();
				String password = et_password.getText().toString();
				
				sp = getSharedPreferences("MyChatData",MODE_PRIVATE);
				String name_to_check = sp.getString("myChatName","NAN");
				String pass_to_check = sp.getString("myChatPass","NAN");
				int current_port = sp.getInt("myChatPort",1516);
				
				if(!name.equals(name_to_check) && password.equals(pass_to_check))
				{
					Toast.makeText(getBaseContext(), "Неверное имя или пароль",Toast.LENGTH_SHORT).show();
					return;//
				}
				else
				{
					md = new MessageData();
					md.setName(name);
					md.setPassword(password);
					md.setIp(ip);
					md.setPort(current_port);
					md.setAction(MessageData.ENTER);
				
					Thread t2 = new Thread(new WaitnigForMessage(md,current_port));
					t2.start();
					
					Thread t = new Thread(new SendRequest(md));
					t.start();
				}
				
			}
		});
        
        al = adb.create();
        al.show();
        
        
        menu = new MenuFragment();
        
        ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.menu_in,R.animator.items_in);
        ft.add(R.id.menu_fl,menu);
       
        ft.commit();
    
        handler = new MyHandler(this);
       
    }

    static class MyHandler extends Handler
    {
    	WeakReference<MainActivity> ref;
    	
    	public MyHandler(MainActivity ma)
    	{
    		ref = new WeakReference<MainActivity>(ma); 
    	}
		
    	@Override
		public void handleMessage(Message msg) 
    	{
			super.handleMessage(msg);
			{
				MainActivity ma = ref.get();
				
				if(ma!=null)
				{
					switch(msg.what)
					{
						case MessageData.REGISTRATION:		ma.registration(msg);
															break;
					
						case MessageData.MESSAGE_RECIEVED:	ma.recievingMessage(msg);
															break;
					
						case MessageData.ENTER:				ma.entering(msg);
															break;
					
						case MessageData.ALL_PEOPLE_LIST:	ma.showAllPeople(msg);
															break;
					
						case MessageData.DIALOGS_LIST:		ma.showAllDialogs(msg);
															break;
					
						case MessageData.HISTORY:			ma.showingHistory(msg);
															break;
					}
				}
			}
		}
    }
		
    
	private void showingHistory(Message msg)
	{
		md = (MessageData)msg.obj;
		String history = md.getMessage();
		showHistory(history,HistoryFragment.ll_dialogs);
	}
    
    private void showAllDialogs(Message msg)
	{
		md = (MessageData)msg.obj;
		
		dialogs = md.getSend_list();
		
		adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_spinner_item,dialogs);
		
		MessageFragment.lv_dialogs.setAdapter(adapter);
		
		MessageFragment.lv_dialogs.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent,
					View view, int position, long id) 
			{
				md = new MessageData();
				md.setAction(MessageData.HISTORY);//history return
				md.setName(MainActivity.my_name);
				md.setReciever(dialogs.get(position));
			
				thread = new Thread(new SendRequest(md));
				thread.start();
			
				HistoryFragment hf = new HistoryFragment();
				
				ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.items_in,R.animator.items_out);
				ft.replace(R.id.details_fl,hf);
				ft.addToBackStack(null);
				ft.commit();
			}

			
		});
	}
    
    
    private void showAllPeople(Message msg)
	{
		md = (MessageData)msg.obj;
		
		ArrayList<MessageData> al_md = md.getList();
		
		ArrayList<String>people = new ArrayList<>();		
		
		for(int i=0;i<al_md.size();i++)
		{
			people.add(i, al_md.get(i).getName());
			if(people.get(i).equals("No people online") )
			{
				Toast.makeText(MainActivity.this,"No people online",Toast.LENGTH_LONG).show();
			}
		}
		people.remove(MainActivity.my_name);
		if(people.size()==0)
		{
			Toast.makeText(MainActivity.this,"No people online",Toast.LENGTH_LONG).show();
		}
				
		if(md.getName().equals("People"))
		{
			adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_spinner_item,people);
			
			if(md.getMessage().equals("all"))
			{
				PeopleFragment.lv_all_people.setAdapter(adapter);
			}
			else
			{
				PeopleFragment.lv_online_people.setAdapter(adapter);
			}
		}
		else
		{
			if(md.getMessage().equals("single"))
			{
				adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_single_choice,people);
			}
			else
			{
				adapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_multiple_choice,people);
			}
		
			MessageFragment.ll_recievers.setAdapter(adapter);
		}
	}
    
    private void registration(Message msg)
	{
		text = (String)msg.obj;
		
		if(text.equals("Вы зарегистрированы"))
		{
			al.dismiss();
			
			sp = getSharedPreferences("MyChatData",MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("myChatName",name_to_save);
			editor.putString("myChatPass",pass_to_save);
			editor.putInt("myChatPort",port);
			editor.commit();
			
			Thread t = new Thread(new WaitnigForMessage(ip,port));
			t.start();
		}
		Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();
	}
	
	private void recievingMessage(Message msg)
	{
		text = (String)msg.obj;
		showHistory(text,MessageFragment.ll_messages);
	}
	
	private void entering(Message msg)
	{
		text = (String)msg.obj;
		Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();
	}
	
	
	
	public static String getIPAddress(boolean useIPv4) 
	 {
	        try 
	        {
	            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
	            for (NetworkInterface intf : interfaces) 
	            {
	                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
	                for (InetAddress addr : addrs) 
	                {
	                    if (!addr.isLoopbackAddress()) 
	                    {
	                        String sAddr = addr.getHostAddress();
	                        boolean isIPv4 = sAddr.indexOf(':')<0;

	                        if (useIPv4) 
	                        {
	                            if (isIPv4) 
	                            return sAddr;
	                        } 
	                        else 
	                        {
	                            if (!isIPv4) 
	                            {
	                                int delim = sAddr.indexOf('%'); 
	                                return delim<0 ? sAddr.toUpperCase(Locale.getDefault()) : sAddr.substring(0, delim).toUpperCase();
	                            }
	                        }
	                    }
	                }
	            }
	        } 
	        catch (Exception ex) 
	        { 
	        	
	        } // for now eat exceptions
	        return "";
	 }


	public String getText() 
	{
		return text;
	}


	public void setText(String text) 
	{
		this.text = text;
	}

	private boolean isEmpty(EditText et)
	{
		if(et.getText().toString().trim().length()==0)return true;
		else return false;
	}
	
	public Context getContext()
	{
		return MainActivity.this;
	}

	@Override
	protected void onStop() 
	{
		
		super.onStop();
	}

	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
	}

	private void showHistory(String history,LinearLayout ll)
	{
		
		int letters=0;
		String message="";
		String name="";
		String date="";
		
		for(int i=0;i<MainActivity.my_name.length();i++)
		{
			letters++;
		}
	
		for(int i=0;i<history.length();i++)
		{
			if(history.charAt(i)=='\u2663')
			{
				for(int j=0;j<letters;j++)
				{
					name+=message.charAt(j);
				}
				
				int length = message.length()-17;
				
				StringBuilder sb = new StringBuilder(message);
				date = sb.substring(length);
				
				for(int k=message.length()-1;length<=k;k--)
				{
					sb.deleteCharAt(k);
				}
				
				sb.deleteCharAt(message.length()-18);
				
				message = sb.toString();
			
				if(name.equals(MainActivity.my_name))
				{
					TextView tv = new TextView(this);
					tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
					tv.setTextSize(18);
					tv.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
					tv.setTextColor(Color.BLACK);										
					tv.setText(message);
					tv.setGravity(Gravity.LEFT);
					tv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
					ll.addView(tv);
					
					TextView tv2 = new TextView(this);
					tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					tv2.setTextSize(14);
					tv2.setTextColor(Color.BLACK);
					tv2.setText(date);
					tv2.setGravity(Gravity.RIGHT);
					tv2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
					ll.addView(tv2);
				}
				else
				{
					TextView tv = new TextView(this);
					tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					tv.setTextSize(18);
					tv.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
					tv.setTextColor(Color.WHITE);
					tv.setText(message);
					tv.setBackgroundColor(Color.parseColor("#FF009966"));
					tv.setGravity(Gravity.RIGHT);
					ll.addView(tv);
				
					TextView tv2 = new TextView(this);
					tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT));
					tv2.setTextSize(14);
					tv2.setTextColor(Color.BLACK);
					tv2.setText(date);
					tv2.setGravity(Gravity.LEFT);
					tv2.setBackgroundColor(Color.parseColor("#FF009966"));
					ll.addView(tv2);
				}
				
				message="";
				name="";
				date ="";
			}
			else
			{
				message+=history.charAt(i);
			}
		}
	}
}
