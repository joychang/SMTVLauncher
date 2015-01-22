package com.shenma.tvlauncher.fragment;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.UserActivity;
import com.shenma.tvlauncher.application.MyVolley;
import com.shenma.tvlauncher.domain.Recommend;
import com.shenma.tvlauncher.domain.RecommendInfo;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.ScaleAnimEffect;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.vod.SearchActivity;
import com.shenma.tvlauncher.vod.VodDetailsActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * @Description 推荐
 * @author joychang
 *
 */
public class RecommendFragment extends BaseFragment implements OnFocusChangeListener,OnClickListener{
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate()........");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.d(TAG, "onCreateView()........");
		if(container==null){
			return null;
		}
		if(null == view){
			view = inflater.inflate(R.layout.layout_recommend, container,false);
			init();
		}else{
			((ViewGroup)view.getParent()).removeView(view);
		}
		if(data == null){
			initData();	
		}
		return view;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Logger.d(TAG, "onStop()........");
		if(null!=mQueue){
			mQueue.stop();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.d(TAG, "onDestroy()........");
		if(null!=mQueue){
			mQueue.cancelAll(this);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Logger.d(TAG, "onResume()........");
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
	
	//初始化
	private void init(){
		loadViewLayout();
		findViewById();
		setListener();
		//re_fls[0].requestFocus();
	}
	
	private void initData(){
			mQueue = Volley.newRequestQueue(context, new HurlStack());
			imageLoader = MyVolley.getImageLoader();
			GsonRequest<Recommend> mRecommend = new GsonRequest<Recommend>(Method.GET, Constant.RECOMMEND_URL,
					Recommend.class,createMyReqSuccessListener(),createMyReqErrorListener()){
									@Override
	                                public Map<String, String> getHeaders()
	                                		throws AuthFailureError {
	            						HashMap<String, String> headers = new HashMap<String, String>();
	            						String base64 = new String(android.util.Base64.encode(
	            								"admin:1234".getBytes(), android.util.Base64.DEFAULT));
	            						headers.put("Authorization", "Basic " + base64);
	                                	return headers;
	                                }};
	                                
	      mQueue.add(mRecommend);     //     执行        
	}
	
	//请求成功
    private Response.Listener<Recommend> createMyReqSuccessListener() {
        return new Response.Listener<Recommend>() {
            @Override
            public void onResponse(Recommend response) {
            	data = response.getData();
            	int paramInt = 0;
            	String paramUrl;
            	for(int i=0;i<data.size();i++){
            		if(data.get(i).getTjwei().equals("1")){
            			paramInt = 3;
            			tvs[0].setText(data.get(i).getTjinfo());
            		}else if(data.get(i).getTjwei().equals("2")){
            			paramInt = 4;
            			tvs[1].setText(data.get(i).getTjinfo());
            		}else if(data.get(i).getTjwei().equals("3")){
            			paramInt = 5;
            			tvs[2].setText(data.get(i).getTjinfo());
            		}else if(data.get(i).getTjwei().equals("4")){
            			paramInt = 6;
            			tvs[3].setText(data.get(i).getTjinfo());
            		}else if(data.get(i).getTjwei().equals("5")){
            			paramInt = 7;
            			tvs[4].setText(data.get(i).getTjinfo());
            		}else if(data.get(i).getTjwei().equals("6")){
            			tvs[5].setText(data.get(i).getTjinfo());
            			paramInt = 8;
            		}
            		paramUrl = Constant.HEARD_URL+data.get(i).getTjpicurl();
            		Logger.v("joychang", "paramUrl="+paramUrl);
            		//Logger.d(TAG, "getTjtype = "+data.get(i).getTjtype()+"...getTjid="+data.get(i).getTjid());
            		setTypeImage(paramInt,paramUrl);
            	}
            	
            }
        };
    }
    
    private void setTypeImage(int paramInt,String paramUrl){
        imageLoader.get(paramUrl, 
                ImageLoader.getImageListener(re_typeLogs[paramInt], 
                                              re_typebgs[paramInt], 
                                              re_typebgs[paramInt]));
    }
    
    //请求失败
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	if(error instanceof TimeoutError){
            		Logger.e("joychang", "请求超时");
            	}else if(error instanceof AuthFailureError){
            		Logger.e("joychang", "AuthFailureError="+error.toString());
                }
            }
        };
    }
	
	
	protected void loadViewLayout() {
		re_fls = new FrameLayout[9];
		re_typeLogs = new ImageView[9];
		re_typebgs = new int[9];
		rebgs = new ImageView[9];
		tvs=  new TextView[6];
		animEffect = new ScaleAnimEffect();
	}


	protected void findViewById() {
		re_fls[0] = (FrameLayout) view.findViewById(R.id.fl_re_0);
		re_fls[1] = (FrameLayout) view.findViewById(R.id.fl_re_1);
		re_fls[2] = (FrameLayout) view.findViewById(R.id.fl_re_2);
		re_fls[3] = (FrameLayout) view.findViewById(R.id.fl_re_3);
		re_fls[4] = (FrameLayout) view.findViewById(R.id.fl_re_4);
		re_fls[5] = (FrameLayout) view.findViewById(R.id.fl_re_5);
		re_fls[6] = (FrameLayout) view.findViewById(R.id.fl_re_6);
		re_fls[7] = (FrameLayout) view.findViewById(R.id.fl_re_7);
		re_fls[8] = (FrameLayout) view.findViewById(R.id.fl_re_8);
		
		re_typeLogs[0] = (ImageView) view.findViewById(R.id.iv_re_0);
		re_typeLogs[1] = (ImageView) view.findViewById(R.id.iv_re_1);
		re_typeLogs[2] = (ImageView) view.findViewById(R.id.iv_re_2);
		re_typeLogs[3] = (ImageView) view.findViewById(R.id.iv_re_3);
		re_typeLogs[4] = (ImageView) view.findViewById(R.id.iv_re_4);
		re_typeLogs[5] = (ImageView) view.findViewById(R.id.iv_re_5);
		re_typeLogs[6] = (ImageView) view.findViewById(R.id.iv_re_6);
		re_typeLogs[7] = (ImageView) view.findViewById(R.id.iv_re_7);
		re_typeLogs[8] = (ImageView) view.findViewById(R.id.iv_re_8);
		
		re_typebgs[0] =  R.drawable.fl_re_1;
		re_typebgs[1] =  R.drawable.fl_re_1;
		re_typebgs[2] =  R.drawable.fl_re_1;
		re_typebgs[3] =  R.drawable.fl_re_0;
		re_typebgs[4] =  R.drawable.fl_re_1;
		re_typebgs[5] =  R.drawable.fl_re_1;
		re_typebgs[6] =  R.drawable.fl_re_3;
		re_typebgs[7] =  R.drawable.fl_re_4;
		re_typebgs[8] =  R.drawable.fl_re_4;
		
		
		rebgs[0] = (ImageView) view.findViewById(R.id.re_bg_0);
		rebgs[1] = (ImageView) view.findViewById(R.id.re_bg_1);
		rebgs[2] = (ImageView) view.findViewById(R.id.re_bg_2);
		rebgs[3] = (ImageView) view.findViewById(R.id.re_bg_3);
		rebgs[4] = (ImageView) view.findViewById(R.id.re_bg_4);
		rebgs[5] = (ImageView) view.findViewById(R.id.re_bg_5);
		rebgs[6] = (ImageView) view.findViewById(R.id.re_bg_6);
		rebgs[7] = (ImageView) view.findViewById(R.id.re_bg_7);
		rebgs[8] = (ImageView) view.findViewById(R.id.re_bg_8);
		
		tvs[0] = (TextView) view.findViewById(R.id.tv_re_3);
		tvs[1] = (TextView) view.findViewById(R.id.tv_re_4);
		tvs[2] = (TextView) view.findViewById(R.id.tv_re_5);
		tvs[3] = (TextView) view.findViewById(R.id.tv_re_6);
		tvs[4] = (TextView) view.findViewById(R.id.tv_re_7);
		tvs[5] = (TextView) view.findViewById(R.id.tv_re_8);
	}

	private int getPX(int i){
		return getResources().getDimensionPixelSize(i);
	}
	
	protected void setListener() {
		for(int i=0;i<re_typeLogs.length;i++){
			re_typeLogs[i].setOnClickListener(this);
			//if(ISTV){
//				re_typeLogs[i].setOnFocusChangeListener(this);
			//}
			re_typeLogs[i].setOnFocusChangeListener(this);
			rebgs[i].setVisibility(View.GONE);
		}
	}
	
	
	
	

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
//		int[] location = new int[2];
//		re_typeLogs[0].getLocationOnScreen(location);
//		int width = re_typeLogs[0].getWidth();
//		int height = re_typeLogs[0].getHeight();
//		float x = (float) location[0];
//		float y = (float) location[1];
//		Logger.d(TAG, "X="+x+"---Y="+y);
		//home.flyWhiteBorder(width, height, x, y);
		int paramInt = 0;
		switch (v.getId()) {
		case R.id.iv_re_0:
			paramInt = 0;
			break;
		case R.id.iv_re_1:
			paramInt = 1;
			break;
		case R.id.iv_re_2:
			paramInt = 2;
			break;
		case R.id.iv_re_3:
			paramInt = 3;
			break;
		case R.id.iv_re_4:
			paramInt = 4;
			break;
		case R.id.iv_re_5:
			paramInt = 5;
			break;
		case R.id.iv_re_6:
			paramInt = 6;
			break;
		case R.id.iv_re_7:
			paramInt = 7;
			break;
		case R.id.iv_re_8:
			paramInt = 8;
			break;
		}
		if(hasFocus){
			showOnFocusTranslAnimation(paramInt);
			if(null!=home.whiteBorder){
				home.whiteBorder.setVisibility(View.VISIBLE);
			}
			flyAnimation(paramInt);
		}else{
			showLooseFocusTranslAinimation(paramInt);
		}
		for (TextView tv : tvs) {
			if(tv.getVisibility()!=View.GONE) {
				tv.setVisibility(View.GONE);
			}
		}
		
	}
	/**
	 * 飞框焦点动画
	 * @param paramInt
	 */
	private void flyAnimation(int paramInt){
		int[] location = new int[2];
		re_typeLogs[paramInt].getLocationOnScreen(location);
		int width = re_typeLogs[paramInt].getWidth();
		int height = re_typeLogs[paramInt].getHeight();
		float x = (float) location[0];
		float y = (float) location[1];
		Logger.v("joychang", "paramInt="+paramInt+"..x="+x+"...y="+y);
		switch (paramInt) {
		case 0:
//			width = width+1;
//			height = height+3;
//			x = (float) location[0]-21;
//			y = (float) location[1]-7;
			//x = 42-21;
			//y = 189-7;
			if(mHeight>1000&&mWidth>1000){
				//1080p
				x = getResources().getDimensionPixelSize(R.dimen.sm_49); 
				y = getResources().getDimensionPixelSize(R.dimen.sm_190)-3;  
			}else {
				x = getResources().getDimensionPixelSize(R.dimen.sm_21);
				y = getResources().getDimensionPixelSize(R.dimen.sm_164);
			}
			break;
		case 1:
//			width = width+1;
//			height = height+3;
//			x = (float) location[0]-21;
//			y = (float) location[1];
//			x = 42-21;
			if(mHeight>1000&&mWidth>1000){
				//1080p
				y = getResources().getDimensionPixelSize(R.dimen.sm_310)+14;
				x = getResources().getDimensionPixelSize(R.dimen.sm_49);
			}else {
				y = 298;
				x = getResources().getDimensionPixelSize(R.dimen.sm_21);
			}
			//y = getResources().getDimensionPixelSize(R.dimen.sm_316);
			break;
		case 2:
//			width = width+1;
//			height = height+3;
//			x = (float) location[0]-21;
//			y = (float) location[1]+4;
			if(mHeight>1000&&mWidth>1000){
				//1080p
				x = getResources().getDimensionPixelSize(R.dimen.sm_49);
				y = getResources().getDimensionPixelSize(R.dimen.sm_450)-1;
			}else {
				x = 42-21;
				y = 425+4;
			}
			break;
		case 3:
//			x = (float) location[0]+154;
//			y = (float) location[1]+60;
			if(mHeight>1000&&mWidth>1000){
				//1080p
				width = width+24+14;
				height = height+13+8;
				x = getResources().getDimensionPixelSize(R.dimen.sm_370)-2;
				y = getResources().getDimensionPixelSize(R.dimen.sm_252)+1;
			}else {
				width = width+24;
				height = height+16;
				x = (float) 188+154;
				y = (float) 189+40;
			}
			break;
		case 4:
//			x = (float) location[0]+28;
//			y = (float) location[1]+6;
			if(mHeight>1000&&mWidth>1000){
				//1080p
				width = width+13+6;
				height = height+7+5;
				x = getResources().getDimensionPixelSize(R.dimen.sm_246) - 2;
				y = getResources().getDimensionPixelSize(R.dimen.sm_456) + 12;
			}else {
				width = width+13;
				height = height+8;
				x = (float) 188+28;
				y = (float) 436+8;
			}
			break;
		case 5:
//			x = (float) location[0]+38;
//			y = (float) location[1]+7;
			
			if(mHeight>1000&&mWidth>1000){
				//1080p
				width = width+13+6;
				height = height+7+5;
				x = getResources().getDimensionPixelSize(R.dimen.sm_481) + 2;
				y = getResources().getDimensionPixelSize(R.dimen.sm_456) + 12;
			}else {
				width = width+13;
				height = height+8;
				x = (float) 420+38;
				y = (float) 436+8;
			}
			break;
		case 6:
			if(mHeight>1000&&mWidth>1000){
				//1080p
				width = width+15+8;
				height = height+22+13;
				x = getResources().getDimensionPixelSize(R.dimen.sm_746) + 3;
				y = getResources().getDimensionPixelSize(R.dimen.sm_320) + 9;
			}else {
				width = width+18;
				height = height+26;
				x = (float) 654+75;
				y = (float) 189+115;
			}
			break;
		case 7:
			if(mHeight>1000&&mWidth>1000){
				//1080p
				width = width+17+10;
				height = height+12+5;
				x = getResources().getDimensionPixelSize(R.dimen.sm_1000) + 73;
				y = getResources().getDimensionPixelSize(R.dimen.sm_220) + 1;
			}else {
				width = width+17;
				height = height+14;
				x = (float) 924+111;
				y = (float) 189+8;
			}
			break;
		case 8:
			if(mHeight>1000&&mWidth>1000){
				//1080p
				width = width+17+10;
				height = height+12+5;
				x = getResources().getDimensionPixelSize(R.dimen.sm_1000) + 73;
				y = getResources().getDimensionPixelSize(R.dimen.sm_435) - 2;
			}else {
				width = width+17;
				height = height+14;
				x = (float) 924+111;
				y = (float) 394+18;
			}
			break;

		}
		Logger.d(TAG, "X="+x+"---Y="+y);
		home.flyWhiteBorder(width, height, x, y);
}
	
	private void showOnFocusTranslAnimation(int paramInt){
		
		re_fls[paramInt].bringToFront();//将当前FrameLayout置为顶层
		Animation mtAnimation = null;
		Animation msAnimation = null;
		switch (paramInt) {
		case 0:
			mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, -5.0f);
			break;
		case 1:
			mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, 1.0f);
			break;
		case 2:
			mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, 5.0f);
			break;
		case 3:
			mtAnimation = animEffect.translAnimation(0.0f, -10.0f, 0.0f, -5.0f);
			break;
		case 4:
			mtAnimation = animEffect.translAnimation(0.0f, -20.0f, 0.0f, 5.0f);
			break;
		case 5:
			mtAnimation = animEffect.translAnimation(0.0f, -10.0f, 0.0f, 5.0f);
			break;
		case 6:
			mtAnimation = animEffect.translAnimation(0.0f, 10.0f, 0.0f, 0.0f);
			break;
		case 7:
			mtAnimation = animEffect.translAnimation(0.0f, 20.0f, 0.0f, -5.0f);
			break;
		case 8:
			mtAnimation = animEffect.translAnimation(0.0f, 20.0f, 0.0f, 5.0f);
			break;
		default:
			break;
		}
		msAnimation = animEffect.ScaleAnimation(1.0F, 1.05F, 1.0F, 1.05F);
		AnimationSet set=new AnimationSet(true);
		set.addAnimation(msAnimation);
		set.addAnimation(mtAnimation);
		set.setFillAfter(true);
