package com.shenma.tvlauncher.wifi;

import java.util.List;

import com.shenma.tvlauncher.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WifiAdapter extends BaseAdapter {

	private List<AccessPoint> mData;
	private Context ctx;
	private ViewHolder viewHolder;

	public WifiAdapter(Context context, List<AccessPoint> data) {
		ctx = context;
		mData = data;
	}

	@Override
	public int getCount() {
		if(mData==null){
			return 0;
		}
		return mData.size();
	}

	public void setData(List<AccessPoint> mData) {
		this.mData = mData;
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			viewHolder = new ViewHolder();
			convertView = View.inflate(ctx, R.layout.layout_setting_wifi_item, null);
			viewHolder.wifi_item_ssid_tv = (TextView) convertView.findViewById(R.id.wifi_item_ssid_tv);
			viewHolder.wifi_item_info_tv = (TextView) convertView.findViewById(R.id.wifi_item_info_tv);
			viewHolder.wifi_item_signal_iv = (ImageView) convertView.findViewById(R.id.wifi_item_signal_iv);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		AccessPoint ap = mData.get(position);
		viewHolder.wifi_item_ssid_tv.setText(ap.getSsid());

		if (ap.getmRssi() == Integer.MAX_VALUE) {
			viewHolder.wifi_item_signal_iv.setImageDrawable(null);
		} else {
			viewHolder.wifi_item_signal_iv.setImageResource((ap.getSecurity() != AccessPoint.SECURITY_NONE) ? R.drawable.wifi_lock_signal : R.drawable.wifi_signal);
			viewHolder.wifi_item_signal_iv.setImageLevel(ap.getLevel());
		}
		viewHolder.wifi_item_info_tv.setText(ap.getSummary());
		convertView.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				viewHolder.wifi_item_ssid_tv.setTextColor(0xFFFFFFFF);
				viewHolder.wifi_item_info_tv.setTextColor(0xFFFFFFFF);
			}
		});
		return convertView;
	}

	// ListView 中某项被选中后的逻辑
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Toast.makeText(ctx, "onListItemClick", Toast.LENGTH_LONG).show();
	}

	/**
	 * listview中点击按键弹出对话框
	 */
	public void showInfo() {
		Toast.makeText(ctx, "showInfo", Toast.LENGTH_LONG).show();

	}

	private class ViewHolder{
		ImageView wifi_item_signal_iv;
		TextView wifi_item_ssid_tv,wifi_item_info_tv;
	}

}
