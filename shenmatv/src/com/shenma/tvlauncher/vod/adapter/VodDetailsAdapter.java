package com.shenma.tvlauncher.vod.adapter;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.R;
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
 * 影视类型适配器
 * 
 * @author joychang
 * 
 */
public class VodDetailsAdapter extends BaseAdapter {
	private List<VodDataInfo> vodDatas;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private Context context;
	private ViewHolder holder;
	private DisplayImageOptions options;

	public VodDetailsAdapter(Context context,ArrayList<VodDataInfo> datas,ImageLoader imageLoader) {
		this.context = context;
		this.vodDatas = datas;
		this.imageLoader = imageLoader;
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.mv_video_details_recommend_item, null);
			holder = new ViewHolder();
			holder.iv_details_recommend_poster = (ImageView) convertView
					.findViewById(R.id.details_recommend_poster);
			holder.tv_details_recommend_name = (TextView) convertView
					.findViewById(R.id.details_recommend_name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		VodDataInfo vd = vodDatas.get(position);
		imageLoader.displayImage(vd.getPic(), holder.iv_details_recommend_poster, options);
		holder.tv_details_recommend_name.setText(vd.getTitle());
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
		private TextView tv_details_recommend_name;
		private ImageView iv_details_recommend_poster;

	}

}
