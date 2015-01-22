package com.shenma.tvlauncher.view;

import com.shenma.tvlauncher.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 直播加载框
 * @author joychang
 *
 */
public class LiveLoadingDialog extends Dialog {
	private TextView tv_loading;

	public LiveLoadingDialog(Context paramContext) {
		super(paramContext, R.style.Exitdialog);
		View loadingView = LayoutInflater.from(paramContext).inflate(
				R.layout.live_loading_dialog, null);
		tv_loading = (TextView) loadingView
				.findViewById(R.id.live_loading_tv);
		ImageView iv_loading = (ImageView) loadingView
				.findViewById(R.id.live_loading_img);
		Animation localAnimation = AnimationUtils.loadAnimation(paramContext,
				R.anim.loading_rotate);
		iv_loading.startAnimation(localAnimation);
		setCancelable(true);
		setContentView(loadingView);
	}

	public void setLoadingMsg(int paramInt) {
		this.tv_loading.setText(paramInt);
	}

	public void setLoadingMsg(String paramString) {
		this.tv_loading.setText(paramString);
	}

	public void setMsgGone() {
		this.tv_loading.setVisibility(View.VISIBLE);
	}
}