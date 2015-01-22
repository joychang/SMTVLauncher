package com.shenma.tvlauncher.fragment;

import java.lang.reflect.Field;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenma.tvlauncher.AboutActivity;
import com.shenma.tvlauncher.ClearActivity;
import com.shenma.tvlauncher.OtherActivity;
import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.SettingActActvity;
import com.shenma.tvlauncher.SettingPlayActivity;
import com.shenma.tvlauncher.SettingRemoteActivity;
import com.shenma.tvlauncher.SettingWallpaperActivity;
import com.shenma.tvlauncher.SettingWifiActivity;
import com.shenma.tvlauncher.utils.ScaleAnimEffect;
import com.shenma.tvlauncher.utils.Utils;
/**
 * @Description 设置
 * @author drowtram
 */
public class SettingFragment extends BaseFragment implements OnFocusChangeListener,OnClickListener{
	
	private View view;
	private FrameLayout[] st_fls;
	public ImageView[] st_typeLogs;
	private ImageView[] settingbgs;
	ScaleAnimEffect animEffect;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(container==null){
			return null;
		}
		if(null == view){
			view = inflater.inflate(R.layout.layout_setting, container,false);
			init();
		}else{
			((ViewGroup)view.getParent()).removeView(view);
		}
		return view;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 初始化相关信息
	 */
	void init(){
		loadViewLayout();
		findViewById();
		setListener();
		//st_fls[0].requestFocus();
	}
	
	protected void loadViewLayout(){
		st_fls = new FrameLayout[8];
		st_typeLogs = new ImageView[8];
		settingbgs = new ImageView[8];
		animEffect = new ScaleAnimEffect();
	}
	
	protected void findViewById(){
		st_fls[0] = (FrameLayout) view.findViewById(R.id.setting_fl_re_0);
		st_fls[1] = (FrameLayout) view.findViewById(R.id.setting_fl_re_1);
		st_fls[2] = (FrameLayout) view.findViewById(R.id.setting_fl_re_2);
		st_fls[3] = (FrameLayout) view.findViewById(R.id.setting_fl_re_3);
		st_fls[4] = (FrameLayout) view.findViewById(R.id.setting_fl_re_4);
		st_fls[5] = (FrameLayout) view.findViewById(R.id.setting_fl_re_5);
		st_fls[6] = (FrameLayout) view.findViewById(R.id.setting_fl_re_6);
		st_fls[7] = (FrameLayout) view.findViewById(R.id.setting_fl_re_7);
		
		st_typeLogs[0] = (ImageView) view.findViewById(R.id.setting_iv_remote);
		st_typeLogs[1] = (ImageView) view.findViewById(R.id.setting_iv_network);
		st_typeLogs[2] = (ImageView) view.findViewById(R.id.setting_iv_play);
		st_typeLogs[3] = (ImageView) view.findViewById(R.id.setting_iv_clean);
		st_typeLogs[4] = (ImageView) view.findViewById(R.id.setting_iv_wallpaper);
		st_typeLogs[5] = (ImageView) view.findViewById(R.id.setting_iv_other);
		st_typeLogs[6] = (ImageView) view.findViewById(R.id.setting_iv_act);
		st_typeLogs[7] = (ImageView) view.findViewById(R.id.setting_iv_about);
		
		
		settingbgs[0] = (ImageView) view.findViewById(R.id.setting_bg_0);
		settingbgs[1] = (ImageView) view.findViewById(R.id.setting_bg_1);
		settingbgs[2] = (ImageView) view.findViewById(R.id.setting_bg_2);
		settingbgs[3] = (ImageView) view.findViewById(R.id.setting_bg_3);
		settingbgs[4] = (ImageView) view.findViewById(R.id.setting_bg_4);
		settingbgs[5] = (ImageView) view.findViewById(R.id.setting_bg_5);
		settingbgs[6] = (ImageView) view.findViewById(R.id.setting_bg_6);
		settingbgs[7] = (ImageView) view.findViewById(R.id.setting_bg_7);
		
	}
	
