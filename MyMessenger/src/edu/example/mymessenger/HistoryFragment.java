package edu.example.mymessenger;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryFragment extends Fragment 
{
	public static TextView tv_history;
	public static LinearLayout ll_dialogs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.history_fragment,null);
		tv_history = (TextView)v.findViewById(R.id.tv_history);
		ll_dialogs = (LinearLayout)v.findViewById(R.id.ll_dialog);
		return v;
		
	}

	
}
