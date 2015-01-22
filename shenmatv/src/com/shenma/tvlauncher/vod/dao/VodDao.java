package com.shenma.tvlauncher.vod.dao;

import java.util.List;

import com.shenma.tvlauncher.utils.Logger;
import com.shenma.tvlauncher.vod.db.Album;
import net.tsz.afinal.FinalDb;
import android.content.Context;

public class VodDao {

	private FinalDb db;
	
	public VodDao(Context ctx){
		db = FinalDb.create(ctx,"shenma.db");
	}
	
	/**
	 * 添加一个影片记录
	 * @param Album
	 */
	public void addAlbums(Album album){
		String where ="albumType='"+album.getAlbumType()+
				"' and albumId='"+album.getAlbumId()+
				"' and typeId="+album.getTypeId();
		Logger.d("joychang", "where="+where);
		List<Album> lst = db.findAllByWhere(Album.class, where);
		if(lst.size() == 0){
			Logger.d("joychang", "添加=collectionTime="+album.getCollectionTime());
			db.save(album);//添加
		}else{
			db.update(album, where);
			Logger.d("joychang", "修改=collectionTime="+album.getCollectionTime());
		}
	}
	
	/**
	 * 根据albumId查询Album
	 * @return Album
	 */
	public List<Album> queryAlbumById(String albumId,int typeId){
		String where = "albumId='"+albumId+"'"+" and typeId="+typeId;
		Logger.d("joychang", "查询Album条件="+where);
		return db.findAllByWhere(Album.class, where);
	}
	
	/**
	 * 根据albumId typeId查询是否追剧或者是否收藏
	 * @param albumId
	 * @param typeId
	 * @return
	 */
	public Boolean queryZJById(String albumId,int typeId){
		List<Album> albums = null;
		Boolean res = false;
		String where = "albumId='"+albumId+"' and typeId="+typeId;
		Logger.d("joychang", "查询where="+where);
		albums = db.findAllByWhere(Album.class, where);
		if(null!=albums && albums.size()>0){
			res = true;
		}
		return res;
	}
	
//	/**
//	 * 根据albumId查询Album
//	 * @return Album
//	 */
//	public Album queryAlbumById(String albumId,int typeId){
//		String where = "albumId='"+albumId+"'"+" and typeId="+typeId;
//		return db.findWithManyToOneById(id, clazz, findClass)
//	}
	
	
	/**
	 * 查询所有指定类型的Album
	 * @return 所有Album
	 */
	public List<Album> queryAllAppsByType(int typeId){
		String where = "typeid="+typeId;
		return db.findAllByWhere(Album.class, where);
	}
	
	/**
	 * 删除单个app
	 * @param app
	 */
	public void deleteApps(Album app){
		db.delete(app);
	}
	
	
	
	/**
	 * 删除指定条件的app  条件为空则全部删除
	 * @param where
	 */
	public void deleteByWhere(String albumId, String albumType, int typeId){
		String where = "albumId='"+albumId+
				"' and albumType='"+albumType+
				"' and typeId="+typeId;
		db.deleteByWhere(Album.class, where);
	}
	
	/**
	 * 删除指定条件的app  条件为空则全部删除
	 * @param where
	 */
	public void deleteAllByWhere(int typeId){
		String where = "typeId="+typeId;
		db.deleteByWhere(Album.class, where);
	}
	
}
