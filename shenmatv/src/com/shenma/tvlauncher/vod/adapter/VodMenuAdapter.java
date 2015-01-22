package com.shenma.tvlauncher.vod.adapter;

import java.util.ArrayList;
import java.util.List;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.vod.VideoPlayerActivity;
import com.shenma.tvlauncher.vod.domain.MediaInfo;
import com.shenma.tvlauncher.vod.domain.VideoInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/***
 * 播放菜单设配器
 * 
 * @author joychang
 * 
 */
public class VodMenuAdapter<T> extends BaseAdapter {
	private Context context;
	private List<T> medialist;
	private LayoutInflater mInflater;
	private int type;
	private boolean isMenuItemShow = false;

	public VodMenuAdapter(Context context, List<T> medialist,int type,Boolean isMenuItemShow) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.medialist = medialist;
		this.type = type;
		this.isMenuItemShow = isMenuItemShow;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return medialist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return medialist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.mv_controler_menu_item, null);
			viewHolder = new ViewHolder();

			viewHolder.textView = (TextView) convertView
					.findViewById(R.id.tv_menu_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(type==0){
			viewHolder.textView.setText("第"+(position+1)+"集-"+((VideoInfo)medialist.get(position)).title);
		}else if(type==1){
			viewHolder.textView.setText(((MediaInfo)medialist.get(position)).getName());
		}else{
			viewHolder.textView.setText(((String)medialist.get(position)));
		}
		int mPosition = 0;
		viewHolder.textView.setTextColor(context.getResources().getColor(R.color.white));
		if(isMenuItemShow){
			switch (type) {
			case 0:
				mPosition = VideoPlayerActivity.xjposition;
				break;
			case 1:
				mPosition = VideoPlayerActivity.qxdposition;
				break;
			case 2:
				mPosition = VideoPlayerActivity.jmposition;
				break;
			case 3:
				mPosition = VideoPlayerActivity.hmblposition;
				break;
			case 4:
				mPosition = VideoPlayerActivity.phszposition;
				break;
			}
			if (mPosition==position) {
				viewHolder.textView.setTextColor(context.getResources().getColor(R.color.text_focus));
				convertView.setBackgroundResource(android.R.color.transparent);
			}else{
				convertView.setBackgroundResource(android.R.color.transparent);
			}
		}
		return convertView;
	}

}

	class ViewHolder {
		public TextView textView;
	}

