package com.project.zuji;

import java.util.ArrayList;

import com.project.zuji.activity.CompassActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ToolboxFragment extends Fragment {

	private View rootView;
	private ListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.calendar, container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}

		listView = (ListView) rootView.findViewById(R.id.listView);
		initView();
		return rootView;
	}

	private void initView() {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1,
				getCalendarData());
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {
				Intent intent=new Intent();
				intent.setClass(getActivity(), CompassActivity.class);
				startActivity(intent);
			}
		});
	}

	private ArrayList<String> getCalendarData() {
		ArrayList<String> toolList = new ArrayList<String>();
		toolList.add("指南针");
		toolList.add("手电筒");
		return toolList;
	}
}