	/**
	 * 飞框焦点动画
	 * @param paramInt
	 */
	private void flyAnimation(int paramInt){
		int[] location = new int[2];
		st_typeLogs[paramInt].getLocationOnScreen(location);
		int width = st_typeLogs[paramInt].getWidth();
		int height = st_typeLogs[paramInt].getHeight();
		float x = (float) location[0];
		float y = (float) location[1];
		if(mHeight>1000 && mWidth > 1000) {
			switch (paramInt) {
			case 0:
				width = width + 23;
				height = height + 23;
				x = 443+32;
				y = 336+4;
				break;
			case 1:
				width = width + 23;
				height = height + 23;
				x = 443+32;
				y = 632+4;
				break;
			case 2:
				width = width + 23;
				height = height + 23;
				x = 735+32;
				y = 336+4;
				break;
			case 3:
				width = width + 23;
				height = height + 23;
				x = 735+32;
				y = 632+4;
				break;
			case 4:
				width = width + 23;
				height = height + 23;
				x = 1028+32;
				y = 336+4;
				break;
			case 5:
				width = width + 23;
				height = height + 23;
				x = 1028+32;
				y = 632+4;
				break;
			case 6:
				width = width + 23;
				height = height + 23;
				x = 1320+32;
				y = 336+4;
				break;
			case 7:
				width = width + 23;
				height = height + 23;
				x = 1320+32;
				y = 632+4;
				break;
			}
		} else {
			switch (paramInt) {
			case 0:
				width = width + 15;
				height = height + 15;
				x = 260 + 35;
				y = 189 + 35 - 19;
				break;
			case 1:
				width = width + 15;
				height = height + 15;
				x = 260 + 35;
				y = 386 + 35 - 19;
				break;
			case 2:
				width = width + 15;
				height = height + 15;
				x = 455 + 35;
				y = 189 + 35 - 19;
				break;
			case 3:
				width = width + 15;
				height = height + 15;
				x = 455 + 35;
				y = 386 + 35 - 19;
				break;
			case 4:
				width = width + 15;
				height = height + 15;
				x = 650 + 35;
				y = 189 + 35 - 19;
				break;
			case 5:
				width = width + 15;
				height = height + 15;
				x = 650 + 35;
				y = 386 + 35 - 19;
				break;
			case 6:
				width = width + 15;
				height = height + 15;
				x = 845 + 35;
				y = 189 + 35 - 19;
				break;
			case 7:
				width = width + 15;
				height = height + 15;
				x = 845 + 35;
				y = 386 + 35 - 19;
				break;
			}
		}
		home.flyWhiteBorder(width, height, x, y);
}
	
	
	@Override
	public void onClick(View v) {
		Intent mIntent = new Intent();
		switch (v.getId()) {
		case R.id.setting_iv_remote:
			mIntent.setClass(context, SettingRemoteActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_network:
			mIntent.setClass(getActivity(), SettingWifiActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_play:
			mIntent.setClass(getActivity(), SettingPlayActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_clean:
			mIntent.setClass(getActivity(), ClearActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_wallpaper:
			mIntent.setClass(getActivity(), SettingWallpaperActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_other:
			mIntent.setClass(getActivity(), OtherActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_act:
			mIntent.setClass(getActivity(), SettingActActvity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_iv_about:
			mIntent.setClass(getActivity(), AboutActivity.class);
			startActivity(mIntent);
			break;
		}
		home.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int paramInt = -1;
		switch (v.getId()) {	
			case R.id.setting_iv_remote:
				paramInt = 0;
				break;
			case R.id.setting_iv_network:
				paramInt = 1;
				break;
			case R.id.setting_iv_play:
				paramInt = 2;
				break;
			case R.id.setting_iv_clean:
				paramInt = 3;
				break;
			case R.id.setting_iv_wallpaper:
				paramInt = 4;
				break;
			case R.id.setting_iv_other:
				paramInt = 5;
				break;
			case R.id.setting_iv_act:
				paramInt = 6;
				break;
			case R.id.setting_iv_about:
				paramInt = 7;
				break;
			}
			if(hasFocus){
				showOnFocusAnimation(paramInt);
				if(null!=home.whiteBorder){
					home.whiteBorder.setVisibility(View.VISIBLE);
				}
				flyAnimation(paramInt);
				//白框动画
			}else{
				showLoseFocusAinimation(paramInt);
				//将白框隐藏
			}
	}
	
	/**
	 * joychang 设置获取焦点时icon放大凸起
	 * 
	 * @param paramInt
	 */
	private void showOnFocusAnimation(final int paramInt) {
		st_fls[paramInt].bringToFront();//将当前FrameLayout置为顶层
		float f1 = 1.0F;
		float f2 = 1.1F;
		this.animEffect.setAttributs(1.0F, 1.1F, f1, f2, 200L);
		Animation mAnimation = this.animEffect.createAnimation();
		mAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				//settingbgs[paramInt].startAnimation(animEffect.alphaAnimation(0.0F, 1.0F, 150L, 0L));
				settingbgs[paramInt].setVisibility(View.VISIBLE);
				//settingbgs[paramInt].bringToFront();
			}
		});
		st_typeLogs[paramInt].startAnimation(mAnimation);
	}
	
	/**
	 * 失去焦点缩小
	 * @param paramInt
	 */
	private void showLoseFocusAinimation(final int paramInt) {
		float f1 = 1.1F;
		float f2 = 1.0F;
		animEffect.setAttributs(1.1F, 1.0F, f1, f2, 200L);
		Animation mAnimation = this.animEffect.createAnimation();
		settingbgs[paramInt].setVisibility(View.GONE);
//		mAnimation.setAnimationListener(new AnimationListener() {
//			@Override
//			public void onAnimationStart(Animation animation) {}
//			@Override
//			public void onAnimationRepeat(Animation animation) {}
//			@Override
//			public void onAnimationEnd(Animation animation) {
//				settingbgs[paramInt].setVisibility(View.GONE);
//			}
//		});
		st_typeLogs[paramInt].startAnimation(mAnimation);
	}

	protected void setListener() {
		for(int i=0;i<st_typeLogs.length;i++){
			settingbgs[i].setVisibility(View.GONE);
			st_typeLogs[i].setOnClickListener(this);
			//if(ISTV){
				st_typeLogs[i].setOnFocusChangeListener(this);
			//}
		}
	}

}
