package com.shenma.tvlauncher.utils;

import java.util.ArrayList;
public class VideoPlayUtils {

	/**
	 * menu主界面目录
	 * 
	 * @return
	 */
	public static ArrayList<String> getData(int type) {
		ArrayList<String> list = new ArrayList<String>();
		if (type == 0) {
			list.add("选集");
			list.add("清晰度");
			list.add("视频解码");
			list.add("画面比例");
			list.add("偏好设置");
			//list.add("上下键设置");
		} else if (type == 1) {
			// 画面质量
			list.add("流畅");
			list.add("高清");
			list.add("超清");
			list.add("自适应");
		} else if (type == 2) {
			// 解码
			list.add("软解码");
			list.add("硬解码");
		} else if (type == 3) {
			list.add("原始比例");
			list.add("4:3");
			list.add("16:9");
			list.add("默认全屏");
		}else if (type == 4) {
			list.add("上下键切换选集");
			list.add("上下键调节音量");
		}
		return list;
	}

}