//		set.setFillEnabled(true);
		set.setAnimationListener(new MyOnFocusAnimListenter(paramInt));
//		ImageView iv = re_typeLogs[paramInt];
//		iv.setAnimation(set);
//		set.startNow(); TODO
		re_fls[paramInt].startAnimation(set);
		//re_fls[paramInt].startAnimation(set);

	}
	
	/**
	 * 失去焦点缩小
	 * @param paramInt
	 */
	private void showLooseFocusTranslAinimation(int paramInt) {
		Animation mAnimation = null;
		Animation mtAnimation = null;
		Animation msAnimation = null;
		AnimationSet set = null;
		switch (paramInt) {
		case 0:
			mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, -5.0f, 0.0f);
			break;
		case 1:
			mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 1.0f, 0.0f);
			break;
		case 2:
			mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
			break;
		case 3:
			mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, -5.0f, 0.0f);
			break;
		case 4:
			mtAnimation = animEffect.translAnimation(-20.0f, 0.0f, 5.0f, 0.0f);
			break;
		case 5:
			mtAnimation = animEffect.translAnimation(-10.0f, 0.0f, 5.0f, 0.0f);
			break;
		case 6:
			mtAnimation = animEffect.translAnimation(10.0f, 0.0f, 0.0f, 0.0f);
			break;
		case 7:
			mtAnimation = animEffect.translAnimation(20.0f, 0.0f, -5.0f, 0.0f);
			break;
		case 8:
			mtAnimation = animEffect.translAnimation(20.0f, 0.0f, 5.0f, 0.0f);
			break;

		default:
			break;
			
		}
		msAnimation = animEffect.ScaleAnimation(1.05F, 1.0F, 1.05F, 1.0F);
		set =new AnimationSet(true);
		set.addAnimation(msAnimation);
		set.addAnimation(mtAnimation);
		set.setFillAfter(true);
