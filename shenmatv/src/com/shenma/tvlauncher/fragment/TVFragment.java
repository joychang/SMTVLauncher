package com.shenma.tvlauncher.fragment;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.TvStationActivity;
import com.shenma.tvlauncher.application.MyVolley;
import com.shenma.tvlauncher.dao.TVStationDao;
import com.shenma.tvlauncher.dao.bean.TVSCollect;
import com.shenma.tvlauncher.domain.TVStation;
import com.shenma.tvlauncher.domain.TVStationInfo;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.tvback.TVBackActivity;
import com.shenma.tvlauncher.tvlive.TVLivePlayer;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.ScaleAnimEffect;
import com.shenma.tvlauncher.view.HomeDialog;
/**
 * @Description 网络电视
 * @author joychang
 *
 */
public class TVFragment extends BaseFragment{
	
	private FrameLayout[] tv_fls;
	public ImageView[] tv_typeLogs;
	private ImageView[] tvbgs;
	ScaleAnimEffect animEffect;
	private View view;
	private TVStationDao dao;
	private List<TVSCollect> tvs;
	private List<TVStationInfo> data;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dao = TVStationDao.getInstance(context);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(container==null){
			return null;
		}
		if(null == view){
			view = inflater.inflate(R.layout.layout_tv, container,false);
			init();
		}else{
			((ViewGroup)view.getParent()).removeView(view);
		}
		return view;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void init() {
		loadViewLayout();
		findViewById();
		setListener();
	}
	
	protected void loadViewLayout() {
		tv_fls = new FrameLayout[15];
		tv_typeLogs = new ImageView[15];
		tvbgs = new ImageView[15];
		animEffect = new ScaleAnimEffect();
	}
	
