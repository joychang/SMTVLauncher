package com.shenma.tvlauncher;

import java.io.UnsupportedEncodingException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.utils.Utils;
import com.shenma.tvlauncher.view.ExitDialog;
import com.shenma.tvlauncher.view.ExitFullDialog;

/**
 * @Description 基类
 * @author joychang
 * 
 */
public abstract class BaseActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏
		context = BaseActivity.this;
		sp = getSharedPreferences("shenma", MODE_PRIVATE);
		breathingAnimation = AnimationUtils.loadAnimation(context,
				R.anim.breathing);
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		mWidth = dm.widthPixels;
		mHeight = dm.heightPixels;
		Logger.d(TAG, "mWidth="+mWidth+"..mHeight="+mHeight);
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2)
				+ Math.pow(dm.heightPixels, 2));
		screenSize = diagonalPixels / (160 * dm.density);
		from = Utils.getFormInfo(BaseActivity.class, 0);
		devicetype = Utils.getFormInfo(BaseActivity.class, 3);
		version = Utils.getVersion(this);
	    try {
			params = Utils.encode("version=" + version + "&from=" + from + "&devicetype="+ devicetype, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 初始化
	 */
	protected abstract void initView();

	/**
	 * 加载布局文件
	 */
	protected abstract void loadViewLayout();

	/**
	 * 初始化控件
	 */
	protected abstract void findViewById();

	/**
	 * 设置监听器
	 */
	protected abstract void setListener();

	@Override
	protected void onStart() {
		super.onStart();
		Logger.i(TAG, "BaseActivity... onStart");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "BaseActivity... onDestroy");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Logger.i(TAG, "BaseActivity... onPause");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "BaseActivity... onResume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Logger.i(TAG, "BaseActivity... onStop");

	}
	
	protected void openActivity(Class<?> pClass) {
		openActivity(pClass, null);
	}

	protected void openActivity(Class<?> pClass, Bundle pBundle) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	protected void openActivity(String pAction) {
		openActivity(pAction, null);
	}

	protected void openActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	/**
	 * 联网Dialog
	 * 
	 * @param context
	 */
	protected void showNetDialog(Context context) {
		if (netDialog == null) {
			ExitDialog exitDialog = new ExitDialog(context);
			netDialog = exitDialog;
			// netDialog.setMsgLineVisible();
		}
		netDialog.setIsNet(true);
		netDialog.setTitle("网络未连接");
		netDialog.setMessage("当前网络未连接，海量电影、电视剧等无法观看哦，现在设置网络？");
		netDialog.setConfirm("好，现在设置");
		netDialog.setCancle("算了，现在不管");
		netDialog.setCancelable(true);
		netDialog.setCanceledOnTouchOutside(false);
		netDialog.show();
	}

	/**
	 * 退出Dialog
	 * 
	 */
	protected void showExitDialog() {
		if (exitfullDialog == null) {
			ExitFullDialog.Builder builder = new ExitFullDialog.Builder(context);
			builder.setNeutralButton(R.string.exitdialog_back, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.setPositiveButton(R.string.exitdialog_out, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					BaseActivity.this.finish();
					dialog.dismiss();
				}
			});
			exitfullDialog = builder.create();
			// netDialog.setMsgLineVisible();
			Logger.v("joychang", "exitDialog == null");
		}
		exitfullDialog.setCancelable(true);
		exitfullDialog.setCanceledOnTouchOutside(false);
		exitfullDialog.show();
	}

	/**
	 * 退出Dialog
	 * 
	 * @param context
	 * @param title
	 */
	protected void showExitDialog(String title, Context context) {
		if (exitDialog == null) {
			ExitDialog eDialog = new ExitDialog(context);
			exitDialog = eDialog;
			// netDialog.setMsgLineVisible();
			Logger.v("joychang", "exitDialog == null");
		}
		exitDialog.setIsNet(false);
		exitDialog.setTitle(title);
		exitDialog.setMessage(exit);
		exitDialog.setConfirm("退出，真的不看了");
		exitDialog.setCancle("返回，还想再看会儿");
		exitDialog.setCancelable(true);
		exitDialog.setCanceledOnTouchOutside(false);
		exitDialog.show();
	}
	/**
	 * 显示音量的吐司
	 * 
	 * @param max
	 * @param current
	 */
	private void showVolumeToast(int resId, int max, int current) {
		View view;
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, current, 0);
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
		mToast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
		mToast.setDuration(Toast.LENGTH_SHORT);
		mToast.show();
	}
	/**
	 * 应用崩溃toast
	 */
	protected void handleFatalError() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "发生了一点意外，程序终止！",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}

	/**
	 * 内存空间不足
	 */
	protected void handleOutmemoryError() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(BaseActivity.this, "内存空间不足！", Toast.LENGTH_SHORT)
						.show();
				finish();
			}
		});
	}

	/**
	 * Activity关闭和启动动画
	 */
	public void finish() {
		super.finish();
		// overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
	}

	private static final String TAG = "BaseActivity";
	protected Context context;
	protected SharedPreferences sp;
	protected Animation breathingAnimation;
	protected int mWidth;
	protected int mHeight;
	protected String from;
	protected String devicetype;
	protected String version;
	protected String params;
	/**
	 * @brief 退出对话框。
	 */
	protected ExitFullDialog exitfullDialog = null;
	
	protected ExitDialog exitDialog = null;

	/**
	 * @brief 联网对话框。
	 */
	protected ExitDialog netDialog = null;
	protected double screenSize;
	protected Toast mToast = null;
	protected AudioManager mAudioManager = null;
	//protected static String exit = "亲！感谢您支持神马视频，诚邀您参与用户体验改进计划，做一款你喜欢的视频聚合！意见反馈QQ群：375132069"; 
	protected static String exit = "亲爱的小伙伴们！为了保证您更好的观看体验，请及时升级到最新版本！感谢您的支持！意见反馈QQ群：375132069"; 
}
