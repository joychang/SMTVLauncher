package com.shenma.tvlauncher.vod.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import com.shenma.tvlauncher.R;

public class TypeDetailsSubMenuAdapter extends BaseAdapter {
	private Context context;
	private List<String> lists;
	private int selcted = -1;

	public TypeDetailsSubMenuAdapter(Context paramContext,
			List<String> paramArrayList) {
		this.context = paramContext;
		if (paramArrayList != null) {
			this.lists = paramArrayList;
			return;
		}
		ArrayList localArrayList = new ArrayList();
		this.lists = localArrayList;
	}

	public int getCount() {
		return this.lists.size();
	}

	public Object getItem(int paramInt) {
		return this.lists.get(paramInt);
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		View localView = LayoutInflater.from(this.context).inflate(R.layout.mv_type_details_filter_item,
				null);
		TextView localTextView = (TextView) localView.findViewById(R.id.filter_name);
		ImageView localImageView = (ImageView) localView
				.findViewById(R.id.filter_gou);
		if (this.selcted == paramInt) {
			localImageView.setVisibility(0);
			localView.setBackgroundResource(R.drawable.filter_sleted);
		}
		CharSequence localCharSequence = (CharSequence) this.lists.get(paramInt);
		localTextView.setText(localCharSequence);
		return localView;
	}

	public void setSelctItem(int paramInt) {
		this.selcted = paramInt;
		notifyDataSetChanged();
	}
}