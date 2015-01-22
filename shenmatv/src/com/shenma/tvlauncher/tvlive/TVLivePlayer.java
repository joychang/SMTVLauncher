package com.shenma.tvlauncher.tvlive;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.nfc.LlcpPacket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.forcetech.android.ForceTV;
import com.shenma.tvlauncher.BaseActivity;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.tvlive.adapter.ContentAdapter;
import com.shenma.tvlauncher.tvlive.adapter.TVLiveMenuAdapter;
import com.shenma.tvlauncher.tvlive.network.ClassManager;
import com.shenma.tvlauncher.tvlive.network.DownloadResourse;
import com.shenma.tvlauncher.tvlive.network.LiveConstant;
import com.shenma.tvlauncher.tvlive.network.DownLoadTools;
import com.shenma.tvlauncher.tvlive.network.ThreadPoolManager;
import com.shenma.tvlauncher.tvlive.parsexml.EPG;
import com.shenma.tvlauncher.tvlive.parsexml.EPGS;
import com.shenma.tvlauncher.tvlive.parsexml.NetMedia;
import com.shenma.tvlauncher.tvlive.parsexml.NetMediaCollection;
import com.shenma.tvlauncher.tvlive.parsexml.NetMediaXmlParse;
import com.shenma.tvlauncher.tvlive.parsexml.PtoPMainInterface;
import com.shenma.tvlauncher.tvlive.parsexml.PtopMainInterfaceParse;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.MainUrl;
import com.shenma.tvlauncher.utils.TVLiveUtils;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.LiveLoadingDialog;
import com.shenma.tvlauncher.view.WiFiDialog;
import com.umeng.analytics.MobclickAgent;
import com.wepower.live.parser.ILetv;
import com.wepower.live.parser.IPlay;

/**
 * @Decription 电视直播
 * @author joychang
 *
 */
