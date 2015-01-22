package com.shenma.tvlauncher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.adapter.FragAdapter;
import com.shenma.tvlauncher.application.MyApplication;
import com.shenma.tvlauncher.domain.Update;
import com.shenma.tvlauncher.fragment.AppFragment;
import com.shenma.tvlauncher.fragment.TopicFragment;
import com.shenma.tvlauncher.fragment.RecommendFragment;
import com.shenma.tvlauncher.fragment.SettingFragment;
import com.shenma.tvlauncher.fragment.TVFragment;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.ui.DepthPageTransformer;
import com.shenma.tvlauncher.ui.FixedSpeedScroller;
import com.shenma.tvlauncher.utils.BlurUtils;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.LruCacheUtils;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.HomeDialog;
import com.shenma.tvlauncher.vod.VodTypeActivity;
import com.umeng.analytics.MobclickAgent;
public class HomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Logger.i(TAG, "ISTV="+ISTV);
		Logger.d(TAG, "HomeActivity....onCreate");
		MyApplication mApp = (MyApplication) getApplication();
		technology = mApp.getTechnology();
		mCacheUtils = LruCacheUtils.getInstance();
		homeFrom = from;
		homeParams = params;
		Logger.i(TAG, "technology="+technology);
		if("".equals(technology)){
			if (screenSize > 9) {
				ISTV = true;
				devicetype = "TV";
			} else {
				ISTV = false;
				devicetype = "MOBILE";
			}
		}else{
			if(null!=technology && !"null".equals(technology)){
				if (screenSize > 9) {
					ISTV = true;
					devicetype = "TV";
				} else {
					ISTV = false;
					devicetype = "MOBILE";
				}
			}else{
				ISTV = true;
				devicetype = "TV";
			}
		}
		initView();//初始化
		initData();//初始化数据
		//if(ISTV){
			initwhiteBorder();	
		//}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt("titile_position", titile_position);
		outState.putFloat("fromXDelta", fromXDelta);
		if(whiteBorder!=null){
			int wWidth = whiteBorder.getWidth();
			int wHeight = whiteBorder.getHeight();
			float wX = whiteBorder.getX();
			float wY = whiteBorder.getY();
			outState.putInt("wWidth", wWidth);
			outState.putFloat("wX", wX);
			outState.putInt("wHeight", wHeight);
			outState.putFloat("wY", wY);
			Logger.d(TAG, "onSaveInstanceState...wWidth="+wWidth+"--wHeight="+wHeight+"--wX="+wX+"--wY="+wY);
		}
		Logger.d(TAG, "onSaveInstanceState...");
		//outState.putInt("mPosition", value);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		Logger.d(TAG, "onRestoreInstanceState...");
		titile_position =  savedInstanceState.getInt("titile_position");
		float toX = savedInstanceState.getFloat("fromXDelta");
		if(titile_position != 0){
			mAnimationSet = new AnimationSet(true);
			mTranslateAnimation = new TranslateAnimation(
					fromXDelta,toX , 0f, 0f);
			initAnimation(mAnimationSet, mTranslateAnimation);
			iv_titile.startAnimation(mAnimationSet);//titile蓝色横条图片的动画切换
			fromXDelta = toX;
		}
		//initTitle(titile_position);
		if(whiteBorder!=null){
			whiteBorder.setVisibility(View.GONE);
			fl_main.removeView(whiteBorder);
			whiteBorder = null;
		}
		//if(ISTV){
			int wWidth = savedInstanceState.getInt("wWidth");
			int wHeight = savedInstanceState.getInt("wHeight");
			float wX = savedInstanceState.getFloat("wX");
			float wY = savedInstanceState.getFloat("wY");
			this.whiteBorder = new ImageView(this);
			fl_main.addView(whiteBorder);
			whiteBorder.setBackgroundResource(R.drawable.white_border);
			FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(wWidth, wHeight);
			layoutparams.leftMargin = (int)wX;
			layoutparams.topMargin = (int)wY;
			whiteBorder.setLayoutParams(layoutparams);
			whiteBorder.setVisibility(View.INVISIBLE);
			//initwhiteBorder();
			Logger.d(TAG, "fly...wWidth="+wWidth+"--wHeight="+wHeight+"--wX="+wX+"--wY="+wY);
			//flyWhiteBorder(wWidth, wHeight, wX, wY);
			//whiteBorder.setVisibility(View.VISIBLE);
		//}
	}	@Override
	protected void onStart() {
		super.onStart();
		//initTitle(titile_position);
		Logger.i(TAG, "HomeActivity....onStart");
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(null!=mQueue){
			mQueue.stop();
		}
		Logger.i(TAG, "HomeActivity....onStop");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
		Logger.i(TAG, "HomeActivity....onPause");
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
		//查询所有已安装的app
		queryInstalledApp();
		isRunning = true;
		Logger.i(TAG, "HomeActivity....onResume");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		//解除广播
		unregisterReceiver(mReceiver);
		unregisterReceiver(mWallReceiver);
		if(null!=mQueue){
			mQueue.cancelAll(this);
		}
		Logger.v(TAG, "HomeActivity....onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			 if (isRunning) {
//			int[] location = new int[2];
			//vpager.getLocationOnScreen(location);
//			vpager.getLocationInWindow(location);
//			Logger.i(TAG, "vpager...x="+location[0]);
//			Logger.i(TAG, "vpager...y="+location[1]);
//			Logger.i(TAG, "vpager.getY()="+vpager.getY());
//			Logger.i(TAG, "vpager.getTop()="+vpager.getTop());
//			int[] location1 = new int[2];
//			//vpager.getLocationOnScreen(location);
//			ll_rb.getLocationInWindow(location1);
//			Logger.i(TAG, "ll_rb...x="+location1[0]);
//			Logger.i(TAG, "ll_rb...y="+location1[1]);
//			Logger.i(TAG, "ll_rb.getY()="+ll_rb.getY());
//			Logger.i(TAG, "ll_rb.getHeight="+ll_rb.getHeight());
//			Logger.i(TAG, "ll_rb.getTop()="+ll_rb.getTop());
			 showExitDialog("神马视频", HomeActivity.this);
			 //showExitDialog();
			 Logger.v(TAG, "Activity isRunning");
			 }else{
			 Logger.v(TAG, "Activity not isRunning");
			 }
			 return true;
		}

		return super.onKeyDown(keyCode, event);

	}


	// 初始化
	protected void initView() {
		loadViewLayout();
		findViewById();
		setListener();
		//rb_recommend.requestFocus();
		//if(!ISTV){
			rb_recommend.setChecked(true);	
		//}
		//rb_recommend.setSelected(true);
		registerNetworkReceiver();
		registerPackageReceiver();
		registerWallpaperReceiver();
		homeHandler.sendEmptyMessageDelayed(WindowMessageID.REFLESH_TIME, 1000);// 刷新时间
	}
	
	private void initTitle(int position){
		Logger.i(TAG, "position="+position);
		if(position != 0){
			float toX = title_group.getChildAt(position).getX();
			Logger.v(TAG, "viewpage值="+position);
			Logger.v(TAG, "toX="+toX);
			mAnimationSet = new AnimationSet(true);
			mTranslateAnimation = new TranslateAnimation(
					fromXDelta,toX , 0f, 0f);
			initAnimation(mAnimationSet, mTranslateAnimation);
			iv_titile.startAnimation(mAnimationSet);//titile蓝色横条图片的动画切换
			fromXDelta = toX;
		}
	}
	
	private void registerWallpaperReceiver() {
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction("com.shenma.changewallpaper");
		registerReceiver(mWallReceiver, mFilter);
	}
	
	//数据初始化
	private void initData(){
		//请求更新
		Logger.d(TAG, "updataUrl="+ Constant.UPDATE_URL+params);
		//String version = Utils.getVersion(HomeActivity.this);
		mQueue = Volley.newRequestQueue(HomeActivity.this, new HurlStack());
		GsonRequest<Update> mUpdate = new GsonRequest<Update>(Method.GET, Constant.UPDATE_URL+params,
					Update.class,createMyReqSuccessListener(),createMyReqErrorListener()){
									@Override
                                    public Map<String, String> getHeaders()
                                    		throws AuthFailureError {
                						HashMap<String, String> headers = new HashMap<String, String>();
                						String base64 = new String(android.util.Base64.encode(
                								"admin:1234".getBytes(), android.util.Base64.DEFAULT));
                						headers.put("Authorization", "Basic " + base64);
                                    	return headers;
                                    }};
                                    
       mQueue.add(mUpdate);     //     执行                   

	}
	
	//请求成功
    private Response.Listener<Update> createMyReqSuccessListener() {
        return new Response.Listener<Update>() {
            @Override
            public void onResponse(Update response) {
            	if(null!=response && "200".equals(response.getCode())){
              		Logger.d(TAG, "版本更新");
            		if(null!=response.getData()){
            			String type = response.getData().getType();
        				// 是否提示更新版本
        				if (null != type && type.equals("1")) {
        					showUpdateDialog(response.getData().getVersionremark(),Constant.HEARD_URL+response.getData().getApkurl());// 版本不一致提示升级
        				}
            		}
            	}
            }
        };
    }
    
    //请求失败
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	if(error instanceof TimeoutError){
            		Logger.e(TAG, "请求超时");
            	}else if(error instanceof AuthFailureError){
            		Logger.e(TAG, "AuthFailureError="+error.toString());
                }
            }
        };
    }
	@Override
	protected void loadViewLayout() {
		
	}

	@Override
	protected void findViewById() {
		rl_bg = (RelativeLayout) findViewById(R.id.rl_bg);
		Bitmap bmp = mCacheUtils.getBitmapFromMemCache(String.valueOf(R.drawable.bg));
		if (bmp == null) {
			bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
			mCacheUtils.addBitmapToMemoryCache(String.valueOf(R.drawable.bg), bmp);
		}
		rl_bg.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
		fl_main = (FrameLayout) findViewById(R.id.fl_main);
		tv_time = (TextView) findViewById(R.id.tv_main_time);
		time_colon = (TextView) findViewById(R.id.time_colon);
		iv_net_state = (ImageView) findViewById(R.id.iv_net_state);
		iv_titile = (ImageView) findViewById(R.id.iv_titile);
		vpager = (ViewPager) findViewById(R.id.pager);
		title_group = (RadioGroup) findViewById(R.id.title_group);
		rb_recommend = (RadioButton) findViewById(R.id.rb_recommend);
		rb_Internet = (RadioButton) findViewById(R.id.rb_Internet);
		rb_video_type = (RadioButton) findViewById(R.id.rb_video_type);
		rb_user = (RadioButton) findViewById(R.id.rb_user);
		rb_app_store = (RadioButton) findViewById(R.id.rb_app_store);
		rb_settings = (RadioButton) findViewById(R.id.rb_settings);
		rg_video_type_bottom = (RadioGroup) findViewById(R.id.rg_video_type_bottom);
		rb_bm_comic = (RadioButton) findViewById(R.id.rb_bm_comic);
		rb_bm_diy = (RadioButton) findViewById(R.id.rb_bm_diy);
		rb_bm_documentary = (RadioButton) findViewById(R.id.rb_bm_documentary);
		rb_bm_movice = (RadioButton) findViewById(R.id.rb_bm_movice);
		rb_bm_music = (RadioButton) findViewById(R.id.rb_bm_music);
		rb_bm_teach = (RadioButton) findViewById(R.id.rb_bm_teach);
		rb_bm_tv_show = (RadioButton) findViewById(R.id.rb_bm_tv_show);
		rb_bm_tvplay = (RadioButton) findViewById(R.id.rb_bm_tvplay);
		tv_main_date = (TextView) findViewById(R.id.tv_main_date);
		ll_rb = (LinearLayout) findViewById(R.id.ll_rb);
		
		fragments = new ArrayList<Fragment>();
        Bundle args = new Bundle();
        
        rf = new RecommendFragment();
        args.putInt("num", 0);
        rf.setArguments(args);
		fragments.add(rf);

        tf = new TVFragment();
        args.putInt("num", 1);
        tf.setArguments(args);
        fragments.add(tf);
        
        mf = new TopicFragment();
        args.putInt("num", 2);
        mf.setArguments(args);
		fragments.add(mf);
		
//		uf = new UserFragment();
//        args.putInt("num", 3);
//        uf.setArguments(args);
//		fragments.add(uf);
		
		
		af = new AppFragment();
        args.putInt("num", 3);
        af.setArguments(args);
		fragments.add(af);
		
		sf = new SettingFragment();
        args.putInt("num",4);
        sf.setArguments(args);
		fragments.add(sf);
		adapter  = new FragAdapter(getSupportFragmentManager(),fragments);
		//adapter = new FragAdapter(getSupportFragmentManager(), fragments);
		vpager.setAdapter(adapter);
		vpager.setCurrentItem(0);
		vpager.setPageTransformer(true, new DepthPageTransformer());
		//vpager.setPageTransformer(true, new MyPageTransformer());
		if(ISTV){
			try {
				Field field = ViewPager.class.getDeclaredField("mScroller");
				field.setAccessible(true);
				FixedSpeedScroller scroller = new FixedSpeedScroller(
						vpager.getContext(), new AccelerateInterpolator());
				field.set(vpager, scroller);
				scroller.setmDuration(700);
				//scroller.setmDuration(300);
			} catch (Exception e) {
				Logger.v(TAG, "Exception" + e);
			}
		}
	}
	/**
	 * 初始化飞框
	 */
	public void initwhiteBorder(){
		Logger.d(TAG, "开始whiteBorder="+whiteBorder);
//		fl_main.removeView(whiteBorder);
//		whiteBorder = null;
		this.whiteBorder = new ImageView(this);
		fl_main.addView(whiteBorder);
		whiteBorder.setBackgroundResource(R.drawable.white_border);
		FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(128, 130);
		layoutparams.leftMargin = 42;
		layoutparams.topMargin = 183;
		whiteBorder.setLayoutParams(layoutparams);
		whiteBorder.setVisibility(View.INVISIBLE);
//		whiteBorder.bringToFront();
		Logger.d(TAG, "结束whiteBorder="+whiteBorder);
		//whiteBorder.startAnimation(breathingAnimation);
	}
	
	@Override
	protected void setListener() {
		//更换背景
		String fileName = sp.getString("wallpaperFileName", null);
		if(fileName !=null && !"".equals(fileName)) {
			changeBackImage(fileName);
		} 
		
		fromXDelta = title_group.getChildAt(0).getX();
		int j = title_group.getChildCount();
		for (int i = 0; i < j; i++) {
			final int index = i;
			View v = title_group.getChildAt(i);
			v.setOnFocusChangeListener(
					new OnFocusChangeListener() {
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if (hasFocus) {
								//if(ISTV){
									whiteBorder.clearAnimation();
									whiteBorder.setVisibility(View.INVISIBLE);
								//}
								((RadioButton)title_group.getChildAt(index)).setSelected(true);
								vpager.setCurrentItem(index, true);
							}else{
								((RadioButton)title_group.getChildAt(index)).setSelected(false);
							}
						}
					});
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					//if(ISTV){
						whiteBorder.clearAnimation();
						whiteBorder.setVisibility(View.INVISIBLE);
					//}
					vpager.setCurrentItem(index, true);
				}
			});
		}
		
		int k = rg_video_type_bottom.getChildCount();
		for(int i=0;i<k;i++){
			final int paramInt = i;
			//if(ISTV){
				rg_video_type_bottom.getChildAt(i).setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if(hasFocus){
							whiteBorder.startAnimation(breathingAnimation);
							whiteBorder.setVisibility(View.VISIBLE);
							int[] location = new int[2];
							v.getLocationOnScreen(location);
							int width = v.getWidth()-25;
							int height = v.getHeight()-20;
							float x = (float) location[0];
							float y = (float) location[1];
							if(mHeight > 1000 && mWidth >1000) {
								y = 947;
								switch (paramInt) {
								case 0:
									x = 115;
									break;
								case 1:
									x = 341;
									break;
								case 2:
									x = 566;
									break;
								case 3:
									x = 788;
									break;
								case 4:
									x = 1011;
									break;
								case 5:
									x = 1234;
									break;
								case 6:
									x = 1460;
									break;
								case 7:
									x = 1685;
									break;
								}
							}else {
								if(mHeight==800||mHeight==752){
									y = (float) 643;
								}else if(mHeight==736){
									y = (float) 643-16;
								}else{
									y = (float) 643-32;
								}
								switch (paramInt) {
								case 0:
									x = (float) 44+9;
									break;
								case 1:
									x = (float) 190+14;
									break;
								case 2:
									x = (float) 340+14;
									break;
								case 3:
									x = (float) 488+14;
									break;
								case 4:
									x = (float) 636+15;
									break;
								case 5:
									x = (float) 784+15;
									break;
								case 6:
									x = (float) 932+18;
									break;
								case 7:
									x = (float) 1080+20;
									break;

								}
							}
							flyWhiteBorder(width, height, x, y);
						}
					}
				});
				
			//}
			rg_video_type_bottom.getChildAt(i).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle pBundle = null;
					switch (v.getId()) {
					case R.id.rb_bm_comic:
						pBundle = new Bundle();
						pBundle.putString("TYPE", "COMIC");
						openActivity(VodTypeActivity.class, pBundle);
						break;
					case R.id.rb_bm_diy:
						Utils.showToast(HomeActivity.this, "暂未开放,敬请期待！", R.drawable.toast_smile);
						break;
					case R.id.rb_bm_documentary:
						pBundle = new Bundle();
						pBundle.putString("TYPE", "DOCUMENTARY");
						openActivity(VodTypeActivity.class, pBundle);
						break;
					case R.id.rb_bm_movice:
						pBundle = new Bundle();
						pBundle.putString("TYPE", "MOVIE");
						openActivity(VodTypeActivity.class, pBundle);
						break;
					case R.id.rb_bm_music:
						Utils.showToast(HomeActivity.this, "暂未开放,敬请期待！", R.drawable.toast_smile);
						break;
					case R.id.rb_bm_teach:
						pBundle = new Bundle();
						pBundle.putString("TYPE", "TEACH");
						openActivity(VodTypeActivity.class, pBundle);
						break;
					case R.id.rb_bm_tv_show:
						pBundle = new Bundle();
						pBundle.putString("TYPE", "TVSHOW");
						openActivity(VodTypeActivity.class, pBundle);
						break;
					case R.id.rb_bm_tvplay:
						pBundle = new Bundle();
						pBundle.putString("TYPE", "TVPLAY");
						openActivity(VodTypeActivity.class, pBundle);
						break;
					}
				}
			});
			
		}

		//导航按下监听
		title_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
			}
		});
		
		title_group.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Logger.v(TAG, "title_group获取焦点="+hasFocus);
			}
		});
		
		vpager.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Logger.v(TAG, "vpager获取焦点="+hasFocus);
			}
		});
		/**
		 * ViewPager的PageChangeListener(页面改变的监听器)
		 */
		vpager.setOnPageChangeListener(new OnPageChangeListener() {
			
			/**
			 * 滑动viewPage页面获取焦点时更新导航标记
			 */
			@Override
			public void onPageSelected(int position) {
				int i = title_group.getChildCount();
				Logger.v(TAG, "position="+position+"..i="+i);
				if(!ISTV){
					if(position<i){
					((RadioButton)title_group.getChildAt(position)).setChecked(true);
					}
				}
				switch (position) {
				case 0:
					if(!title_group.getChildAt(position).isSelected()){
						if(null!=rf.re_typeLogs){
							rf.re_typeLogs[0].requestFocus();
						}
					}
					break;
				case 1:
					if(!title_group.getChildAt(position).isSelected()){
						if(null!=tf.tv_typeLogs){
							tf.tv_typeLogs[0].requestFocus();
						}
					}
					break;
				case 2:
					if(!title_group.getChildAt(position).isSelected()){
						if(null!=mf.mv_typeLogs){
							mf.mv_typeLogs[0].requestFocus();
						}
					}
					break;
//				case 3:
//					if(!title_group.getChildAt(position).isSelected()){
//						uf.typeLogs[0].requestFocus();
//					}
//					break;
				case 3:
					if(!title_group.getChildAt(position).isSelected()){
						if(null!=af.app_typeLogs){
							af.app_typeLogs[0].requestFocus();
						}
					}
					break;
				case 4:
					if(!title_group.getChildAt(position).isSelected()){
						if(null!=sf.st_typeLogs){
							sf.st_typeLogs[0].requestFocus();
						}
					}
					break;
				}
				float toX = title_group.getChildAt(position).getX();
				Logger.v(TAG, "viewpage值="+position);
				Logger.v(TAG, "toX="+toX);
				mAnimationSet = new AnimationSet(true);
				mTranslateAnimation = new TranslateAnimation(
						fromXDelta,toX , 0f, 0f);
				initAnimation(mAnimationSet, mTranslateAnimation);
				iv_titile.startAnimation(mAnimationSet);//titile蓝色横条图片的动画切换
				fromXDelta = toX;
				titile_position = position;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageScrollStateChanged(int position) {
				
			}
		});

	}
	
	
	
	/**
	 * titile动画
	 * @param _AnimationSet
	 * @param _TranslateAnimation
	 */
	private void initAnimation(AnimationSet _AnimationSet,
			TranslateAnimation _TranslateAnimation) {
		_AnimationSet.addAnimation(_TranslateAnimation);
		_AnimationSet.setFillBefore(true);
		_AnimationSet.setFillAfter(true);
		_AnimationSet.setDuration(250L);
	}

	
	
	private Handler homeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 调用窗口消息处理函数
			onMessage(msg);
		}
	};

	/**
	 * @brief 窗口消息处理函数。
	 * @author joychang
	 * @param[in] msg 窗口消息。
	 */
	private void onMessage(final Message msg) {
		if (msg != null) {
			switch (msg.what) {
			case WindowMessageID.ERROR:
				Toast.makeText(getApplicationContext(), "服务器内部异常", 1).show();
				break;
			case WindowMessageID.DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "下载失败", 1).show();
				break;
			case WindowMessageID.GET_INFO_SUCCESS:

				break;
			case WindowMessageID.DOWNLOAD_SUCCESS:
				// 安装apk
				installApk(msg.obj.toString());
				break;
			case WindowMessageID.REFLESH_TIME:
				tv_time.setText(Utils.getStringTime(" "));
				tv_main_date.setText(Utils.getStringData());
				if (time_colon.getVisibility() == View.VISIBLE) {
					time_colon.setVisibility(View.GONE);
				} else {
					time_colon.setVisibility(View.VISIBLE);
				}
				homeHandler.sendEmptyMessageDelayed(
						WindowMessageID.REFLESH_TIME, 1000);
				break;

			}
		}
	}

	/**
	 * 显示升级提示的对话框
	 */
	private void showUpdateDialog(String remark,final String updateurl) {
		HomeDialog.Builder builder = new HomeDialog.Builder(context);
		builder.setTitle("版本升级");
		String [] remarks = remark.split(";");
		String str = "";
		for(int i=0;i<remarks.length;i++){
			if(i==remarks.length-1){
				str = str+remarks[i];
				continue;
			}else{
				str = str+remarks[i]+"\n";
			}
		}
		Logger.d(TAG, "msg="+str);
		builder.setMessage(str);
		builder.setPositiveButton("等不及了，立即更新", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Utils.showToast(context, R.string.version_updata_downlond, R.drawable.toast_smile);
				Utils.startDownloadApk(context, updateurl);
				dialog.dismiss();
			}
		});
		builder.setNeutralButton("先看片呢，稍后提醒", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
		
	}

	/**
	 * 安装一个apk文件
	 * 
	 * @param file
	 */
	protected void installApk(String file) {
		File updateFile = new File(file.trim());
		try {
			String[] args2 = { "chmod", "604", updateFile.getPath() };
			Runtime.getRuntime().exec(args2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(updateFile),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 注册网络广播
	 */
	private void registerNetworkReceiver() {
		IntentFilter filter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(receiver, filter);
	}

	private void registerPackageReceiver(){
		//注册广播
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		mFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mFilter.addDataScheme("package");
		registerReceiver(mReceiver, mFilter);
	}
	/**
	 * 注册网络变动的广播接收
	 * 
	 * @author joychang
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
				if (networkInfo == null || networkInfo.isConnected()) {
					// 没有网络
					iv_net_state.setImageResource(R.drawable.wifi_n);
				}
				NetworkInfo ethNetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
				if (ethNetworkInfo != null && ethNetworkInfo.isConnected()) {
					// 有线网
					iv_net_state.setImageResource(R.drawable.enh);
				}
				NetworkInfo wifiNetworkInfo = connectMgr
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
					// 无线网
					iv_net_state.setImageResource(R.drawable.wifi);
				}
			}
		}
	};
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			queryInstalledApp();
		}
	};
	private BroadcastReceiver mWallReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String fileName = intent.getStringExtra("wallpaperFileName");
			if(fileName == null)
				return;
			sp.edit().putString("wallpaperFileName", fileName).commit();
			changeBackImage(fileName);
			Utils.showToast(context, R.string.updata_bg, R.drawable.toast_smile);
		}
	};
	
	public void queryInstalledApp() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				packLst = getPackageManager().getInstalledPackages(0);
			}
		}).start();
	}
	
	private void changeBackImage(String fileName) {
		if(fileName == null)
			return;
		if(context.getFilesDir().exists()) {
			Bitmap bmp = null;
			if("开".equals(sp.getString("open_blur", "关"))) {
				//开启高斯模糊背景效果
				bmp = mCacheUtils.getBitmapFromMemCache(context.getFilesDir().getAbsolutePath()+"/"+fileName+"_blur");
				if(bmp == null) {
					try {
						Bitmap tempBmp = mCacheUtils.getBitmapFromMemCache(context.getFilesDir().getAbsolutePath()+"/"+fileName);
						if(tempBmp == null) {
							tempBmp = BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath()+"/"+fileName);
							mCacheUtils.addBitmapToMemoryCache(context.getFilesDir().getAbsolutePath()+"/"+fileName, tempBmp);
						}
						bmp = BlurUtils.doBlur(tempBmp, 7, false);
						mCacheUtils.addBitmapToMemoryCache(context.getFilesDir().getAbsolutePath()+"/"+fileName+"_blur", bmp);
					} catch (OutOfMemoryError oom) {
						mCacheUtils.clearAllImageCache();
						Bitmap tempBmp = mCacheUtils.getBitmapFromMemCache(context.getFilesDir().getAbsolutePath()+"/"+fileName);
						if(tempBmp == null) {
							tempBmp = BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath()+"/"+fileName);
							mCacheUtils.addBitmapToMemoryCache(context.getFilesDir().getAbsolutePath()+"/"+fileName, tempBmp);
						}
						bmp = BlurUtils.doBlur(tempBmp, 7, false);
						mCacheUtils.addBitmapToMemoryCache(context.getFilesDir().getAbsolutePath()+"/"+fileName+"_blur", bmp);
					}
				}
				rl_bg.setBackgroundDrawable(new BitmapDrawable(getResources(),bmp));
				Logger.d("zhouchuan","模糊显示"+context.getFilesDir().getAbsolutePath()+"/"+fileName);
				
			}else {
				bmp = mCacheUtils.getBitmapFromMemCache(context.getFilesDir().getAbsolutePath()+"/"+fileName);
				if(bmp == null) {
					try {
						bmp = BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath()+"/"+fileName);
						mCacheUtils.addBitmapToMemoryCache(context.getFilesDir().getAbsolutePath()+"/"+fileName, bmp);
					} catch (OutOfMemoryError oom) {
						mCacheUtils.clearAllImageCache();
						bmp = BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath()+"/"+fileName);
						mCacheUtils.addBitmapToMemoryCache(context.getFilesDir().getAbsolutePath()+"/"+fileName, bmp);
					}
				}
				rl_bg.setBackgroundDrawable(new BitmapDrawable(getResources(),bmp));
				Logger.d("zhouchuan","正常显示"+context.getFilesDir().getAbsolutePath()+"/"+fileName);
			}
		}
	}
	
	/**
	 * 飞框动画
	 * @param width
	 * @param height
	 * @param paramFloat1
	 * @param paramFloat2
	 */
	public void flyWhiteBorder(int width, int height,
			float paramFloat1, float paramFloat2) {
		if (whiteBorder == null)
			return;
		int mWidth = whiteBorder.getWidth();
		int mheight = whiteBorder.getHeight();
		ViewPropertyAnimator localViewPropertyAnimator = whiteBorder.animate();
		localViewPropertyAnimator.setDuration(300L);//400
		localViewPropertyAnimator.scaleX((float)width/(float)mWidth);
		localViewPropertyAnimator.scaleY((float)height/(float)mheight);
		localViewPropertyAnimator.x(paramFloat1);localViewPropertyAnimator.y(paramFloat2);
		localViewPropertyAnimator.start();
	}
	
	
	public static boolean getIsTV(){
		return ISTV==null?true:ISTV;
	}
	private final String TAG = "HomeActivity"; 
	public RequestQueue mQueue;
	public FrameLayout fl_main;
	private RelativeLayout rl_bg;
	private TextView tv_time, time_colon;
	private ImageView iv_net_state,iv_titile;
	private RadioButton rb_recommend,rb_Internet,rb_video_type,rb_user,rb_app_store,rb_settings;
	private RadioButton rb_bm_comic,rb_bm_diy,rb_bm_documentary,rb_bm_movice,rb_bm_music,rb_bm_teach,
	rb_bm_tv_show,rb_bm_tvplay;
	private boolean isRunning = false;
	private ViewPager vpager;
	private RadioGroup title_group, rg_video_type_bottom;
	private FragAdapter adapter;
	private float fromXDelta;
	private AnimationSet mAnimationSet;
	private TranslateAnimation mTranslateAnimation;
	private List<Fragment> fragments;
	public List<PackageInfo> packLst;
	public ImageView whiteBorder = null;// 白色背景框
	protected static Boolean ISTV;
	private static int titile_position  = 0;
	protected String technology = "";
	//private boolean isFromPageChange;

	private TextView tv_main_date;

	private RecommendFragment rf;

	private TVFragment tf;

	private TopicFragment mf;

	//private UserFragment uf;

	private AppFragment af;

	private SettingFragment sf;
	
	private Boolean isHasFouse;
	
	private LinearLayout ll_rb;

	private LruCacheUtils mCacheUtils;
	
	public static String homeFrom;
	public static String homeParams;
	
	/**
	 * @class WindowMessageID
	 * @brief 内部消息ID定义类。
	 * @author joychang
	 */
	private class WindowMessageID {

		/**
		 * @brief 刷新数据。
		 */
		public static final int REFLESH_TIME = 0x00000005;

		/**
		 * @brief 请求出错。
		 */
		public final static int ERROR = 0x00000004;
		// 版本更新的消息
		private final static int DOWNLOAD_ERROR = 0x000000010;
		private final static int DOWNLOAD_SUCCESS = 0x000000012;
		private final static int GET_INFO_SUCCESS = 0x000000013;
	}
}
