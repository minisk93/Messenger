package edu.example.mymessenger;

import edu.data.chat.MessageData;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class PeopleFragment extends Fragment 
{
	public static ListView lv_all_people;
	public static ListView lv_online_people;
	private MessageData md;
	private Thread thread;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		
		View v = inflater.inflate(R.layout.people_fragment,null);
		lv_all_people = (ListView)v.findViewById(R.id.lv_all_people);
		lv_online_people = (ListView)v.findViewById(R.id.lv_online_people);
		
		md = new MessageData();
		md.setName("People");
		md.setAction(MessageData.ALL_PEOPLE_LIST);
		md.setIp(MainActivity.my_name);
		md.setMessage("all");
		
		thread = new Thread(new SendRequest(md));
		thread.start();
		
		View v_all = inflater.inflate(R.layout.for_tab, null);
	       
	    TabHost tabs = (TabHost)v.findViewById(android.R.id.tabhost);
	       
	    tabs.setup();

	    TabHost.TabSpec spec = tabs.newTabSpec("tab_all");
	    spec.setContent(R.id.tab1);
		spec.setIndicator(v_all);
		((TextView)v_all).setText("Все люди");
		tabs.addTab(spec);
		
		View v_online = inflater.inflate(R.layout.for_tab, null);
		spec = tabs.newTabSpec("tab_online");
	    spec.setContent(R.id.tab2);
		spec.setIndicator(v_online);
		((TextView)v_online).setText("Люди в сети");
		tabs.addTab(spec);
		
		tabs.setCurrentTab(0);
		
		tabs.setOnTabChangedListener(new OnTabChangeListener() 
		{
			
			@Override
			public void onTabChanged(String tabId) 
			{
				if(tabId.equals("tab_all"))
				{
					md = new MessageData();
					md.setName("People");
					md.setMessage("all");
					md.setAction(MessageData.ALL_PEOPLE_LIST);
					md.setIp(MainActivity.my_name);
					
					thread = new Thread(new SendRequest(md));
					thread.start();
					
				}
				else
				{
					md = new MessageData();
					md.setName("People");
					md.setMessage("online");
					md.setAction(MessageData.ONLINE_PEOPLE_LIST);
					md.setIp(MainActivity.my_name);
					
					thread = new Thread(new SendRequest(md));
					thread.start();
				}
			}
		});
		
		return v;
	}
	
}