public class TVLivePlayer extends BaseActivity implements OnItemClickListener,
		OnClickListener, OnItemSelectedListener {
	private static final String TAG = "TVLivePlayer";
	private BVideoView mVV = null;
	private ImageView mLeftView = null;
	private ImageView mRightView = null;
	private ImageView downview = null;
	private TextView mProgramTitle = null;
	private TextView mProgramNum = null;
	private TextView mProgramNews = null;
	private ListView mProgranList = null;
	private View mLeftLayout = null;
	private LiveLoadingDialog Loadingdialog = null;
	private NetMediaCollection liveData = null;
	private ContentAdapter mAdapter = null;
	private int mClassPoint = 0; // 当前的节目分类
	private int mProgramPoint = 0; // 当前的电视台
	private int mSourcePoint = 0; // 当前的电视台url
	private Dialog mSwitchDialog = null;
	private static int DELAY_TIME = 8000;
	private static final int DELAY_NEXT_TIME = 60000;
	private static final int TURN_LEFT = 1; // 遥控往左
	private static final int TRUN_RIGHT = 2;// 遥控往右
	private static final String CLASS_TYPE = "class_type";// 保存在配置文件中的节目分类
	private static final String PROGRAM_TYPE = "program_type";// 保存在配置文件中的电视台
	private static final String FILE_NAME = "play_file";// 配置文件名
	private NetMedia mCurrentMedia = null;// 当前播放的电台
	public static String keyChanne = "";//数字选台
	public static final String ENGER_FILE_NAME = "enter.xml";
	public static final String DATA_FILE_NAME = "data.xml";
	public static final String NEWS_FILE_NAME = "news.xml";
	private PtoPMainInterface mEnterList = new PtoPMainInterface();
	private String newsString = "";
	private AudioManager mAudioManager = null;
	private String mCurEpg = "";
	private DownEpg mDownEpg = null;
	private long mTimeOut = 0;
	private boolean isSendNext = false;
	private TextView epg1;
	private TextView epg2;
	private TextView curren_tv_title;
	private TextView tv_name;
	private RelativeLayout epg_layout;
	private TextView tv_line,tv_speed;
	private GestureDetector mGestureDetector = null;//手势
	private long firstTime = 0;
	private int keyChannenum = 0;
	
	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "TVLivePlayer";
	
	//p2p
	private long port;//启动服务id号
	
	ClassManager classManager = new ClassManager(TVLivePlayer.this);
	private ILetv iletv;
	private IPlay iplay;
	private  enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;//策略监控 true开启 false关闭
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDialog().build());
//			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
//		}
		setContentView(R.layout.lives_main);
		mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		mProgramNews = (TextView) findViewById(R.id.program_news);
		showProgressDialog(R.string.load_msg);
		mQueue = Volley.newRequestQueue(this, new HurlStack());
		initView();
		Intent intent = getIntent();
		keyChannenum = intent.getIntExtra("KEYCHANNE", 1);
		TVTYPE = intent.getStringExtra("TVTYPE");
		if(TVTYPE.equals(Constant.TVLIVE_DIY)){
			uName = sp.getString("userName", null);
			if(null!=uName && !uName.equals("")){
				initData();
			}else{
				showLogoutDialog();
				//Utils.showToast(TVLivePlayer.this, "亲爱的用户，您还没有登录呢！", R.drawable.toast_shut);
			}
		}else{
			new DownloadResourse(this, myHandler).isUpdate();
		}
	}
	
	private void initData(){
		String ckinfo = "231231";
		StringRequest sr = new StringRequest("http://www.smtvzm.com/index.php/user/getmychannel.xml?"+"loginname="+uName+"&ckinfo="+ckinfo, createMyReqSuccessListener(), createMyReqErrorListener()){
			@Override
	        public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				String base64 = new String(android.util.Base64.encode("admin:1234".getBytes(), android.util.Base64.DEFAULT));
				headers.put("Authorization", "Basic " + base64);
	        	return headers;
	        }
		};
		mQueue.add(sr);
	}
	
	private void initPlugs(){
		//在线插件初始化
		classManager.initFile();
		iletv = classManager.getLetvClass();
		String soName = sp.getString(LiveConstant.SO_NAME, "libutp.so");
		iletv.loadLetv(soName, context);
		port = iletv.start();
		iplay = classManager.getPlayClass();
		iplay.returnIP();
	}
	
	//请求成功
    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            	new LoadXMLThread("http://www.smtvzm.com/xmlfile/"+uName.toLowerCase()+".xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
            }
        };
    }
    
    //请求失败
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            	Utils.showToast(TVLivePlayer.this, "亲爱的用户，您后台配置的数据有误！", R.drawable.toast_shut);
            	if(error instanceof TimeoutError){
            		Logger.e(TAG, "请求超时");
            	}else if(error instanceof AuthFailureError){
            		Logger.e(TAG, "AuthFailureError="+error.toString());
                }
            }
        };
    }

	@Override
	protected void onResume() {
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
		acquireWakeLock();
		if(!mHandlerThread.isAlive()){
			/**
			 * 开启后台事件处理线程
			 */
			mHandlerThread = new HandlerThread("event handler thread",android.os.Process.THREAD_PRIORITY_BACKGROUND);
			mHandlerThread.start();
			mEventHandler = new EventHandler(mHandlerThread.getLooper());
		}
		mSpeedHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case START_SPEED:
					tv_speed.setText(rxByte);
					Log.i(TAG, "speed="+rxByte);
					break;
				}
			}
		
		};
		speedRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(lastRxByte!=0 && lastSpeedTime != 0){
					long nowtime = System.currentTimeMillis();
					long nowRxbyte = TrafficStats.getTotalRxBytes();
					long rxbyte = nowRxbyte-lastRxByte;
					long time = nowtime-lastSpeedTime;
					if(rxbyte>0 && time>0){
						 speed = rxbyte / time * 1000L/ 1024L;
						 if(speed>=1024){
							 rxByte = String.valueOf(speed/1024L)+"MB/S";
						 }else{
							 rxByte = String.valueOf(speed)+"KB/S";
						 }
						 mSpeedHandler.sendEmptyMessage(START_SPEED);
					}
					lastRxByte = nowRxbyte;
					lastSpeedTime = nowtime;
				}
				mSpeedHandler.postDelayed(speedRunnable, 500); 
			}
		};
		super.onResume();
	}

	protected void onPause() {
		Logger.i(TAG, "onPause()...");
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
		putConfig(CLASS_TYPE, mClassPoint);
		putConfig(PROGRAM_TYPE, mProgramPoint);
		myHandler.removeMessages(LiveConstant.SWICH_LINE);
		myHandler.removeMessages(LiveConstant.PLAY_NEXT);
		myHandler.removeMessages(LiveConstant.SELECT_CHANNE);
		releaseWakeLock();
		mSpeedHandler.removeCallbacks(speedRunnable);
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mVV.stopPlayback();
		}
		super.onPause();
	}
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Logger.i(TAG, "onStop()...");
		if(null!=mQueue){
			mQueue.stop();
		}
		closeDialog();
		mHandlerThread.quit();
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Logger.i(TAG, "onDestroy()...");
		if(null!=mQueue){
			mQueue.cancelAll(this);
		}
		if(null!=iletv){
			iletv.stop();
			iletv = null;	
		}
		super.onDestroy();
	}
	

	@Override
	public void onBackPressed() {
		if (Loadingdialog != null && Loadingdialog.isShowing()) {
			closeDialog();
			return;
		}
		if (mSwitchDialog != null && mSwitchDialog.isShowing()) {
			closeLineDialog();
			return;
		}
		if (mLeftLayout != null && mLeftLayout.getVisibility() == View.VISIBLE) {
			mLeftLayout.setVisibility(View.GONE);
			return;
		}
		long secondTime = System.currentTimeMillis();
		if (secondTime - firstTime > 3000) {
				Logger.i(TAG, "再按一次");
				Utils.showToast(TVLivePlayer.this, R.string.onbackpressed,R.drawable.toast_err);
				firstTime = secondTime;// 更新firstTime
				return;
		} else {
				Logger.i(TAG, "finish()...");
				if(null!=mToast){
					mToast.cancel();
				}
				//myHandler.getLooper().quit();
				finish();
				System.exit(0);
		}
	}
	
	
	
	private void initIntent(){
//		MainUrl mu = new MainUrl();
//		heard_url = mu.getHeardUrl();
		if(tvlive_server==1){
//			new LoadXMLThread("http://wephd.live.cctv1949.com/api_new/wephd.xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
			new LoadXMLThread("http://live.lsott.com/wepower/wephd_v3.xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
		}else{
			new LoadXMLThread("http://live.lsott.com/wepower/wephd_test.xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
		}
		//new LoadXMLThread(heard_url, DATA_FILE_NAME, LiveConstant.DOWNLOAD_ENTER_XML).start();
		new ForceTV().initForceClient();
		initPlugs();
	}

	private void initUI() {
		NetMediaXmlParse parse = new NetMediaXmlParse();
		liveData = parse.parseFileXml(new File(getFilesDir() + File.separator
				+ "data.xml"));
		mClassPoint = getConfig(CLASS_TYPE, 0);
		mProgramPoint = getConfig(PROGRAM_TYPE, 0);
		if (mClassPoint >= liveData.getNetMediaList().size()) {
			mClassPoint = 0;
			mProgramPoint = 0;
		}
		if (mProgramPoint >= liveData.getNetMediaList().get(mClassPoint)
				.getChannlesArrayList().size()) {
			mClassPoint = 0;
			mProgramPoint = 0;
		}
		mCurrentMedia = liveData.getNetMediaList().get(mClassPoint)
				.getChannlesArrayList().get(mProgramPoint);
		if (mCurrentMedia == null) {
			mClassPoint = 0;
			mProgramPoint = 0;
		}
		updateData();
		getFocused();
		closeDialog();
		if(0!=keyChannenum){
			initSelectChannle(keyChannenum);
		}else{
			myHandler.sendEmptyMessage(LiveConstant.REPLAY);
			//showLineDialog();
		}
		//mEventHandler = new EventHandler(mHandlerThread.getLooper());
	}
	
	/**
	 * vv监听
	 */
	private void setvvListener(){
		mVV.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(int what, int extra) {
				// TODO Auto-generated method stub
				mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;//更改状态
				isNext = true;
				return false;
			}
		});
		mVV.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared() {
				// TODO Auto-generated method stub
				//mVV.start();
				Logger.i(TAG, "setOnPreparedListener...");
				myHandler.removeMessages(LiveConstant.SWICH_LINE);
				myHandler.removeMessages(LiveConstant.PLAY_NEXT);
				myHandler.sendEmptyMessage(CLOSE_LINE_DIALOG);
				myHandler.sendEmptyMessage(PROGRESSBAR_GONE);
				isSendNext = false;
				//closeLineDialog();
			}
		});

		mVV.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public boolean onInfo(int what, int extra) {
				switch(what){
				/**
				 * 开始缓冲
				 */
				case BVideoView.MEDIA_INFO_BUFFERING_START:
					myHandler.sendEmptyMessage(PROGRESSBAR_VISIBLE);
					//Utils.loadingShow_tv(VideoPlayerActivity.this,R.string.str_data_loading);
					break;
				/**
				 * 结束缓冲
				 */
				case BVideoView.MEDIA_INFO_BUFFERING_END:
					//Utils.loadingClose_Tv();
					myHandler.sendEmptyMessage(PROGRESSBAR_GONE);
					break;
				}
				return true;
			}
		});
		mVV.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub
