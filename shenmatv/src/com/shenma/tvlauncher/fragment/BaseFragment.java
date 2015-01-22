package com.shenma.tvlauncher.fragment;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import com.shenma.tvlauncher.HomeActivity;
import com.shenma.tvlauncher.utils.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public abstract class BaseFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = getActivity();
		home = (HomeActivity) getActivity();
		//from = home.homefrom;
//		version = Utils.getVersion(home);
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm=new DisplayMetrics();  
	    manager.getDefaultDisplay().getMetrics(dm);  
	    mWidth=dm.widthPixels;  
	    mHeight=dm.heightPixels;  
	    params = home.homeParams;
//	    double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2)+Math.pow(dm.heightPixels, 2)); 
//	    screenSize = diagonalPixels/(160*dm.density);
	    ISTV = home.getIsTV();
	    
//	    devicetype = ISTV?"TV":"MOBILE";
//	    try {
//			params = Utils.encode("version="+version+"&from="+from+"&devicetype="+devicetype, "utf-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
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
	
	protected Context context;
	protected HomeActivity home;
	//protected String from;
//	protected String devicetype;
//	protected String version;
	protected String params;
	protected int mWidth;
	protected int mHeight;
//	protected double screenSize;
	protected static Boolean ISTV;
}