//		set.setFillEnabled(true);
		set.setAnimationListener(new MyLooseFocusAnimListenter(paramInt));
//		ImageView iv = re_typeLogs[paramInt];
//		iv.setAnimation(set);
//		set.startNow();
//		mAnimation.setAnimationListener(new MyLooseFocusAnimListenter(paramInt));
		rebgs[paramInt].setVisibility(View.GONE);
		re_fls[paramInt].startAnimation(set);
	}
	
	/**
	 * 获取焦点时动画监听
	 * @author joychang
	 *
	 */
	public class MyOnFocusAnimListenter implements Animation.AnimationListener {

		private int paramInt;

		public MyOnFocusAnimListenter(int paramInt) {
			this.paramInt = paramInt;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Logger.v("joychang", "onAnimationEnd");
			rebgs[paramInt].setVisibility(View.VISIBLE);
//			Animation localAnimation =animEffect
//					.alphaAnimation(0.0F, 1.0F, 150L, 0L);
//			localImageView.startAnimation(localAnimation);
			if(paramInt >= 3) {
				tvs[paramInt-3].setVisibility(View.VISIBLE);
			}
		}
		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	}
	
	
	/**
	 * 获取焦点时动画监听
	 * @author joychang
	 *
	 */
	public class MyLooseFocusAnimListenter implements Animation.AnimationListener {
		
		private int paramInt;
		
		public MyLooseFocusAnimListenter(int paramInt) {
			this.paramInt = paramInt;
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			Logger.v("joychang", "onAnimationEnd");
//			Animation localAnimation =animEffect
//					.alphaAnimation(0.0F, 1.0F, 150L, 0L);
//			localImageView.startAnimation(localAnimation);
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
	}
	
	/**
	 * 根据状态来下载或者打开app
	 * @author drowtram
	 * @param apkurl
	 * @param packName
	 */
	private void startOpenOrDownload(String apkurl, String packName, String fileName) {
		//判断当前应用是否已经安装
		for (PackageInfo pack : home.packLst) {
			if(pack.packageName.equals(packName)){
				//已安装了apk，则直接打开
				Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(packName);
				startActivity(intent);  
				return;
			}
		}
		//如果没有安装，则查询本地是否有安装包文件，有则直接安装
		if(!Utils.startCheckLoaclApk(home,fileName)){
			//如果没有安装包  则进行下载安装
			Utils.startDownloadApk(home,apkurl);
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.iv_re_0:
			//搜索
			i = new Intent();
			i.setClass(home, SearchActivity.class);
			i.putExtra("TYPE", "ALL");
			startActivity(i);
			break;
		case R.id.iv_re_1:
			//排行
			i = new Intent();
			i.setClass(home, UserActivity.class);
			startActivity(i);
			//Utils.showToast(home, "即将开放,敬请期待！", R.drawable.toast_smile);
			break;
		case R.id.iv_re_2:
			//应用
			//if(ISTV){
			String apkUrl = Utils.getFormInfo(getClass(), 1);
			String apkPack = Utils.getFormInfo(getClass(), 2);
			Logger.d("zhouchuan","apkUrl="+apkUrl+"apkPack="+apkPack);
			startOpenOrDownload(apkUrl, apkPack, apkUrl.substring(apkUrl.lastIndexOf("/")+1));
//			}else{
//				Utils.showToast(home, "检测到您是手机设备，不适合使用TV商城哦", R.drawable.toast_smile);
//			}
			//Utils.showToast(home, "小米商城暂未开放！", R.drawable.toast_smile);
			break;
		case R.id.iv_re_3:
			if(null!=data){
				i = new Intent();
				i.setClass(home, VodDetailsActivity.class);
				i.putExtra("nextlink", data.get(0).getTjurl());
				i.putExtra("vodstate", data.get(0).getState());
				i.putExtra("vodtype", data.get(0).getTjtype().toUpperCase());
				startActivity(i);
			}
			break;
		case R.id.iv_re_4:
			if(null!=data){
				i = new Intent();
				i.setClass(home, VodDetailsActivity.class);
				i.putExtra("nextlink", data.get(1).getTjurl());
				i.putExtra("vodstate", data.get(1).getState());
				i.putExtra("vodtype", data.get(1).getTjtype().toUpperCase());
				startActivity(i);
			}
			break;
		case R.id.iv_re_5:
			if(null!=data){
				i = new Intent();
				i.setClass(home, VodDetailsActivity.class);
				i.putExtra("nextlink", data.get(2).getTjurl());
				i.putExtra("vodstate", data.get(2).getState());
				i.putExtra("vodtype", data.get(2).getTjtype().toUpperCase());
				startActivity(i);
			}
			break;
		case R.id.iv_re_6:
			if(null!=data){
				i = new Intent();
				i.setClass(home, VodDetailsActivity.class);
				i.putExtra("nextlink", data.get(3).getTjurl());
				i.putExtra("vodstate", data.get(3).getState());
				i.putExtra("vodtype", data.get(3).getTjtype().toUpperCase());
				Logger.d(TAG, "推荐位类型===="+data.get(3).getTjtype().toUpperCase());
				startActivity(i);
			}
			break;
		case R.id.iv_re_7:
			if(null!=data){
				i = new Intent();
				i.setClass(home, VodDetailsActivity.class);
				i.putExtra("nextlink", data.get(4).getTjurl());
				i.putExtra("vodstate", data.get(4).getState());
				i.putExtra("vodtype", data.get(4).getTjtype().toUpperCase());
				startActivity(i);
			}
			break;
		case R.id.iv_re_8:
			if(null!=data){
				i = new Intent();
				i.setClass(home, VodDetailsActivity.class);
				i.putExtra("nextlink", data.get(5).getTjurl());
				i.putExtra("vodstate", data.get(5).getState());
				i.putExtra("vodtype", data.get(5).getTjtype().toUpperCase());
				Logger.d(TAG, "星星state="+data.get(5).getState());
				startActivity(i);
			}
			break;
		}
		home.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
	
	private View view;
	private FrameLayout[] re_fls;
	public ImageView[] re_typeLogs;
	private TextView[] tvs;
	private int[] re_typebgs;
	private ImageView[] rebgs;
	ScaleAnimEffect animEffect;
	private final String TAG = "RecommendFragment"; 
	
	public RequestQueue mQueue;
	public ImageLoader imageLoader;
	private List<RecommendInfo> data = null;
	private TextView tv_intro = null;

}
