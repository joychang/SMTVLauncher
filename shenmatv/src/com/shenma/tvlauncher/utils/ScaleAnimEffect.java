package com.shenma.tvlauncher.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 放大动画
 * 
 * @author joychang
 * 
 */
public class ScaleAnimEffect {
	private long duration;
	private float fromAlpha;
	private float fromXScale;
	private float fromYScale;
	private float toAlpha;
	private float toXScale;
	private float toYScale;

	public Animation alphaAnimation(float paramFloat1, float paramFloat2,
			long paramLong1, long paramLong2) {
		AlphaAnimation localAlphaAnimation = new AlphaAnimation(paramFloat1,
				paramFloat2);
		localAlphaAnimation.setDuration(paramLong1);//设置动画时间
		localAlphaAnimation.setStartOffset(paramLong2);//设置启动时间
		AccelerateInterpolator localAccelerateInterpolator = new AccelerateInterpolator();
		localAlphaAnimation.setInterpolator(localAccelerateInterpolator);
		return localAlphaAnimation;
	}
	/**
	 * 平移
	 * @param fromXDelta
	 * @param toXDelta
	 * @param fromYDelta
	 * @param toYDelta
	 * @return
	 */
	public Animation translAnimation(float fromXDelta, float toXDelta,
			float fromYDelta, float toYDelta) {
		TranslateAnimation localTranslAnimation = new TranslateAnimation(
				fromXDelta, toXDelta, fromYDelta, toYDelta);
		localTranslAnimation.setDuration(400L);
		localTranslAnimation.setFillAfter(true);
		localTranslAnimation.setInterpolator(new DecelerateInterpolator());//new DecelerateInterpolator(0.2f)减速AccelerateDecelerateInterpolator中间加速
		return localTranslAnimation;

	}

	/**
	 * 放大动画
	 * @return
	 */
	public Animation ScaleAnimation(float fromXScale,float toXScale,float fromYScale,float toYScale){
		ScaleAnimation localScaleAnimation = new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale,
				1, 0.5F, 1, 0.5f);
		localScaleAnimation.setFillAfter(true);//设置动画后是否保存状态
		AccelerateInterpolator localAccelerateInterpolator = new AccelerateInterpolator();
		localScaleAnimation.setInterpolator(localAccelerateInterpolator);//设置动画启动后几秒开始
		localScaleAnimation.setDuration(400L);
		return localScaleAnimation;
	}

	public Animation createAnimation() {
		float f1 = this.fromXScale;
		float f2 = this.toXScale;
		float f3 = this.fromYScale;
		float f4 = this.toYScale;
		int i = 1;
		float f5 = 0.5F;
		ScaleAnimation localScaleAnimation = new ScaleAnimation(f1, f2, f3, f4,
				1, 0.5F, i, f5);
		localScaleAnimation.setFillAfter(true);//设置动画后是否保存状态
		AccelerateInterpolator localAccelerateInterpolator = new AccelerateInterpolator();
		localScaleAnimation.setInterpolator(localAccelerateInterpolator);//设置动画启动后几秒开始
		long l = this.duration;
		localScaleAnimation.setDuration(l);
		return localScaleAnimation;
	}
	
	public Animation createAlphaAnimation(){
		AlphaAnimation mAnimation = new AlphaAnimation(fromAlpha, toAlpha);
		mAnimation.setFillAfter(true);
		mAnimation.setDuration(duration);
		return mAnimation;
	}

	public void setAttributs(float paramFloat1, float paramFloat2,
			float paramFloat3, float paramFloat4, long paramLong) {
		this.fromXScale = paramFloat1;
		this.fromYScale = paramFloat3;
		this.toXScale = paramFloat2;
		this.toYScale = paramFloat4;
		this.duration = paramLong;
	}
	
	public void setAlphaAttributs(float fromAlpha, float toAlpha, long duration){
		this.duration = duration;
		this.fromAlpha = fromAlpha;
		this.toAlpha = toAlpha;
	}
}