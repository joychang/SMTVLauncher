package com.shenma.tvlauncher.tvback.adapter;

import java.util.ArrayList;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.application.MyApplication;
import com.shenma.tvlauncher.tvback.TVBackActivity;
import com.shenma.tvlauncher.tvback.domain.ChannelInfo;
import com.shenma.tvlauncher.tvback.domain.ProgramInfo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @Description	节目目录的Adapter
 * @author joychang
 *
 */
public class ProgramAdapter extends BaseAdapter {
	private ArrayList<ProgramInfo> mPrograms;
	private ViewHolder holder;
	private Context context;
	private LayoutInflater mInflater;
	private MyApplication mApp;
	private static View mView;
	public ProgramAdapter(Context context, ArrayList<ProgramInfo> mPrograms) {
		this.context = context;
		this.mPrograms = mPrograms;
		mInflater = LayoutInflater.from(context);
		mApp = (MyApplication) ((Activity) context).getApplication();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.lv_tv_back_column_item, null);
			holder = new ViewHolder();
			holder.fl_item = (FrameLayout) convertView.findViewById(R.id.fl_item);
			holder.tv_Program_time = (TextView) convertView
					.findViewById(R.id.tv_back_time);
			holder.tv_Program_name = (TextView) convertView
					.findViewById(R.id.tv_back_name);
			holder.im_program_state = (ImageView) convertView
					.findViewById(R.id.tv_back_log);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProgramInfo info = (ProgramInfo) mPrograms.get(position);
		holder.tv_Program_name.setText(info.getProgramName());
		holder.tv_Program_time.setText(info.getTime());
		String sate = mApp.getOnPlay();
		//Log.v("joychang", "sate==="+sate);
		if(null!=sate && sate.equals(TVBackActivity.rbChecked+info.getTime()+info.getProgramName())){
			mView = convertView;
			holder.tv_Program_name.setText(info.getProgramName()+"      （再次点击全屏显示）");
			holder.im_program_state.setImageResource(R.drawable.onplay);
		}else{
			holder.tv_Program_name.setText(info.getProgramName());
			holder.im_program_state.setImageResource(R.drawable.huikan);
		}
//		holder.fl_item.setClickable(true);
//		holder.fl_item.setOnClickListener(new OnClickImpl(position,holder));
		return convertView;
	}
	
	public static void setViewSate(){
		if(null!=mView){
			mView.findViewById(R.id.tv_back_log).setBackgroundResource(R.drawable.huikan);
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		if(null!=mPrograms){
			return mPrograms.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mPrograms.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	class ViewHolder {
		FrameLayout fl_item;
		TextView tv_Program_time;
		TextView tv_Program_name;
		ImageView im_program_state;
	}
}
