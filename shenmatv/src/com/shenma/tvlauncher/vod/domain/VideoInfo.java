package com.shenma.tvlauncher.vod.domain;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/***
 * @Description 媒体数据info
 * @author joychang
 * 
 */
public class VideoInfo implements Parcelable {
	public String title;
	public String url;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putString("url", url);
		dest.writeBundle(bundle);
	}
	public static final Parcelable.Creator<VideoInfo> CREATOR = new Parcelable.Creator<VideoInfo>() {

		@Override
		public VideoInfo createFromParcel(Parcel source) {
			Bundle bundle = source.readBundle();
			VideoInfo data = new VideoInfo();
			data.title = bundle.getString("title");
			data.url = bundle.getString("url");
			return data;
		}

		@Override
		public VideoInfo[] newArray(int size) {
			return new VideoInfo[size];
		}
		
	};

}
