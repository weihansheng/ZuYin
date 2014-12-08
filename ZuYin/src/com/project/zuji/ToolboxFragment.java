package com.project.zuji;

import java.util.ArrayList;

import com.project.zuji.activity.CompassActivity;
import com.project.zuji.activity.LightActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ToolboxFragment extends Fragment {

	private View rootView;
	private View toolCompass;
	private View toolLight;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_tools_box, container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}

		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		toolCompass=rootView.findViewById(R.id.toolCompass);
		toolLight=rootView.findViewById(R.id.toolLight);
		ClickListener click=new ClickListener();
		toolCompass.setOnClickListener(click);
		toolLight.setOnClickListener(click);
	}
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent=new Intent();
			// TODO Auto-generated method stub
			switch (v.getId()) {
			
			case R.id.toolCompass:
				intent.setClass(getActivity(), CompassActivity.class);
				break;

			case R.id.toolLight:
				intent.setClass(getActivity(), LightActivity.class);
				break;
			}
			startActivity(intent);
		}
		
	}

	
}
