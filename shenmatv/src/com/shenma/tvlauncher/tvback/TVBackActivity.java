package com.shenma.tvlauncher.tvback;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.shenma.tvlauncher.BaseActivity;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.application.MyApplication;
import com.shenma.tvlauncher.network.PullXmlParserCallback;
import com.shenma.tvlauncher.network.PullXmlParserError;
import com.shenma.tvlauncher.network.PullXmlParserThread;
import com.shenma.tvlauncher.tvback.adapter.ChannelAdapter;
import com.shenma.tvlauncher.tvback.adapter.ProgramAdapter;
import com.shenma.tvlauncher.tvback.domain.ChannelDateInfo;
import com.shenma.tvlauncher.tvback.domain.ChannelInfo;
import com.shenma.tvlauncher.tvback.domain.MediaInfo;
import com.shenma.tvlauncher.tvback.domain.ProgramInfo;
import com.shenma.tvlauncher.tvback.domain.UpdateInfo;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.VideoView;
import com.umeng.analytics.MobclickAgent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/***
 * @Description 电视回看主界面
 * @author joychang
 * 
 */
public class TVBackActivity extends BaseActivity {
	private final static String TAG = "TVBackActivity";
	private static ArrayList<ChannelInfo> mchannellist;// 电视频道
	private static ArrayList<ProgramInfo> mProgramlist;// 日期对应节目单
	private static ArrayList<ChannelDateInfo> mChannelDatelist;// 节目日期
	private static ArrayList<MediaInfo> medialist;//
	private static ArrayList<ProgramInfo> mNowProgramlist;//
	// private final static String WEPOWER_URL
	// ="http://apl.lsott.com/liveback/index.php";
	// private final static String WEPOWER_UPDATE_URL =
	// "http://api.lsott.com/apk/wepowertvback.xml";
	// private final static String WEPOWER_UPDATE_URL
	// ="http://huibo.lsott.com/apk/wepowertvback.xml";
	private ListView lv_tv_back_channles, lv_tv_back_videos;
	private TextView tv_back_current_channel, tv_back_current_tv,
			tv_back_next_tv;