	protected void findViewById() {
		tv_fls[0] = (FrameLayout) view.findViewById(R.id.tv_fl_re_0);
		tv_fls[1] = (FrameLayout) view.findViewById(R.id.tv_fl_re_1);
		tv_fls[2] = (FrameLayout) view.findViewById(R.id.tv_fl_re_2);
		tv_fls[3] = (FrameLayout) view.findViewById(R.id.tv_fl_re_3);
		tv_fls[4] = (FrameLayout) view.findViewById(R.id.tv_fl_re_4);
		tv_fls[5] = (FrameLayout) view.findViewById(R.id.tv_fl_re_5);
		tv_fls[6] = (FrameLayout) view.findViewById(R.id.tv_fl_re_6);
		tv_fls[7] = (FrameLayout) view.findViewById(R.id.tv_fl_re_7);
		tv_fls[8] = (FrameLayout) view.findViewById(R.id.tv_fl_re_8);
		tv_fls[9] = (FrameLayout) view.findViewById(R.id.tv_fl_re_9);
		tv_fls[10] = (FrameLayout) view.findViewById(R.id.tv_fl_re_10);
		tv_fls[11] = (FrameLayout) view.findViewById(R.id.tv_fl_re_11);
		tv_fls[12] = (FrameLayout) view.findViewById(R.id.tv_fl_re_12);
		tv_fls[13] = (FrameLayout) view.findViewById(R.id.tv_fl_re_13);
		tv_fls[14] = (FrameLayout) view.findViewById(R.id.tv_fl_re_14);
		
		tv_typeLogs[0] = (ImageView) view.findViewById(R.id.tv_iv_livetv);
		tv_typeLogs[1] = (ImageView) view.findViewById(R.id.tv_iv_watchback);
		tv_typeLogs[2] = (ImageView) view.findViewById(R.id.tv_iv_sourcemg);
		tv_typeLogs[3] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_1);
		tv_typeLogs[4] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_2);
		tv_typeLogs[5] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_3);
		tv_typeLogs[6] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_4);
		tv_typeLogs[7] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_5);
		tv_typeLogs[8] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_6);
		tv_typeLogs[9] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_7);
		tv_typeLogs[10] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_8);
		tv_typeLogs[11] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_9);
		tv_typeLogs[12] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_10);
		tv_typeLogs[13] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_11);
		tv_typeLogs[14] = (ImageView) view.findViewById(R.id.tv_iv_channeladd_12);
		
		tvbgs[0] = (ImageView) view.findViewById(R.id.tv_bg_0);
		tvbgs[1] = (ImageView) view.findViewById(R.id.tv_bg_1);
		tvbgs[2] = (ImageView) view.findViewById(R.id.tv_bg_2);
		tvbgs[3] = (ImageView) view.findViewById(R.id.tv_bg_3);
		tvbgs[4] = (ImageView) view.findViewById(R.id.tv_bg_4);
		tvbgs[5] = (ImageView) view.findViewById(R.id.tv_bg_5);
		tvbgs[6] = (ImageView) view.findViewById(R.id.tv_bg_6);
		tvbgs[7] = (ImageView) view.findViewById(R.id.tv_bg_7);
		tvbgs[8] = (ImageView) view.findViewById(R.id.tv_bg_8);
		tvbgs[9] = (ImageView) view.findViewById(R.id.tv_bg_9);
		tvbgs[10] = (ImageView) view.findViewById(R.id.tv_bg_10);
		tvbgs[11] = (ImageView) view.findViewById(R.id.tv_bg_11);
		tvbgs[12] = (ImageView) view.findViewById(R.id.tv_bg_12);
		tvbgs[13] = (ImageView) view.findViewById(R.id.tv_bg_13);
		tvbgs[14] = (ImageView) view.findViewById(R.id.tv_bg_14);
	}
	
	protected void setListener() {
		initClickListener();
		initLongClickListener();
	}
	
	private void initClickListener() {
		for (int i = 0; i < tv_typeLogs.length; i++) {
			tvbgs[i].setVisibility(View.GONE);
			tv_typeLogs[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					switch (v.getId()) {
					case R.id.tv_iv_livetv:
						//直播
						i.setClass(home, TVLivePlayer.class);
						i.putExtra("TVTYPE", Constant.TVLIVE);
						startActivity(i);
						break;
					case R.id.tv_iv_watchback:
						//回看
						i.setClass(home, TVBackActivity.class);
						startActivity(i);
						break;
					case R.id.tv_iv_sourcemg:
						//diy
						i.setClass(home, TVLivePlayer.class);
						i.putExtra("TVTYPE", Constant.TVLIVE_DIY);
						startActivity(i);
						break;
					case R.id.tv_iv_channeladd_1:
						//选台
						initOnClickListener(i, 1);
						break;
					case R.id.tv_iv_channeladd_2:
						//选台
						initOnClickListener(i, 2);
						break;
					case R.id.tv_iv_channeladd_3:
						//选台
						initOnClickListener(i, 3);
						break;
					case R.id.tv_iv_channeladd_4:
						//选台
						initOnClickListener(i, 4);
						break;
					case R.id.tv_iv_channeladd_5:
						//选台
						initOnClickListener(i, 5);
						break;
					case R.id.tv_iv_channeladd_6:
						//选台
						initOnClickListener(i, 6);
						break;
					case R.id.tv_iv_channeladd_7:
						//选台
						initOnClickListener(i, 7);
						break;
					case R.id.tv_iv_channeladd_8:
						//选台
						initOnClickListener(i, 8);
						break;
					case R.id.tv_iv_channeladd_9:
						//选台
						initOnClickListener(i, 9);
						break;
					case R.id.tv_iv_channeladd_10:
						//选台
						initOnClickListener(i, 10);
						break;
					case R.id.tv_iv_channeladd_11:
						//选台
						initOnClickListener(i, 11);
						break;
					case R.id.tv_iv_channeladd_12:
						//选台
						initOnClickListener(i, 12);
						break;
					}
					home.overridePendingTransition(android.R.anim.fade_in,
							android.R.anim.fade_out);
				}
			});
			//if(ISTV){
				tv_typeLogs[i].setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						int paramInt = 0;
						switch (v.getId()) {
						case R.id.tv_iv_livetv:
							paramInt = 0;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_watchback:
							paramInt = 1;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_sourcemg:
							paramInt = 2;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_1:
							paramInt = 3;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_2:
							paramInt =4;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_3:
							paramInt = 5;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_4:
							paramInt = 6;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_5:
							paramInt = 7;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_6:
							paramInt = 8;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_7:
							paramInt = 9;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_8:
							paramInt = 10;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_9:
							paramInt = 11;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_10:
							paramInt = 12;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_11:
							paramInt = 13;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
						case R.id.tv_iv_channeladd_12:
							paramInt = 14;
							// 此处设置不同大小的item的长宽 ,以及框飞入的X和Y轴.
							break;
							
						}
						if(hasFocus){
							showOnFocusAnimation(paramInt);
							if(null!=home.whiteBorder){
								home.whiteBorder.setVisibility(View.VISIBLE);
							}
							flyAnimation(paramInt);
							//白框动画
						}else{
							showLoseFocusAinimation(paramInt);
							//将白框隐藏
						}
					}
				});
			}
		//}
	}

	/**
	 * 飞行焦点框
	 * @param paramInt
	 */
	private void flyAnimation(int paramInt){
		int[] location = new int[2];
		tv_typeLogs[paramInt].getLocationOnScreen(location);
		int width = tv_typeLogs[paramInt].getWidth()-2;
		int height = tv_typeLogs[paramInt].getHeight();
		float x =  location[0];
		float y =  location[1];
		Logger.v("joychang", "paramInt="+paramInt+"..x="+x+"...y="+y);
		if(mHeight>1000 && mWidth>1000) {
			width = width+22;
			height = height+13;
			switch (paramInt) {
			case 0:
				x =  220+198;
				y =  189+98;
				break;
			case 1:
				x =  220+198;
				y =  319+163;
				break;
			case 2:
				x =  220+198;
				y =  449+228;
				break;
			case 3:
				x =  393+284;
				y =  189+98;
				break;
			case 4:
				x =  393+284;
				y =  319+163;
				break;
			case 5:
				x =  393+284;
				y =  449+228;
				break;
			case 6:
				x =  566+370;
				y =  189+98;
				break;
			case 7:
				x =  566+370;
				y =  319+163;
				break;
			case 8:
				x =  566+370;
				y =  449+228;
				break;
			case 9:
				x =  739+456;
				y =  189+98;
				break;
			case 10:
				x =  739+456;
				y =  319+163;
				break;
			case 11:
				x =  739+456;
				y =  449+228;
				break;
			case 12:
				x =  912+542;
				y =  189+98;
				break;
			case 13:
				x =  912+542;
				y =  319+163;
				break;
			case 14:
				x =  912+542;
				y =  449+228;
				break;
			}
		}else {
			width = width+15;
			height = height+8;
			switch (paramInt) {
			case 0:
				x =  220+36;
				y =  169+1;
				break;
			case 1:
				x =  220+36;
				y =  299+1;
				break;
			case 2:
				x =  220+36;
				y =  429+1;
				break;
			case 3:
				x =  393+36;
				y =  169+1;
				break;
			case 4:
				x =  393+36;
				y =  299+1;
				break;
			case 5:
				x =  393+36;
				y =  429+1;
				break;
			case 6:
				x =  566+36;
				y =  169+1;
				break;
			case 7:
				x =  566+36;
				y =  299+1;
				break;
			case 8:
				x =  566+36;
				y =  429+1;
				break;
			case 9:
				x =  739+36;
				y =  169+1;
				break;
			case 10:
				x =  739+36;
				y =  299+1;
				break;
			case 11:
				x =  739+36;
				y =  429+1;
				break;
			case 12:
				x =  912+36;
				y =  169+1;
				break;
			case 13:
				x =  912+36;
				y =  299+1;
				break;
			case 14:
				x =  912+36;
				y =  429+1;
				break;
			}
		}
		home.flyWhiteBorder(width, height, x, y);
}
	
	/**
	 * joychang 设置获取焦点时icon放大凸起
	 * 
	 * @param paramInt
	 */
	private void showOnFocusAnimation(final int paramInt) {
		tv_fls[paramInt].bringToFront();//将当前FrameLayout置为顶层
		float f1 = 1.0F;
		float f2 = 1.1F;
		animEffect.setAttributs(1.0F, 1.1F, f1, f2, 200L);
		Animation mAnimation = this.animEffect.createAnimation();
		mAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				tvbgs[paramInt].setVisibility(View.VISIBLE);
			}
		});
		tv_typeLogs[paramInt].startAnimation(mAnimation);
	}
	
	
	/**
	 * 失去焦点缩小
	 * @param paramInt
	 */
	private void showLoseFocusAinimation(final int paramInt) {
		float f1 = 1.1F;
		float f2 = 1.0F;
		animEffect.setAttributs(1.1F, 1.0F, f1, f2, 200L);
		Animation mAnimation = this.animEffect.createAnimation();
		tv_typeLogs[paramInt].startAnimation(mAnimation);
		tvbgs[paramInt].setVisibility(View.GONE);
//		mAnimation.setAnimationListener(new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				tvbgs[paramInt].setVisibility(View.GONE);
//			}
//		});
		
		
	}
	
	@Override
	public void onResume() {
		tvs = dao.queryAllTvsi();
		//每次onResume时，更新服务端返回的数据
		getAllTVStations();
		loadImage();
		super.onResume();
	}
	
	private TVSCollect getTvsByIndex(int index) {
		if(tvs != null){
			for (TVSCollect tv : tvs) {
				if (index == tv.getTvindex()){
					return tv;
				}
			}
		}
		return null;
	}
	
	private void getAllTVStations() {
		RequestQueue mQueue = Volley.newRequestQueue(context, new HurlStack());
		GsonRequest<TVStation> mTvs = new GsonRequest<TVStation>(Method.GET, Constant.TVSTATIONS, TVStation.class, 
		new Listener<TVStation>() {

			@Override
			public void onResponse(TVStation response) {
				if(response != null && "200".equals(response.getCode())){
					data = response.getData();
				}
			}
		}, 
		new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error instanceof TimeoutError) {
					Logger.e("zhouchuan", "请求超时");
				} else if (error instanceof AuthFailureError) {
					Logger.e("zhouchuan", "AuthFailureError=" + error.toString());
				}
			}
		}){
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> hearders = new HashMap<String, String>();
				String base64 = new String(Base64.encode("admin:1234".getBytes(), Base64.DEFAULT));
				hearders.put("Authorization", "Basic " + base64);
				return hearders;
			}
		};
		mQueue.add(mTvs);
	}
	
	private int getCIDByName(String name) {
		int id = -1;
		if(data != null){
			for (TVStationInfo info : data) {
				if(info.getChannelname().equals(name)) {
					id = Integer.parseInt(info.getChannelid().trim());
					break;
				}
			}
		}
		return id;
	}
	
	private void loadImage(){
		ImageLoader imageLoader = MyVolley.getImageLoader();
		for (TVSCollect tv : tvs) {
			if(tv.getTvindex() != -1){
				imageLoader.get(Constant.HEARD_URL+tv.getChannelpic(), ImageLoader.getImageListener(tv_typeLogs[tv.getTvindex()+2], R.drawable.channeladd, R.drawable.channeladd));
			}
		}
	}
	
	private void initOnClickListener(Intent intent, int index){
		TVSCollect tv = getTvsByIndex(index);
		if(tv != null) {
			intent.setClass(home, TVLivePlayer.class);
			intent.putExtra("KEYCHANNE", getCIDByName(tv.getChannelname()));
			intent.putExtra("TVTYPE", Constant.TVLIVE);
			startActivity(intent);
		}else {
			intent.setClass(home, TvStationActivity.class);
			intent.putExtra("index", index);
			startActivity(intent);
		}
	}
	
	private void initLongClickListener(){
		for (int i = 3; i < tv_typeLogs.length; i++) {
			tv_typeLogs[i].setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					switch (v.getId()) {
					case R.id.tv_iv_channeladd_1:
						showDialog(getTvsByIndex(1));
						break;
					case R.id.tv_iv_channeladd_2:
						showDialog(getTvsByIndex(2));
						break;
					case R.id.tv_iv_channeladd_3:
						showDialog(getTvsByIndex(3));
						break;
					case R.id.tv_iv_channeladd_4:
						showDialog(getTvsByIndex(4));
						break;
					case R.id.tv_iv_channeladd_5:
						showDialog(getTvsByIndex(5));
						break;
					case R.id.tv_iv_channeladd_6:
						showDialog(getTvsByIndex(6));
						break;
					case R.id.tv_iv_channeladd_7:
						showDialog(getTvsByIndex(7));
						break;
					case R.id.tv_iv_channeladd_8:
						showDialog(getTvsByIndex(8));
						break;
					case R.id.tv_iv_channeladd_9:
						showDialog(getTvsByIndex(9));
						break;
					case R.id.tv_iv_channeladd_10:
						showDialog(getTvsByIndex(10));
						break;
					case R.id.tv_iv_channeladd_11:
						showDialog(getTvsByIndex(11));
						break;
					case R.id.tv_iv_channeladd_12:
						showDialog(getTvsByIndex(12));
						break;
					}
					return true;
				}
			});
		}
	}
	private void showDialog(final TVSCollect tvsc) {
		if(tvsc != null) {
			HomeDialog.Builder builder = new HomeDialog.Builder(context);
			builder.setTitle("网络电视");
			builder.setMessage("请选择\n您要操作的功能");
			builder.setPositiveButton("删除该电视台", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					TVStationDao.getInstance(context).deleteTvsi(tvsc);
					tv_typeLogs[tvsc.getTvindex()+2].setImageResource(R.drawable.channeladd);
					tvs = dao.queryAllTvsi();
					dialog.dismiss();
				}
			});
			builder.setNeutralButton("替换该电视台", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setClass(home, TvStationActivity.class);
					intent.putExtra("index", tvsc.getTvindex()).putExtra("isUpdate", true);
					dialog.dismiss();
					startActivity(intent);
				}
			});
			builder.create().show();
		}
	}
}

