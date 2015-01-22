package com.shenma.tvlauncher.vod.adapter;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.vod.adapter.VodtypeAdapter.ViewHolder;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodUrl;
public class DetailsBottomListAdapter extends BaseAdapter {
	private Context context;
	private List<VodUrl> list;
	private LayoutInflater mInflater;

	public DetailsBottomListAdapter(Context paramContext, List<VodUrl> paramList) {
		this.context = paramContext;
		this.list = paramList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void changData(List<VodUrl> paramArrayList) {
		list = paramArrayList;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.mv_details_key_list_item, null);
//			convertView.getLayoutParams().height = context.getResources().getDimensionPixelSize(R.dimen.sm_55);
			TextView lv_text = (TextView) convertView.findViewById(R.id.lv_text);
			lv_text.setText(list.get(position).getTitle());
			return convertView;
	}

}