	private ImageView back_video_blck;
	private static boolean loadFlag = false;
	private ChannelAdapter channelAdapter;
	private ProgramAdapter programAdapter;
	private static int[] WD_Selector = { R.drawable.tv_back_monday_selector,
			R.drawable.tv_back_tuesday_selector,
			R.drawable.tv_back_wenesday_seletor,
			R.drawable.tv_back_thuresday_selector,
			R.drawable.tv_back_friday_selector,
			R.drawable.tv_back_saturday_selector,
			R.drawable.tv_back_sunday_selector };
	private RadioGroup weekdays;
	private static RadioButton[] rb;
	public static int rbChecked;
	private RadioButton rb_tv_back_rd_1, rb_tv_back_rd_2, rb_tv_back_rd_3,
			rb_tv_back_rd_4, rb_tv_back_rd_5, rb_tv_back_rd_6, rb_tv_back_rd_7;// 星期
	private VideoView vv;
	private int mPostion = 0;
	private int mProgramPostion = 0;
	private final static int SCREEN_FULL = 0;// 填充屏幕
	private final static int SCREEN_DEFAULT = 1;// 默认
	private boolean isFull = false;// 是否是全屏
	private boolean isProgress = false;// 是否更改进度
	private MyApplication mApp;
	private int screenWidth;
	private int screenHeight;
	private long firstTime = 0;
	private View controlView;
	private PopupWindow controler;
	private boolean isControllerShow = false;
	private int controlHeight = 0;
	private final static int TIME = 6000;// 显示时间
	private final static int DURATION_TIME = 300000;
	private SeekBar seekBar;
	private TextView tv_currentTime, tv_totalTime;
	private Boolean isOnline = false;// 确定是否更新进度条
	private int videoLength;
	private UpdateInfo updateinfo = null;
	private GestureDetector mGestureDetector = null;// 手势
	private RelativeLayout rl_ProgressBar;
	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "TVBackActivity";
	private String tvbackname = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tvback);
		Log.d(TAG, "TVBackActivity---->onCreate");
		mApp = (MyApplication) getApplication();
		loadMainUI();
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "TVBackActivity---->onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		acquireWakeLock();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
		// if (null != mWakeLock && (!mWakeLock.isHeld())) {
		// mWakeLock.acquire();
		// }
		mProgressHandler.post(updateThread);
		Logger.d(TAG, "TVBackActivity---->onResume");
	}

	private void acquireWakeLock() {
		if (mWakeLock == null) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			// PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, this.getClass()
					.getCanonicalName());
			mWakeLock.acquire();
		}
	}

	private void releaseWakeLock() {
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Utils.loadingClose_Tv();
		if (null != vv && vv.isPlaying()) {
			vv.stopPlayback();
		}
		Logger.d(TAG, "TVBackActivity---->onDestroy");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
		releaseWakeLock();
		mProgressHandler.removeCallbacks(updateThread);
		Logger.d(TAG, "TVBackActivity---->onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Utils.loadingClose_Tv();
		Logger.d(TAG, "TVBackActivity---->onStop");

	}

	/**
	 * 初始化主界面
	 */
	private void loadMainUI() {
		initView();
		initData();
	}

	/**
	 * 初始化View
	 */
	protected void initView() {
		rbChecked = 0;
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		getScreenSize();
		loadViewLayout();
		findViewById();
		setListener();
		setvvListener();
		mchannellist = new ArrayList<ChannelInfo>();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 启动加载
		Utils.loadingShow_tv(this, R.string.load_msg);
		loadFlag = false;
		loadDataFromXml(Constant.WEPOWER_URL, MenuType.CHANNEL_MENU);// 取得频道列表
	}

	/**
	 * 初始化控件
	 */
	protected void findViewById() {
		rb = new RadioButton[7];
		lv_tv_back_channles = (ListView) findViewById(R.id.tv_back_channles);
		lv_tv_back_videos = (ListView) findViewById(R.id.tv_back_videos);
		tv_back_current_channel = (TextView) findViewById(R.id.tv_back_current_channel);
		tv_back_current_tv = (TextView) findViewById(R.id.tv_back_current_tv);
		tv_back_next_tv = (TextView) findViewById(R.id.tv_back_next_tv);
		back_video_blck = (ImageView) findViewById(R.id.back_video_blck);
		weekdays = (RadioGroup) findViewById(R.id.rg_tv_back_weekdays);
		rb_tv_back_rd_1 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_1);
		rb[0] = rb_tv_back_rd_1;
		rb_tv_back_rd_2 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_2);
		rb[1] = rb_tv_back_rd_2;
		rb_tv_back_rd_3 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_3);
		rb[2] = rb_tv_back_rd_3;
		rb_tv_back_rd_4 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_4);
		rb[3] = rb_tv_back_rd_4;
		rb_tv_back_rd_5 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_5);
		rb[4] = rb_tv_back_rd_5;
		rb_tv_back_rd_6 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_6);
		rb[5] = rb_tv_back_rd_6;
		rb_tv_back_rd_7 = (RadioButton) weekdays
				.findViewById(R.id.rb_tv_back_rd_7);
		rb[6] = rb_tv_back_rd_7;
		vv = (VideoView) findViewById(R.id.videoview);
		rl_ProgressBar = (RelativeLayout) findViewById(R.id.rl_progressBar);
	}

	protected void loadViewLayout() {
		controlView = getLayoutInflater().inflate(R.layout.tv_media_controler,
				null);
		controler = new PopupWindow(controlView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		seekBar = (SeekBar) controlView.findViewById(R.id.seekbar);
		tv_currentTime = (TextView) controlView
				.findViewById(R.id.tv_currentTime);
		tv_totalTime = (TextView) controlView.findViewById(R.id.tv_totalTime);
		onCreateMenu();
	}

	/**
	 * 设置控件监听
	 */
	@SuppressWarnings("deprecation")
	protected void setListener() {
		lv_tv_back_channles.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int postion, long id) {
				if (isFull) {
					if (!isControllerShow) {
						showController();
						hideControllerDelay();
					} else {
						cancelDelayHide();
						hideController();
					}
					hideMenu();
					return;
				}
				String dataUrl = mchannellist.get(postion).getChanneUrl();
				loadDataFromXml(dataUrl, MenuType.DATE_MENU);
				tvbackname = mchannellist.get(postion).getChanneName();
			}
		});
		lv_tv_back_videos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (isFull) {
					if (!isControllerShow) {
						showController();
						hideControllerDelay();
					} else {
						cancelDelayHide();
						hideController();
					}
					hideMenu();
					return;
				}
				if (mApp.getOnPlay().equals(
						rbChecked + mProgramlist.get(position).getTime()
								+ mProgramlist.get(position).getProgramName())) {
					vv.bringToFront();
					setVideoScale(SCREEN_FULL);
					if (!isControllerShow) {
						showController();
					}
					// } else if(mProgramlist.equals(mNowProgramlist)){
					// mProgramPostion = position;
					// String nowProgram = rbChecked+
					// mNowProgramlist.get(position).getTime()+
					// mNowProgramlist.get(position).getProgramName();
					// mApp.setOnPlay(nowProgram);
					// programAdapter.setViewSate();
					// //lv_tv_back_videos.setSelection(position);
					// String media_url = mNowProgramlist.get(position)
					// .getProgramUrl();
					// loadMediaFromXml(media_url, MenuType.MEDIA_MENU);
					// Utils.loadingShow_tv(TVBackActivity.this,R.string.load_msg);
					// }else{
				} else {
					mNowProgramlist = (ArrayList<ProgramInfo>) mProgramlist
							.clone();
					mProgramPostion = position;
					String nowProgram = rbChecked
							+ mNowProgramlist.get(position).getTime()
							+ mNowProgramlist.get(position).getProgramName();
					mApp.setOnPlay(nowProgram);
					Logger.d(TAG, "nowProgram==" + nowProgram);
					programAdapter = new ProgramAdapter(TVBackActivity.this,
							mNowProgramlist);
					programAdapter.notifyDataSetChanged();
					lv_tv_back_videos.setAdapter(programAdapter);
					lv_tv_back_videos.setSelection(position);
					String media_url = mNowProgramlist.get(position)
							.getProgramUrl();
					loadMediaFromXml(media_url, MenuType.MEDIA_MENU);
					Utils.loadingShow_tv(TVBackActivity.this, R.string.load_msg);
					Logger.d(TAG, "lv_tv_back_videos....loadingShow_tv");
				}
				// mHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_VISIBLE);
			}

		});

		weekdays.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (isFull) {
					if (!isControllerShow) {
						showController();
						hideControllerDelay();
					} else {
						cancelDelayHide();
						hideController();
					}
					hideMenu();
					return;
				}
				switch (checkedId) {
				case R.id.rb_tv_back_rd_1:
					rbChecked = 0;
					setProgramData(rbChecked);
					break;
				case R.id.rb_tv_back_rd_2:
					rbChecked = 1;
					setProgramData(rbChecked);
					break;
				case R.id.rb_tv_back_rd_3:
					rbChecked = 2;
					setProgramData(rbChecked);
					break;
				case R.id.rb_tv_back_rd_4:
					rbChecked = 3;
					setProgramData(rbChecked);
					break;
				case R.id.rb_tv_back_rd_5:
					rbChecked = 4;
					setProgramData(rbChecked);
					break;
				case R.id.rb_tv_back_rd_6:
					rbChecked = 5;
					setProgramData(rbChecked);
					break;
				case R.id.rb_tv_back_rd_7:
					rbChecked = 6;
					setProgramData(rbChecked);
					break;
				default:
					break;
				}
				rb[rbChecked].setChecked(true);
			}
		});
		// 手势监听
		mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {
				// 双击
				if (isFull) {
					hideController();
					showMenu();
				}
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e) {
				if (!isControllerShow) {
					showController();
					hideControllerDelay();
				} else {
					cancelDelayHide();
					hideController();
				}
				hideMenu();
				// return super.onSingleTapConfirmed(e);
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				// super.onLongPress(e);
				if (isFull) {
					hideController();
					showMenu();
				}
			}
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				hideControllerDelay();
				// Utils.loadingShow_tv(TVBackActivity.this,
				// "",getString(R.string.tvback_str_data_loading));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mProgressHandler.removeCallbacks(updateThread);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					if (ISCNTV) {
						if (progress > DURATION_TIME) {
							pose_start = (Integer) progress / DURATION_TIME;
							if (mPostion != pose_start
									&& medialist.size() > pose_start) {
								mPostion = pose_start;
								vv.setVideoPath(medialist.get(mPostion)
										.getMediaurl());
							}
						}
						int pose_end = progress % DURATION_TIME;
						vv.seekTo(pose_end);
					} else {
						vv.seekTo(pose);
					}
					mProgressHandler.post(updateThread);
				}
			}
		});

	}

	/**
	 * 设置VoidView监听
	 */
	private void setvvListener() {
		// 异常
		vv.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				vv.stopPlayback();
				isOnline = false;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TVBackActivity.this);
				builder.setMessage("对不起！服务器忙...");
				builder.setNegativeButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								// TVBackActivity.this.finish();
							}
						});
				builder.create().show();
				return false;
			}
		});
		// 准备OK
		vv.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// mHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_VISIBLE);
				tv_back_current_channel.setText(tvbackname);
				String tv_back_current = mNowProgramlist.get(mProgramPostion)
						.getProgramName();
				tv_back_current_tv.setText(tv_back_current);
				if (mNowProgramlist.size() <= mProgramPostion + 1) {
					tv_back_next_tv.setText("以实际播放节目为准");
				} else {
					tv_back_next_tv.setText(mNowProgramlist.get(
							mProgramPostion + 1).getProgramName());
				}
				Map<String, String> m_value = new HashMap<String, String>();
				m_value.put("播放频道统计", tvbackname);
				m_value.put("播放节目统计", tv_back_current);
				MobclickAgent.onEvent(TVBackActivity.this, "TV_BACK", m_value);
				mApp.setOnPlay(rbChecked
						+ mNowProgramlist.get(mProgramPostion).getTime()
						+ mNowProgramlist.get(mProgramPostion).getProgramName());
				if (!mNowProgramlist.equals(mProgramlist)) {
					programAdapter = new ProgramAdapter(TVBackActivity.this,
							mNowProgramlist);
					programAdapter.notifyDataSetChanged();
					lv_tv_back_videos.setAdapter(programAdapter);
					lv_tv_back_videos.setSelection(mProgramPostion);
				}
				vv.start();
				seekBarUpdate();
				Utils.loadingClose_Tv();
				mApp.setOnPlay(rbChecked
						+ mNowProgramlist.get(mProgramPostion).getTime()
						+ mNowProgramlist.get(mProgramPostion).getProgramName());
				/*
				 * Log.d(TAG, "准备好了vv.start();" +
				 * mNowProgramlist.get(mProgramPostion) .getProgramName());
				 */
			}
		});

		// 播放完成
		vv.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				// 判断是否播放节目的下一段，获取下一个节目的媒体列表
				isOnline = false;
				if (null != medialist && medialist.size() > mPostion + 1) {
					isOnline = true;
					mPostion++;
					// pose_start = mPostion;
					vv.setVideoPath(medialist.get(mPostion).getMediaurl());
				} else if (null != mNowProgramlist
						&& mNowProgramlist.size() > mProgramPostion + 1) {
					isOnline = true;
					Utils.loadingShow_tv(TVBackActivity.this, R.string.load_msg);
					Logger.d(TAG, "播放完成....loadingShow_tv");
					mProgramPostion++;
					loadMediaFromXml(mNowProgramlist.get(mProgramPostion)
							.getProgramUrl(), MenuType.MEDIA_MENU);
				} else {
					vv.stopPlayback();
				}
				// Utils.loadingClose_Tv();
			}
		});
		vv.setOnInfoListener(new OnInfoListener() {
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				if (mp.MEDIA_INFO_BUFFERING_START == what) {
					if (!Utils.isShowing()) {
						mHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_VISIBLE);
					}
					// Logger.d(TAG, "缓冲开始");
					// 缓冲开始
					// Utils.loadingShow_tv(TVBackActivity.this,R.string.loading_tv);
					Logger.d(TAG, "缓冲中....loadingShow_tv");
				} else if (mp.MEDIA_INFO_BUFFERING_END == what) {
					mHandler.sendEmptyMessage(WindowMessageID.PROGRESSBAR_GONE);
					// Logger.d(TAG, "缓冲结束");
					// 缓冲结束
					// Utils.loadingClose_Tv();
				}
				return false;
			}
		});

		vv.setOnPlayingBufferCacheListener(new OnBufferingUpdateListener() {

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// mProgressBar.setProgress(percent);
			}
		});
	}

	/**
	 * 频道列表.日期列表.节目列表
	 * 
	 * @brief 解析xml数据。
	 * @author joychang
	 * @param[in] url 页面数据地址
	 */
	private void loadDataFromXml(final String url, final int menuType) {
		Logger.d(TAG, "_loadDataFromXml() start");
		String encodeUrl = url;
		try {
			encodeUrl = Utils.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			encodeUrl = url;
		}
		PullXmlParserThread parser = new PullXmlParserThread(0,
				new PullXmlParserCallback() {
					@Override
					public void text(String text) {
						Logger.d(TAG, TAG + "======text==" + text);
					}

					@Override
					public void startFlag(String nodeName,
							Map<String, String> attribute) {

						if (nodeName.equals("list")) {
							mlist = 1;
						} else if (nodeName.equals("m")) {
							mm = 1;
							switch (menuType) {
							case MenuType.CHANNEL_MENU:
								mChannelinfo = new ChannelInfo();
								mChannelinfo.setChanneName(attribute
										.get("label"));
								mChannelinfo.setChanneUrl(attribute
										.get("list_src"));
								break;
							case MenuType.DATE_MENU:
								mchannelDateInfo = new ChannelDateInfo();
								mchannelDateInfo.setChannelDate_label(attribute
										.get("label"));
								mchannelDateInfo.setChannelDate_Url(attribute
										.get("list_src"));
								mchannelDateInfo.setChannelDate(attribute
										.get("date"));
								break;
							case MenuType.PROGRAMINFO_MENU:
								mProgramInfo = new ProgramInfo();
								mProgramInfo.setProgramName(Utils
										.getNameToLabel(attribute.get("label")));
								mProgramInfo.setProgramUrl(attribute.get("src"));
								mProgramInfo.setTime(Utils
										.getTimeToLabel(attribute.get("label")));
								break;
							case MenuType.REFRESH_MENU:
								mProgramInfo = new ProgramInfo();
								mProgramInfo.setProgramName(Utils
										.getNameToLabel(attribute.get("label")));
								mProgramInfo.setProgramUrl(attribute.get("src"));
								mProgramInfo.setTime(Utils
										.getTimeToLabel(attribute.get("label")));
								break;
							default:
								break;
							}
						}

					}

					@Override
					public void startDocument() {
						Logger.d(TAG, TAG + "======startDocument");
						switch (menuType) {
						case MenuType.DATE_MENU:
							mChannelDatelist = new ArrayList<ChannelDateInfo>();
							break;
						case MenuType.PROGRAMINFO_MENU:
							mNowProgramlist = new ArrayList<ProgramInfo>();
							break;
						case MenuType.CHANNEL_MENU:
							mchannellist = new ArrayList<ChannelInfo>();
							break;
						case MenuType.REFRESH_MENU:
							mProgramlist = new ArrayList<ProgramInfo>();
							break;
						default:
							break;
						}
					}

					@Override
					public void haveError(PullXmlParserError error) {
						Logger.d(TAG, TAG + "======haveError");
					}

					@Override
					public void endFlag(String nodeName) {
						Logger.d(TAG, TAG + "======endFlag");
						if (nodeName.equals("list")) {
							mlist = 0;
						} else if (nodeName.equals("m")) {
							mm = 0;
							switch (menuType) {
							case MenuType.CHANNEL_MENU:
								mchannellist.add(mChannelinfo);
								Logger.d(TAG, "mChannelinfo.getChanneName()=="
										+ mChannelinfo.getChanneName());
								mChannelinfo = null;
								break;
							case MenuType.DATE_MENU:
								mChannelDatelist.add(mchannelDateInfo);
								/*
								 * Log.d(TAG, "mchannelDate==" +
								 * mchannelDateInfo.getChannelDate());
								 */
								mchannelDateInfo = null;
								break;
							case MenuType.PROGRAMINFO_MENU:
								mNowProgramlist.add(mProgramInfo);
								/*
								 * Log.d(TAG, "mNowProgramlist==" +
								 * mProgramInfo.getProgramName()); Log.d(TAG,
								 * "mProgramTime==" + mProgramInfo.getTime());
								 */
								mProgramInfo = null;
								break;
							case MenuType.REFRESH_MENU:
								mProgramlist.add(mProgramInfo);
								/*
								 * Log.d(TAG, "mProgramlist==" +
								 * mProgramInfo.getProgramName()); Log.d(TAG,
								 * "mProgramlist==" + mProgramInfo.getTime());
								 */
								mProgramInfo = null;
								break;
							}
						}
					}

					@Override
					public void endDocument() {
						Logger.d(TAG, TAG + "======endDocument");
						switch (menuType) {
						case MenuType.CHANNEL_MENU:
							mHandler.sendEmptyMessage(WindowMessageID.CHANNEL);
							break;
						case MenuType.DATE_MENU:
							mHandler.sendEmptyMessage(WindowMessageID.DATE);
							break;
						case MenuType.PROGRAMINFO_MENU:
							mHandler.sendEmptyMessage(WindowMessageID.PROGRAM);
							break;
						case MenuType.REFRESH_MENU:
							mHandler.sendEmptyMessage(WindowMessageID.REFRESH);
							break;
						}

					}

					protected int mlist = -1;// 开始
					protected int mm = -1;// 节目
					protected ChannelInfo mChannelinfo = null;
					protected ProgramInfo mProgramInfo = null;
					protected ChannelDateInfo mchannelDateInfo = null;

				}, url);

		parser.start();

		Logger.d(TAG, "_loadDataFromXml() end");

	}

	/**
	 * 更新设置日期列表
	 */
	protected void setChannelDate() {
		if (mChannelDatelist.size() > 6) {
			for (int i = 0; i < 7; i++) {
				String mdate = mChannelDatelist.get(i).getChannelDate_label();
				String mweek = Utils.getWeekToDate(mdate);
				String text = mdate.substring(mdate.indexOf("/") + 1,
						mdate.length());
				if (null != mweek && mweek.equals("星期一")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_monday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期二")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_tuesday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期三")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_wenesday_seletor);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期四")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_thuresday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期五")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_friday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期六")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_saturday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期日")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_sunday_selector);
					rb[i].setText(text);
				}
			}
		} else {
			for (int i = 0; i < mChannelDatelist.size(); i++) {
				String mdate = mChannelDatelist.get(i).getChannelDate_label();
				String mweek = Utils.getWeekToDate(mdate);
				String text = mdate.substring(mdate.indexOf("/") + 1,
						mdate.length());
				if (null != mweek && mweek.equals("星期一")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_monday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期二")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_tuesday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期三")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_wenesday_seletor);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期四")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_thuresday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期五")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_friday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期六")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_saturday_selector);
					rb[i].setText(text);
				} else if (null != mweek && mweek.equals("星期日")) {
					rb[i].setBackgroundResource(R.drawable.tv_back_sunday_selector);
					rb[i].setText(text);
				}
			}
		}
		this.rb[rbChecked].setChecked(true);
		lv_tv_back_channles.requestFocus();

	}

	/**
	 * 获取媒体数据
	 */
	private void loadMediaFromXml(final String url, final int menuType) {

		String encodeUrl = url;
		PullXmlParserThread parser = new PullXmlParserThread(0,
				new PullXmlParserCallback() {

					@Override
					public void startDocument() {
						switch (menuType) {
						case MenuType.MEDIA_MENU:
							medialist = new ArrayList<MediaInfo>();
							break;
						case MenuType.VERSION_MENU:
							break;
						}
					}

					@Override
					public void startFlag(String nodeName,
							Map<String, String> attribute) {
						if (nodeName.equals("data")) {
							mdata = 1;
						} else if (nodeName.equals("url")) {
							murl = 1;
							switch (menuType) {
							case MenuType.MEDIA_MENU:
								mediainfo = new MediaInfo();
								mediainfo.setMediaurl(attribute.get("link"));
								mediainfo.setName(attribute.get("name"));
								break;
							case MenuType.VERSION_MENU:
								updateinfo = new UpdateInfo();
								updateinfo.setVersion(attribute.get("version"));
								updateinfo.setApkurl(attribute.get("link"));
								updateinfo.setDescription(attribute
										.get("description"));
								break;

							}
						}

					}

					@Override
					public void endFlag(String nodeName) {
						if (nodeName.equals("data")) {
							mdata = 0;
						} else if (nodeName.equals("url")) {
							murl = 0;
							switch (menuType) {
							case MenuType.MEDIA_MENU:
								medialist.add(mediainfo);
								mediainfo = null;
								break;
							case MenuType.VERSION_MENU:

								break;

							}

						}
					}

					@Override
					public void text(String text) {
					}

					@Override
					public void endDocument() {
						switch (menuType) {
						case MenuType.MEDIA_MENU:
							mPostion = 0;
							mHandler.sendEmptyMessage(WindowMessageID.PLAY);
							break;
						}
					}

					@Override
					public void haveError(PullXmlParserError error) {

					}

					protected int mdata = -1;// 开始
					protected int murl = -1;// 节目
					MediaInfo mediainfo = null;
				}, url);

		parser.start();

	}

	/**
	 * 设置节目列表
	 */
	protected void setProgramData(int type) {
		if (mChannelDatelist.size() > type) {
			String dataUrl = mChannelDatelist.get(type).getChannelDate_Url();
			loadDataFromXml(dataUrl, MenuType.REFRESH_MENU);
			Utils.loadingShow_tv(this, R.string.load_msg);
			Logger.d(TAG, "加载界面列表....loadingShow_tv");
		}
	}

	/**
	 * 刷新节目显示
	 */
	protected void notifyData(int postion) {
		// Log.d(TAG, "刷新节目显示执行了。。。。。");
		tv_back_current_channel.setText(mchannellist.get(0).getChanneName());
		tv_back_current_tv.setText(mNowProgramlist.get(postion)
				.getProgramName());
		if (mNowProgramlist.size() <= postion + 1) {
			tv_back_next_tv.setText("以实际播放节目为准");
		} else {
			tv_back_next_tv.setText(mNowProgramlist.get(postion + 1)
					.getProgramName());
		}
		Utils.loadingShow_tv(this, R.string.load_msg);
		Logger.d(TAG, "刷新节目列表....loadingShow_tv");
		loadMediaFromXml(mNowProgramlist.get(postion).getProgramUrl(),
				MenuType.MEDIA_MENU);
	}

	/**
	 * @brief 窗口消息处理函数。
	 * @author joychang
	 * @param[in] msg 窗口消息。
	 */
	private void onMessage(final Message msg) {
		if (msg != null) {
			switch (msg.what) {
			case WindowMessageID.DATE:
				// 设置日期列表
				String programUrl = "";
				Utils.loadingShow_tv(this, R.string.load_msg);
				Logger.d(TAG, "设置日期列表....loadingShow_tv");
				if (rbChecked == 0 && !loadFlag) {
					setChannelDate();
					programUrl = mChannelDatelist.get(0).getChannelDate_Url();
					loadDataFromXml(programUrl, MenuType.PROGRAMINFO_MENU);
				} else {
					programUrl = mChannelDatelist.get(rbChecked)
							.getChannelDate_Url();
					loadDataFromXml(programUrl, MenuType.REFRESH_MENU);
				}
				// 设置节目页
				break;
			case WindowMessageID.CHANNEL:
				// 构造频道列表
				channelAdapter = new ChannelAdapter(TVBackActivity.this,
						mchannellist);
				lv_tv_back_channles.setAdapter(channelAdapter);
				if (mchannellist.size() > 0) {
					String dataUrl = mchannellist.get(0).getChanneUrl();
					tvbackname = mchannellist.get(0).getChanneName();
					tv_back_current_channel.setText(tvbackname);
					loadDataFromXml(dataUrl, MenuType.DATE_MENU);
				}
				break;
			case WindowMessageID.PROGRAM:
				// 刷新当前显示节目
				mApp.setOnPlay(rbChecked
						+ mNowProgramlist.get(mProgramPostion).getTime()
						+ mNowProgramlist.get(mProgramPostion).getProgramName());
				programAdapter = new ProgramAdapter(TVBackActivity.this,
						mNowProgramlist);
				lv_tv_back_videos.setAdapter(programAdapter);
				if (!loadFlag) {
					notifyData(0);
				}
				loadFlag = true;
				break;
			case WindowMessageID.REFRESH:
				// 刷新节目
				programAdapter = new ProgramAdapter(TVBackActivity.this,
						mProgramlist);
				lv_tv_back_videos.setAdapter(programAdapter);
				Utils.loadingClose_Tv();
				break;
			case WindowMessageID.PLAY:
				// 播放节目
				ISCNTV = false;
				if (null != medialist && medialist.size() > 0) {
					ISCNTV = true;
					String mediaurl = medialist.get(mPostion).getMediaurl();
					vv.setVideoPath(mediaurl);
				} else if (mNowProgramlist.size() > mProgramPostion + 1) {
					mProgramPostion = mProgramPostion + 1;
					loadMediaFromXml(mNowProgramlist.get(mProgramPostion)
							.getProgramUrl(), MenuType.MEDIA_MENU);
				} else {
					vv.stopPlayback();
				}
				break;
			case WindowMessageID.ERROR:
				Toast.makeText(TVBackActivity.this,
						getString(R.string.tvback_str_data_loading_error),
						Toast.LENGTH_LONG).show();
				Utils.loadingClose_Tv();
				break;

			case WindowMessageID.HIDE_CONTROLER:
				hideController();
				break;
			case WindowMessageID.PROGRESSBAR_VISIBLE:
				rl_ProgressBar.setVisibility(View.VISIBLE);
				// rl_ProgressBar.bringToFront();
				break;
			case WindowMessageID.PROGRESSBAR_GONE:
				rl_ProgressBar.setVisibility(View.GONE);
				break;
			case WindowMessageID.PROGRESSBAR_PROGRESS_RESET:
				// rl_ProgressBar.setProgress(0);
				break;
			}
		}
		Logger.d(TAG, "_onMessage() end");
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 调用窗口消息处理函数
			onMessage(msg);
		}
	};

	private Handler mProgressHandler = new Handler();

	// show出控制栏
	private void showController() {
		controler.setAnimationStyle(R.style.AnimationFade);
		controler.showAtLocation(vv, Gravity.BOTTOM, 0, 0);
		controler.update(0, 0, LayoutParams.MATCH_PARENT, controlHeight / 2);
		isControllerShow = true;
		mHandler.sendEmptyMessageDelayed(WindowMessageID.HIDE_CONTROLER, TIME);
	}

	// 隐藏控制栏
	private void hideController() {
		if (null != controler && controler.isShowing()) {
			controler.dismiss();
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

	/**
	 * 设置vv的显示方式
	 * 
	 * @param flag
	 */
	private void setVideoScale(int flag) {
		switch (flag) {
		case SCREEN_FULL:
			back_video_blck.setVisibility(View.VISIBLE);
			screenWidth = vv.getWidth();
			screenHeight = vv.getHeight();
			FrameLayout.LayoutParams fl_full = new FrameLayout.LayoutParams(-1,
					-1, 17);
			fl_full.setMargins(0, 0, 0, 0);
			vv.setLayoutParams(fl_full);
			isFull = true;
			vv.setFocusable(true);
			vv.requestFocus();
			break;
		case SCREEN_DEFAULT:
			back_video_blck.setVisibility(View.INVISIBLE);
			FrameLayout.LayoutParams fl_def = new FrameLayout.LayoutParams(
					screenWidth, screenHeight);
			fl_def.setMargins(
					getResources().getDimensionPixelSize(R.dimen.sm_370),
					getResources().getDimensionPixelSize(R.dimen.sm_80), 0, 0);
			vv.setLayoutParams(fl_def);
			vv.setFocusable(false);
			vv.setClickable(false);
			isFull = false;
			lv_tv_back_videos.requestFocus();
			lv_tv_back_videos.setSelection(mProgramPostion);
			break;
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
		controlHeight = screenHeight / 4;
	}

	// vv准备好时设置seekBar
	private void seekBarUpdate() {
		isOnline = true;
		videoLength = vv.getDuration();
		if (null != medialist && medialist.size() > 1) {
			videoLength = DURATION_TIME * (medialist.size());
		}
		seekBar.setMax(videoLength);
		tv_totalTime.setText(Utils.toTime(videoLength));
		hideControllerDelay();
		mProgressHandler.post(updateThread);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (isFull && isProgress) {
				hideControllerDelay();
				if (ISCNTV) {
					if (pose > DURATION_TIME) {
						pose_start = (Integer) pose / DURATION_TIME;
						if (mPostion != pose_start
								&& medialist.size() > pose_start) {
							mPostion = pose_start;
							vv.setVideoPath(medialist.get(mPostion)
									.getMediaurl());
						}
					}
					int pose_end = pose % DURATION_TIME;
					vv.seekTo(pose_end);
				} else {
					vv.seekTo(pose);
				}
				// tv_currentTime.setText(Utils.toTime(pose));
				mProgressHandler.post(updateThread);
				isProgress = false;
			}
			return super.dispatchKeyEvent(event);
		}
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Logger.v(TAG,
					"weekdays.requestFocus()===" + weekdays.requestFocus());
			Logger.v(
					TAG,
					"lv_tv_back_videos.hasFocus()==="
							+ lv_tv_back_videos.hasFocus());
			long secondTime = System.currentTimeMillis();
			if (isFull) {
				hideController();
				setVideoScale(SCREEN_DEFAULT);
				return true;
			} else if (secondTime - firstTime > 3000) {
				weekdays.requestFocus();
				Utils.showToast(TVBackActivity.this, R.string.onbackpressed,
						R.drawable.toast_err);
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {
				finish();
				// System.exit(0);
			}
			// else if (null != lv_tv_back_videos
			// // && lv_tv_back_videos.hasFocus()) {
			// // weekdays.requestFocus();
			// // return true;
			// // }else {
			// // System.exit(0);
			// // }
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if (isFull) {
				if (!isControllerShow) {
					showController();
				}
				rewind();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if (isFull) {
				if (!isControllerShow) {
					showController();
				}
				fastForward();
			}
			break;
		case KeyEvent.KEYCODE_ENTER:
			if (isFull) {
				hideController();
				showMenu();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (isFull) {
				hideController();
				showMenu();
			}
		case KeyEvent.KEYCODE_MENU:
			if (isFull) {
				hideController();
				showMenu();
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			if (isFull) {
				if (!isControllerShow) {
					showController();
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if (isFull) {
				hideController();
			}
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	Runnable updateThread = new Runnable() {
		@Override
		public void run() {
			pose = vv.getCurrentPosition();
			if (ISCNTV)
				pose = mPostion * DURATION_TIME + pose;
			seekBar.setProgress(pose);
			mProgressHandler.postDelayed(updateThread, 1000);
			tv_currentTime.setText(Utils.toTime(pose));
			Log.d("joychang", "pose=" + pose);
		}

	};
	int pose = 0;
	int pose_start = 0;

	/**
	 * 快进
	 */
	private void fastForward() {
		if (videoLength - pose > 15000) {
			pose += 15000;
		} else {
			pose = videoLength - 15000;
		}
		isProgress = true;
		cancelDelayHide();
		mProgressHandler.removeCallbacks(updateThread);
		seekBar.setProgress(pose);
		tv_currentTime.setText(Utils.toTime(pose));
	}

	/**
	 * 快退
	 */

	private void rewind() {
		if (pose > 15000) {
			pose -= 15000;
		} else {
			pose = 0;
		}
		isProgress = true;
		cancelDelayHide();
		mProgressHandler.removeCallbacks(updateThread);
		seekBar.setProgress(pose);
		tv_currentTime.setText(Utils.toTime(pose));
	}

	private PopupWindow menupopupWindow;
	private ListView menulist;
	private boolean isMenuItemShow = false;

	/**
	 * 初始化menu
	 */
	public void onCreateMenu() {
		View menuView = View.inflate(this, R.layout.tv_controler_menu, null);
		menulist = (ListView) menuView
				.findViewById(R.id.tv_back_media_controler_menu);
		menupopupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		menupopupWindow.setOutsideTouchable(true);// 允许在外点击popu消失
		menupopupWindow.setTouchable(true);
		menupopupWindow.setFocusable(true);
		menulist.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP) {
					return false;
				}
				switch (keyCode) {
				case KeyEvent.KEYCODE_BACK:
					menupopupWindow.dismiss();
					break;
				}
				return false;
			}
		});
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mProgramPostion = position;
				mApp.setOnPlay(rbChecked
						+ mNowProgramlist.get(position).getTime()
						+ mNowProgramlist.get(position).getProgramName());
				programAdapter = new ProgramAdapter(TVBackActivity.this,
						mNowProgramlist);
				programAdapter.notifyDataSetChanged();
				lv_tv_back_videos.setAdapter(programAdapter);
				lv_tv_back_videos.setSelection(position);
				menulist.setAdapter(programAdapter);
				menulist.setSelection(position);
				String media_url = mNowProgramlist.get(position)
						.getProgramUrl();
				loadMediaFromXml(media_url, MenuType.MEDIA_MENU);
				Utils.loadingShow_tv(TVBackActivity.this, R.string.load_msg);
				Logger.d(TAG, "菜单点击....loadingShow_tv");
			}
		});
	}

	// 第一次打开menu
	private void showMenu() {
		menulist.setAdapter(programAdapter);
		menupopupWindow.setAnimationStyle(R.style.AnimationMenu);
		menupopupWindow.showAtLocation(vv, Gravity.TOP | Gravity.RIGHT, 0, 0);
		menupopupWindow.update(0, 0,
				getResources().getDimensionPixelSize(R.dimen.sm_500),
				LayoutParams.WRAP_CONTENT);
		isMenuItemShow = false;
	}

	// 刷新menu
	private void updataMenu() {
		menupopupWindow.showAtLocation(vv, Gravity.TOP | Gravity.RIGHT, 0, 0);
		menupopupWindow.update(0, 0,
				getResources().getDimensionPixelSize(R.dimen.sm_500),
				LayoutParams.WRAP_CONTENT);
	}

	// 隐藏menu
	private void hideMenu() {
		if (menupopupWindow.isShowing()) {
			menupopupWindow.dismiss();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// TODO Auto-generated method stub
		boolean result = mGestureDetector.onTouchEvent(event);
		// mPosition = mLastPos;
		DisplayMetrics screen = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(screen);
		if (mSurfaceYDisplayRange == 0)
			mSurfaceYDisplayRange = Math.min(screen.widthPixels,
					screen.heightPixels);
		float y_changed = event.getRawY() - mTouchY;
		float x_changed = event.getRawX() - mTouchX;

		float coef = Math.abs(y_changed / x_changed);
		float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
		Logger.i("joychang", "y_changed=" + y_changed + "...x_changed="
				+ x_changed + "...coef=" + coef + "...xgesturesize="
				+ xgesturesize);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Logger.i(TAG, "MotionEvent.ACTION_DOWN.......");
			mTouchAction = TOUCH_NONE;
			mTouchY = event.getRawY();
			mTouchX = event.getRawX();
			maxVolume = mAudioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			currentVolume = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			Lightness = Utils.GetLightness(TVBackActivity.this);
			break;
		case MotionEvent.ACTION_MOVE:
			Logger.i(TAG, "MotionEvent.ACTION_MOVE.......");
			if (coef > 2) {
				// 音量和亮度
				if (mTouchX > (screenWidth / 2)) {
					// 音量
					doVolumeTouch(y_changed);
				}
				if (mTouchX < (screenWidth / 2)) {
					doBrightnessTouch(y_changed);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			Logger.i(TAG, "MotionEvent.ACTION_UP.......");
			break;
		}
		return true;
	}

	// 调节音量
	private void doVolumeTouch(float y_changed) {
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_VOLUME)
			return;
		mTouchAction = TOUCH_VOLUME;
		int delta = -(int) ((y_changed / mSurfaceYDisplayRange) * maxVolume);
		int vol = (int) Math.min(Math.max(currentVolume + delta, 0), maxVolume);
		Logger.d("doVolumeTouch", "vol====" + vol + "...delta=" + delta);
		if (delta != 0) {
			if (vol < 1) {
				showVolumeToast(R.drawable.mv_ic_volume_mute, maxVolume, vol,
						true);
				// Logger.d("doVolumeTouch", "vol <1 ............."+vol);
			} else if (vol >= 1 && vol < maxVolume / 2) {
				showVolumeToast(R.drawable.mv_ic_volume_low, maxVolume, vol,
						true);
				// Logger.d("doVolumeTouch",
				// "vol>=1&&currentVolume<maxVolume/2 ............."+vol);
			} else if (vol >= maxVolume / 2) {
				showVolumeToast(R.drawable.mv_ic_volume_high, maxVolume, vol,
						true);
				// Logger.d("doVolumeTouch",
				// "vol >= maxVolume/2 ............."+vol);
			}
		}
	}

	// 调节亮度
	private void doBrightnessTouch(float y_changed) {
		// 屏幕亮度
		// float delta = -y_changed / mSurfaceYDisplayRange * 0.07f;
		if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_BRIGHTNESS)
			return;
		mTouchAction = TOUCH_BRIGHTNESS;
		float delta = -y_changed / mSurfaceYDisplayRange * 2f;
		int vol = (int) ((Math.min(Math.max(Lightness + delta, 0.01f) * 255,
				255)));
		if (delta != 0) {
			if (vol < 5) {
				showVolumeToast(R.drawable.mv_ic_brightness, 255, 0, false);
			} else {
				showVolumeToast(R.drawable.mv_ic_brightness, 255, vol, false);
			}
			Logger.d("doBrightnessTouch", "Lightness=" + Lightness + "....vol="
					+ vol + "...delta=" + delta + "....mSurfaceYDisplayRange="
					+ mSurfaceYDisplayRange);
		}
	}

	/**
	 * 显示音量的吐司
	 * 
	 * @param max
	 * @param current
	 */
	private void showVolumeToast(int resId, int max, int current,
			Boolean isVolume) {
		View view;
		if (!isVolume) {
			Utils.SetLightness(TVBackActivity.this, current);
		} else {
			mAudioManager
					.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
		}
		if (mToast == null) {
			mToast = new Toast(this);
			view = LayoutInflater.from(this).inflate(
					R.layout.mv_media_volume_controler, null);
			ImageView center_image = (ImageView) view
					.findViewById(R.id.center_image);
			// TextView textView = (TextView)
			// view.findViewById(R.id.center_info);
			ProgressBar center_progress = (ProgressBar) view
					.findViewById(R.id.center_progress);
			center_progress.setMax(max);
			center_progress.setProgress(current);
			center_image.setImageResource(resId);
			mToast.setView(view);
		} else {
			view = mToast.getView();
			// TextView textView = (TextView)
			// view.findViewById(R.id.center_info);
			ImageView center_image = (ImageView) view
					.findViewById(R.id.center_image);
			ProgressBar center_progress = (ProgressBar) view
					.findViewById(R.id.center_progress);
			center_progress.setMax(max);
			center_progress.setProgress(current);
			center_image.setImageResource(resId);
		}
		mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,
				0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}

	/**
	 * @class MenuType
	 * @brief 菜单类型
	 * @author joychang
	 */
	public class MenuType {
		/**
		 * @brief 电视列表。
		 */
		public final static int CHANNEL_MENU = 1;

		/**
		 * @brief 节目列表。
		 */
		public final static int PROGRAMINFO_MENU = 2;

		/**
		 * @brief 日期列表
		 */
		public final static int DATE_MENU = 3;

		/**
		 * @brief 媒体路径
		 */
		public final static int MEDIA_MENU = 4;
		/**
		 * @brief 刷新列表
		 */
		public final static int REFRESH_MENU = 5;
		/**
		 * @brief 版本升级
		 */
		public final static int VERSION_MENU = 6;

	}

	/**
	 * @class WindowMessageID
	 * @brief 内部消息ID定义类。
	 * @author joychang
	 */
	private class WindowMessageID {
		/**
		 * @brief 日期。
		 */
		public static final int DATE = 0x00000001;

		/**
		 * @brief 频道。
		 */
		public static final int CHANNEL = 0x00000002;

		/**
		 * @brief 节目。
		 */
		public static final int PROGRAM = 0x00000003;

		/**
		 * @brief 刷新数据。
		 */
		public static final int REFRESH = 0x00000005;

		/**
		 * @brief 请求出错。
		 */
		public final static int ERROR = 0x00000004;
		/**
		 * @brief 播放。
		 */
		public final static int PLAY = 0x00000006;
		/**
		 * @brief 显示进度条。
		 */
		public final static int PROGRESS_CHANGED = 0x00000007;
		/**
		 * @brief 隐藏进度条。
		 */
		public final static int HIDE_CONTROLER = 0x00000008;

		/**
		 * @brief 显示加载框进度
		 */
		public final static int PROGRESSBAR_VISIBLE = 0x00000009;
		/**
		 * 
		 * @brief 隐藏加载框进度
		 */
		public final static int PROGRESSBAR_GONE = 0x000000010;
		/**
		 * 
		 * @brief 重置加载框进度
		 */
		public final static int PROGRESSBAR_PROGRESS_RESET = 0x000000011;
	}

	private Toast mToast = null;
	private float Lightness;
	private float mTouchY, mTouchX;
	private int mSurfaceYDisplayRange;
	private int currentVolume;
	private int maxVolume;
	// Touch Events
	private static final int TOUCH_NONE = 0;
	private static final int TOUCH_VOLUME = 1;
	private static final int TOUCH_BRIGHTNESS = 2;
	private static final int TOUCH_SEEK = 3;
	private int mTouchAction;
	private boolean ISCNTV = false;
}
