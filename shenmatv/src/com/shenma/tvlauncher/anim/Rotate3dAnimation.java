package com.shenma.tvlauncher.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 自定义3D旋转动画
 * @author drowtram
 *
 */
public class Rotate3dAnimation extends Animation {
	
	// 开始角度 
	private final float mFromDegrees; 
	// 结束角度 
	private final float mToDegrees; 
	// 中心点 
	private final float mCenterX; 
	private final float mCenterY; 
	private final float mDepthZ; 
	// 是否需要扭曲 
	private final boolean mReverse; 
	// 摄像头 
	private Camera mCamera; 
	/**
	 * 构造方法
	 * @param fromDegrees 开始角度
	 * @param toDegrees 结束角度
	 * @param centerX
	 * @param centerY 中心点 
	 * @param depthZ
	 * @param reverse 是否需要扭曲
	 */
	public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse) { 
		mFromDegrees = fromDegrees; 
		mToDegrees = toDegrees; 
		mCenterX = centerX; 
		mCenterY = centerY; 
		mDepthZ = depthZ; 
		mReverse = reverse; 
	} 
	@Override
	public void initialize(int width, int height, int parentWidth,int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}
	/**
	 *  生成Transformation 
	 */
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees; 
		// 生成中间角度 
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime); 
		final float centerX = mCenterX; 
		final float centerY = mCenterY; 
		final Camera camera = mCamera; 
		final Matrix matrix = t.getMatrix(); 
		camera.save(); 
		if (mReverse) { 
		camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime); 
		} else { 
		camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime)); 
		} 
		camera.rotateY(degrees); 
		// 取得变换后的矩阵 
		camera.getMatrix(matrix); 
		camera.restore(); 
		matrix.preTranslate(-centerX, -centerY); 
		matrix.postTranslate(centerX, centerY); 
		
	}
}
