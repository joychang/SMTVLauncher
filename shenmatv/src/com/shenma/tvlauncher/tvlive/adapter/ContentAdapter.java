package com.shenma.tvlauncher.tvlive.adapter;

import java.util.ArrayList;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.tvlive.network.LiveConstant;
import com.shenma.tvlauncher.tvlive.parsexml.NetMedia;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContentAdapter extends BaseAdapter {
	private ArrayList<NetMedia> netMediaCollection;
	private LayoutInflater mInflater;
	TextView contentText = null;
	private int currentPosition = 0;

	public ContentAdapter(ArrayList<NetMedia> netMediaCollection,
			Context context) {
		this.netMediaCollection = netMediaCollection;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return netMediaCollection.size();
	}

	public Object getItem(int arg0) {
		return netMediaCollection.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View view, ViewGroup arg2) {

		if (view == null) {
			view = mInflater.inflate(R.layout.live_fourlink, null);
			contentText = (TextView) view.findViewById(R.id.fourlink);
			view.setTag(contentText);
		} else {
			contentText = (TextView) view.getTag();
		}
		int channlePosition = netMediaCollection.get(position)
				.getTotlePosition();
		String strPosition = null;
		if (channlePosition < 10) {
			strPosition = "00" + channlePosition;
		} else if (channlePosition < 100) {
			strPosition = "0" + channlePosition;
		} else {
			strPosition = "" + channlePosition;
		}
		contentText.setText(strPosition + "  "
				+ netMediaCollection.get(position).getChannlename());
		// if(position==currentPosition){
		// view.setBackgroundResource(R.drawable.selected_item_bg);
		// }else{
		// view.setBackgroundResource(R.drawable.media_list_selector);
		// }
		return view;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
		notifyDataSetChanged();
	}

}
