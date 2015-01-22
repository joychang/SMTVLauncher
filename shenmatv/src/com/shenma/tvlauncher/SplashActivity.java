package com.shenma.tvlauncher;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.shenma.tvlauncher.domain.UserStatement;
import com.shenma.tvlauncher.network.GsonRequest;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 应用开启启动界面
 * 
 * @author joychang
 * 
 */
public class SplashActivity extends BaseActivity {
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		findViewById();
		Utils.loadingShow(SplashActivity.this, "");
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isNetWork();// 判断网络是否连接
	}

	/**
	 * 加载主界面
	 */
	private void loadMainUI() {
		openActivity(HomeActivity.class);
		finish();// 把当前的activity从任务栈里面移除
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(null!=mQueue){
			mQueue.stop();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Utils.loadingClose();
		if(null!=mQueue){
			mQueue.cancelAll(this);
		}
	}

	@Override
	public void onBackPressed() {
		System.exit(0);
		//this.finish();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_INFO_SUCCESS:
				loadMainUI();
				break;
			case DETECTION_NET:
				if (Utils.hasNetwork(context)) {
					handler.sendEmptyMessage(GET_INFO_SUCCESS);
				}else{
					handler.sendEmptyMessageDelayed(DETECTION_NET, 3000);
				}
				break;
			}
		}
	};
	
	/**
	 * 判断是否联网
	 */
	private void isNetWork() {
		if (Utils.hasNetwork(context)) {
			firstTime = System.currentTimeMillis();
			initData();//初始化数据
		} else {
			showNetDialog(context);
			handler.sendEmptyMessageDelayed(DETECTION_NET, 1000);
		}
	}

	private void initData(){
		String version = Utils.getVersion(SplashActivity.this);
		String DISPLAY = android.os.Build.DISPLAY;
		String VERSION_RELEASE = android.os.Build.VERSION.RELEASE;
		String MODEL = android.os.Build.MODEL;
		String DEVICE = android.os.Build.DEVICE;
		TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		String DeviceId = telephonyManager.getDeviceId();
		String username = "smtest";
		String devicecode = VERSION_RELEASE+"-"+MODEL+"-"+DeviceId;
		try {
			devicecode = URLEncoder.encode(devicecode, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.d(TAG, "DISPLAY="+DISPLAY+"....DEVICE="+DEVICE);
		Logger.d(TAG, "统计数据："+devicecode);
		Logger.i(TAG,"统计调用："+Constant.STARTINFO_URL+params+"&username="+username+"&devicecode="+devicecode);
		mQueue = Volley.newRequestQueue(SplashActivity.this, new HurlStack());
		GsonRequest<UserStatement> mUserStatement = new GsonRequest<UserStatement>(Method.GET, Constant.STARTINFO_URL+params+"&username="+username+"&devicecode="+devicecode,
				UserStatement.class,createMyReqSuccessListener(),createMyReqErrorListener()){
									@Override
                                    public Map<String, String> getHeaders()
                                    		throws AuthFailureError {
                						HashMap<String, String> headers = new HashMap<String, String>();
                						String base64 = new String(android.util.Base64.encode(
                								"admin:1234".getBytes(), android.util.Base64.DEFAULT));
                						headers.put("Authorization", "Basic " + base64);
                                    	return headers;
                                    }};
                                    
       mQueue.add(mUserStatement);     //     执行      
	}
	
	//请求成功
    private Response.Listener<UserStatement> createMyReqSuccessListener() {
        return new Response.Listener<UserStatement>() {
            @Override
            public void onResponse(UserStatement response) {
            	if(null!=response){
            		String exitStr = response.getExitinfo();
            		if(!exitStr.equals("")){
            			//exit = exitStr;
            		}
            	}
            	startHome();
            	Logger.d(TAG, "统计成功!"+"退出提示："+exit);
            }
        };
    }
    
    //请求失败
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	startHome();
            	if(error instanceof TimeoutError){
            		Logger.e(TAG, "请求超时");
            	}else if(error instanceof AuthFailureError){
            		Logger.e(TAG, "AuthFailureError="+error.toString());
                }
            }
        };
    }

    private void startHome(){
    	long secondTime = System.currentTimeMillis()-firstTime;
    	handler.sendEmptyMessage(GET_INFO_SUCCESS);
//    	if(secondTime>3000){
//        	handler.sendEmptyMessage(GET_INFO_SUCCESS);
//    	}else{
//    		handler.sendEmptyMessageDelayed(GET_INFO_SUCCESS, 1000);
//    	}
    }
    
	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 初始化控件
	 */
	@Override
	protected void findViewById() {
		rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
		rl_splash.setBackgroundResource(R.drawable.splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("Version: "+Utils.getVersion(SplashActivity.this));
		//tv_splash_version.setText("余波♥周晴");
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub
		
	}
	
	private TextView tv_splash_version;
	private static final int GET_INFO_SUCCESS = 10;
	private static final int DETECTION_NET = 18;
	protected static final String TAG = "SplashActivity";
	private RelativeLayout rl_splash;
	public RequestQueue mQueue;
	private long firstTime;
	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		
	}
}