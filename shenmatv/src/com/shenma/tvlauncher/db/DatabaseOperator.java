package com.shenma.tvlauncher.db;

import java.util.ArrayList;
import java.util.List;

import com.shenma.tvlauncher.utils.Logger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * 数据库操作对象
 * 
 */
public class DatabaseOperator {
	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_CREATED = "created";

	private DatabaseHelper databaseHelper;
	private Context context;
	private SQLiteDatabase sqliteDatabase;

	public DatabaseOperator(Context context) {
		this.context = context;
		open();
	}

	public void open() {
		databaseHelper = DatabaseHelper.getInstance(context);
		try {
			sqliteDatabase = databaseHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			sqliteDatabase = databaseHelper.getReadableDatabase();
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		sqliteDatabase.close();
	}

	/**
	 * 添加一条常用app记录
	 * 
	 * @param pkgname
	 */
	public void addApp(String pkgname) {
		if (!"".equals(pkgname) && !queryExistByName(pkgname)) {
			SQLiteDatabase db = databaseHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("packagename", pkgname);
			db.insert("apploves", "_id", values);
			db.close();
		}
	}

	/**
	 * 根据条件查询数据是否有该记录
	 * @param pkgname 查询条件
	 * @return
	 */
	private boolean queryExistByName(String pkgname) {
		if (!"".equals(pkgname)) {
			SQLiteDatabase db = databaseHelper.getReadableDatabase();
			Cursor c = db.query("apploves", null, "packagename=?", new String[] { pkgname }, null, null, null);
			if(c.moveToNext()) {
				Logger.d("zhouchuan", pkgname+"is Exist in DB");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 删除一条常用app记录
	 * 
	 * @param packName
	 */
	public void deleteApp(String packName) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.delete("apploves", "packagename=?", new String[] { packName });
		db.close();
	}

	/**
	 * 查询所有常用的app
	 * 
	 * @return
	 */
	public List<String> queryAll() {
		List<String> lst = new ArrayList<String>();
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor c = db.query("apploves", null, null, null, null, null, null);
		while (c.moveToNext()) {
			lst.add(c.getString(c.getColumnIndex("packagename")));
		}
		db.close();
		return lst;
	}
}