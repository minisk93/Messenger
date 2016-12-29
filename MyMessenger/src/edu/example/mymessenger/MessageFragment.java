package edu.example.mymessenger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import edu.data.chat.MessageData;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class MessageFragment extends Fragment 
{
	private RadioGroup rg_recievers;
	private Button btn_add;
	private TextView tv_recievers;
	private EditText et_message;
	private Button btn_send;
	private MessageData md;
	private MainActivity main;
	public static LinearLayout ll_messages;
	private AlertDialog.Builder adb;
	private AlertDialog al;
	private LayoutInflater dialog_inflater;
	public static ListView ll_recievers;
	public static ListView lv_dialogs;
	private ArrayList<String> al_recievers;
	private String reciever;
	private EditText et_cipher_pas;
	private Thread thread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		md = new MessageData();
		main = ((MainActivity)getActivity());
		
		View v = inflater.inflate(R.layout.message_fragment,null);
		rg_recievers = (RadioGroup)v.findViewById(R.id.radioGroup2);
		btn_add = (Button)v.findViewById(R.id.btn_add);
		btn_send = (Button)v.findViewById(R.id.btn_send);
		tv_recievers = (TextView)v.findViewById(R.id.tv_recievers);
		et_message = (EditText)v.findViewById(R.id.et_message);
		ll_messages = (LinearLayout)v.findViewById(R.id.ll_mes);
		lv_dialogs = (ListView)v.findViewById(R.id.lv_dialogs);
		
		tv_recievers.setVisibility(TextView.INVISIBLE);
		
		btn_add.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				dialog_inflater = (LayoutInflater)main.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				
				adb = new AlertDialog.Builder(main);
				adb.setTitle("Добавить получателя");
				View view_reciever = dialog_inflater.inflate(R.layout.recievers,null);
				ll_recievers = (ListView)view_reciever.findViewById(R.id.ll_adresats);
				
				int selectedId = rg_recievers.getCheckedRadioButtonId();
				
				if(selectedId == R.id.rb_single)
				{
					ll_recievers.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					md = new MessageData();
					md.setAction(MessageData.ONLINE_PEOPLE_LIST);
					md.setName("Recievers");
					md.setMessage("single");
					md.setIp(MainActivity.my_name);
				}
				else if(selectedId == R.id.rb_multiple)
				{
					ll_recievers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
					md = new MessageData();
					md.setAction(MessageData.ONLINE_PEOPLE_LIST);
					md.setName("Recievers");
					md.setMessage("multiple");
					md.setIp(MainActivity.my_name);
				}
				
				thread = new Thread(new SendRequest(md));
				thread.start();
				
				adb.setView(view_reciever);
				adb.setNeutralButton("OK",new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						if(ll_recievers.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE)
						{
							SparseBooleanArray checked = ll_recievers.getCheckedItemPositions();
							if(checked.size() == 0)
							{
								Toast.makeText(main,"No recievers added",Toast.LENGTH_SHORT).show();
								return;
							}
							al_recievers = new ArrayList<>();
							StringBuilder sb = new StringBuilder();
							
							for(int i=0;i<checked.size();i++)
							{
								int position = checked.keyAt(i);
								if (checked.valueAt(i))
								{
									al_recievers.add(MainActivity.adapter.getItem(position));
									sb.append(MainActivity.adapter.getItem(position)+";");
								}
							}
							tv_recievers.setVisibility(TextView.VISIBLE);
							tv_recievers.setText(sb);
						}
						else if(ll_recievers.getChoiceMode() == ListView.CHOICE_MODE_SINGLE)
						{
							int position = ll_recievers.getCheckedItemPosition();
							if(position == -1)
							{
								Toast.makeText(main, "No reciever added",Toast.LENGTH_SHORT).show();
								return;
							}
							reciever = MainActivity.adapter.getItem(position);
							tv_recievers.setVisibility(TextView.VISIBLE);
							tv_recievers.setText(reciever);
						}
						
					}
				});
				
				al = adb.create();
				al.show();
					
			}
		});
		
		btn_send.setOnClickListener(new OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(al_recievers == null && reciever == null)
				{
					Toast.makeText(main, "Add reciever(s) to send message",Toast.LENGTH_LONG).show();
					return;
				}
				
				md = new MessageData();
				
				if(ll_recievers.getChoiceMode() == ListView.CHOICE_MODE_SINGLE)
					{
						md.setAction(MessageData.ENCRYPTED_SINGLE);
						
						if(!MainActivity.is_cipher)
						{
							dialog_inflater = (LayoutInflater)main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							View pas_view = dialog_inflater.inflate(R.layout.cipher_key,null);
							et_cipher_pas = (EditText)pas_view.findViewById(R.id.et_cipher_pas);
							adb = new AlertDialog.Builder(main);
							adb.setTitle("Ввод ключа");
							adb.setView(pas_view);
							adb.setPositiveButton("OK",new DialogInterface.OnClickListener() 
							{
							
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									String pas = et_cipher_pas.getText().toString();
									if(!pas.equals("negstcerb777"))
									{
										Toast.makeText(main, "Wrong password",Toast.LENGTH_LONG).show();
										return;
									}
								
									TextView tv = new TextView(main);
									tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
										ViewGroup.LayoutParams.WRAP_CONTENT));
									tv.setTextSize(18);
									tv.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
									tv.setTextColor(Color.BLACK);
									tv.setText(MainActivity.my_name+": "+et_message.getText().toString());
									tv.setGravity(Gravity.LEFT);
									tv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
									ll_messages.addView(tv);
									
									TextView tv2 = new TextView(main);
									tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
											ViewGroup.LayoutParams.WRAP_CONTENT));
									tv2.setTextSize(14);
									tv2.setTextColor(Color.BLACK);
									tv2.setText(getDate());
									tv2.setGravity(Gravity.RIGHT);
									tv2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
									ll_messages.addView(tv2);
								
									md.setReciever(reciever);
									md.setName(MainActivity.my_name);
									md.setMessage(MainActivity.my_name+": "+et_message.getText().toString()+"\n"+"\u2666"+getDate()+"\u2663");
									Thread t = new Thread(new SendRequest(md));
									t.start();
									MainActivity.is_cipher = true;
							}
						});
						
						al = adb.create();
						al.show();
						}
						else
						{
							TextView tv = new TextView(main);
							tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.WRAP_CONTENT));
							tv.setTextSize(18);
							tv.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
							tv.setTextColor(Color.BLACK);
							tv.setText(MainActivity.my_name+": "+et_message.getText().toString());
							tv.setGravity(Gravity.LEFT);
							tv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
							ll_messages.addView(tv);
							
							TextView tv2 = new TextView(main);
							tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT));
							tv2.setTextSize(14);
							tv2.setTextColor(Color.BLACK);
							tv2.setText(getDate());
							tv2.setGravity(Gravity.RIGHT);
							tv2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
							ll_messages.addView(tv2);
						
							md.setReciever(reciever);
							md.setName(MainActivity.my_name);
							md.setMessage(MainActivity.my_name+": "+et_message.getText().toString()+"\n"+"\u2666"+getDate()+"\u2663");
							Thread t = new Thread(new SendRequest(md));
							t.start();
						}
					}
					else
					{
						md.setAction(MessageData.ENCRYPTED_MULTIPLE);
						
						if(!MainActivity.is_cipher)
						{
							dialog_inflater = (LayoutInflater)main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							View pas_view = dialog_inflater.inflate(R.layout.cipher_key,null);
							et_cipher_pas = (EditText)pas_view.findViewById(R.id.et_cipher_pas);
							adb = new AlertDialog.Builder(main);
							adb.setTitle("Ввод ключа");
							adb.setView(pas_view);
							adb.setPositiveButton("OK",new DialogInterface.OnClickListener() 
							{
							
								@Override
								public void onClick(DialogInterface dialog, int which) 
								{
									String pas = et_cipher_pas.getText().toString();
									if(!pas.equals("negstcerb777"))
									{
										Toast.makeText(main, "Wrong password",Toast.LENGTH_LONG).show();
										return;
									}
								
									TextView tv = new TextView(main);
									tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
											ViewGroup.LayoutParams.WRAP_CONTENT));
									tv.setTextSize(18);
									tv.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
									tv.setTextColor(Color.BLACK);
									tv.setText(MainActivity.my_name+": "+et_message.getText().toString());
									tv.setGravity(Gravity.LEFT);
									tv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
									ll_messages.addView(tv);
									
									TextView tv2 = new TextView(main);
									tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
											ViewGroup.LayoutParams.WRAP_CONTENT));
									tv2.setTextSize(14);
									tv2.setTextColor(Color.BLACK);
									tv2.setText(getDate());
									tv2.setGravity(Gravity.RIGHT);
									tv2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
									ll_messages.addView(tv2);
								
									md.setName(MainActivity.my_name);
									md.setSend_list(al_recievers);
									md.setMessage(MainActivity.my_name+": "+et_message.getText().toString()+"\n"+"\u2666"+getDate()+"\u2663");
									Thread t = new Thread(new SendRequest(md));
									t.start();
									MainActivity.is_cipher = true;
							
								}
							});
						
							al = adb.create();
							al.show();
						}
						else
						{
							TextView tv = new TextView(main);
							tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT));
							tv.setTextSize(18);
							tv.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);
							tv.setTextColor(Color.BLACK);
							tv.setText(MainActivity.my_name+": "+et_message.getText().toString());
							tv.setGravity(Gravity.LEFT);
							tv.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
							ll_messages.addView(tv);
							
							TextView tv2 = new TextView(main);
							tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
									ViewGroup.LayoutParams.WRAP_CONTENT));
							tv2.setTextSize(14);
							tv2.setTextColor(Color.BLACK);
							tv2.setText(getDate());
							tv2.setGravity(Gravity.RIGHT);
							tv2.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
							ll_messages.addView(tv2);
						
							md.setName(MainActivity.my_name);
							md.setSend_list(al_recievers);
							md.setMessage(MainActivity.my_name+": "+et_message.getText().toString()+"\n"+"\u2666"+getDate()+"\u2663");
							Thread t = new Thread(new SendRequest(md));
							t.start();
						}
					}
				}
		});
		
		View v_send = inflater.inflate(R.layout.for_tab, null);
	       
	    TabHost tabs = (TabHost)v.findViewById(android.R.id.tabhost);
	       
	    tabs.setup();

	    TabHost.TabSpec spec = tabs.newTabSpec("tab_send");
	    spec.setContent(R.id.tab1);
		spec.setIndicator(v_send);
		((TextView)v_send).setText("Написать сообщение");
		tabs.addTab(spec);
		
		View v_history = inflater.inflate(R.layout.for_tab, null);
		spec = tabs.newTabSpec("tab_history");
	    spec.setContent(R.id.tab2);
		spec.setIndicator(v_history);
		((TextView)v_history).setText("История сообщений");
		tabs.addTab(spec);
		
		tabs.setCurrentTab(0);
		
		tabs.setOnTabChangedListener(new OnTabChangeListener() 
		{
			
			@Override
			public void onTabChanged(String tabId) 
			{
				if(tabId.equals("tab_history"))
				{
					md = new MessageData();
					md.setAction(MessageData.DIALOGS_LIST);
					md.setName(MainActivity.my_name);
				
					thread = new Thread(new SendRequest(md));
					thread.start();
				}
				
			}
		});
		
		return v;
		
	}
	
	 
	private String getDate()
	{
		Calendar c = Calendar.getInstance();
		
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm",Locale.US);
		String formattedDate = df.format(c.getTime());
		
		return formattedDate;
	}
}
