package com.shenma.tvlauncher.adapter;

import java.util.ArrayList;
import java.util.List;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.dao.bean.AppInfo;
import com.shenma.tvlauncher.vod.db.Album;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/***
 * 用户二级界面类型适配器
 * 
 * @author joychang
 * 
 */
public class UserTypeAdapter<T> extends BaseAdapter {
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private Context context;
	private Boolean ISAPP;
	private ViewHolder holder;
	private DisplayImageOptions options;
	private List<T> vodDatas;

	public UserTypeAdapter(Context context,List<T> datas,ImageLoader imageLoader,Boolean ISAPP) {
		this.context = context;
		this.vodDatas = new ArrayList<T>();
		vodDatas.addAll(datas);
		this.imageLoader = imageLoader;
		this.ISAPP = ISAPP;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.default_film_img)//默认图片
		.showImageForEmptyUri(R.drawable.default_film_img)
		.showImageOnFail(R.drawable.default_film_img)
		.resetViewBeforeLoading(true)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
	}
	
	public void changData(ArrayList<T> paramArrayList) {
		setVideos(paramArrayList);
		//notifyDataSetChanged();
	}

	public void setAppData(List applist) {
		if(applist != null) {
			this.vodDatas = applist;
		}
	}
	private void setVideos(ArrayList<T> paramArrayList) {
		if (paramArrayList != null) {
			this.vodDatas = paramArrayList;
			return;
		}
		ArrayList<T> localArrayList = new ArrayList<T>();
		this.vodDatas = localArrayList;		
	}
	public void clearDatas(){
		if(null!=vodDatas&&vodDatas.size()>0){
			vodDatas.clear();
		}
	}
	
	public void remove(int index){
		if(null!=vodDatas&&vodDatas.size()>index){
			vodDatas.remove(index);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(ISAPP){
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.my_app_item, null);
				holder = new ViewHolder();
				holder.app_icon = (ImageView) convertView
						.findViewById(R.id.app_icon);
				holder.app_title = (TextView) convertView
						.findViewById(R.id.app_title);
				holder.packflag = (TextView) convertView
						.findViewById(R.id.packflag);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			AppInfo appinfo = (AppInfo)vodDatas.get(position);
			holder.app_icon.setImageDrawable(appinfo.getAppicon());
			holder.app_title.setText(appinfo.getAppname());
			holder.packflag.setText(appinfo.getApppack());
		}else{
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.user_type_details_item, null);
				holder = new ViewHolder();
				holder.poster = (ImageView) convertView
						.findViewById(R.id.user_video_poster);
				holder.checked = (ImageView) convertView
						.findViewById(R.id.user_video_checked);
				holder.state = (TextView) convertView
						.findViewById(R.id.user_video_state);
				holder.videoName = (TextView) convertView
						.findViewById(R.id.user_video_name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			Album vd = (Album)vodDatas.get(position);
			imageLoader.displayImage(vd.getAlbumPic(), holder.poster, options);
			holder.videoName.setText(vd.getAlbumTitle());
			holder.state.setText(vd.getAlbumState());
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return vodDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return vodDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	class ViewHolder {
		private TextView state;
		private ImageView poster;
		private ImageView checked;
		private TextView videoName;
		
		private TextView app_title;
		private TextView packflag;
		private ImageView app_icon;
	}
}
