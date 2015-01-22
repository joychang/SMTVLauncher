package com.shenma.tvlauncher.fragment;

import java.util.ArrayList;
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
import com.shenma.tvlauncher.TopicActivity;
import com.shenma.tvlauncher.application.MyVolley;
import com.shenma.tvlauncher.domain.Topic;
import com.shenma.tvlauncher.domain.TopicInfo;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.ImageUtil;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.ScaleAnimEffect;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @Description 影视分類
 * @author joychang
 * 
 */
public class TopicFragment extends BaseFragment implements
		OnFocusChangeListener, OnClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "topicFragment...onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Logger.d(TAG, "topicFragment...onCreateView");
		if (container == null) {
			return null;
		}
		if (null == view) {
			view = inflater.inflate(R.layout.layout_topic, container, false);
			init();
		} else {
			((ViewGroup) view.getParent()).removeView(view);
		}
		if(data == null){
			initData();
		}
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	// 初始化
	private void init() {
		loadViewLayout();
		findViewById();
		setListener();
		// mv_fls[0].requestFocus();
	}

	private void initData() {
		mQueue = Volley.newRequestQueue(home, new HurlStack());
		imageLoader = MyVolley.getImageLoader();

		GsonRequest<Topic> mtopics = new GsonRequest<Topic>(Method.GET,
				Constant.TOPIC_URL, Topic.class, createMyReqSuccessListener(),
				createMyReqErrorListener()) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String base64 = new String(android.util.Base64.encode(
						"admin:1234".getBytes(), android.util.Base64.DEFAULT));
				headers.put("Authorization", "Basic " + base64);
				return headers;
			}
		};

		mQueue.add(mtopics); // 执行
	}

	// 请求成功
	private Response.Listener<Topic> createMyReqSuccessListener() {
		return new Response.Listener<Topic>() {
			@Override
			public void onResponse(Topic response) {
				data = response.getData();
				//count = new int[6];
				int paramInt = 0;
				String paramUrl;
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).getTjwei().equals("1")) {
						paramInt = 0;
						qxk = i;
					} else if (data.get(i).getTjwei().equals("2")) {
						paramInt = 1;
						tvb = i;
					} else if (data.get(i).getTjwei().equals("3")) {
						paramInt = 2;
						etjy = i;
					} else if (data.get(i).getTjwei().equals("4")) {
						zrf = i;
						paramInt = 3;
					} else if (data.get(i).getTjwei().equals("5")) {
						wpzy = i;
						paramInt = 4;
					} else if (data.get(i).getTjwei().equals("6")) {
						gx = i;
						paramInt = 5;
					}
					paramUrl = Constant.HEARD_URL + data.get(i).getSmallpic();
					Logger.v("joychang", "paramUrl=" + paramUrl);
					if (null != paramUrl && !paramUrl.contains("null")) {
						setTypeImage(paramInt, paramUrl);
					}
				}
			}
		};
	}

	private void setTypeImage(int paramInt, String paramUrl) {
		Logger.d(TAG, "paramUrl=" + paramUrl);
		// imageLoader.get(paramUrl,
		// ImageLoader.getImageListener(typeLog_bgs[paramInt],mvbgs[paramInt],mvbgs[paramInt]));
		imageLoader.get(paramUrl, ImageUtil.getmImageListener(
				mv_typeLogs[paramInt], mvbgs[paramInt], mvbgs[paramInt]));
	}

	// 请求失败
	private Response.ErrorListener createMyReqErrorListener() {
		return new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (error instanceof TimeoutError) {
					Logger.e("joychang", "请求超时");
				} else if (error instanceof AuthFailureError) {
					Logger.e("joychang", "AuthFailureError=" + error.toString());
				}
			}
		};
	}

	protected void loadViewLayout() {
		mv_fls = new FrameLayout[6];
		mv_typeLogs = new ImageView[6];
		// typeLog_bgs = new ImageView[7];
		mvLogs = new ImageView[6];
		mvbgs = new int[6];
		animEffect = new ScaleAnimEffect();
	}

	protected void findViewById() {
		mv_fls[0] = (FrameLayout) view.findViewById(R.id.topic_fl_0);
		mv_fls[1] = (FrameLayout) view.findViewById(R.id.topic_fl_1);
		mv_fls[2] = (FrameLayout) view.findViewById(R.id.topic_fl_2);
		mv_fls[3] = (FrameLayout) view.findViewById(R.id.topic_fl_3);
		mv_fls[4] = (FrameLayout) view.findViewById(R.id.topic_fl_4);
		mv_fls[5] = (FrameLayout) view.findViewById(R.id.topic_fl_5);

		mv_typeLogs[0] = (ImageView) view.findViewById(R.id.topic_iv_0);
		mv_typeLogs[1] = (ImageView) view.findViewById(R.id.topic_iv_1);
		mv_typeLogs[2] = (ImageView) view.findViewById(R.id.topic_iv_2);
		mv_typeLogs[3] = (ImageView) view.findViewById(R.id.topic_iv_3);
		mv_typeLogs[4] = (ImageView) view.findViewById(R.id.topic_iv_4);
		mv_typeLogs[5] = (ImageView) view.findViewById(R.id.topic_iv_5);

		mvLogs[0] = (ImageView) view.findViewById(R.id.topic_bg_0);
		mvLogs[1] = (ImageView) view.findViewById(R.id.topic_bg_1);
		mvLogs[2] = (ImageView) view.findViewById(R.id.topic_bg_2);
		mvLogs[3] = (ImageView) view.findViewById(R.id.topic_bg_3);
		mvLogs[4] = (ImageView) view.findViewById(R.id.topic_bg_4);
		mvLogs[5] = (ImageView) view.findViewById(R.id.topic_bg_5);

		mvbgs[0] = R.drawable.topic_iv_0;
		mvbgs[1] = R.drawable.topic_iv_1;
		mvbgs[2] = R.drawable.topic_iv_2;
		mvbgs[3] = R.drawable.topic_iv_3;
		mvbgs[4] = R.drawable.topic_iv_4;
		mvbgs[5] = R.drawable.topic_iv_5;
		for (int i = 0; i < mv_typeLogs.length; i++) {
			mvLogs[i].setVisibility(View.GONE);
			mv_typeLogs[i].setOnClickListener(this);
			// if(ISTV){
			mv_typeLogs[i].setOnFocusChangeListener(this);
			// }
		}
	}

	protected void setListener() {

	}

	@Override
	public void onClick(View v) {
		//TODO 跳转二级界面 传参：专题类型、专题二级接口地址、专题大海报地址
		Intent i = new Intent(home,TopicActivity.class);
		switch (v.getId()) {
		case R.id.topic_iv_0:
			//抢先看
			if(null!=data && data.size()>0){
				i.putExtra("describe", data.get(qxk).getZtdescribe());
				i.putExtra("bigpic", data.get(qxk).getBigpic());
				i.putExtra("linkurl", data.get(qxk).getLinkurl());
				i.putExtra("TYPE", "MOVIE");
				startActivity(i);
			}
			break;
		case R.id.topic_iv_1:
			//热播电视剧
			if(null!=data && data.size()>1){
				i.putExtra("describe", data.get(tvb).getZtdescribe());
				i.putExtra("bigpic", data.get(tvb).getBigpic());
				i.putExtra("linkurl", data.get(tvb).getLinkurl());
				i.putExtra("TYPE", "MOVIE");
				startActivity(i);
			}
			break;
		case R.id.topic_iv_2:
			//儿童教育
			if(null!=data && data.size()>2){
				i.putExtra("describe", data.get(etjy).getZtdescribe());
				i.putExtra("bigpic", data.get(etjy).getBigpic());
				i.putExtra("linkurl", data.get(etjy).getLinkurl());
				i.putExtra("TYPE", "MOVIE");
				startActivity(i);
			}
			break;
		case R.id.topic_iv_3:
			//周润发专题
			if(null!=data && data.size()>3){
				i.putExtra("describe", data.get(zrf).getZtdescribe());
				i.putExtra("bigpic", data.get(zrf).getBigpic());
				i.putExtra("linkurl", data.get(zrf).getLinkurl());
				i.putExtra("TYPE", "MOVIE");
				startActivity(i);
			}
			break;
		case R.id.topic_iv_4:
			//王牌综艺
			if(null!=data && data.size()>4){
				i.putExtra("describe", data.get(wpzy).getZtdescribe());
				i.putExtra("bigpic", data.get(wpzy).getBigpic());
				i.putExtra("linkurl", data.get(wpzy).getLinkurl());
				i.putExtra("TYPE", "MOVIE");
				startActivity(i);
			}
			break;
		case R.id.topic_iv_5:
			//搞笑
			if(null!=data && data.size()>5){
				i.putExtra("describe", data.get(gx).getZtdescribe());
				i.putExtra("bigpic", data.get(gx).getBigpic());
				i.putExtra("linkurl", data.get(gx).getLinkurl());
				i.putExtra("TYPE", "MOVIE");
				startActivity(i);
			}
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int paramInt = 0;
		switch (v.getId()) {
		case R.id.topic_iv_0:
			paramInt = 0;
			break;
		case R.id.topic_iv_1:
			paramInt = 1;
			break;
		case R.id.topic_iv_2:
			paramInt = 2;
			break;
		case R.id.topic_iv_3:
			paramInt = 3;
			break;
		case R.id.topic_iv_4:
			paramInt = 4;
			break;
		case R.id.topic_iv_5:
			paramInt = 5;
			break;
		}
		if (hasFocus) {
			showOnFocusAnimation(paramInt);
			if (null != home.whiteBorder) {
				home.whiteBorder.setVisibility(View.VISIBLE);
			}
			flyAnimation(paramInt);
			// 白框动画
		} else {
			showLoseFocusAinimation(paramInt);
			// 将白框隐藏
		}
	}

	/**
	 * 飞框焦点动画
	 * 
	 * @param paramInt
	 */
	private void flyAnimation(int paramInt) {
		int[] location = new int[2];
		mv_typeLogs[paramInt].getLocationOnScreen(location);
		int width = mv_typeLogs[paramInt].getWidth();
		int height = mv_typeLogs[paramInt].getHeight();
		float x = (float) location[0];
		float y = (float) location[1];
		Logger.v("joychang", "paramInt=" + paramInt + "..x=" + x + "...y=" + y);
		if (mHeight > 1000 && mWidth > 1000) {
			switch (paramInt) {
			case 0:
				width = width + 46;
				height = height + 65;
				x = 285;
				y = 490;
				break;
			case 1:
				width = width + 65;
				height = height + 39;
				x = 781;
				y = 377;
				break;
			case 2:
				width = width + 30;
				height = height + 23;
				x = 630;
				y = 679;
				break;
			case 3:
				width = width + 30;
				height = height + 23;
				x = 926;
				y = 679;
				break;
			case 4:
				width = width + 40;
				height = height + 60;
				x = 1272;
				y = 490;
				break;
			case 5:
				width = width + 30;
				height = height + 60;
				x = 1620;
				y = 339 + 150;
			}
		} else {
			switch (paramInt) {
			case 0:
				width = width + 26;
				height = height + 40;
				x = 102 + 66;
				y = 189 + 116;
				break;
			case 1:
				width = width + 43;
				height = height + 27;
				x = 365 + 132;
				y = 189 + 41;
				break;
			case 2:
				width = width + 21;
				height = height + 14;
				x = 365 + 33;
				y = 422 + 9;
				break;
			case 3:
				width = width + 21;
				height = height + 14;
				x = 561 + 35;
				y = 422 + 9;
				break;
			case 4:
				width = width + 26;
				height = height + 42;
				x = 760 + 67;
				y = 189 + 115;
				break;
			case 5:
				width = width + 20;
				height = height + 40;
				x = 1023 + 35;
				y = 289 + 15;
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
		mv_fls[paramInt].bringToFront();// 将当前FrameLayout置为顶层
		float f1 = 1.0F;
		float f2 = 1.1F;
		this.animEffect.setAttributs(1.0F, 1.1F, f1, f2, 200L);
		Animation mAnimation = this.animEffect.createAnimation();
		mAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// settingbgs[paramInt].startAnimation(animEffect.alphaAnimation(0.0F,
				// 1.0F, 150L, 0L));
				mvLogs[paramInt].setVisibility(View.VISIBLE);
				// settingbgs[paramInt].bringToFront();
			}
		});
		mv_typeLogs[paramInt].startAnimation(mAnimation);
		// typeLog_bgs[paramInt].startAnimation(mAnimation);
	}

	/**
	 * 失去焦点缩小
	 * 
	 * @param paramInt
	 */
	private void showLoseFocusAinimation(final int paramInt) {
		float f1 = 1.1F;
		float f2 = 1.0F;
		animEffect.setAttributs(1.1F, 1.0F, f1, f2, 200L);
		Animation mAnimation = this.animEffect.createAnimation();
		mvLogs[paramInt].setVisibility(View.GONE);
		// mAnimation.setAnimationListener(new AnimationListener() {
		// @Override
		// public void onAnimationStart(Animation animation) {}
		// @Override
		// public void onAnimationRepeat(Animation animation) {}
		// @Override
		// public void onAnimationEnd(Animation animation) {
		// settingbgs[paramInt].setVisibility(View.GONE);
		// }
		// });
		// typeLog_bgs[paramInt].startAnimation(mAnimation);
		mv_typeLogs[paramInt].startAnimation(mAnimation);

	}

	@Override
	public void onStop() {
		super.onStop();
		if (null != mQueue) {
			mQueue.stop();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != mQueue) {
			mQueue.cancelAll(this);
		}
	}

	private FrameLayout[] mv_fls;
	public ImageView[] mv_typeLogs;
	private ImageView[] mvLogs;
	private int[] mvbgs;
	ScaleAnimEffect animEffect;
	private View view;
	public RequestQueue mQueue;
	public ImageLoader imageLoader;
	private List<TopicInfo> data = null;
	private String TAG = "topicFragment";
	//private  int[] count;
	private int qxk = 0;
	private int tvb = 0;
	private int etjy = 0;
	private int zrf = 0;
	private int wpzy = 0;
	private int gx = 0;
	
}
