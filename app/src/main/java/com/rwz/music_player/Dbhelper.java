package com.rwz.music_player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Dbhelper extends SQLiteOpenHelper{
	public static final String NAME = "tb_music_love";
	public static final int VERSION = 1;
	public static final String TB_MUSIC_LOVE = "tb_music_love";
	public static final String ID = "_id";
	public static final String MUSIC_NAME = "musicname";
	public static final String SINGER = "singer";
	public static final String MUSIC_PATH = "path";
	public static final String LRC_PATH = "lrcPath";
	public static final String DURATION = "duration";
	public static final String TIMES = "times";
	private SQLiteDatabase db;


	public Dbhelper(Context context) {
		super(context, NAME, null, VERSION);
		db=getReadableDatabase();
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table "+TB_MUSIC_LOVE+"("+ID+" integer primary key autoincrement,"
				+MUSIC_NAME+" charAt(10),"+SINGER+" character(10),"+MUSIC_PATH+" text,"
				+LRC_PATH+" text,"+DURATION+" text,"+TIMES+" integer)");
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	/**
	 * 插入数据
	 * @param music
	 */
	public void insertData(Music music){
		Cursor cursor = queryData();
		//如果数据库中存在该音乐，就不插入。
		while(cursor.moveToNext()){
			String pathStr=cursor.getString(cursor.getColumnIndex(Dbhelper.MUSIC_PATH));
			if(pathStr.equals(music.getMusicPath())){
				return;
			}
		}
		ContentValues values=new ContentValues();
		values.put(MUSIC_NAME,music.getName());
		values.put(SINGER, music.getSinger());
		values.put(MUSIC_PATH, music.getMusicPath());
		values.put(LRC_PATH, music.getLrcPath());
		values.put(DURATION, music.getDuration());
		values.put(TIMES, music.getTimes());
		db.insert(TB_MUSIC_LOVE, null, values);
	}
	/**
	 * 删除数据
	 */
	public void deleteData(String id){
		String whereClause = "id=?";
		String[] whereArgs = {id};
		db.delete(TB_MUSIC_LOVE, whereClause, whereArgs);
	}
	public void deleteAllData(){
		db.delete(TB_MUSIC_LOVE, null, null);
	}
	/**
	 * 修改数据
	 */
	public void updateData(String id,int times){
		ContentValues values=new ContentValues();
		values.put(TIMES, times);
		String whereClause=ID+"=?";
		String[] whereArgs={id};
		db.update(TB_MUSIC_LOVE, values, whereClause, whereArgs);
	}
	/**
	 * 查询数据
	 */
	public Cursor queryData(){
		Cursor cursor = db.query(TB_MUSIC_LOVE, null, null, null, null, null, TIMES);
		return cursor;
	}
}
