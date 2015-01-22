package com.shenma.tvlauncher.application;



import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

/**
 * @Description 客户端主程序类 ，对应用程序进行初始化，完了话。
 * @author joychang
 * 
 */
public final class MyApplication extends Application {
	
	private final static String TAG = "MyApplication";
	private String onPlay;
	private static File cacheDir;
	public static class Config {
		public static final boolean DEVELOPER_MODE = false;//策略监控 true开启 false关闭
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter technologyfilter = new IntentFilter();
		technologyfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(mBroadcastReceiver, technologyfilter);
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(receiver, filter);
		//discCache缓存目录
		cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "UniversalImageLoader/Cache");
		//创建当前程序sd卡资源存放目录
		makedir();
		init();
		initImageLoader(getApplicationContext());
	}
	
	private void makedir(){
		File file = new File(Constant.PUBLIC_DIR);
		if(!file.exists()){
			//每次启动检测该目录是否存在，不存在则创建这个目录
			boolean flag = file.mkdir();
			Logger.d("zhouchuan", "Application class. 创建ShenMa文件夹"+(flag==true?"成功":"失败")+Constant.PUBLIC_DIR);
		}else {
			//如果存在，则删除该目录下的所有apk文件
			Utils.deleteAppApks(Constant.PUBLIC_DIR);
			Logger.d("zhouchuan", "Application class. ShenMa文件夹存在");
		}
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		unregisterReceiver(mBroadcastReceiver);
		unregisterReceiver(receiver);
	}
	
	public void setOnPlay(String onPlay){
		this.onPlay  = onPlay;
	}
	
	public String getOnPlay(){
		
		return onPlay;
	}
	
	public String getTechnology(){
		WifiManager mm = null;
		return technology;
	}
	/**
	 * 初始化工作
	 */
	@SuppressWarnings("unused")
	protected void init() {
        Log.d(TAG, "init() start");
        //
		if (Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().penaltyDialog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().penaltyDeath().build());
		}
		MyVolley.init(this);//初始化MyVolley
        //bug捕获和bug信息收集
        CrashHandler crashHandler = CrashHandler.getInstance();          
        crashHandler.init(this);
	}
	
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY - 2)//线程优先级
		.threadPoolSize(3)//线程3个
		.denyCacheImageMultipleSizesInMemory()//当你显示一个图像在一个小的ImageView后来你试图显示这个图像（从相同的URI）在一个大的，大尺寸的ImageView解码图像将被缓存在内存中为先前解码图像的小尺寸。
		.memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))//设置内存缓存大小
		.discCache(new UnlimitedDiscCache(cacheDir))//硬盘缓存目录
		.discCacheFileNameGenerator(new Md5FileNameGenerator())//生产缓存的名称
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	/**
	 * 电池信息监测广广播
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				int status = intent.getIntExtra("status", 0);
				int health = intent.getIntExtra("health", 0);
				boolean present = intent.getBooleanExtra("present", false);
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 0);
				int icon_small = intent.getIntExtra("icon-small", 0);
				int plugged = intent.getIntExtra("plugged", 0);
				int voltage = intent.getIntExtra("voltage", 0);
				int temperature = intent.getIntExtra("temperature", 0);
				technology = intent.getStringExtra("technology");
				
				String statusString = "";

				switch (status) {
				case BatteryManager.BATTERY_STATUS_UNKNOWN:
					statusString = "unknown";
					break;
				case BatteryManager.BATTERY_STATUS_CHARGING:
					statusString = "charging";
					break;
				case BatteryManager.BATTERY_STATUS_DISCHARGING:
					statusString = "discharging";
					break;
				case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
					statusString = "not charging";
					break;
				case BatteryManager.BATTERY_STATUS_FULL:
					statusString = "full";
					break;
				}

				String healthString = "";

				switch (health) {
				case BatteryManager.BATTERY_HEALTH_UNKNOWN:
					healthString = "unknown";
					break;
				case BatteryManager.BATTERY_HEALTH_GOOD:
					healthString = "good";
					break;
				case BatteryManager.BATTERY_HEALTH_OVERHEAT:
					healthString = "overheat";
					break;
				case BatteryManager.BATTERY_HEALTH_DEAD:
					healthString = "dead";
					break;
				case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
					healthString = "voltage";
					break;
				case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
					healthString = "unspecified failure";
					break;
				}

				String acString = "";
				switch (plugged) {
				case BatteryManager.BATTERY_PLUGGED_AC:
					acString = "plugged ac";
					break;
				case BatteryManager.BATTERY_PLUGGED_USB:
					acString = "plugged usb";
					break;
				}
//				Logger.v(TAG,"status="+statusString);
//				Logger.v(TAG,"health="+healthString);
//				Logger.v(TAG,"present="+String.valueOf(present));
//				Logger.v(TAG,"level="+String.valueOf(level));
//				Logger.v(TAG,"scale="+String.valueOf(scale));
//				Logger.v(TAG,"icon_small="+String.valueOf(icon_small));
//				Logger.v(TAG,"plugged="+acString);
//				Logger.v(TAG,"voltage="+String.valueOf(voltage));
//				Logger.v(TAG,"temperature="+String.valueOf(temperature));
				Logger.v(TAG,"technology="+technology);
			}
		}
	};
	
	/**
	 * 注册网络变动的广播接收
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
				if (networkInfo == null || !networkInfo.isConnected()) {
					Utils.showToast(context,
							getString(R.string.tvback_str_data_loading_error),
							R.drawable.toast_err);
				}
//				NetworkInfo ethNetworkInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
//				if (ethNetworkInfo != null && ethNetworkInfo.isConnected()) {
//					// 有线网
//					Utils.showToast(context,
//							getString(R.string.tvback_str_data_loading_error),
//							R.drawable.toast_err);
//				}
//				NetworkInfo wifiNetworkInfo = connectMgr
//						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//				if (wifiNetworkInfo != null && wifiNetworkInfo.isConnected()) {
//					// 无线网
//				}
			}
		}
	};
	/**
	 * 结束操作
	 */
    protected void _finish() {

        Log.d(TAG, "_finish() start");
        Log.d(TAG, "_finish() end");
    }
    /**
     * 系统内存过低，进行内存回收策略
     */
    @Override
    public void onLowMemory() {
    	// TODO Auto-generated method stub
    	super.onLowMemory();
    }
    protected static String technology = "";
}