//				if(isNext){
//					isNext = false;
//					//myHandler.sendEmptyMessage(LiveConstant.PLAY_NEXT);
//				}else{
//					mVV.stopPlayback();
//					finish();
//				}
			}
		});
	}
	
	@Override
	protected void loadViewLayout() {
		// TODO Auto-generated method stub
		onCreateMenu();
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mVV = (BVideoView) findViewById(R.id.vv);
		mLeftView = (ImageView) findViewById(R.id.leftview);
		mRightView = (ImageView) findViewById(R.id.rightview);
		downview = (ImageView) findViewById(R.id.downview);
		mProgramTitle = (TextView) findViewById(R.id.programtitle);
		mProgramNum = (TextView) findViewById(R.id.program_num);
		mProgranList = (ListView) findViewById(R.id.programlist);
		mProgranList.setSelector(new ColorDrawable(0));
		mLeftLayout = findViewById(R.id.left_layout);
		epg1 = (TextView) findViewById(R.id.epg1);
		epg2 = (TextView) findViewById(R.id.epg2);
		epg_layout = (RelativeLayout) findViewById(R.id.epg_layout);
		curren_tv_title = (TextView) findViewById(R.id.curren_tv_title);
		tv_name = (TextView) findViewById(R.id.tv_name);
		rl_progressBar = (RelativeLayout) findViewById(R.id.rl_progressBar);
		tv_line = (TextView) findViewById(R.id.tv_line);
		tv_speed = (TextView) findViewById(R.id.tv_speed);
		BVideoView.setAKSK(Constant.AK, Constant.SK);//设置ak及sk的前16位
		mVV.setDecodeMode(mIsHwDecode?BVideoView.DECODE_HW:BVideoView.DECODE_SW);//设置解码模式
	}
	
	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_PLAY:
				/**
				 * 设置播放url
				 */
				Logger.d(TAG, "mVideoSource="+mVideoSource);
				mVV.setVideoPath(mVideoSource);
				/**
				 * 续播，如果需要如此
				 */
				/**
				 * 显示或者隐藏缓冲提示 
				 */
				mVV.showCacheInfo(false);
				/**
				 * 开始播放
				 */
				mVV.start();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void setListener() {
		mLeftView.setOnClickListener(this);
		mRightView.setOnClickListener(this);
		downview.setOnClickListener(this);
		mProgranList.setOnItemClickListener(this);
		mProgranList.setOnItemSelectedListener(this);
		//手势监听
		mGestureDetector = new GestureDetector(new SimpleOnGestureListener(){

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				//双击
				showMenu();
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// TODO Auto-generated method stub
				//return super.onSingleTapConfirmed(e);
				if (epg_layout.getVisibility() == View.GONE || mLeftLayout.getVisibility() == View.GONE) {
					epg_layout.setVisibility(View.VISIBLE);
					//getFocused();
					myHandler.removeMessages(LiveConstant.EPG_VIEW);
					myHandler.sendEmptyMessageDelayed(LiveConstant.EPG_VIEW, 10000);
					mLeftLayout.setVisibility(View.VISIBLE);
					getFocused();
					myHandler.removeMessages(LiveConstant.HIDE_VIEW);
					myHandler.sendEmptyMessageDelayed(LiveConstant.HIDE_VIEW, 10000);
				}else{
					epg_layout.setVisibility(View.GONE);
					myHandler.removeMessages(LiveConstant.EPG_VIEW);
					mLeftLayout.setVisibility(View.GONE);
					myHandler.removeMessages(LiveConstant.HIDE_VIEW);
				}
				return true;
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
		          return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				showMenu();
			}	
        });
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		Log.d(TAG, "mLeftLayout.getX()="+mLeftLayout.getX());
//		Log.d(TAG, "mLeftLayout.getY()="+mLeftLayout.getY());
		if(mLeftLayout.getVisibility() == View.VISIBLE && mTouchX < getResources().getDimensionPixelSize(R.dimen.sm_360)){
			return true;
		}
		boolean result = mGestureDetector.onTouchEvent(event);
		//mPosition = mLastPos;
		DisplayMetrics screen = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(screen);
		if (mSurfaceYDisplayRange == 0)
			mSurfaceYDisplayRange = Math.min(screen.widthPixels,
					screen.heightPixels);
		float y_changed = event.getRawY() - mTouchY;
		float x_changed = event.getRawX() - mTouchX;
		Log.d(TAG, "mTouchX="+mTouchX);
		Log.d(TAG, "mTouchY="+mTouchY);
		float coef = Math.abs(y_changed / x_changed);
		float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
		Logger.i("joychang", "y_changed="+y_changed+"...x_changed="+x_changed+"...coef="+coef+"...xgesturesize="+xgesturesize);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Logger.i(TAG, "MotionEvent.ACTION_DOWN.......");
			mTouchAction = TOUCH_NONE;
			mTouchY = event.getRawY();
			mTouchX = event.getRawX();
			maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Lightness = Utils.GetLightness(TVLivePlayer.this);
			break;
		case MotionEvent.ACTION_MOVE:
			Logger.i(TAG, "MotionEvent.ACTION_MOVE.......");
			if(coef>2){
				//音量和亮度
				if(mLeftLayout.getVisibility() == View.GONE && mTouchX > (screenWidth / 2)){
					//音量
					mTouchAction = TOUCH_VOLUME;
					if(playPreCode!=0){
						doVolumeTouch(y_changed);
					}
				}
				if (mLeftLayout.getVisibility() == View.GONE && mTouchX < (screenWidth / 2)) {
					mTouchAction = TOUCH_BRIGHTNESS;
					doBrightnessTouch(y_changed);
				}
			}else{
				mTouchAction = TOUCH_SEEK;
			}
			break;
		case MotionEvent.ACTION_UP:
			Logger.i(TAG, "MotionEvent.ACTION_UP.......");
			if(mTouchAction == TOUCH_VOLUME && playPreCode == 0){
				//上下换台
				if(y_changed>80){
					 ChannelNext();
				}else if(y_changed<-80){
					ChannelLast();
				}
			}else if(mTouchAction == TOUCH_SEEK){
				//左右切换源
				if(x_changed>150){
					 swichLine(TRUN_RIGHT);
				}else if(x_changed<-150){
					swichLine(TURN_LEFT);
				}
			}
			break;
		}
		return true;
	}
	
	//调节音量
	private void doVolumeTouch(float y_changed){
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
			return;
		mTouchAction = TOUCH_VOLUME;
		int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * maxVolume);
		int vol = (int) Math.min(Math.max(currentVolume + delta, 0), maxVolume);
		Logger.d("doVolumeTouch", "vol===="+vol+"...delta="+delta);
		if (delta != 0) {
			if(vol <1){
				showVolumeToast(R.drawable.mv_ic_volume_mute, maxVolume, vol,true); 
				//Logger.d("doVolumeTouch", "vol <1 ............."+vol);
			}else if(vol>=1&&vol<maxVolume/2){
				showVolumeToast(R.drawable.mv_ic_volume_low, maxVolume, vol,true); 
				//Logger.d("doVolumeTouch", "vol>=1&&currentVolume<maxVolume/2 ............."+vol);
			}else if(vol >= maxVolume/2){
				showVolumeToast(R.drawable.mv_ic_volume_high, maxVolume, vol,true); 
				//Logger.d("doVolumeTouch", "vol >= maxVolume/2 ............."+vol);
			}
		}
	}
	
	//调节亮度
	private void doBrightnessTouch(float y_changed){
   	 //屏幕亮度 
		//float delta = -y_changed / mSurfaceYDisplayRange * 0.07f;
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
			return;
		mTouchAction = TOUCH_BRIGHTNESS;
		float delta = -y_changed / mSurfaceYDisplayRange * 2f;
		int vol = (int) ((Math.min(Math.max(Lightness + delta, 0.01f)*255, 255)));
		if (delta != 0) {
			if(vol<5){
				showVolumeToast(R.drawable.mv_ic_brightness, 255, 0,false);
			}else{
				showVolumeToast(R.drawable.mv_ic_brightness, 255,vol,false);
			}
			Logger.d("doBrightnessTouch", "Lightness="+Lightness+"....vol="+vol+"...delta="+delta+"....mSurfaceYDisplayRange="+mSurfaceYDisplayRange);
		}
	}
	
	/**
	 * 显示音量的吐司
	 * 
	 * @param max
	 * @param current
	 */
	private void showVolumeToast(int resId, int max, int current,Boolean isVolume) {
		View view;
		if(!isVolume){
			Utils.SetLightness(TVLivePlayer.this, current);
		}else{
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
		}
		if (mToast == null) {
			mToast = new Toast(this);
			view = LayoutInflater.from(this).inflate(R.layout.mv_media_volume_controler,
					null);
			ImageView center_image = (ImageView) view.findViewById(R.id.center_image);
			//TextView textView = (TextView) view.findViewById(R.id.center_info);
			ProgressBar center_progress = (ProgressBar) view
					.findViewById(R.id.center_progress);
			center_progress.setMax(max);
			center_progress.setProgress(current);
			center_image.setImageResource(resId);
			mToast.setView(view);
		} else {
			view = mToast.getView();
			//TextView textView = (TextView) view.findViewById(R.id.center_info);
			ImageView center_image = (ImageView) view.findViewById(R.id.center_image);
			ProgressBar center_progress = (ProgressBar) view
					.findViewById(R.id.center_progress);
			center_progress.setMax(max);
			center_progress.setProgress(current);
			center_image.setImageResource(resId);
		}
		mToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL,0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}

	private void getFocused() {
		mProgranList.setFocusable(true);
		mProgranList.setFocusableInTouchMode(true);
		mProgranList.requestFocus();
		mProgranList.setSelection(mProgramPoint);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER,
					AudioManager.FX_FOCUS_NAVIGATION_UP);
			return true;

		case KeyEvent.KEYCODE_VOLUME_UP:
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE,
					AudioManager.FX_FOCUS_NAVIGATION_UP);
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (mLeftLayout.getVisibility() == View.VISIBLE) {
				if (mClassPoint == 0) {
					mClassPoint = liveData.getNetMediaList().size() - 1;
				} else {
					mClassPoint--;
				}
				updateData();
			} else {
				swichLine(TURN_LEFT);
				//showLineDialog();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (mLeftLayout.getVisibility() == View.VISIBLE) {
				if (mClassPoint == liveData.getNetMediaList().size() - 1) {
					mClassPoint = 0;
				} else {
					mClassPoint++;
				}
				updateData();
			} else {
				swichLine(TRUN_RIGHT);
				//showLineDialog();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(playPreCode==0){
				if (mLeftLayout.getVisibility() == View.GONE
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					ChannelNext();
				}
			}else if(mLeftLayout.getVisibility() == View.GONE){
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if(playPreCode==0){
				if (mLeftLayout.getVisibility() == View.GONE
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					ChannelLast();
				}
			}else if(mLeftLayout.getVisibility() == View.GONE){
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
			if (epg_layout.getVisibility() == View.GONE) {
				epg_layout.setVisibility(View.VISIBLE);
				//getFocused();
				myHandler.removeMessages(LiveConstant.EPG_VIEW);
				myHandler.sendEmptyMessageDelayed(LiveConstant.EPG_VIEW, 10000);
			}
			if (mLeftLayout.getVisibility() == View.GONE) {
				mLeftLayout.setVisibility(View.VISIBLE);
				getFocused();
				myHandler.removeMessages(LiveConstant.HIDE_VIEW);
				myHandler.sendEmptyMessageDelayed(LiveConstant.HIDE_VIEW, 10000);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (epg_layout.getVisibility() == View.GONE) {
				epg_layout.setVisibility(View.VISIBLE);
				//getFocused();
				myHandler.removeMessages(LiveConstant.EPG_VIEW);
				myHandler.sendEmptyMessageDelayed(LiveConstant.EPG_VIEW, 10000);
			}
			if (mLeftLayout.getVisibility() == View.GONE) {
				mLeftLayout.setVisibility(View.VISIBLE);
				getFocused();
				myHandler.removeMessages(LiveConstant.HIDE_VIEW);
				myHandler.sendEmptyMessageDelayed(LiveConstant.HIDE_VIEW, 10000);
			}
			break;
		case KeyEvent.KEYCODE_MENU:
			showMenu();
//			if (mLeftLayout.getVisibility() == View.GONE) {
//				mLeftLayout.setVisibility(View.VISIBLE);
//				getFocused();
//				myHandler.removeMessages(LiveConstant.HIDE_VIEW);
//				myHandler.sendEmptyMessageDelayed(LiveConstant.HIDE_VIEW, 10000);
//			}else{
//				mLeftLayout.setVisibility(View.GONE);
//				myHandler.removeMessages(LiveConstant.HIDE_VIEW);
//			}
			break;
		case KeyEvent.KEYCODE_BACK:
			onBackPressed();
			return true;
		case KeyEvent.KEYCODE_0:
		case KeyEvent.KEYCODE_1:
		case KeyEvent.KEYCODE_2:
		case KeyEvent.KEYCODE_3:
		case KeyEvent.KEYCODE_4:
		case KeyEvent.KEYCODE_5:
		case KeyEvent.KEYCODE_6:
		case KeyEvent.KEYCODE_7:
		case KeyEvent.KEYCODE_8:
		case KeyEvent.KEYCODE_9:
			initMessage(event.getKeyCode() - KeyEvent.KEYCODE_0);
			// selectPtopChanne(event.getKeyCode() - KeyEvent.KEYCODE_0);
			break;
//		case KeyEvent.KEYCODE_MENU:
//			initMessage(42);
//			break;
		default:
			// getFocused();
			break;

		}
		return false;
	}

	private void ChannelLast(){
		if (mProgramPoint == 0) {
			mProgramPoint = liveData.getNetMediaList()
					.get(mClassPoint).getChannlesArrayList()
					.size() - 1;
		} else {
			mProgramPoint--;
		}
		mCurrentMedia = liveData.getNetMediaList()
				.get(mClassPoint).getChannlesArrayList()
				.get(mProgramPoint);
		mSourcePoint = 0;
		myHandler.sendEmptyMessage(LiveConstant.REPLAY);
		//showLineDialog();
	}
	
	private void ChannelNext(){
		if (mProgramPoint == liveData.getNetMediaList()
				.get(mClassPoint).getChannlesArrayList().size() - 1) {
			mProgramPoint = 0;
		} else {
			mProgramPoint++;
		}
		mCurrentMedia = liveData.getNetMediaList()
				.get(mClassPoint).getChannlesArrayList()
				.get(mProgramPoint);
		mSourcePoint = 0;
		myHandler.sendEmptyMessage(LiveConstant.REPLAY);
		//showLineDialog();
	
	}
	
	/**
	 * 视频源toast
	 * 
	 * @param max
	 * @param current
	 */
	private void showLineToast() {
		myHandler.sendEmptyMessage(PROGRESSBAR_GONE);
		if(System.currentTimeMillis()-lastTime>300){
			playUrl(mSourcePoint);
			myHandler.sendEmptyMessage(PROGRESSBAR_GONE);
		}
		startSpeed();
		lastTime = System.currentTimeMillis();
		tv_line.setText(mCurrentMedia.getChannlename() + " "
				+ (mSourcePoint + 1) + "/"
				+ (mCurrentMedia.getChannlesArrayList().size())+"源");
		tv_line.setVisibility(View.VISIBLE);
		//ll_live_toast.setVisibility(View.VISIBLE);
		myHandler.removeMessages(LiveConstant.SWICH_LINE);
		Logger.d(TAG, "keyChanne="+keyChanne);
		mProgramNum.setText(keyChanne);
		myHandler.sendEmptyMessageDelayed(LiveConstant.SWICH_LINE, DELAY_TIME);
		epg_layout.setVisibility(View.VISIBLE);
	}
	

	private void closeLineDialog() {
		tv_line.setVisibility(View.GONE);
		endSpeed();
		myHandler.removeMessages(LiveConstant.SWICH_LINE);
		if (mProgramNum != null)
			mProgramNum.setText("");
		if (epg_layout != null)
			epg_layout.setVisibility(View.GONE);
	}

	private void swichLine(int flag) {
		switch (flag) {
		case TURN_LEFT:
			if (mSourcePoint == 0) {
				mSourcePoint = mCurrentMedia.getChannlesArrayList().size() - 1;
			} else {
				mSourcePoint--;
			}
			break;
		case TRUN_RIGHT:
			if (mSourcePoint == mCurrentMedia.getChannlesArrayList().size() - 1) {
				mSourcePoint = 0;
			} else {
				mSourcePoint++;
			}
			break;
		}
		myHandler.sendEmptyMessage(LiveConstant.REPLAY);

	}

	private void showProgressDialog(int msgId) {
		if (Loadingdialog != null && Loadingdialog.isShowing()) {
			Loadingdialog.dismiss();
		}
		Loadingdialog = new LiveLoadingDialog(this);
		Loadingdialog.setLoadingMsg(msgId);
		Loadingdialog.setCanceledOnTouchOutside(false);
		Loadingdialog.show();
	}

	private void recycle() {
		closeDialog();
		closeLineDialog();
		this.finish();
		System.exit(0);
	}

	private void closeDialog() {

		if (Loadingdialog != null && Loadingdialog.isShowing()) {
			Loadingdialog.dismiss();
		}

	}

	class LoadXMLThread extends Thread {
		public String url;
		private int msg = 0;
		private String file = "";

		public LoadXMLThread(String link, String file, int msg) {
			url = link;
			this.file = file;
			this.msg = msg;
		}

		public void run() {
			DownLoadTools downLoadTools = new DownLoadTools(myHandler);
			downLoadTools.getNetXml(TVLivePlayer.this.getFilesDir()
					+ File.separator + file, url,
					TVLivePlayer.this.getApplicationContext(), msg);
		}
	}
	
	
	
	
	class MyThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			
		}
	}
	
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LiveConstant.PLUGS_END:
				initIntent();
				break;
			case LiveConstant.PLUGS_START:
				Utils.showToast(TVLivePlayer.this, "正在为您更新直播插件！", R.drawable.toast_smile);
				break;
			case LiveConstant.APPLICATION_EXCEPTION:
				showDialog(R.string.load_err);
				break;
			case LiveConstant.DOWNLOAD_XML_DONE:
				initUI();
				break;
			case LiveConstant.PLAYER_VIDEO:

				break;
			case LiveConstant.SWICH_LINE:
				swichLine(TRUN_RIGHT);
				myHandler.sendEmptyMessageDelayed(LiveConstant.SWICH_LINE,
						DELAY_TIME);
				break;
			case LiveConstant.HIDE_VIEW:
				mLeftLayout.setVisibility(View.GONE);
				break;
			case LiveConstant.EPG_VIEW:
				epg_layout.setVisibility(View.GONE);
				break;

			case LiveConstant.SELECT_CHANNE:
				// 选台功能
				if (!keyChanne.equals("")) {
					int position = Integer.parseInt(keyChanne);
					initSelectChannle(position);
				}
				keyChanne = "";
				mProgramNum.setText(keyChanne+"频道");
				Log.d(TAG, "keyChanne="+keyChanne);
				break;
			case LiveConstant.DISAPPEAR:
				mProgramNum.setText("");
				break;
			case LiveConstant.DOWNLOAD_IMG_XML:
				break;
			case LiveConstant.DOWNLOAD_NEWS_XML:
				mEnterList = new PtopMainInterfaceParse()
						.parseFileXml(new File(getFilesDir() + File.separator
								+ NEWS_FILE_NAME));
				if (mEnterList != null) {
					for (int i = 0; i < mEnterList.getMainItems().size(); i++) {
						if (mEnterList.getMainItems().get(i).getList_name()
								.equals("timeout")) {
							if (mEnterList.getMainItems().get(i).getList_name()
									.equals("timeout")) {
								mTimeOut = Long.parseLong(mEnterList
										.getMainItems().get(i).getList_src());
							}
						} else {
							newsString = newsString
									+ mEnterList.getMainItems().get(i)
											.getList_name()
									+ ":"
									+ mEnterList.getMainItems().get(i)
											.getList_src();
						}
					}
				}
				myHandler.sendEmptyMessage(LiveConstant.START_NEWS);
				break;
			case LiveConstant.DOWNLOAD_ENTER_XML:
				mEnterList = new PtopMainInterfaceParse()
						.parseFileXml(new File(getFilesDir() + File.separator
								+ DATA_FILE_NAME));
				if(TVTYPE.equals(Constant.TVLIVE_DIY)){
					//new LoadXMLThread("http://wephd.live.cctv1949.com/api_new/wephd.xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
					//new LoadXMLThread("http://www.smtvzm.com/xmlfile/joy.xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
					new LoadXMLThread("http://diy.vbohd.com/user_list_xml.php?user_id=10086", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
				}else{
					//new LoadXMLThread(mEnterList.getMainItems().get(2).getList_src(), DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
					if(tvlive_server==1){
						new LoadXMLThread("http://wephd.live.cctv1949.com/api_new/wephd.xml", DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
					}else{
						new LoadXMLThread(mEnterList.getMainItems().get(2).getList_src(), DATA_FILE_NAME,LiveConstant.DOWNLOAD_XML_DONE).start();
					}
				}
				new LoadXMLThread(mEnterList.getMainItems().get(5)
						.getList_src(), NEWS_FILE_NAME,
						LiveConstant.DOWNLOAD_NEWS_XML).start();
				
//				new LoadXMLThread(mEnterList.getMainItems().get(7)
//						.getList_src(), PTOP_FILE_NAME,
//						Constant.DOWNLOAD_PTOP_XML).start();
				break;
			case LiveConstant.START_NEWS:
				if (mTimeOut > 0) {
					mProgramNews.setText(newsString);
					Log.d("mProgramNews", "mProgramNews="+mProgramNews);
					//mProgramNews.init(getWindowManager());
					//mProgramNews.startScroll();
					myHandler.sendEmptyMessageDelayed(LiveConstant.PAUSE_NEWS,
							40000);
				}
				break;
			case LiveConstant.PAUSE_NEWS:
				Log.d("mProgramNews", "mProgramNews="+mProgramNews);
				//mProgramNews.setText("");
				//mProgramNews.stopScroll();
				myHandler.sendEmptyMessageDelayed(LiveConstant.START_NEWS, mTimeOut
						* LiveConstant.MINU_NUTE);
				break;
			case LiveConstant.PLAY_NEXT:
				if (mProgramPoint == liveData.getNetMediaList()
						.get(mClassPoint).getChannlesArrayList().size() - 1) {
					mProgramPoint = 0;
				} else {
					mProgramPoint++;
				}
				mCurrentMedia = liveData.getNetMediaList().get(mClassPoint)
						.getChannlesArrayList().get(mProgramPoint);
				mSourcePoint = 0;
				myHandler.sendEmptyMessage(LiveConstant.REPLAY);
				Logger.i(TAG, "LiveConstant.PLAY_NEXT...不能播放");
				Utils.showToast(TVLivePlayer.this, R.string.play_next, R.drawable.toast_err);
				break;
			case PROGRESSBAR_VISIBLE:
				rl_progressBar.setVisibility(View.VISIBLE);
				break;
			case PROGRESSBAR_GONE:
				rl_progressBar.setVisibility(View.GONE);
				break;
			case CLOSE_LINE_DIALOG:
				closeLineDialog();
				break;
			case LiveConstant.REPLAY:
				showLineToast();
				break;
			}
			super.handleMessage(msg);
		}
	};


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mProgramPoint = arg2;
		mCurrentMedia = liveData.getNetMediaList().get(mClassPoint)
				.getChannlesArrayList().get(mProgramPoint);
		mSourcePoint = 0;
		myHandler.sendEmptyMessage(LiveConstant.REPLAY);
		Map<String, String> m_value = new HashMap<String, String>();
		m_value.put("TVLiveName", mCurrentMedia.getChannlename());
		MobclickAgent.onEvent(TVLivePlayer.this, "TVLive", m_value);
		//showLineDialog();
	}

	private void playUrl(final int point) {
		Logger.i(TAG, "epg===="+mCurrentMedia.getEpg());
		startTask(mCurrentMedia.getEpg());
		if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
			mVV.stopPlayback();
			//isNext = false;
		}
