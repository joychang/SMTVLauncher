package com.shenma.tvlauncher.vod.adapter;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.shenma.tvlauncher.R;
/**
 * @Dericption 影视详情选集右边Grid
 * @author joychang
 *
 */
public class DetailsBottomGridAdapter extends BaseAdapter {
	private Context context;
	private List<String> list;
	private LayoutInflater mInflater;

	public DetailsBottomGridAdapter(Context paramContext, List<String> paramList) {
		this.context = paramContext;
		this.list = paramList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public void changData(ArrayList<String> paramArrayList) {
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
			convertView = mInflater.inflate(R.layout.mv_details_key_grid_item, null);
			TextView gv_text = (TextView) convertView.findViewById(R.id.gv_text);
			gv_text.setText(list.get(position));
			return convertView;
	}

}