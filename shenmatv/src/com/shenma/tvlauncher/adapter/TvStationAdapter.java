package com.shenma.tvlauncher.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.domain.TVStationInfo;
import com.shenma.tvlauncher.utils.Constant;

public class TvStationAdapter extends BaseAdapter {

	private List<TVStationInfo> tvs;
	private Context context;
	private ViewHoder viewHoder;
	private ImageLoader imageLoader;

	public TvStationAdapter(Context context, List<TVStationInfo> tvs, ImageLoader imageLoader){
		this.context = context;
		this.tvs = tvs;
		this.imageLoader = imageLoader;
	}
	
	public void setData(List<TVStationInfo> tvs){
		this.tvs = tvs;
	}
	
	@Override
	public int getCount() {
		return tvs.size();
	}

	@Override
	public Object getItem(int position) {
		return tvs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			viewHoder = new ViewHoder();
			convertView = LayoutInflater.from(context).inflate(R.layout.tvstation_item, null);
			viewHoder.tvs_item_img_iv = (ImageView) convertView.findViewById(R.id.tvs_item_img_iv);
			convertView.setTag(viewHoder);
		}else {
			viewHoder = (ViewHoder) convertView.getTag();
		}
		imageLoader.get(Constant.HEARD_URL+tvs.get(position).getChannelpic(), ImageLoader.getImageListener(viewHoder.tvs_item_img_iv, android.R.color.transparent, android.R.color.transparent));
		return convertView;
	}
	
	private class ViewHoder{
		public ImageView tvs_item_img_iv;
	}

}