//		Runnable runnable = new Runnable() {
//			@Override
//			public void run() {
				// TODO Auto-generated method stub
				//判断当前流媒体类型
				if(null == mCurrentMedia){
					return;
				}
				String currentMedia = mCurrentMedia.getChannlesArrayList().get(point).getLink();
				if(null==currentMedia || "".equals(currentMedia)){
					return;
				}
				if(TVTYPE.equals(Constant.TVLIVE_DIY)){
					mVideoSource = currentMedia;
				}else{
					mVideoSource = iplay.returnPlayUrl(currentMedia);
				}
				Logger.i(TAG, "mVideoSource="+mVideoSource+"---port="+port);
				mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
				if(mEventHandler.hasMessages(EVENT_PLAY))
					mEventHandler.removeMessages(EVENT_PLAY);
				mEventHandler.sendEmptyMessage(EVENT_PLAY);
				Logger.i(TAG, mVideoSource);
				if (!isSendNext) {
					Logger.i(TAG, "isSendNext..."+isSendNext);
					myHandler.sendEmptyMessageDelayed(LiveConstant.PLAY_NEXT,DELAY_NEXT_TIME);
					isSendNext = true;
				}
//			}
//		};
//		ThreadPoolManager.getInstance().addTask(runnable);	
	}

	private void updateData() {
		mAdapter = new ContentAdapter(liveData.getNetMediaList()
				.get(mClassPoint).getChannlesArrayList(), this);
		mProgranList.setAdapter(mAdapter);
		mProgramTitle.setText(liveData.getNetMediaList().get(mClassPoint)
				.getChannleClass());
		myHandler.removeMessages(LiveConstant.HIDE_VIEW);
		myHandler.sendEmptyMessageDelayed(LiveConstant.HIDE_VIEW, 10000);
	}

	private void putConfig(String key, int value) {
		SharedPreferences sp = this.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private int getConfig(String key, int defValue) {
		SharedPreferences sp = this.getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.leftview:
			if (mLeftLayout.getVisibility() == View.VISIBLE) {
				if (mClassPoint == 0) {
					mClassPoint = liveData.getNetMediaList().size() - 1;
				} else {
					mClassPoint--;
				}
				updateData();
			} else {
				swichLine(TURN_LEFT);
				//showLineDialog();
			}
			//swichLine(TURN_LEFT);
			break;
		case R.id.rightview:
			if (mLeftLayout.getVisibility() == View.VISIBLE) {
				if (mClassPoint == liveData.getNetMediaList().size() - 1) {
					mClassPoint = 0;
				} else {
					mClassPoint++;
				}
				updateData();
			} else {
				swichLine(TRUN_RIGHT);
				//showLineDialog();
			}
			//swichLine(TRUN_RIGHT);
			break;
		case R.id.downview://切换源
			swichLine(TRUN_RIGHT);
			break;

		default:
			break;
		}

	}

	private void initMessage(int what) {
		Message msg = new Message();
		msg.what = LiveConstant.SELECT_CHANNE;
		myHandler.removeMessages(LiveConstant.SELECT_CHANNE);
		keyChanne = keyChanne + what;
		mProgramNum.setText(keyChanne);
		Log.d(TAG, "initMessage...keyChanne="+keyChanne);
		myHandler.sendMessageDelayed(msg, 700);
	}

	private void initSelectChannle(int selectNum) {
		int totleNum = 0;
		int channleNum = 0;
		while (true) {
			if (selectNum >= liveData.getNetMediaList().get(totleNum)
					.getChannlesArrayList().size()) {
				selectNum = selectNum
						- liveData.getNetMediaList().get(totleNum)
								.getChannlesArrayList().size();
				totleNum++;
			} else {
				channleNum = selectNum;
				break;
			}
			if (totleNum >= liveData.getNetMediaList().size()) {
				return;
			}
		}
		mClassPoint = totleNum;
		if (channleNum != 0) {
			channleNum = channleNum - 1;
		} else {
			channleNum = 0;
		}
		if (mClassPoint >= liveData.getNetMediaList().size()) {
			return;
		}
		if (mProgramPoint >= liveData.getNetMediaList().get(mClassPoint)
				.getChannlesArrayList().size()) {
			return;
		}
		mProgramPoint = channleNum;
		mSourcePoint = 0;
		mCurrentMedia = liveData.getNetMediaList().get(mClassPoint)
				.getChannlesArrayList().get(mProgramPoint);
		updateData();
		mProgranList.setSelection(mProgramPoint);
		myHandler.sendEmptyMessage(LiveConstant.REPLAY);
		//showLineDialog();
	}

	public String getMacAddress() {
		String result = "";
		String Mac = "";
		result = callCmd("busybox ifconfig eth0", "HWaddr");

		if (result == null) {
			return "555666";
		}
		if (result.length() > 0 && result.contains("HWaddr") == true) {
			Mac = result.substring(result.indexOf("HWaddr") + 6,
					result.length() - 1);

			if (Mac.length() > 1) {
				Mac = Mac.replaceAll(" ", "");
			}

		}

		return Mac;
	}

	public static String getCPUSerial() {

		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			str = input.readLine();
			if (str != null) {
				str.length();

			} else {

			}

		} catch (IOException ex) {
		}
		return cpuAddress;
	}

	public String callCmd(String cmd, String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			while ((line = br.readLine()) != null
					&& line.contains(filter) == false) {
				// result += line;
			}

			result = line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		myHandler.removeMessages(LiveConstant.HIDE_VIEW);
		myHandler.sendEmptyMessageDelayed(LiveConstant.HIDE_VIEW, 10000);

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private class DownEpg extends AsyncTask<String, Integer, EPGS> {
		private String epgUrl = "";
		private final String Egp = LiveConstant.EPG;
		private final String EgpFormat = ".xml";

		public DownEpg(String epg) {
			tv_name.setText(mCurrentMedia.getChannlename());
			curren_tv_title.setText("");
			epg2.setText("");
			epg1.setText("");
			epgUrl = epg;
			epgUrl = Egp + epg + EgpFormat;

		}

		protected void onPostExecute(EPGS result) {
			epgPosition(result);
		}

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected EPGS doInBackground(String... params) {
			URL temUrl = null;
			InputStream inStream = null;
			HttpURLConnection conn = null;
			String epgString = "";
			byte data[] = new byte[1024];
			try {
				temUrl = new URL(epgUrl);
				conn = (HttpURLConnection) temUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(10 * 1000);
				inStream = conn.getInputStream();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = inStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				inStream.close();
				outStream.close();
				epgString = new String(outStream.toByteArray(), "UTF-8");
			} catch (ProtocolException e) {
				Log.e(TAG, "doInBackground" + e.toString());
				e.printStackTrace();
			} catch (MalformedURLException e) {
				Log.e(TAG, "doInBackground" + e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "doInBackground" + e.toString());
				e.printStackTrace();
			}
			EPGS mEpgs = null;
			try {
				JSONObject m = new JSONObject(epgString);
				Iterator i = m.keys();
				mEpgs = new EPGS();
				while (i.hasNext()) {
					String key = (String) i.next();
					String value = m.getString(key);
					EPG epg = new EPG();
					epg.setKeyTime(key);
					epg.setValueContent(value);
					mEpgs.getEpgs().add(epg);
				}
			} catch (JSONException e) {
				Log.e("weibo", "doInBackground" + e.toString());
				e.printStackTrace();
			}
			Logger.i(TAG, "return mEpgs="+mEpgs);
			return mEpgs;
		}

	}

	private void startTask(String epg) {
		if (mCurEpg.equals(epg)) {
			return;
		}
		if (mDownEpg != null) {
			mDownEpg.cancel(true);
		}
		mCurEpg = epg;
		mDownEpg = new DownEpg(epg);
		mDownEpg.execute("");

	}

	private void epgPosition(EPGS epgs) {
		if (epgs == null || epgs.getEpgs() == null
				|| epgs.getEpgs().size() <= 0) {
			return;
		}
		Collections.sort(epgs.getEpgs(), new SortTime());
		Date currentDate = new Date();
		int hours = currentDate.getHours();
		int minutes = currentDate.getMinutes();
		int keysValue = hours * 60 + minutes;
		Log.e("handan", "hours:" + hours + "  minutes:" + minutes
				+ "  keysValue:" + keysValue);
		int position = -1;
		if (epgs != null && epgs.getEpgs() != null && epgs.getEpgs().size() > 1) {
			for (int i = 0; i < epgs.getEpgs().size(); i++) {
				if (keysValue < Integer.valueOf(epgs.getEpgs().get(i)
						.getKeyTime())) {
					position = i;
					break;
				}
			}
		}

		if (position != -1) {
			if (position > 0) {
				String[] str = epgs.getEpgs().get(position - 1)
						.getValueContent().split("-");
				if (str.length == 2) {
					curren_tv_title.setText(str[1]);
					tv_name.setText(mCurrentMedia.getChannlename());
				} else {

				}

			}
			epg1.setText(epgs.getEpgs().get(position).getValueContent());
			position = position + 1;
			if (position >= epgs.getEpgs().size()) {
				epg2.setText(epgs.getEpgs().get(0).getValueContent());
			} else {
				epg2.setText(epgs.getEpgs().get(position).getValueContent());
			}
		}

	}

	private class SortTime implements Comparator<EPG> {

		@Override
		public int compare(EPG arg0, EPG arg1) {
			int time0 = Integer.valueOf(arg0.getKeyTime());
			int time1 = Integer.valueOf(arg1.getKeyTime());
			if (time0 > time1) {
				return 1;
			} else if (time0 < time1) {
				return -1;
			} else {
				return 0;
			}
		}

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		String playratio = sp.getString("play_ratio", "16:9");
		if("原始比例".equals(playratio)){
			hmblposition = 0;
		}else if("4:3".equals(playratio)){
			hmblposition = 1;
		}else if("16:9".equals(playratio)){
			hmblposition = 2;
		}else if("默认全屏".equals(playratio)){
			hmblposition = 3;
		}
		setDecode();
		getPlayPreferences();
		getdelay_time();
		getPlayServerPreferences();
		getScreenSize();
		loadViewLayout();
		findViewById();
		setListener();
		setvvListener();
		TVLiveUtils.selectScales(TVLivePlayer.this,mVV,hmblposition,mWidth,mHeight);
		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread",android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
	}
	
	/**
	 * 初始化menu
	 */
	public void onCreateMenu() {
		View menuView = View.inflate(this, R.layout.mv_controler_menu, null);
		menulist = (ListView) menuView.findViewById(R.id.media_controler_menu);
		menupopupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		menupopupWindow.setOutsideTouchable(true);// 允许在外点击popu消失
//		// menupopupWindow.setAnimationStyle(R.style.AnimationMenu);
		menupopupWindow.setTouchable(true);
		menupopupWindow.setFocusable(true);
		menulist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isMenuShow) {
					isMenuShow = false;
					isMenuItemShow = true;
					switch (position) {
					//视频解码
					case 0:
						tvmenutype = 0;
						menulist.setAdapter(new  TVLiveMenuAdapter(TVLivePlayer.this, TVLiveUtils.getData(1), 0,isMenuItemShow));
						break;
					//显示比例
					case 1:
						tvmenutype = 1;
						menulist.setAdapter(new TVLiveMenuAdapter(TVLivePlayer.this, TVLiveUtils.getData(2), 1,isMenuItemShow));
						break;
					//偏好设置
					case 2:
						tvmenutype = 2;
						menulist.setAdapter(new TVLiveMenuAdapter(TVLivePlayer.this, TVLiveUtils.getData(3), 2,isMenuItemShow));
						break;
					case 3:
					//切换源超时
						tvmenutype = 3;
						menulist.setAdapter(new TVLiveMenuAdapter(TVLivePlayer.this, TVLiveUtils.getData(4), 3,isMenuItemShow));
						break;
					}
				}else if(isMenuItemShow){
					Editor editor = null;
					switch (tvmenutype) {
					//解码
					case 0:
						jmposition = position;
						if(position==0){
							editor = sp.edit();
							editor.putInt("mIsHwDecode", BVideoView.DECODE_SW);
							editor.putString("play_decode", "软解码");
							editor.commit();
						}else if(position==1){
							editor = sp.edit();
							editor.putInt("mIsHwDecode", BVideoView.DECODE_HW);
							editor.putString("play_decode", "硬解码");
							editor.commit();
						}
						setDecode();
						mVV.setDecodeMode(mIsHwDecode?BVideoView.DECODE_HW:BVideoView.DECODE_SW);//设置解码模式
//						if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
//							mVV.stopPlayback();
//						}
						hideMenu();
						myHandler.sendEmptyMessage(LiveConstant.REPLAY);
						//showLineToast();
						break;
					//显示比例
					case 1:
						hmblposition = position;
						//TVLiveUtils.selectScales(mVV,hmblposition);
						TVLiveUtils.selectScales(TVLivePlayer.this,mVV,hmblposition,mWidth,mHeight);						editor = sp.edit();
						if(position==0){
							editor.putString("play_ratio", "原始比例");
							editor.commit();
						}else if(position==1){
							editor.putString("play_ratio", "4:3");
							editor.commit();
						}else if(position==2){
							editor.putString("play_ratio", "16:9");
							editor.commit();
						}else if(position==3){
							editor.putString("play_ratio", "默认全屏");
							editor.commit();
						}
						hideMenu();
						break;
					case 2:
						//偏好设置
						phszposition = position;
						editor = sp.edit();
						if(position==0){
							editor.putInt("playPre", 0);
							editor.commit();
						}else if(position==1){
							editor.putInt("playPre", 1);
							editor.commit();
						}
						getPlayPreferences();
						hideMenu();
						break;
					case 3:
						//超时设置
						qycsposition = position;
						editor = sp.edit();
						if(position==0){
							editor.putInt("delay_time", 5000);
							editor.commit();
						}else if(position==1){
							editor.putInt("delay_time", 8000);
							editor.commit();
						}else if(position==2){
							editor.putInt("delay_time", 10000);
							editor.commit();
						}else if(position==3){
							editor.putInt("delay_time", 12000);
							editor.commit();
						}else if(position==4){
							editor.putInt("delay_time", 15000);
							editor.commit();
						}
						getdelay_time();
						hideMenu();
						break;
					}
				}
			}
		});
		menulist.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_UP) {
					return false;
				}
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					if (isMenuItemShow) {
						isMenuShow = true;
						isMenuItemShow = false;
						menulist.setAdapter(new TVLiveMenuAdapter(TVLivePlayer.this, TVLiveUtils.getData(0),5,isMenuItemShow));
					} else if (isMenuShow) {
						menupopupWindow.dismiss();
					}
					break;
				}
				return false;
			}
		});
	}
	
	// 打开menu
	private void showMenu() {
		epg_layout.setVisibility(View.GONE);
		mLeftLayout.setVisibility(View.GONE);
		if(null!=menupopupWindow){
			tvliveMenuAdapter = new TVLiveMenuAdapter(TVLivePlayer.this, TVLiveUtils.getData(0), 5,isMenuItemShow);
			menulist.setAdapter(tvliveMenuAdapter);
			menupopupWindow.setAnimationStyle(R.style.AnimationMenu);
			menupopupWindow.showAtLocation(mVV, Gravity.TOP | Gravity.RIGHT, 0, 0);
			menupopupWindow.update(0, 0, getResources().getDimensionPixelSize(R.dimen.sm_350),screenHeight);	
			isMenuShow = true;
			isMenuItemShow = false;
		}else{
			Utils.showToast(TVLivePlayer.this, "菜单加载未完成", R.drawable.toast_shut);
		}
	}

	// 隐藏menu
	private void hideMenu() {
		if (null!=menupopupWindow && menupopupWindow.isShowing()) {
			menupopupWindow.dismiss();
		}
	}
	
	/**
	 * 获取屏幕当前宽度
	 */
	@SuppressWarnings("deprecation")
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		//controlHeight = screenHeight / 4;
	}
	
	/**
	 * 设置编解码
	 */
	private void setDecode(){
		int Decode = sp.getInt("mIsHwDecode",BVideoView.DECODE_HW);
		if(Decode==BVideoView.DECODE_SW){
			mIsHwDecode = false;
			jmposition = 0;
		}else{
			mIsHwDecode = true;
			jmposition = 1;
		}
	}
	
	private void getPlayPreferences(){
		playPreCode = sp.getInt("playPre", 0);
		if(playPreCode==0){
			//换台
			phszposition = 0;
		}else{
			//调节音量
			phszposition = 1;
		}
	}
	
	private void getdelay_time(){
		DELAY_TIME = sp.getInt("delay_time", 8000);
		switch (DELAY_TIME) {
		case 5000:
			qycsposition = 0;
			break;
		case 8000:
			qycsposition = 1;
			break;
		case 10000:
			qycsposition = 2;
			break;
		case 12000:
			qycsposition = 3;
			break;
		case 15000:
			qycsposition = 4;
			break;
		default:
			qycsposition = 0;
			break;
		}
	}
	
	private void getPlayServerPreferences(){
		tvlive_server = sp.getInt("open_tvlive", 0);
		Logger.d(TAG, "当前服务器："+tvlive_server);
	}
	
	private void startSpeed(){
		mSpeedHandler.removeCallbacks(speedRunnable);
		lastRxByte = TrafficStats.getTotalRxBytes();
		lastSpeedTime = System.currentTimeMillis();
		mSpeedHandler.postDelayed(speedRunnable, 0);
		tv_speed.setVisibility(View.VISIBLE);
	}
	
	private void endSpeed(){
		mSpeedHandler.removeCallbacks(speedRunnable);
		tv_speed.setVisibility(View.GONE);
	}
	
	
	/**
	 * 用户注销登录
	 */
	private void showLogoutDialog() {
		WiFiDialog.Builder builder = new WiFiDialog.Builder(context);
		View mView = View.inflate(context, R.layout.logout_dialog, null);
		TextView tv_logout_msg = (TextView) mView.findViewById(R.id.tv_logout_msg);
		tv_logout_msg.setText("亲！请在个人中心进行注册登录。电脑登录www.smtvzm.com可添加观看您自定义的节目。当然！现在无需登录就可观看体验神马小组自定义的节目啦！");
		builder.setContentView(mView);
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//注销登录
//				sp.edit().putString("userName", null).putString("passWord", null).commit();
//				tv_no_data.setText("您还没有登录哦~");
//				tv_user_name.setText(R.string.no_login);
				dialog.dismiss();
				uName = "sm0001";
				initData();
			}
		});
		builder.setNeutralButton("退出", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		mDialog = builder.create();
		mDialog.show();
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);
	}
	
	
	private void acquireWakeLock() {
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			//PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,this.getClass().getCanonicalName());
			mWakeLock.acquire();
		}
	}


	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	
	}
	
	private static final int PROGRESSBAR_VISIBLE = 0x000000001;
	private static final int PROGRESSBAR_GONE = 0x000000002;
	/**
	 * @brief 请求出错。
	 */
	//private final static int ERROR = 0x000000003;
	/**
	 * @brief 播放。
	 */
	private final static int EVENT_PLAY = 0x000000004;
	
	private final static int CLOSE_LINE_DIALOG = 0x000000003;
	
	private final static int START_SPEED = 0x000000005;
	private RelativeLayout rl_progressBar;
	private String TVTYPE = Constant.TVLIVE;
	private String mVideoSource = null;//视频源
	private boolean mIsHwDecode = false;//设置解码模式
	private boolean isNext = false;
	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;
	private ListView menulist;
	private PopupWindow menupopupWindow;
	private TVLiveMenuAdapter tvliveMenuAdapter;
	private static int tvmenutype;
	private boolean isMenuShow = false;
	private boolean isMenuItemShow = false;
	public static int phszposition = 0;
	public static int jmposition = 0;
	public static int hmblposition = 0;
	public static int qycsposition = 0;
	private int screenWidth;
	private int screenHeight;
	private int playPreCode;
	private int tvlive_server = 0;
	private long lastTime = 0;
	
	private float Lightness;
	private float mTouchY,mTouchX;
	private int mSurfaceYDisplayRange;
	private int currentVolume;
	private int maxVolume;
	// Touch Events
	private static final int TOUCH_NONE = 0;
	private static final int TOUCH_VOLUME = 1;
	private static final int TOUCH_BRIGHTNESS = 2;
	private static final int TOUCH_SEEK = 3;
	private int mTouchAction;
	
	private long lastRxByte;
	private long lastSpeedTime;
	private long speed;
	private static String rxByte;
	private Runnable speedRunnable;
	private Handler mSpeedHandler;
	
	public RequestQueue mQueue;
	private String uName;
	private Dialog mDialog;
}
