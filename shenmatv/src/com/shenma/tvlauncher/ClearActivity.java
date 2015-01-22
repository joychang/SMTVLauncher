package com.shenma.tvlauncher;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.vod.dao.VodDao;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ClearActivity extends BaseActivity {

	private RelativeLayout clear_setting_content_decode,clear_setting_content_definition,
							clear_setting_content_playratio,clear_setting_content_jump,clear_setting_other;
	private TextView all_cache_clear_tv;
	
	private VodDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_setting_clear);
		findViewById(R.id.setting_clear).setBackgroundResource(R.drawable.video_details_bg);
		initView();
	}

	@Override
	protected void initView() {
		dao = new VodDao(this);
		findViewById();
		setListener();
	}

	@Override
	protected void loadViewLayout() {
		
	}

	@Override
	protected void findViewById() {
		clear_setting_content_decode = (RelativeLayout) findViewById(R.id.clear_setting_content_decode);
		clear_setting_content_definition = (RelativeLayout) findViewById(R.id.clear_setting_content_definition);
		clear_setting_content_playratio = (RelativeLayout) findViewById(R.id.clear_setting_content_playratio);
		clear_setting_content_jump = (RelativeLayout) findViewById(R.id.clear_setting_content_jump);
		clear_setting_other = (RelativeLayout) findViewById(R.id.clear_setting_other);
		all_cache_clear_tv = (TextView) findViewById(R.id.all_cache_clear_tv);
	}

	@Override
	protected void setListener() {
		clear_setting_content_decode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.showToast(context, "图片缓存清理成功", R.drawable.toast_smile);
				//Toast.makeText(context, "图片缓存清理暂时不可用", 0).show();
			}
		});
		clear_setting_content_definition.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dao.deleteAllByWhere(Constant.TYPE_SC);
				Utils.showToast(context, "收藏记录清理成功", R.drawable.toast_smile);
			}
		});
		clear_setting_content_playratio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dao.deleteAllByWhere(Constant.TYPE_LS);
				Utils.showToast(context, "播放记录清理成功", R.drawable.toast_smile);
			}
		});
		clear_setting_content_jump.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dao.deleteAllByWhere(Constant.TYPE_ZJ);
				Utils.showToast(context, "追剧记录清理成功", R.drawable.toast_smile);
				//Utils.showToast(context, "自定义频道清理暂时不可用", R.drawable.toast_shut);
			}
		});
		all_cache_clear_tv.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					all_cache_clear_tv.setTextColor(0xFF000000);
				}else {
					all_cache_clear_tv.setTextColor(0xFFFFFFFF);
				}
			}
		});
		all_cache_clear_tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dao.deleteAllByWhere(Constant.TYPE_ZJ);
				dao.deleteAllByWhere(Constant.TYPE_SC);
				dao.deleteAllByWhere(Constant.TYPE_LS);
				Utils.showToast(context, "全部清理干净啦O(∩_∩)O", R.drawable.toast_smile);
			}
		});
		clear_setting_other.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.showToast(context, "自定义频道清理暂不可用", R.drawable.toast_smile);
			}
		});
	}
	
}
