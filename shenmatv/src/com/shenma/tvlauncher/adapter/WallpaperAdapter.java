package com.shenma.tvlauncher.adapter;


import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.application.MyVolley;
import com.shenma.tvlauncher.domain.WallpaperInfo;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;

public class WallpaperAdapter extends BaseAdapter {

	private List<WallpaperInfo> wallpaperes;
	private Context context;
	private ViewHoder viewHoder;
	private ImageLoader imageLoader;

	public WallpaperAdapter(Context context, List<WallpaperInfo> wallpaperes){
		this.context = context;
		this.wallpaperes = wallpaperes;
		this.imageLoader = MyVolley.getImageLoader();
	}
	
	public void setData(List<WallpaperInfo> wallpaperes){
		this.wallpaperes = wallpaperes;
	}
	
	@Override
	public int getCount() {
		return wallpaperes.size();
	}

	@Override
	public Object getItem(int position) {
		return wallpaperes.get(position);
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
		imageLoader.get(Constant.HEARD_URL+wallpaperes.get(position).getSkinpath(), ImageLoader.getImageListener(viewHoder.tvs_item_img_iv, android.R.color.transparent, android.R.color.transparent),201,150);
		Logger.d("zhouchuan", Constant.HEARD_URL+wallpaperes.get(position).getSkinpath());
		return convertView;
	}
	
	private class ViewHoder{
		public ImageView tvs_item_img_iv;
	}

}
