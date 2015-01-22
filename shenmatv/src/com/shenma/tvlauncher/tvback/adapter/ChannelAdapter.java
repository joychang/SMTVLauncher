package com.shenma.tvlauncher.tvback.adapter;

import java.util.ArrayList;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.tvback.domain.ChannelInfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @Description	节目频道的Adapter
 * @author joychang
 *
 */
public class ChannelAdapter extends BaseAdapter {
	private ArrayList<ChannelInfo> mChannels;
	private ViewHolder holder;
	private Context context;
	private LayoutInflater mInflater;

	public ChannelAdapter(Context context, ArrayList<ChannelInfo> mChannels) {
		this.context = context;
		this.mChannels = mChannels;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.lv_tv_back_channel_item, null);
			holder = new ViewHolder();
			holder.tv_channel = (TextView) convertView
					.findViewById(R.id.tv_back_channel_item_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChannelInfo info = mChannels.get(position);
		holder.tv_channel.setText(info.getChanneName());
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(null!=mChannels){
			return mChannels.size();
		}
		Log.i("joychang", "mChannels.size()=" + mChannels.size());
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mChannels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder {
		TextView tv_channel;
	}
}
