package com.shenma.tvlauncher.vod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.network.NetUtil;
import com.shenma.tvlauncher.network.PullXmlParserCallback;
import com.shenma.tvlauncher.network.PullXmlParserThread;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.utils.VideoPlayUtils;
import com.shenma.tvlauncher.view.HomeDialog;
import com.shenma.tvlauncher.view.PlayerProgressBar;
import com.shenma.tvlauncher.vod.adapter.VodMenuAdapter;
import com.shenma.tvlauncher.vod.dao.VodDao;
import com.shenma.tvlauncher.vod.db.Album;
import com.shenma.tvlauncher.vod.domain.MediaInfo;
import com.shenma.tvlauncher.vod.domain.VideoInfo;
import com.shenma.tvlauncher.vod.domain.VodUrl;
import com.umeng.analytics.MobclickAgent;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
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
import android.view.ViewGroup;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class VideoPlayerActivity extends Activity implements OnClickListener {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mv_videoplayer);
		dao = new VodDao(this);
		Utils.stopAutoBrightness(this);
		initView();
		initData();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		/**
		 * 退出后台事件处理线程
		 */
		isDestroy = true;
		Utils.startAutoBrightness(this);
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		//android.os.Process.killProcess(android.os.Process.myPid());
		Logger.d(TAG, "onDestroy");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		isDestroy = true;
		mHandler.removeMessages(WindowMessageID.SWITCH_CODE);
		mHandler.removeMessages(WindowMessageID.HIDE_CONTROLER);
		mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
		xjposition = 0;
		mHandlerThread.quit();
		//startActivity(new Intent(VideoPlayerActivity.this, VodDetailsActivity.class));
		Logger.d(TAG, "onStop");

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
		Map<String, String> m_value = new HashMap<String, String>();
		m_value.put("vodname", vodname);
		m_value.put("domain", domain);
		m_value.put("vodtype", vodtype);
		MobclickAgent.onEvent(VideoPlayerActivity.this, "VOD_PLAY", m_value);
		isDestroy = false;
		Logger.d(TAG, "onResume...mPlayerStatus="+mPlayerStatus);
		acquireWakeLock();
		if(!mHandlerThread.isAlive()){
			/**
			 * 开启后台事件处理线程
			 */
			mHandlerThread = new HandlerThread("event handler thread",
					Process.THREAD_PRIORITY_BACKGROUND);
			mHandlerThread.start();
			mEventHandler = new EventHandler(mHandlerThread.getLooper());
		}
		/**
		 * 发起一次播放任务,当然您不一定要在这发起
		 */
		if(null!=mVideoSource && !"".equals(mVideoSource) && mPlayerStatus==PLAYER_STATUS.PLAYER_IDLE){
			mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);	
			Logger.d(TAG, "onResume... 发起播放");
		}
		mSpeedHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case WindowMessageID.START_SPEED:
					tv_mv_speed.setText(rxByte);
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
					if(rxbyte!=0 && time!=0){
						 speed = rxbyte / time * 1000L/ 1024L;
						 if(speed>=1024){
							 rxByte = String.valueOf(speed/1024L)+"MB/S";
						 }else{
							 rxByte = String.valueOf(speed)+"KB/S";
						 }
						 mSpeedHandler.sendEmptyMessage(WindowMessageID.START_SPEED);
					}
					lastRxByte = nowRxbyte;
					lastSpeedTime = nowtime;
				}
				mSpeedHandler.postDelayed(speedRunnable, 500); 
			}
		};
		mProgressBar.setProgress(0);
		mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isDestroy = true;
		//Utils.loadingClose_Tv();
		Logger.d(TAG, "onPause...mPlayerStatus="+mPlayerStatus);
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
		releaseWakeLock();
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
		}
		hideController();
		mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
		
	}
	
	/**
	 * 准备播放数据
	 */
	private void PrepareVodData(int postion) {
		isSwitch = true;
		if(mLastPos!=0){
			//Utils.loadingShow_tv(VideoPlayerActivity.this,R.string.str_collection_loading);
		}else{
			//Utils.loadingShow_tv(VideoPlayerActivity.this,R.string.str_data_loading);
		}
		if (null != videoInfo && videoInfo.size() > 0 && videoInfo.size()>=playIndex) {
			if (vodtype.equals("MOVIE")&&videoInfo.size()==1) {
				tv_mv_name.setText(vodname);
			} else {
				tv_mv_name.setText(videoInfo.get(playIndex).title);
			}
			url = videoInfo.get(postion).url;
			final String vodUrl = Constant.URL_HEAD + "domain=" + domain + "&url="
					+ url;
			Logger.v(TAG, "vodUrl==" + vodUrl);
			loadMediaFromXml(vodUrl);
		}

	}

	/**
	 * 初始化数据
	 */

	private void initData() {
		Intent intent = getIntent();
		videoInfo = new ArrayList<VideoInfo>();
		videoInfo = intent.getParcelableArrayListExtra("videoinfo");
		albumPic = intent.getStringExtra("albumPic");
		vodtype = intent.getStringExtra("vodtype");
		videoId = intent.getStringExtra("videoId");
		vodname = intent.getStringExtra("vodname");
		domain = intent.getStringExtra("domain");
		nextlink = intent.getStringExtra("nextlink");
		vodstate = intent.getStringExtra("vodstate");
		sourceId = intent.getStringExtra("sourceId");
		playIndex = intent.getIntExtra("playIndex",0);
		mLastPos = intent.getIntExtra("collectionTime",0);
		xjposition = playIndex;
		// 根据id查询数据库获取上次播放的时间和剧集数
		if (vodtype.equals("MOVIE")&&videoInfo.size()==1) {
			tv_mv_name.setText(vodname);
		} else {
			tv_mv_name.setText(videoInfo.get(playIndex).title);
		}
		if(domain.contains("qiyi") || domain.contains("pps")){
			isaiqiyi = true;
		}
		PrepareVodData(playIndex);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		sp = getSharedPreferences("shenma", MODE_PRIVATE);
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
		getScreenSize();
		loadViewLayout();
		findViewById();
		setListener();
		setvvListener();
		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());
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
	/**
	 * 初始化View
	 */
	private void findViewById() {
		seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		tv_currentTime = (TextView) controlView
				.findViewById(R.id.tv_currentTime);
		tv_totalTime = (TextView) controlView.findViewById(R.id.tv_totalTime);
		tv_menu  = (TextView) controlView.findViewById(R.id.tv_menu);
		ib_playStatus = (ImageButton) controlView
				.findViewById(R.id.ib_playStatus);
		ib_playStatus.setOnClickListener(this);
		mProgressBar = (PlayerProgressBar) findViewById(R.id.progressBar);
		tv_progress_time = (TextView) findViewById(R.id.tv_progress_time);
		tv_mv_speed = (TextView) findViewById(R.id.tv_mv_speed);
		tv_time = (TextView)time_controlView.findViewById(R.id.tv_time);
		tv_mv_name = (TextView)time_controlView.findViewById(R.id.tv_mv_name);
		mProgressBar.setVisibility(View.GONE);
		BVideoView.setAKSK(Constant.AK, Constant.SK);//设置ak及sk的前16位
		mVV = (BVideoView) findViewById(R.id.b_video_view);//获取BVideoView对象		
		mVV.setDecodeMode(mIsHwDecode?BVideoView.DECODE_HW:BVideoView.DECODE_SW);//设置解码模式
	}
	private void loadViewLayout() {
		controlView = getLayoutInflater().inflate(R.layout.mv_media_controler,
				null);
		controler = new PopupWindow(controlView);
		time_controlView = getLayoutInflater().inflate(R.layout.mv_media_time_controler,
				null);
		time_controler = new PopupWindow(time_controlView);
	}
	
	
	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WindowMessageID.EVENT_PLAY:
				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
							Logger.i(TAG, "SYNC_Playing.wait()");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				/**
				 * 设置播放url
				 */
				Logger.d(TAG, "mVideoSource="+mVideoSource);
				mVV.setVideoPath(mVideoSource);
				/**
				 * 续播，如果需要如此
				 */
				if (mLastPos > 0) {
					mVV.seekTo(mLastPos);
					Logger.i(TAG, "seekTo======"+mLastPos);
					mLastPos = 0;
				}
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
	private void setListener(){
		tv_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hideController();
				showMenu();
			}
		});
		//手势监听
		mGestureDetector = new GestureDetector(new SimpleOnGestureListener(){



			@Override
			public boolean onDoubleTap(MotionEvent e) {
				//双击
//				hideController();
//				showMenu();
				return true;
			}
			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				// TODO Auto-generated method stub
				if(!isControllerShow){
					showController();
					hideControllerDelay();
				}else {
					cancelDelayHide();
					hideController();
				}
				hideMenu();
				//return super.onSingleTapConfirmed(e);
				return true;
			}
			@Override
			public void onLongPress(MotionEvent e) {
				// TODO Auto-generated method stub
				//super.onLongPress(e);
				hideController();
				showMenu();
			}	
			
			@Override
			public boolean onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				Logger.i(TAG, "mGestureDetector...onDown");
				return super.onDown(e);
			}
		
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				// TODO Auto-generated method stub
				Logger.i(TAG, "mGestureDetector...onSingleTapUp");
				return super.onSingleTapUp(e);
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				Logger.i(TAG, "mGestureDetector...onFling");
				return false;
			}
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTapEvent(e);
		}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				return false;
			}
        });

		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				hideControllerDelay();
				int iseekPos = seekBar.getProgress();
				/**
				 * SeekBark完成seek时执行seekTo操作并更新界面
				 *
				 */
				mVV.seekTo(iseekPos);
				Log.v(TAG, "seek to " + iseekPos);
				mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				/**
				 * SeekBar开始seek时停止更新
				 */
				mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				//ib_rewind.setImageResource(R.drawable.media_rewind);
				//ib_fastForward.setImageResource(R.drawable.media_fastforward);
				updateTextViewWithTimeFormat(tv_currentTime, progress);
			}
		});
	}

	/**
	 * 设置vv监听
	 */
	private void setvvListener() {
		// 异常
		mVV.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(int what, int extra) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "what="+what);
				synchronized (SYNC_Playing) {
					SYNC_Playing.notify();
					Logger.i(TAG, "onError...SYNC_Playing.notify()");
				}
				mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;//更改状态
				mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
				mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);//移除时间更新
				isDestroy = false;
				if(!isDestroy){
					mHandler.sendEmptyMessage(WindowMessageID.HIDE_MENU);
					if(isSwitch){
						mHandler.sendEmptyMessage(WindowMessageID.SWITCH_CODE);
					}else{
						if(videoInfo.size()>playIndex+1){
							mHandler.sendEmptyMessage(WindowMessageID.PLAY_ERROR);	
						}else{
							mHandler.sendEmptyMessage(WindowMessageID.ERROR);
						}
					}
				}
				return true;
			}
		});
		
		// 准备OK
		mVV.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared() {
				// TODO Auto-generated method stub
				Log.v(TAG, "onPrepared");
				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
				mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
				mediaHandler.sendEmptyMessage(WindowMessageID.SELECT_SCALES);
				mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
//				if (!isControllerShow) {
//					showController();
//				}
				//Utils.loadingClose_Tv();
			}
		});
		//播放完成
		mVV.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion() {
				Logger.d(TAG, "onCompletion中Thread="+Thread.currentThread().getName());
				// TODO Auto-generated method stub
				synchronized (SYNC_Playing) {
					SYNC_Playing.notify();
					Logger.i(TAG, "onCompletion...SYNC_Playing.notify()");
				}
				mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
				mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				Logger.i(TAG, "isNext="+isNext+".....isLast="+isLast+"....isPause="+isPause+"....isDestroy="+isDestroy);
				if(isNext){
					if(videoInfo.size()>playIndex+1){
						playIndex += 1;
						xjposition = playIndex;
						collectionTime = 0;
						mLastPos = 0;
						mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
						mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
						mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
					}else{
						mVV.stopPlayback();
						finish();
					}
					isNext = false;
				}else if(isLast){
					mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
					//mProgressHandler.removeCallbacks(updateThread);
					if(playIndex>0){
						playIndex -= 1;
						collectionTime = 0;
						xjposition = playIndex;
						mLastPos = 0;
						mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
						mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
						mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
					}
					isLast = false;
				}else if(isPause){
					chooseSource(qxdposition);
					isPause = false;
				}else{
					if(!isDestroy){
						mHandler.sendEmptyMessage(WindowMessageID.HIDE_MENU);
						if(videoInfo.size()>playIndex+1){
							playIndex += 1;
							xjposition = playIndex;
							collectionTime = 0;
							mLastPos = 0;
							mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
							mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
							mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
						}else{
							mVV.stopPlayback();
							finish();
						}
					}
				}
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
					mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
					mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
					//Utils.loadingShow_tv(VideoPlayerActivity.this,R.string.str_data_loading);
					break;
				/**
				 * 结束缓冲
				 */
				case BVideoView.MEDIA_INFO_BUFFERING_END:
					//Utils.loadingClose_Tv();
					mediaHandler.sendEmptyMessage(WindowMessageID.COLSE_SHOW_TV);
					mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
					break;
				default:
					break;
				}
				return true;
			}
		});

		mVV.setOnPlayingBufferCacheListener(new OnPlayingBufferCacheListener() {
			
			@Override
			public void onPlayingBufferCache(int arg0) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "缓冲。。。="+arg0);
				mProgressBar.setProgress(arg0);
			}
		});
	}
	
	/**
	 * 获取屏幕当前宽度
	 */
	@SuppressWarnings("deprecation")
	private void getScreenSize() {
		Display display = getWindowManager().getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		controlHeight = screenHeight / 4;
	}

	//private Handler mProgressHandler = new Handler();

	// show出控制栏
	private void showController() {
		if(null!= mVV){
			tv_time.setText(Utils.getStringTime(":"));
			time_controler.setAnimationStyle(R.style.AnimationTimeFade);
			time_controler.showAtLocation(mVV, Gravity.TOP, 0, 0);
			controler.setAnimationStyle(R.style.AnimationFade);
			controler.showAtLocation(mVV, Gravity.BOTTOM, 0, 0);
			time_controler.update(0, 0, screenWidth, controlHeight/3);
			controler.update(0, 0, screenWidth, controlHeight/2);
			isControllerShow = true;
			mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_CONTROLER, TIME);
		}
	}

	// 隐藏控制栏
	private void hideController() {
		if (null!=controler && controler.isShowing()) {
			controler.dismiss();
			time_controler.dismiss();
			isControllerShow = false;
		}
	}

	private void cancelDelayHide() {
		mHandler.removeMessages(WindowMessageID.HIDE_CONTROLER);

	}

	private void hideControllerDelay() {
		cancelDelayHide();
		mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_CONTROLER, TIME);
	}

	// 打开menu
	private void showMenu() {
		if(null!=menupopupWindow){
			vmAdapter = new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(0), 5,isMenuItemShow);
			menulist.setAdapter(vmAdapter);
			menupopupWindow.setAnimationStyle(R.style.AnimationMenu);
			menupopupWindow.showAtLocation(mVV, Gravity.TOP | Gravity.RIGHT, 0, 0);
			menupopupWindow.update(0, 0, getResources().getDimensionPixelSize(R.dimen.sm_350),screenHeight);	
			isMenuShow = true;
			isMenuItemShow = false;
		}else{
			Utils.showToast(VideoPlayerActivity.this, "视频菜单加载未完成", R.drawable.toast_shut);
		}
	}

	// 隐藏menu
	private void hideMenu() {
		if (null!=menupopupWindow && menupopupWindow.isShowing()) {
			menupopupWindow.dismiss();
		}
	}
	
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
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
			case WindowMessageID.SWITCH_CODE:
				switchCode();
				isSwitch = false;
				break;
			case WindowMessageID.ERROR:
				Utils.showToast(VideoPlayerActivity.this, R.string.str_no_data_error, R.drawable.toast_err);
				//mVV.stopPlayback();
				finish();
				break;
			case WindowMessageID.PLAY_ERROR:
				showUpdateDialog("=_= 该源暂时无法播放了，请选择其它视频源吧！我们会努力解决的！", VideoPlayerActivity.this);
				//Utils.loadingClose_Tv();
				break;
			case WindowMessageID.HIDE_CONTROLER:
				hideController();
				break;
			case WindowMessageID.HIDE_MENU:
				hideMenu();
				break;
			case WindowMessageID.HIDE_PROGRESS_TIME:
				tv_progress_time.setVisibility(View.GONE);
				break;
			case WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION:
				int currPosition = mVV.getCurrentPosition();
				videoLength = mVV.getDuration();
