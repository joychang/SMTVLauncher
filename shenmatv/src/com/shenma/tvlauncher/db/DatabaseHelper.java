package com.shenma.tvlauncher.db;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static DatabaseHelper mInstance = null;

	/** 数据库名称 **/
	public static final String DATABASE_NAME = "mikulauncher";
	public static final String TABLE_APPLOVES = "apploves";

	/** 数据库版本号 **/
	private static final int DATABASE_VERSION = 1;

	/** 数据库SQL语句 添加一个表 **/
	private String CREATE_TABLE_APPLOVE = "create table if not exists "
			+ TABLE_APPLOVES
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,packagename VARCHAR(50))";

	private Context context;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	/** 单例模式 **/
	public static synchronized DatabaseHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		Editor editor = context.getSharedPreferences("MiKu",
				Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();

		/** 向数据中添加表 **/
		db.execSQL(CREATE_TABLE_APPLOVE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/** 可以拿到当前数据库的版本信息 与之前数据库的版本信息 用来更新数据库 **/
		if (oldVersion != newVersion) {
			Editor editor = context.getSharedPreferences("MiKu",
					Context.MODE_PRIVATE).edit();
			editor.clear();
			editor.commit();
			db.execSQL(CREATE_TABLE_APPLOVE);
		}
	}

	/**
	 * 删除数据库
	 */
	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}
}
