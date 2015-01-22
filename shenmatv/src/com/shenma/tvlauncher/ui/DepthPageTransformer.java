package com.shenma.tvlauncher.ui;

import com.shenma.tvlauncher.utils.Logger;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
/**
 * 设置viewpage动画效果
 * @author joychang
 *
 */
public class DepthPageTransformer implements PageTransformer {
	private static float MIN_SCALE = 0.75f;

	@Override
	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();
		if (position < -1) {
			view.setAlpha(0);
			Logger.i("joychang", "position < -1===="+position);	
		} else if (position <= 0) {
			view.setAlpha(1);
			view.setTranslationX(0);
			view.setScaleX(1);
			view.setScaleY(1);
			Logger.i("joychang", "position <= 0===="+position);	
		} else if (position <= 1) {
			view.setAlpha(1 - position);
			view.setTranslationX(pageWidth * -position);
			float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - Math.abs(position));
			view.setScaleX(scaleFactor);
			view.setScaleY(scaleFactor);
			Logger.i("joychang", "position <= 1===="+position);	
		} else {
			view.setAlpha(0);
			Logger.i("joychang", "position====="+position);	
		}
	}
}
