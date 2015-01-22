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
public class SearchTypeAdapter extends BaseAdapter {
	public static List<VodDataInfo> vodDatas;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private Context context;
	private ViewHolder holder;
	private DisplayImageOptions options;

	public SearchTypeAdapter(Context context,ArrayList<VodDataInfo> datas,ImageLoader imageLoader) {
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
	
	public void changData(ArrayList<VodDataInfo> paramArrayList) {
		setVideos(paramArrayList);
		notifyDataSetChanged();
	}

	private void setVideos(ArrayList<VodDataInfo> paramArrayList) {
		if (paramArrayList != null) {
			this.vodDatas = paramArrayList;
			return;
		}
		ArrayList<VodDataInfo> localArrayList = new ArrayList<VodDataInfo>();
		this.vodDatas = localArrayList;		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.mv_type_details_item, null);
			holder = new ViewHolder();
			holder.poster = (ImageView) convertView
					.findViewById(R.id.video_poster);
			holder.state = (TextView) convertView
					.findViewById(R.id.video_state);
			holder.videoName = (TextView) convertView
					.findViewById(R.id.video_name);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		VodDataInfo vd = vodDatas.get(position);
		imageLoader.displayImage(vd.getPic(), holder.poster, options);
		holder.videoName.setText(vd.getTitle());
		holder.state.setText(vd.getState());
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
		private ImageView spuerHd;
		private TextView videoName;
	}
}
