package com.shenma.tvlauncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shenma.tvlauncher.dao.bean.AppInfo;
import com.shenma.tvlauncher.db.DatabaseOperator;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Logger;
import com.umeng.analytics.MobclickAgent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout.LayoutParams;
/**
 * 本地应用
 * @author joychang
 *
 */
public class AppManageActivity extends BaseActivity implements OnItemClickListener, OnItemSelectedListener{

	private final int SHOW_GRIDVIEW_ITEM = 1001;
	private static final int REMOVE_FROM_FAV = 1002;
	private static final int ADD_TO_FAV = 1003;
	private static final String TAG = "AppManageActivity";
	private GridView my_app_manage_gv;
	private List<AppInfo> applist;
	private List<String> sysapplst;
	private ListView menulist;
	private PopupWindow menupopupWindow;
	private String lastFocusPkgName;
	private ImageView whiteBorder;// 白色背景框
	private int mIndex = -1;//当前选中的焦点index
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_GRIDVIEW_ITEM:
				my_app_manage_gv.setAdapter(new AppAdapter());
				my_app_manage_gv.setOnItemSelectedListener(AppManageActivity.this);
				if (mIndex == -1 || mIndex == 0) {
					my_app_manage_gv.setSelection(0);
					lastFocusPkgName = applist.get(0).getApppack();
					isVis = applist.get(0).isLove() ? REMOVE_FROM_FAV:ADD_TO_FAV;
				} else if (mIndex != -1 && mIndex < applist.size()) {
					my_app_manage_gv.setSelection(mIndex);
				} else if (mIndex != -1 && mIndex == applist.size()) {
					my_app_manage_gv.setSelection(mIndex - 1);
				} 
				break;
			}
		};
	};
	private LinearLayout my_app_layout;
	private BroadcastReceiver mAppUninstallReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {// 监听到卸载应用的广播
			// String packageName = intent.getData().getSchemeSpecificPart();
			// 获取到卸载的应用包名
				if (my_app_manage_gv != null) {
					queryAllAppInstalled();// 更新应用列表
				}
			}
		}

	};
	private DatabaseOperator dbtools;
	private int isVis;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appmgr);
		initView();
		initData();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void initView() {
		findViewById();
		setListener();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				queryAllAppInstalled();//查询所有已安装的app
				//查询出所有系统的app
				queryAllSystemApp();
			}
		}).start();
		onCreateMenu();
		//注册广播
		IntentFilter mAppFilter = new IntentFilter();
		mAppFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mAppFilter.addDataScheme("package");
		registerReceiver(mAppUninstallReceiver,mAppFilter);
		//init whiteBorder ---------------
		ImageView image = (ImageView) findViewById(R.id.blue_border);
		this.whiteBorder = image;
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.sm_164), getResources().getDimensionPixelSize(R.dimen.sm_144));
	    localLayoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.sm_170);
	    localLayoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.sm_150);
	    this.whiteBorder.setLayoutParams(localLayoutParams);
	    this.whiteBorder.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void loadViewLayout() {}

	@Override
	protected void findViewById() {
		my_app_manage_gv = (GridView) findViewById(R.id.my_app_manage_gv);
		my_app_layout = (LinearLayout) findViewById(R.id.my_app_layout);
		my_app_layout.setBackgroundResource(R.drawable.video_details_bg);
		dbtools = new DatabaseOperator(this);
	}

	@Override
	protected void setListener() {
		my_app_manage_gv.setOnItemClickListener(this);
		my_app_manage_gv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mIndex = position;
				lastFocusPkgName = applist.get(position).getApppack();
				isVis = applist.get(position).isLove() ? REMOVE_FROM_FAV:ADD_TO_FAV;
				showMenu(isVis);
				return true;
			}
		});
	}
	
	/**
	 * 背景框平移
	 * @param paramInt1 边框的宽
	 * @param paramInt2 边框的高
	 * @param paramFloat1 边框相对左边的边距
	 * @param paramFloat2 边框相对上边的边距
	 */
	private void flyWhiteBorder(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
	  {
	    if (this.whiteBorder == null)
	      return;
	    int i = this.whiteBorder.getWidth();
	    int j = this.whiteBorder.getHeight();
	    ViewPropertyAnimator localViewPropertyAnimator1 = this.whiteBorder.animate();
	    localViewPropertyAnimator1.setDuration(150L);
	    float f1 = paramInt1;
	    float f2 = i;
	    float f3 = f1 / f2;
	    localViewPropertyAnimator1.scaleX(f3);
	    float f4 = paramInt2;
	    float f5 = j;
	    float f6 = f4 / f5;
	    localViewPropertyAnimator1.scaleY(f6);
	    localViewPropertyAnimator1.x(paramFloat1);
	    localViewPropertyAnimator1.y(paramFloat2);
	    localViewPropertyAnimator1.start();
	  }
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mAppUninstallReceiver);// 解除广播注册
		super.onDestroy();
	}
	
	/**
	 * 查询所有已安装的app
	 */
	private void queryAllAppInstalled() {
		//先查询数据库，获取所有常用的app
		List<String> lst = dbtools.queryAll();
		//再获取系统所有已安装的app
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> packLst = getPackageManager().queryIntentActivities(intent, 0);
		applist = new ArrayList<AppInfo>();
		ArrayList<AppInfo> templovLst = new ArrayList<AppInfo>();
		ArrayList<AppInfo> tempinsLst = new ArrayList<AppInfo>();
		ResolveInfo mInfo = null;
		for (ResolveInfo info:packLst) {//进行过滤，把不需要显示的app过滤掉
			if("com.shenma.tvlauncher".equals(info.activityInfo.packageName)){
				mInfo = info;
				break;
			}
		}
		packLst.remove(mInfo);//过滤掉本launcher
		//把所有常用的app遍历出来
		for (String pkgname : lst) {
			for (ResolveInfo info : packLst) {
				if(pkgname.equals(info.activityInfo.packageName)){
					AppInfo ai = new AppInfo();
					ai.setAppicon(info.loadIcon(getPackageManager()));
					ai.setAppname(info.loadLabel(getPackageManager()).toString());
					ai.setApppack(info.activityInfo.packageName);
					ai.setLove(true);
					templovLst.add(ai);
					mInfo = info;
					Logger.d("zhouchuan", info.activityInfo.packageName+"is exist");
				}
			}
			packLst.remove(mInfo);//移除掉常用的app
			Logger.d("zhouchuan", mInfo.activityInfo.packageName+"is removed");
		}
		for (ResolveInfo info:packLst) {
			AppInfo ai = new AppInfo();
			ai.setAppicon(info.loadIcon(getPackageManager()));
			ai.setAppname(info.loadLabel(getPackageManager()).toString());
			ai.setApppack(info.activityInfo.packageName);
			ai.setLove(false);
			tempinsLst.add(ai);
			Logger.d("zhouchuan", info.activityInfo.packageName+"is added");
		}
		applist.addAll(templovLst);
		applist.addAll(tempinsLst);
		// 显示gridview中的条目
		mHandler.sendEmptyMessage(SHOW_GRIDVIEW_ITEM);
	}
	
	
	private void queryAllSystemApp() {
		List<PackageInfo> packList = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		sysapplst = new ArrayList<String>(); 
		for (PackageInfo pack : packList) {
		 // 把系统的应用分离出来 
			if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {    
				continue;//第三方app
			}else if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
				continue;//系统app升级后成第三方app
			}
			else {
				//系统app
				sysapplst.add(pack.packageName);
			}
		}
	} 
	
	/**
	 * 查询一个app是否是系统app
	 * @param packname
	 * @return true 是系统app  false  不是系统app
	 */
	private boolean checkIsSysApp(String packname){
		for (String pkgname : sysapplst) {
			if(packname.equals(pkgname)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 初始化menu
	 */
	public void onCreateMenu() {
		View menuView = View.inflate(this, R.layout.controler_menu, null);
		menulist = (ListView) menuView.findViewById(R.id.media_controler_menu);
		menupopupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
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
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				// 添加到常用/从常用中删除
				case 0:
					if(isVis == REMOVE_FROM_FAV){
						//从常用中删除
						dbtools.deleteApp(lastFocusPkgName);
					}else {
						//添加到常用
						dbtools.addApp(lastFocusPkgName);
					}
					hideMenu();
					queryAllAppInstalled();
					break;
				// 卸载
				case 1:
					if(lastFocusPkgName!=null){
						if(checkIsSysApp(lastFocusPkgName)){
							Toast.makeText(AppManageActivity.this, "系统应用,不能卸载!", Toast.LENGTH_SHORT).show();
							return;
						}else {
							Uri packageURI = Uri.parse("package:"+ lastFocusPkgName);
							Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
							startActivity(uninstallIntent);
						}
					}
					hideMenu();
					break;
				}
			}

		});
	}
	
	
	/**
	 * menu主界面目录
	 * @return 
	 * 
	 * @return
	 */
	private ArrayList<String> getData(int type) {
		ArrayList<String> list = new ArrayList<String>();
		if (type == REMOVE_FROM_FAV) {
			list.add("从常用中删除");
			list.add("卸载");
		} else if (type == ADD_TO_FAV) {
			list.add("添加到常用");
			list.add("卸载");
		}
		
		return list;
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			hideMenu();
		}else if (keyCode == KeyEvent.KEYCODE_MENU) {
			showMenu(isVis);
		}else if (keyCode == 1192) {
			showMenu(isVis);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	// 第一次打开menu
	private void showMenu(int type) {
		menulist.setAdapter(new MyAdapter(this, getData(type)));
		menupopupWindow.setAnimationStyle(R.style.AnimationMenu);
		menupopupWindow.showAtLocation(my_app_layout, Gravity.TOP | Gravity.RIGHT, 0, 0);
		menupopupWindow.update(0, 0, getResources().getDimensionPixelSize(R.dimen.sm_350),mHeight);
	}

	// 隐藏menu
	private void hideMenu() {
		if (menupopupWindow.isShowing()) {
			menupopupWindow.dismiss();
		}
	}
	
	class AppAdapter extends BaseAdapter{

		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return applist.size();
		}

		@Override
		public Object getItem(int position) {
			return applist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = View.inflate(AppManageActivity.this, R.layout.myapp_gridview_item, null);
				viewHolder.myapp_gridview_item_iv = (ImageView) convertView.findViewById(R.id.myapp_gridview_item_iv);
				viewHolder.myapp_gridview_item_love_iv = (ImageView) convertView.findViewById(R.id.myapp_gridview_item_love_iv);
				viewHolder.myapp_gridview_item_tv = (TextView) convertView.findViewById(R.id.myapp_gridview_item_tv);
				viewHolder.myapp_gridview_item_flag_tv = (TextView) convertView.findViewById(R.id.myapp_gridview_item_flag_tv);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lp.setMargins(0, -10, 0, 0);
				viewHolder.myapp_gridview_item_tv.setLayoutParams(lp);
				RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.myapp_rl);
				rl.setLayoutParams(lp);
				convertView.setTag(viewHolder);
			}else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			AppInfo aif = applist.get(position);
			viewHolder.myapp_gridview_item_iv.setImageDrawable(aif.getAppicon());
			viewHolder.myapp_gridview_item_tv.setText(aif.getAppname());
			viewHolder.myapp_gridview_item_flag_tv.setText(aif.getApppack());
			Map<String, String> m_value = new HashMap<String, String>();
			m_value.put("LocaAppName", aif.getAppname());
			MobclickAgent.onEvent(AppManageActivity.this, "LOCA_APP_NAME", m_value);
			if(aif.isLove()){
				viewHolder.myapp_gridview_item_love_iv.setVisibility(View.VISIBLE);
			}else {
				viewHolder.myapp_gridview_item_love_iv.setVisibility(View.GONE);
			}
			return convertView;
		}
		
	}
	class ViewHolder{
		public ImageView myapp_gridview_item_iv,myapp_gridview_item_love_iv;
		public TextView myapp_gridview_item_tv,myapp_gridview_item_flag_tv;
	}
	
	/**
	 * 自定义目录显示数据列表
	 * 
	 * @return
	 */
	class MyAdapter extends BaseAdapter {
		private Context context;
		ArrayList<String> mylist;

		public MyAdapter(Context context, ArrayList<String> mylist) {
			this.context = context;
			this.mylist = mylist;
		}

		@Override
		public int getCount() {
			return mylist.size();
		}

		@Override
		public Object getItem(int position) {
			return mylist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = LayoutInflater.from(context).inflate(R.layout.mv_controler_menu_item, null);
			TextView tv = (TextView) v.findViewById(R.id.tv_menu_item);
			tv.setText(mylist.get(position));
			return v;
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//----------------------------
		AppManageActivity.this.whiteBorder.setVisibility(View.VISIBLE);
		AppManageActivity mainActivity = AppManageActivity.this;
		float f1 = view.getX() + getResources().getDimensionPixelSize(R.dimen.sm_170);
        float f2 = view.getY() + getResources().getDimensionPixelSize(R.dimen.sm_150);
        mainActivity.flyWhiteBorder(getResources().getDimensionPixelSize(R.dimen.sm_164), getResources().getDimensionPixelSize(R.dimen.sm_144), f1, f2);
		//____________________________
		String packname = ((TextView)view.findViewById(R.id.myapp_gridview_item_flag_tv)).getText().toString();
		Intent intent = getPackageManager().getLaunchIntentForPackage(packname);
		startActivity(intent);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (view != null) {
			mIndex = position;
			lastFocusPkgName = applist.get(position).getApppack();
			isVis = applist.get(position).isLove() ? REMOVE_FROM_FAV:ADD_TO_FAV;
			//----------------------------
			AppManageActivity.this.whiteBorder.setVisibility(View.VISIBLE);
			AppManageActivity mainActivity = AppManageActivity.this;
			float f1 = view.getX() + getResources().getDimensionPixelSize(R.dimen.sm_170);
	        float f2 = view.getY() + getResources().getDimensionPixelSize(R.dimen.sm_150);
	        mainActivity.flyWhiteBorder(getResources().getDimensionPixelSize(R.dimen.sm_164), getResources().getDimensionPixelSize(R.dimen.sm_144), f1, f2);
			//____________________________
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		lastFocusPkgName = null;
		mIndex = -1;
		isVis = -1;
		AppManageActivity.this.whiteBorder.setVisibility(View.INVISIBLE);
	}
}