//				Logger.d(TAG, "currPosition="+currPosition);
//				Logger.d(TAG, "mLastPos="+mLastPos);
//				Logger.d(TAG, "videoLength="+videoLength);
				updateTextViewWithTimeFormat(tv_currentTime, currPosition);
				updateTextViewWithTimeFormat(tv_totalTime, videoLength);
				seekBar.setMax(videoLength);				
				seekBar.setProgress(currPosition);
				mLastPos = currPosition;
				mHandler.sendEmptyMessageDelayed(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION, 200);
				break;
			}
		}
	}
	
	
	/**
	 * 显示升级提示的对话框
	 */
	private void showUpdateDialog(String str,Context conetxt) {
		HomeDialog.Builder builder = new HomeDialog.Builder(conetxt);
		builder.setTitle("温馨提示");
		builder.setMessage(str);
		builder.setPositiveButton("继续播放下一集", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Utils.showToast(VideoPlayerActivity.this, R.string.str_data_play_error, R.drawable.toast_err);
				playIndex += 1;
				xjposition = playIndex;
				collectionTime = 0;
				mLastPos = 0;
				mediaHandler.sendEmptyMessage(WindowMessageID.PREPARE_VOD_DATA);
				mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
				mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
				dialog.dismiss();
			}
		});
		builder.setNeutralButton("不想再看了", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		builder.create().show();
		
	}
	
	
	private void updateTextViewWithTimeFormat(TextView view, int second){
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
//		if (0 != hh) {
//			strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
//		} else {
//			strTemp = String.format("%02d:%02d", mm, ss);
//		}
		view.setText(strTemp);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (isBack) {
				//Utils.loadingShow_tv(VideoPlayerActivity.this,R.string.str_data_loading);
				hideControllerDelay();
				//ib_rewind.setImageResource(R.drawable.media_rewind);
				//ib_fastForward.setImageResource(R.drawable.media_fastforward);
				// pose = vv.getCurrentPosition();//时间误差加上的
				mVV.seekTo(mLastPos);
				mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				//mProgressHandler.post(updateThread);
				isBack = false;
			}
			return super.dispatchKeyEvent(event);
		}
		int keyCode = event.getKeyCode();
		long secondTime;
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			hideController();
			secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Utils.showToast(VideoPlayerActivity.this, "再按一次退出播放",
						R.drawable.toast_err);
				firstTime = secondTime;// 更新firstTime
				//finish();
				return true;
			} else {
				collectionTime = mVV.getCurrentPosition();
				if(collectionTime!=0){
					Album album = new Album();
					album.setAlbumId(videoId);
					album.setAlbumSourceType(sourceId);
					album.setCollectionTime(collectionTime);
					album.setPlayIndex(playIndex);
					album.setAlbumPic(albumPic);
					album.setAlbumType(vodtype);
					album.setAlbumTitle(vodname);
					album.setAlbumState(vodstate);
					album.setNextLink(nextlink);
					album.setTypeId(2);//记录
					dao.addAlbums(album);
					Logger.d("joychang", "退出时间点="+collectionTime+"...videoId=="+videoId);
					Logger.v(TAG, "存入数据库=+videoId"+videoId+"---sourceId="+getString(Integer.parseInt(sourceId))+"----collectionTime="+collectionTime);
				}
				isDestroy = true;
				finish();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (!isControllerShow) {
				showController();
			}
			//ib_rewind.setImageResource(R.drawable.media_rewind_selector);
			rewind();
			break;
			
			
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (!isControllerShow) {
				showController();
			}
			//ib_fastForward.setImageResource(R.drawable.media_fastforward_selector);
			fastForward();
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if(playPreCode==0){
				secondTime = System.currentTimeMillis();
				if (secondTime - firstTime > 2000) {
					Utils.showToast(VideoPlayerActivity.this, R.string.vod_onpressed_play_last,R.drawable.toast_smile);
					firstTime = secondTime;// 更新firstTime
					return true;
				} else if(playIndex>0){
					isLast = true;
					if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
						mVV.stopPlayback();
					}
					if (!isControllerShow)
						showController();
					//显示播放栏
					mediaHandler.sendEmptyMessage(WindowMessageID.RESET_MOVIE_TIME);
					//ResetMovieTime();
				}else{
					Utils.showToast(VideoPlayerActivity.this,R.string.vod_onpressed_play_frist,R.drawable.toast_shut);
	/*					vv.stopPlayback();
						finish();*/
				}
			}else{
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_RAISE,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if(playPreCode==0){
				secondTime = System.currentTimeMillis();
				if (secondTime - firstTime > 5000) {
					Utils.showToast(VideoPlayerActivity.this, R.string.vod_onpressed_play_next,
							R.drawable.toast_smile);
					firstTime = secondTime;// 更新firstTime
					return true;
				} else if(videoInfo.size()>playIndex+1){
					isNext = true;
					if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
						mVV.stopPlayback();
					}
//					//显示播放栏
					if (!isControllerShow)
						showController();
					mediaHandler.sendEmptyMessage(WindowMessageID.RESET_MOVIE_TIME);
					//ResetMovieTime();
				}else{
					Utils.showToast(VideoPlayerActivity.this, "已经是最后一集了！！！",R.drawable.toast_shut);
	/*					vv.stopPlayback();
					finish();*/
				}
			}else{
				mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
						AudioManager.ADJUST_LOWER,
						AudioManager.FX_FOCUS_NAVIGATION_UP);
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
			if (!isControllerShow)
				showController();
			if (mVV.isPlaying()) {
				mVV.pause();//暂停播放
				mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				ib_playStatus.setImageResource(R.drawable.media_playstatus);
			} else {
				mVV.resume();//继续播放
				mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				ib_playStatus.setImageResource(R.drawable.media_pause);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (!isControllerShow)
				showController();
			if (mVV.isPlaying()) {
				mVV.pause();//暂停播放
				mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				ib_playStatus.setImageResource(R.drawable.media_playstatus);
			} else {
				mVV.resume();//继续播放
				mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				ib_playStatus.setImageResource(R.drawable.media_pause);
			}
			break;
			// 菜单
		case KeyEvent.KEYCODE_MENU:
			hideController();
			showMenu();
			break;	
		}
		return super.dispatchKeyEvent(event);
	}
	
	/**
	 * 快进
	 */
	private void fastForward() {
		if (videoLength - mLastPos > 30) {
			mLastPos += 30;
		} else {
			//mLastPos = videoLength - 30;
			mLastPos = videoLength;
		}
		isBack = true;
		cancelDelayHide();
		mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
		seekBar.setProgress(mLastPos);
		//updateTextViewWithTimeFormat(tv_currentTime, mLastPos);
	}

	/**
	 * 快退
	 */

	private void rewind() {
		if (mLastPos > 30) {
			mLastPos -= 30;
		} else {
			mLastPos = 0;
		}
		isBack = true;
		cancelDelayHide();
		mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
		seekBar.setProgress(mLastPos);
		//updateTextViewWithTimeFormat(tv_currentTime, mLastPos);
	}

	/**
	 * 获取媒体数据
	 */
	private void loadMediaFromXml(final String url) {
		PullXmlParserThread parser = new PullXmlParserThread(0, isaiqiyi, new PullXmlParserCallback() {

			@Override
			public void startDocument() {
				medialist = new ArrayList<MediaInfo>();
			}

			@Override
			public void startFlag(String nodeName,
					Map<String, String> attribute) {
				if (nodeName.equals("data")) {
					mdata = 1;
				} else if (nodeName.equals("url")) {
					mediainfo = new MediaInfo();
					mediainfo.setMediaurl(attribute.get("link"));
					mediainfo.setName(attribute.get("name"));
				} else if (nodeName.equals("type")) {
					mtype = 1;
				}
			}

			@Override
			public void endFlag(String nodeName) {
				if (nodeName.equals("data")) {
					mdata = 0;
				} else if (nodeName.equals("url")) {
					murl = 0;
					medialist.add(mediainfo);
					//Logger.d(TAG, mediainfo.getName()+"="+mediainfo.getMediaurl());
					mediainfo = null;
				} else if (nodeName.equals("type")) {
					mtype = 0;
				}
			}

			@Override
			public void endDocument() {
				mediaHandler
						.sendEmptyMessage(WindowMessageID.DATA_PREPARE_OK);

			}

			@Override
			public void text(String text) {
				if ((mdata == 1) && (mtype == 1)) {
					sourcetype = text;
					Logger.v(TAG, "数据源类型==" + sourcetype);
				}

			}

			@Override
			public void haveError(
					com.shenma.tvlauncher.network.PullXmlParserError error) {
				if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
					mVV.stopPlayback();
				}
				VideoPlayerActivity.this.finish();
				//Utils.loadingClose_Tv();
				// TODO Auto-generated method stub

			}

			protected int mdata = -1;// 开始
			protected int murl = -1;// 媒体路径
			protected int mtype = -1;// 媒体类型

		}, url);
		parser.start();
	
	}

	private void chooseSource(final int index){
		Logger.v(TAG, "medialist==" + medialist.size());
		if (null != medialist && medialist.size() > 0) {
			if (sourcetype.equals("link")) {
				// 直接播放
				if(index<medialist.size()){
					mVideoSource = medialist.get(index).getMediaurl();
				}else{
					mVideoSource = medialist.get(0).getMediaurl();
				}
				Logger.d(TAG, "chooseSource中mVideoSource="+medialist.get(index).getMediaurl());
				if(null!=mVideoSource && !"".equals(mVideoSource)){
					mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
					/**
					 * 发起一次新的播放任务
					 */
					if(mEventHandler.hasMessages(WindowMessageID.EVENT_PLAY))
						mEventHandler.removeMessages(WindowMessageID.EVENT_PLAY);
					mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);
					Logger.v(TAG, "播放Url==="+medialist.get(index).getMediaurl());
				}else{
					mHandler.sendEmptyMessage(WindowMessageID.ERROR);
				}
			} else {
				new Thread() {
					public void run() {
						String vodUrl = medialist.get(index).getMediaurl();
						String Url = NetUtil.get(vodUrl);
						String baseUrl = null;
						Logger.v(TAG, "加密前路径=" + vodUrl);
						try {
							Logger.v(TAG, "加密数据=" + Url);
							baseUrl = Utils.encodeBase64String(Url);
							Logger.v(TAG, "加密后的路径=" + baseUrl);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						List<NameValuePair> pairList = new ArrayList<NameValuePair>();
						BasicNameValuePair basictype = new BasicNameValuePair(
								"type", sourcetype);
						BasicNameValuePair basiclink = new BasicNameValuePair(
								"link", vodUrl);
						BasicNameValuePair basicdata = new BasicNameValuePair(
								"data", baseUrl);
						pairList.add(basictype);
						pairList.add(basiclink);
						pairList.add(basicdata);
						String result = NetUtil.post(Constant.POST_PHP, pairList);
						Logger.v(TAG, "base64返回后的新路径==="
								+ result);
						if (null != result&&!"".equals(result)) {
							Message msg = new Message();
							VodUrl vodurl = new VodUrl();
							vodurl.setUrl(result);
							vodurl.setTitle(vodname);
							msg.obj = vodurl;
							msg.what = WindowMessageID.DATA_BASE64_PREPARE_OK;
							mediaHandler.sendMessage(msg);
						} else {
							mediaHandler.sendEmptyMessage(WindowMessageID.NET_FAILED);
						}
					};
				}.start();

			}
		}else{
			//没有播放源
			Utils.showToast(this, R.string.str_no_data_error, R.drawable.toast_smile);
			if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
				mVV.stopPlayback();
			}
			VideoPlayerActivity.this.finish();
		}
	}
	
	private Handler mediaHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WindowMessageID.DATA_PREPARE_OK:
				if(null!=medialist){
					if(qxdposition>=medialist.size()){
						qxdposition = 0;
					}
					chooseSource(qxdposition);
					onCreateMenu();
				}
				break;
			case WindowMessageID.NET_FAILED:
				//Utils.loadingClose_Tv();
