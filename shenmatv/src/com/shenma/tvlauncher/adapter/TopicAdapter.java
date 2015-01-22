package com.shenma.tvlauncher.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TopicAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<VodDataInfo> datas;
	private ViewHoder mViewHoder;
	private int index;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public TopicAdapter(Context mContext, List<VodDataInfo> data) {
		this.mContext = mContext;
		this.datas = data;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_film_img)
		// 默认图片
		.showImageForEmptyUri(R.drawable.default_film_img) 
		.showImageOnFail(R.drawable.default_film_img)
		.resetViewBeforeLoading(true).cacheInMemory(true)
		.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
	}
	
	@Override
	public int getCount() {
		return datas==null?0:datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas==null?null:datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

//	public void setSelectItem(int index) {
//		if(this.index != index) {
//			this.index = index;
//			notifyDataSetChanged();
//		}
//	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			mViewHoder = new ViewHoder();
			convertView = View.inflate(mContext, R.layout.topic_item, null);
			mViewHoder.image = (ImageView) convertView.findViewById(R.id.topic_item_image);
			convertView.setTag(mViewHoder);
		} else {
			mViewHoder = (ViewHoder) convertView.getTag();
		}
		imageLoader.displayImage(datas.get(position).getPic(), mViewHoder.image, options);
		//mViewHoder.image.setImageResource(R.drawable.default_film_img);
		if(index == position) {
			//选中的效果
//			mViewHoder.image.setBackgroundResource(R.drawable.default_film_img);
//			mViewHoder.image.setImageResource(R.drawable.topic_focus);
		} else {
			//普通效果
		}
		
		return convertView;
	}

	private class ViewHoder {
		public ImageView image;
	}
	
	private int getResPX(int resId) {
		return mContext.getResources().getDimensionPixelSize(resId);
	}
}
