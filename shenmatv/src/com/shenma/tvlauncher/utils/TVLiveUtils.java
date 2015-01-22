package com.shenma.tvlauncher.utils;

import java.util.ArrayList;

import com.baidu.cyberplayer.core.BVideoView;

import android.content.Context;
import android.graphics.Rect;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
public class TVLiveUtils {

	/**
	 * 电视直播menu主界面目录
	 * 
	 * @return
	 */
	public static ArrayList<String> getData(int type) {
		ArrayList<String> list = new ArrayList<String>();
		if (type == 0) {
			list.add("视频解码");
			list.add("画面比例");
			list.add("偏好设置");
			list.add("切源超时设置");
		} else if (type == 1) {
			// 解码
			list.add("软解码");
			list.add("硬解码");
		} else if (type == 2) {
			//画面比例
			list.add("原始比例");
			list.add("4:3");
			list.add("16:9");
			list.add("默认全屏");
		} else if (type == 3) {
			//偏好设置
			list.add("上下键换台");
			list.add("上下键调节音量");
		} else if (type == 4) {
			//切源超时设置
			list.add("5秒");
			list.add("8秒");
			list.add("10秒");
			list.add("12秒");
			list.add("15秒");
		}
		return list;
	}
	
	
//	public static void selectScales(BVideoView mVV,int paramInt) {
//		if (mVV.getWindowToken() == null)
//			return;
//		Rect localRect = new Rect();
//		mVV.getWindowVisibleDisplayFrame(localRect);
//		int i1 = localRect.bottom;
//		int i2 = localRect.top;
//		double d1 = i1 - i2;
//		int i3 = localRect.right;
//		int i4 = localRect.left;
//		double d2 = i3 - i4;
//		String str2 = "diplay = " + d2 + ":" + d1;
//		//int i5 = Log.d(TAG, str2);
//		if (d1 <= 0.0D)
//			return;
//		if (d2 <= 0.0D)
//			return;
//		int mVideoHeight = mVV.getVideoHeight();
//		int mVideoWidth = mVV.getVideoHeight();
//		if (mVideoHeight <= 0.0D)
//			return;
//		if (mVideoWidth <= 0.0D)
//			return;
//		ViewGroup.LayoutParams localLayoutParams = mVV.getLayoutParams();
//		switch (paramInt) {
//		//原始
//		case 0:
//			double d3 = d2 / d1;
//			int i6 = mVideoWidth;
//			int i7 = mVideoHeight;
//			double d4 = i6 / i7;
//			if (d3 < d4){
//				int i24 = (int) d2;
//				localLayoutParams.width = i24;
//				double d7 = mVideoHeight * d2;
//				double d8 = mVideoWidth;
//				int i25 = (int) (d7 / d8);
//				localLayoutParams.height = i25;
//			}else{
//				int i8 = (int) d1;
//				localLayoutParams.height = i8;
//				double d5 = mVideoWidth * d1;
//				double d6 = mVideoHeight;
//				int i9 = (int) (d5 / d6);
//				localLayoutParams.width = i9;	
//			}
//			mVV.setLayoutParams(localLayoutParams);
//			return;
//			//4:3
//		case 1:
//			if (d2 / d1 >= 1.333333333333333D) {
//				int i12 = (int) d1;
//				localLayoutParams.height = i12;
//				int i13 = (int) (4.0D * d1 / 3.0D);
//				localLayoutParams.width = i13;
//			}else{
//				int i16 = (int) d2;
//				localLayoutParams.width = i16;
//				int i17 = (int) (3.0D * d2 / 4.0D);
//				localLayoutParams.height = i17;
//			}
//			mVV.setLayoutParams(localLayoutParams);
//			return;
//			//16：9
//		case 2:
//			if (d2 / d1 >= 1.777777777777778D) {
//				int i18 = (int) d1;
//				localLayoutParams.height = i18;
//				int i19 = (int) (16.0D * d1 / 9.0D);
//				localLayoutParams.width = i19;
//			}else{
//				int i22 = (int) d2;
//				localLayoutParams.width = i22;
//				int i23 = (int) (9.0D * d2 / 16.0D);
//				localLayoutParams.height = i23;
//			}
//			mVV.setLayoutParams(localLayoutParams);
//			return;
//			//全屏
//		case 3:
//			int i24 = (int) d2;
//			localLayoutParams.width = i24;
//			double d7 = mVideoHeight * d2;
//			double d8 = mVideoWidth;
//			int i25 = (int) (d7 / d8);
//			localLayoutParams.height = i25;
//			mVV.setLayoutParams(localLayoutParams);
//			return;
//		}
//	}
	public static void selectScales(Context context,BVideoView mVV,int paramInt,int mWidth,int mHeight) {
		if (mVV.getWindowToken() == null)
			return;
		int dw = mWidth;
		int dh = mHeight;
		Rect localRect = new Rect();
		mVV.getWindowVisibleDisplayFrame(localRect);
		int i1 = localRect.bottom;
		int i2 = localRect.top;
		double d1 = i1 - i2;
		int i3 = localRect.right;
		int i4 = localRect.left;
		double d2 = i3 - i4;
		String str2 = "diplay = " + d2 + ":" + d1;
		//int i5 = Log.d(TAG, str2);
		if (d1 <= 0.0D)
			return;
		if (d2 <= 0.0D)
			return;
		int mVideoHeight = mVV.getVideoHeight();
		int mVideoWidth = mVV.getVideoHeight();
		if (mVideoHeight <= 0.0D)
			return;
		if (mVideoWidth <= 0.0D)
			return;
		android.view.ViewGroup.LayoutParams localLayoutParams =  mVV.getLayoutParams();
		switch (paramInt) {
		//原始
		case 0:
			double d3 = d2 / d1;
			int i6 = mVideoWidth;
			int i7 = mVideoHeight;
			double d4 = i6 / i7;
			if (d3 < d4){
				int i24 = (int) d2;
				localLayoutParams.width = i24;
				double d7 = mVideoHeight * d2;
				double d8 = mVideoWidth;
				int i25 = (int) (d7 / d8);
				localLayoutParams.height = i25;
			}else{
				int i8 = (int) d1;
				localLayoutParams.height = i8;
				double d5 = mVideoWidth * d1;
				double d6 = mVideoHeight;
				int i9 = (int) (d5 / d6);
				localLayoutParams.width = i9;	
			}
			mVV.setLayoutParams(localLayoutParams);
			return;
			//4:3
		case 1:
			if (d2 / d1 >= 1.333333333333333D) {
				int i12 = (int) d1;
				localLayoutParams.height = i12;
				int i13 = (int) (4.0D * d1 / 3.0D);
				localLayoutParams.width = i13;
			}else{
				int i16 = (int) d2;
				localLayoutParams.width = i16;
				int i17 = (int) (3.0D * d2 / 4.0D);
				localLayoutParams.height = i17;
			}
			mVV.setLayoutParams(localLayoutParams);
			return;
			//16：9
		case 2:
			if (d2 / d1 >= 1.777777777777778D) {
			int i18 = (int) d1;
			localLayoutParams.height = i18;
			int i19 = (int) (16.0D * d1 / 9.0D);
			localLayoutParams.width = i19;
		}else{
			int i22 = (int) d2;
			localLayoutParams.width = i22;
			int i23 = (int) (9.0D * d2 / 16.0D);
			localLayoutParams.height = i23;
		}
		mVV.setLayoutParams(localLayoutParams);
			return;
			//全屏
		case 3:
			int i24 = (int) d2;
			localLayoutParams.width = i24;
			//double d7 = mVideoHeight * d2;
			//double d8 = mVideoWidth;
			//int i25 = (int) (d7 / d8);
			localLayoutParams.height = mHeight+120;
			mVV.setLayoutParams(localLayoutParams);
			return;
		}
	}
	/**
	 * letv Base64加密
	 * @param str
	 * @param port
	 * @return
	 */
	public static String getBase64Code(String str,long port){
		byte[]  b = Base64.encode(str.getBytes(), Base64.DEFAULT);
		return "http://127.0.0.1:" + port + "/play?enc=base64&url="+ new String(b) + "&taskid=&mediatype=m3u8";
	}
}