//				Utils.showToast(VideoPlayerActivity.this, "对不起，视频源不存在。",
//						R.drawable.toast_err);
				Logger.d(TAG, "mPlayerStatus="+mPlayerStatus);
				if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
					mVV.stopPlayback();
				}
				VideoPlayerActivity.this.finish();
				//System.exit(0);
				break;
			case WindowMessageID.DATA_BASE64_PREPARE_OK:
				VodUrl vodurl = (VodUrl) msg.obj;
				mVideoSource = vodurl.getUrl();
				mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
				/**
				 * 发起一次新的播放任务
				 */
				if(mEventHandler.hasMessages(WindowMessageID.EVENT_PLAY))
					mEventHandler.removeMessages(WindowMessageID.EVENT_PLAY);
				mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);
				break;
			case WindowMessageID.SHOW_TV:
				mProgressBar.setVisibility(View.VISIBLE);
				startSpeed();
				break;
			case WindowMessageID.COLSE_SHOW_TV:
				mProgressBar.setVisibility(View.GONE);
				endSpeed();
				break;
			case WindowMessageID.PROGRESSBAR_PROGRESS_RESET:
				mProgressBar.setProgress(0);
				break;
			case WindowMessageID.RESET_MOVIE_TIME:
				ResetMovieTime();
				break;
			case WindowMessageID.PREPARE_VOD_DATA:
				PrepareVodData(playIndex);
				break;
			case WindowMessageID.SELECT_SCALES:
				selectScales(hmblposition);
				break;
			}
		};
	};
	/**
	 * 更新电影播放数据
	 */
	private void ResetMovieTime(){
		updateTextViewWithTimeFormat(tv_currentTime, 0);
		updateTextViewWithTimeFormat(tv_totalTime, 0);
		seekBar.setProgress(0);
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
					//选集
					case 0:
						menutype = 0;
						menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, videoInfo, 0,isMenuItemShow));
						menulist.setSelection(xjposition);
						break;
					//清晰度
					case 1:
						menutype = 1;
						menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, medialist, 1,isMenuItemShow));
						break;
					//解码
					case 2:
						menutype = 2;
						menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(2), 2,isMenuItemShow));
						break;
					//显示比例
					case 3:
						menutype = 3;
						menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(3), 3,isMenuItemShow));
						break;
					//上下键配置
					case 4:
						menutype = 4;
						menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(4), 4,isMenuItemShow));
						break;
					}
				}else if(isMenuItemShow){
					switch (menutype) {
					//选集
					case 0:
						 if(videoInfo.size()>position){
								isNext = true;
								isSwitch = true;
								playIndex = position - 1;
								if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
									mVV.stopPlayback();
								}
//								//显示播放栏
								if (!isControllerShow)
									showController();
								mediaHandler.sendEmptyMessage(WindowMessageID.RESET_MOVIE_TIME);
								//ResetMovieTime();
							}
						xjposition = position;
						hideMenu();
						break;
					//清晰度
					case 1:
						isPause = true;
						if(mVV.isPlaying()){
							mLastPos = mVV.getCurrentPosition();
						}
						if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
							mVV.stopPlayback();
						}
						qxdposition = position;
						hideMenu();
