package com.shenma.tvlauncher.tvlive.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.tvlive.parsexml.NetMedia;
public class SourceAdapter extends BaseAdapter {

	private ArrayList<NetMedia>mSources =  null;
	private Context mContext =  null;
	private LayoutInflater mInflater = null;
	TextView contentText = null;
	private int currentPosition =0;
	public SourceAdapter(Context context,ArrayList<NetMedia>sources){
		mContext = context;
		mSources = sources;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() {
		return mSources.size();
	}

	public Object getItem(int arg0) {
		return arg0;
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.live_fourlink, null);
			contentText = (TextView) arg1.findViewById(R.id.fourlink);
			arg1.setTag(contentText);
		} else {
			contentText = (TextView) arg1.getTag();
		}
		contentText.setText(mSources.get(arg0).getSource());
		Log.d("joychang", mSources.get(arg0).getSource());
		return arg1;
		}
	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
		notifyDataSetChanged();
	}
	
	}

