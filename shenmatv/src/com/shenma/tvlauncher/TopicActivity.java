package com.shenma.tvlauncher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenma.tvlauncher.adapter.TopicAdapter;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.ImageUtil;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.Reflect3DImage;
import com.shenma.tvlauncher.vod.VodDetailsActivity;
import com.shenma.tvlauncher.vod.domain.VodDataInfo;
import com.shenma.tvlauncher.vod.domain.VodTypeInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TopicActivity extends BaseActivity {
	
	private Gallery topic_detail_gl;
	private TextView topic_detail_msg_tv,tv_topic_name;
	private TopicAdapter mAdapter;
	private ImageView iv_topic_poster;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_detail);
		initView();
		initData();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(null!=mQueue){
			mQueue.stop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null!=mQueue){
			mQueue.cancelAll(this);
		}
	};
	private void initIntent() {
		Intent intent = getIntent();
		vodtype = intent.getStringExtra("TYPE");
		describe = intent.getStringExtra("describe");
		bigpic = intent.getStringExtra("bigpic");
		linkurl = intent.getStringExtra("linkurl");
		try {
			linkurl = URLEncoder.encode(linkurl, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mQueue = Volley.newRequestQueue(TopicActivity.this, new HurlStack());
		GsonRequest<VodTypeInfo> mVodData = new GsonRequest<VodTypeInfo>(Method.GET, Constant.TOPIC_HEAD_URL+linkurl,
				VodTypeInfo.class,createVodDataSuccessListener(),createVodDataErrorListener());
		mQueue.add(mVodData); 
	}

	@Override
	protected void initView() {
		loadViewLayout();
		findViewById();
		setListener();
		options = new DisplayImageOptions.Builder()
		//.showStubImage(R.color.dark_404040)
		// 默认图片
		//.showImageForEmptyUri(R.color.dark_404040)
		.showImageOnFail(R.drawable.hao260x366)
		.resetViewBeforeLoading(true).cacheInMemory(true)
		.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	@Override
	protected void loadViewLayout() {
		
	}

	@Override
	protected void findViewById() {
		topic_detail_gl = (Gallery) findViewById(R.id.topic_detail_gl);
		topic_detail_msg_tv = (TextView) findViewById(R.id.topic_detail_msg_tv);
		tv_topic_name = (TextView) findViewById(R.id.tv_topic_name);
		iv_topic_poster = (ImageView) findViewById(R.id.iv_topic_poster);
		topic_bg = (ImageView) findViewById(R.id.topic_bg);
	}

	@Override
	protected void setListener() {
		topic_detail_gl.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				mAdapter.setSelectItem(position);
				setTopicPoster(vodDatas.get(position).getPic());
				tv_topic_name.setText(vodDatas.get(position).getTitle());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		topic_detail_gl.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				VodDataInfo vod = vodDatas.get(position);
				Bundle pBundle = new Bundle();
				pBundle.putString("vodtype",vodtype);
				pBundle.putString("vodstate","专题");
				pBundle.putString("nextlink",vod.getNextlink());
				openActivity(VodDetailsActivity.class, pBundle);
			}
		});
	}
	
	private void initData() {
		initIntent();
		imageLoader.displayImage(Constant.HEARD_URL+bigpic, topic_bg);
		topic_detail_msg_tv.setText(describe);
	}
	
	//影视数据请求成功
    private Response.Listener<VodTypeInfo> createVodDataSuccessListener() {
        return new Response.Listener<VodTypeInfo>() {
            @Override
            public void onResponse(VodTypeInfo response) {
    			if (response != null) {
    				vodDatas = (ArrayList<VodDataInfo>) response.getData();
					mAdapter = new TopicAdapter(context, vodDatas);
					topic_detail_gl.setAdapter(mAdapter);
    			}else{
    				Logger.v("joychang", "获取数据失败！");
    			}
    		
            }
        };
    }
    
    //影视数据请求失败
    private Response.ErrorListener createVodDataErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Utils.showToast(context,"亲，没有搜索到相关内容！",R.drawable.toast_err);
            }
        };
    }

	
	
	private void setTopicPoster(String url){
		imageLoader.displayImage(url,iv_topic_poster, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				Drawable drawable = iv_topic_poster.getDrawable();
				if(null!=drawable){
					Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
					Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
					iv_topic_poster.setImageBitmap(bit);
				}
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				Drawable drawable = iv_topic_poster.getDrawable();
				if(null!=drawable){
					Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
					Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
					iv_topic_poster.setImageBitmap(bit);
				}
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				Drawable drawable = iv_topic_poster.getDrawable();
				if(null!=drawable){
					Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
					Bitmap bit = Reflect3DImage.skewImage(bitmap, 60);
					iv_topic_poster.setImageBitmap(bit);
				}
			}
		});
	}
	
	private String vodtype;
	private String describe;
	private String  bigpic;
	private String  linkurl;
	public RequestQueue mQueue;
	private ArrayList<VodDataInfo> vodDatas;
	private ImageView topic_bg;

}