//						if(mEventHandler.hasMessages(WindowMessageID.EVENT_PLAY))
//							mEventHandler.removeMessages(WindowMessageID.EVENT_PLAY);
//						mEventHandler.sendEmptyMessage(WindowMessageID.EVENT_PLAY);
						break;
					//解码
					case 2:
						jmposition = position;
						if(position==0){
							Editor editor = sp.edit();
							editor.putInt("mIsHwDecode", BVideoView.DECODE_SW);
							editor.putString("play_decode", "软解码");
							editor.commit();
						}else if(position==1){
							Editor editor = sp.edit();
							editor.putInt("mIsHwDecode", BVideoView.DECODE_HW);
							editor.putString("play_decode", "硬解码");
							editor.commit();
						}
						setDecode();
						mVV.setDecodeMode(mIsHwDecode?BVideoView.DECODE_HW:BVideoView.DECODE_SW);//设置解码模式
						isPause = true;
						if(mVV.isPlaying()){
							mLastPos = mVV.getCurrentPosition();
						}
						if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
							mVV.stopPlayback();
						}
						hideMenu();
						break;
					//显示比例
					case 3:
						hmblposition = position;
						selectScales(hmblposition);
						Editor editor = sp.edit();
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
					case 4:
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
						menulist.setAdapter(new VodMenuAdapter(VideoPlayerActivity.this, VideoPlayUtils.getData(0),5,isMenuItemShow));
					} else if (isMenuShow) {
						menupopupWindow.dismiss();
					}
					break;
				}
				return false;
			}
		});
		
	}
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//	    if(mGestureDetector.onTouchEvent(event)){  
//            event.setAction(MotionEvent.ACTION_CANCEL);  
//	    }  
//	    return true; 
//	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		boolean result = mGestureDetector.onTouchEvent(event);
		//mPosition = mLastPos;
		DisplayMetrics screen = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(screen);
		if (mSurfaceYDisplayRange == 0)
			mSurfaceYDisplayRange = Math.min(screen.widthPixels,
					screen.heightPixels);
		float y_changed = event.getRawY() - mTouchY;
		float x_changed = event.getRawX() - mTouchX;
		
		float coef = Math.abs(y_changed / x_changed);
		float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
		Logger.i("joychang", "y_changed="+y_changed+"...x_changed="+x_changed+"...coef="+coef+"...xgesturesize="+xgesturesize);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Logger.i(TAG, "MotionEvent.ACTION_DOWN.......");
			boolean isSeekTouch = true;
			mTouchAction = TOUCH_NONE;
			mTouchY = event.getRawY();
			mTouchX = event.getRawX();
			maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			Lightness = Utils.GetLightness(VideoPlayerActivity.this);
			break;
		case MotionEvent.ACTION_MOVE:
			Logger.i(TAG, "MotionEvent.ACTION_MOVE.......");
			if(coef>2){
				isSeekTouch = false;
				//音量和亮度
				if(mTouchX > (screenWidth / 2)){
					//音量
					doVolumeTouch(y_changed);
				}
				if (mTouchX < (screenWidth / 2)) {
					doBrightnessTouch(y_changed);
				}
			}
			doSeekTouch(coef, xgesturesize,false);
			break;
		case MotionEvent.ACTION_UP:
			Logger.i(TAG, "MotionEvent.ACTION_UP.......");
			doSeekTouch(coef, xgesturesize, true);
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
	
	//seek进度
	private void doSeekTouch(float coef, float gesturesize,boolean seek){
		if (coef > 0.5 || Math.abs(gesturesize) < 1)
			return;
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK)
			return;
		mTouchAction = TOUCH_SEEK;
		int time = mVV.getCurrentPosition();
		int jump = (int) ((Math.signum(gesturesize) * ((600000 * Math.pow(
				(gesturesize / 8), 4)) + 3000))/1000);
		Logger.d("doSeekTouch", "jump="+jump);
		if ((jump > 0) && ((time + jump) > videoLength))
			jump = (int) (videoLength - time);
		if ((jump < 0) && ((time + jump) < 0))
			jump = (int) -time;
		if(videoLength>0){
	      	 tv_progress_time.setVisibility(View.VISIBLE);
	      	 updateTextViewWithTimeFormat(tv_progress_time, time+jump);
	      	 mHandler.removeMessages(WindowMessageID.HIDE_PROGRESS_TIME);
	      	 mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_PROGRESS_TIME, 2000);
	         if(seek){
					mVV.seekTo(time+jump);
					mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
	         }
        }
	}
	
    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
            break;
        }
        return name;
    }
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_playStatus:
			if(!isControllerShow)
				showController();
			if (mVV.isPlaying()) {
				mVV.pause();//暂停播放
				mHandler.removeMessages(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				ib_playStatus.setImageResource(R.drawable.media_playstatus);
			} else {
				mVV.resume();//继续播放
				mHandler.sendEmptyMessage(WindowMessageID.UI_EVENT_UPDATE_CURRPOSITION);
				ib_playStatus.setImageResource(R.drawable.media_pause);
			}
			break;
		}
	}
	
	public void selectScales(int paramInt) {
		if (mVV.getWindowToken() == null)
			return;
		Rect localRect = new Rect();
		mVV.getWindowVisibleDisplayFrame(localRect);
		int i1 = localRect.bottom;
		int i2 = localRect.top;
		double d1 = i1 - i2;
		int i3 = localRect.right;
		int i4 = localRect.left;
		double d2 = i3 - i4;
		String str2 = "diplay = " + d2 + ":" + d1;
		//int i5 = Log.d(TAG, str2);
		if (d1 <= 0.0D)
			return;
		if (d2 <= 0.0D)
			return;
		int mVideoHeight = mVV.getVideoHeight();
		int mVideoWidth = mVV.getVideoHeight();
		if (mVideoHeight <= 0.0D)
			return;
		if (mVideoWidth <= 0.0D)
			return;
		Logger.d(TAG, "mVideoHeight="+mVideoHeight+"....mVideoWidth"+mVideoWidth);
		ViewGroup.LayoutParams localLayoutParams = mVV.getLayoutParams();
		switch (paramInt) {
		//原始
		case 0:
			double d3 = d2 / d1;
			int i6 = mVideoWidth;
			int i7 = mVideoHeight;
			double d4 = i6 / i7;
			if (d3 < d4){
				int i24 = (int) d2;
				localLayoutParams.width = i24;
				double d7 = mVideoHeight * d2;
				double d8 = mVideoWidth;
				int i25 = (int) (d7 / d8);
				localLayoutParams.height = i25;
			}else{
				int i8 = (int) d1;
				localLayoutParams.height = i8;
				double d5 = mVideoWidth * d1;
				double d6 = mVideoHeight;
				int i9 = (int) (d5 / d6);
				localLayoutParams.width = i9;	
			}
			mVV.setLayoutParams(localLayoutParams);
			return;
			//4:3
		case 1:
			if (d2 / d1 >= 1.333333333333333D) {
				int i12 = (int) d1;
				localLayoutParams.height = i12;
				int i13 = (int) (4.0D * d1 / 3.0D);
				localLayoutParams.width = i13;
			}else{
				int i16 = (int) d2;
				localLayoutParams.width = i16;
				int i17 = (int) (3.0D * d2 / 4.0D);
				localLayoutParams.height = i17;
			}
			mVV.setLayoutParams(localLayoutParams);
			return;
			//16：9
		case 2:
			if (d2 / d1 >= 1.777777777777778D) {
				int i18 = (int) d1;
				localLayoutParams.height = i18;
				int i19 = (int) (16.0D * d1 / 9.0D);
				localLayoutParams.width = i19;
			}else{
				int i22 = (int) d2;
				localLayoutParams.width = i22;
				int i23 = (int) (9.0D * d2 / 16.0D);
				localLayoutParams.height = i23;
			}
			mVV.setLayoutParams(localLayoutParams);
			return;
			//全屏
		case 3:
			int i24 = (int) d2;
			localLayoutParams.width = i24;
			double d7 = mVideoHeight * d2;
			double d8 = mVideoWidth;
			int i25 = (int) (d7 / d8);
			localLayoutParams.height = i25;
			mVV.setLayoutParams(localLayoutParams);
			return;
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
			Utils.SetLightness(VideoPlayerActivity.this, current);
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
	
	private void startSpeed(){
		mSpeedHandler.removeCallbacks(speedRunnable);
		lastRxByte = TrafficStats.getTotalRxBytes();
		lastSpeedTime = System.currentTimeMillis();
		mSpeedHandler.postDelayed(speedRunnable,0);
		tv_mv_speed.setVisibility(View.VISIBLE);
	}
	
	private void endSpeed(){
		mSpeedHandler.removeCallbacks(speedRunnable);
		tv_mv_speed.setVisibility(View.GONE);
	}

	//切换解码播放
	private void switchCode(){
		//Utils.showToast(VideoPlayerActivity.this, "正在为您尝试切换解码器播放", R.drawable.toast_smile);
		int Decode = sp.getInt("mIsHwDecode",BVideoView.DECODE_HW);
		if(Decode==BVideoView.DECODE_SW){
			mIsHwDecode = true;
			jmposition = 1;
		}else{
			mIsHwDecode = false;
			jmposition = 0;
		}
		mVV.setDecodeMode(mIsHwDecode?BVideoView.DECODE_HW:BVideoView.DECODE_SW);//设置解码模式
		xjposition = playIndex;
		collectionTime = 0;
		mLastPos = 0;
		mediaHandler.sendEmptyMessage(WindowMessageID.DATA_PREPARE_OK);
		mediaHandler.sendEmptyMessage(WindowMessageID.SHOW_TV);
		mediaHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_PROGRESS_RESET);
	}
	
	private void acquireWakeLock() {
			if (mWakeLock == null) {
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				//mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
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
	/**
	 * @class WindowMessageID
	 * @brief 内部消息ID定义类。
	 * @author joychang
	 */
	private class WindowMessageID {

		/**
		 * @brief 服务请求成功。
		 */
		public static final int SUCCESS = 0x00000001;
		/**
		 * @brief 服务请求失败。
		 */
		public static final int NET_FAILED = 0x00000002;
		/**
		 * @brief 请求出错。
		 */
		public final static int ERROR = 0x00000003;
		/**
		 * @brief 播放。
		 */
		public final static int EVENT_PLAY = 0x00000004;
		
		/**
		 * @brief 刷新时间。
		 */
		public static final int UI_EVENT_UPDATE_CURRPOSITION = 0x00000005;
		/**
		 * @brief 显示进度条。
		 */
		public final static int PROGRESS_CHANGED = 0x00000006;
		/**
		 * @brief 隐藏进度条。
		 */
		public final static int HIDE_CONTROLER = 0x00000007;

		/**
		 * @brief 播放数据准备OK。
		 */
		public static final int DATA_PREPARE_OK = 0x00000008;
		/**
		 * @brief base64后的数据准备OK。
		 */
		public static final int DATA_BASE64_PREPARE_OK = 0x00000009;
		
		/**
		 * @brief 准备数据
		 */
		public static final int PREPARE_VOD_DATA = 0x000000010;
		
		public static final int SHOW_TV = 0x00000011;
		
		public static final int COLSE_SHOW_TV = 0x00000012;
		
		public static final int PROGRESSBAR_PROGRESS_RESET = 0x00000013;
		
		public static final int SELECT_SCALES = 0x00000014;//设置视频显示比例
		
		public static final int HIDE_PROGRESS_TIME = 0x00000015;//快进时间显示隐藏
		
		public static final int HIDE_MENU = 0x00000016;//菜单隐藏
		
		public static final int START_SPEED = 0x00000017;//网速
		
		public static final int RESET_MOVIE_TIME = 0x00000018;//重置时间
		
		public static final int PLAY_ERROR = 0x00000019;//播放异常
		
		public static final int SWITCH_CODE = 0x00000020;//切换解码

	}
	
	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;
	/**
	 * 播放状态
	 */
	private  enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	private BVideoView mVV = null;
	private WakeLock mWakeLock = null;
	private String mVideoSource = null;//视频源
	private boolean mIsHwDecode = true;//设置解码模式
	private final Object SYNC_Playing = new Object();//锁
	private HandlerThread mHandlerThread;
	private EventHandler mEventHandler;
	private final static String TAG = "VideoPlayerActivity";
	private int screenWidth;
	private int screenHeight;
	private static int controlHeight = 0;
	private View controlView;
	private View time_controlView;
	private PopupWindow controler;
	private PopupWindow time_controler;
	private boolean isControllerShow = false;
	private final static int TIME = 6000;// 显示时间
	private final static int DURATION_TIME = 300000;
	private SeekBar seekBar;
	private TextView tv_currentTime, tv_totalTime, tv_mv_name,tv_mv_speed,tv_menu;
	private TextView tv_progress_time,tv_time;
	private ImageButton ib_playStatus;
	private ImageView iv_media_menu;
	private int videoLength;
	private long firstTime = 0;
	private boolean isBack = false;// 是否返回
	private static ArrayList<MediaInfo> medialist;
	private List<VideoInfo> videoInfo = null;
	private String vodstate;
	private String nextlink;
	private String domain;
	private String url;// 选集中的以及
	private String vodtype = null;
	private String albumPic = null;
	private MediaInfo mediainfo;
	private String sourcetype;// 数据源类型
	private String videoId;//影视Id
	private String vodname;//影视名称
	private String sourceId;//数据源id
	private int playIndex = 0;
	private int collectionTime = 0;
	private ListView menulist;
	private PopupWindow menupopupWindow;
	private VodMenuAdapter vmAdapter;
	private GestureDetector mGestureDetector = null;//手势
	private VodDao dao;
	//public static int menuposition = 0;
	public static int xjposition = 0;
	public static int qxdposition = 0;
	public static int jmposition = 0;
	public static int hmblposition = 0;
	public static int phszposition = 0;
	private int playPreCode;
	private Boolean isLast = false;
	private Boolean isNext = false;
	private Boolean isPause = false;
	private Boolean isDestroy = false;
	private boolean isMenuShow = false;
	private boolean isMenuItemShow = false;
	private static int menutype;
	private SharedPreferences sp;
	private PlayerProgressBar mProgressBar;
	private AudioManager mAudioManager = null;
	private Toast mToast = null;
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
	private boolean isSwitch = true;
	private boolean isaiqiyi = false;
	
}
