package com.shenma.tvlauncher.vod.db;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

/**
 * @Descripton 用户记录数据库
 * @author joychang
 */
/**
 * @author joychang
 *
 */
@Table(name = "albums")
public class Album implements Parcelable{
	@Id(column = "id")// 数据库主键
	private int id;
	private String albumId;// 影片ID
	private int playIndex;// 剧集标
	private int collectionTime;// 上次播放时间点
	private int typeId;// typeId:0:追剧 1：收藏 2：记录
	private String albumType;// 影片类型
	private String albumSourceType;// 源类型
	private String albumTitle;// 影片名称
	private String albumState;// 影片更新
	private String albumPic;// 影片图片路径
	private String nextLink;// 影片路径

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAlbumId() {
		return albumId;
	}
	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	public int getPlayIndex() {
		return playIndex;
	}
	public void setPlayIndex(int playIndex) {
		this.playIndex = playIndex;
	}
	public int getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(int collectionTime) {
		this.collectionTime = collectionTime;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getAlbumType() {
		return albumType;
	}
	public void setAlbumType(String albumType) {
		this.albumType = albumType;
	}
	public String getAlbumSourceType() {
		return albumSourceType;
	}
	public void setAlbumSourceType(String albumSourceType) {
		this.albumSourceType = albumSourceType;
	}
	public String getAlbumTitle() {
		return albumTitle;
	}
	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}
	public String getAlbumState() {
		return albumState;
	}
	public void setAlbumState(String albumState) {
		this.albumState = albumState;
	}
	public String getAlbumPic() {
		return albumPic;
	}
	public void setAlbumPic(String albumPic) {
		this.albumPic = albumPic;
	}
	public String getNextLink() {
		return nextLink;
	}
	public void setNextLink(String nextLink) {
		this.nextLink = nextLink;
	}
	
	
	@Override
	public String toString() {
		return "Album [id=" + id + ", albumId=" + albumId + ", playIndex="
				+ playIndex + ", collectionTime=" + collectionTime
				+ ", typeId=" + typeId + ", albumType=" + albumType
				+ ", albumSourceType=" + albumSourceType + ", albumTitle="
				+ albumTitle + ", albumState=" + albumState + ", albumPic="
				+ albumPic + ", nextLink=" + nextLink + "]";
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putInt("ID", id);
		bundle.putInt("PlayIndex", playIndex);
		bundle.putInt("CollectionTime", collectionTime);
		bundle.putInt("TypeId", typeId);
		bundle.putString("AlbumId", albumId);
		bundle.putString("AlbumType", albumType);
		bundle.putString("AlbumSourceType", albumSourceType);
		bundle.putString("AlbumPic", albumPic);
		bundle.putString("AlbumTitle", albumTitle);
		bundle.putString("AlbumState", albumState);
		bundle.putString("NextLink", nextLink);
		dest.writeBundle(bundle);
	}
	
	public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
		

		@Override
		public Album createFromParcel(Parcel source) {
			Bundle bundle = source.readBundle();
			Album data = new Album();
			data.id = bundle.getInt("ID");
			data.playIndex = bundle.getInt("PlayIndex");
			data.collectionTime = bundle.getInt("CollectionTime");
			data.typeId = bundle.getInt("TypeId");
			data.albumId = bundle.getString("AlbumId");
			data.albumType = bundle.getString("AlbumType");
			data.albumSourceType = bundle.getString("AlbumSourceType");
			data.albumPic = bundle.getString("AlbumPic");
			data.albumTitle = bundle.getString("AlbumTitle");
			data.albumState = bundle.getString("AlbumState");
			data.nextLink = bundle.getString("NextLink");
			return data;
		}

		@Override
		public Album[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Album[size];
		}
	};
}