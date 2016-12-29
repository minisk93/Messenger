package edu.example.mymessenger;

import edu.data.chat.MessageData;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuFragment extends Fragment implements OnClickListener
{
	private Button btn_messages;
	private Button btn_people;
	private Button btn_exit;
	private FragmentTransaction ft;
	private PeopleFragment pf;
	private MessageFragment mf;
	private AlertDialog.Builder adb;
	private AlertDialog al;
	private MainActivity main;
	private MessageData md;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.menu_fragment,null);
		btn_messages = (Button)v.findViewById(R.id.btn_messages);
		btn_people = (Button)v.findViewById(R.id.btn_people);
		btn_exit = (Button)v.findViewById(R.id.btn_exit);
		
		btn_messages.setOnClickListener(this);
		btn_people.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		
		main = ((MainActivity)getActivity());
		
		return v;
	}


	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.btn_people:pf = new PeopleFragment();
								ft = getFragmentManager().beginTransaction();
								ft.setCustomAnimations(R.animator.items_in,R.animator.items_out);
								ft.replace(R.id.details_fl,pf);
								ft.commit();
				
				
				break;
			
			case R.id.btn_messages:mf = new MessageFragment();
			ft = getFragmentManager().beginTransaction();
			ft.setCustomAnimations(R.animator.items_in,R.animator.items_out);
			ft.replace(R.id.details_fl,mf);
			ft.commit();
				
			break;
			
			case R.id.btn_exit:
				
				adb = new AlertDialog.Builder(main);
				adb.setTitle("Exit");
				adb.setMessage("Вы действительно хотите выйти?");
				adb.setNegativeButton("Нет",new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						dialog.cancel();
					}
				});
				
				adb.setPositiveButton("Да",new DialogInterface.OnClickListener() 
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						md = new MessageData();
						md.setAction(MessageData.EXIT);
						md.setName(MainActivity.my_name);
						
						Thread t = new Thread(new SendRequest(md));
						t.start();
						main.finish();
					}
				});
				al = adb.create();
				al.show();
				
				break;
		}
		
	}
	
}
