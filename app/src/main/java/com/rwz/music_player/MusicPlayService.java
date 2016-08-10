package com.rwz.music_player;

import java.io.IOException;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;

public class MusicPlayService extends Service{
	public static final int PLAY_ALL=0;
	public static final int PLAY_LOVE=1;

	private MediaPlayer mPlayer;
	private Cursor cursor;
	private int currentMusic;//当前播放歌曲对应列表位置。
	private int totalMusic;//歌曲总量
	private Music music;
	private boolean isStart;
	private int playList;//播放列表（所有、最爱）
	private int times;
	private Dbhelper dbhelper;
	public static final String lrcParentPath=Environment.getExternalStorageDirectory().getPath()
			+"/Tecent/QQfile_recv/";

	public Dbhelper getDbhelper() {
		return dbhelper;
	}
	public void setDbhelper(Dbhelper dbhelper) {
		this.dbhelper = dbhelper;
	}
	public int getPlayList() {
		return playList;
	}
	public void setPlayList(int playList) {
		this.playList = playList;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}
	public Music getMusic() {
		return music;
	}
	public void setMusic(Music music) {
		this.music = music;
	}
	public Cursor getCursor() {
		return cursor;
	}
	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}
	public int getCurrentMusic() {
		return currentMusic;
	}
	public void setCurrentMusic(int currentMusic) {
		this.currentMusic = currentMusic;
	}
	public int getTotalMusic() {
		return totalMusic;
	}
	public void setTotalMusic(int totalMusic) {
		this.totalMusic = totalMusic;
	}
	public MediaPlayer getmPlayer() {
		return mPlayer;
	}
	public void setmPlayer(MediaPlayer mPlayer) {
		this.mPlayer = mPlayer;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mPlayer=new MediaPlayer();
		music=new Music();
		dbhelper = new Dbhelper(getApplicationContext());
		cursor=getAllMusicCursor();
		//		cursor=getLoveMusicCursor();
		//播放完毕
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.d("tag", currentMusic+"播放完毕");
				playNextMusic();
				sendBroadcast(new Intent(MainActivity.MUSIC_COMPLECTION));
				//	startService(new Intent(MainActivity.MUSIC_COMPLECTION));
			}
		});
		//播放错误
		mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.d("tag", currentMusic+"播放错误");
				//playNextMusic();
				//startService(new Intent(MainActivity.MUSIC_ERROR));
				return false;
			}
		});
	}
	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy() {
		mPlayer.stop();
		mPlayer.release();
		super.onDestroy();
	}
	/**
	 * 扫描磁盘并获取音乐文件路径
	 * @return
	 */
	public Cursor getAllMusicCursor(){
		ContentResolver resolver=getContentResolver();
		Uri uri=Media.EXTERNAL_CONTENT_URI;
		cursor = resolver.query(uri, null, null, null, null);
		totalMusic=cursor.getCount();
		/*while(cursor.moveToNext()){
			String str=cursor.getString(cursor.getColumnIndex(Media.TITLE));
			Log.d("tag", "====["+str+"]");
		}*/
		return cursor;
	}
	/**
	 * 从数据为获取喜欢歌曲列表
	 * @return
	 */
	public Cursor getLoveMusicCursor(){
		
		Cursor queryCursor = dbhelper.queryData();
		return queryCursor;
	}
	/**
	 * 播放音乐
	 * @param musicPath
	 */
	public boolean playMusic() {
		try {
			//根据选择播放相应列表下的歌曲
			if(playList==PLAY_ALL){
				setAllMusic();
			}else if(playList==PLAY_LOVE){
				setLoveMusic();
			}
			Log.d("tag", "playMusic()内部：getLrcPath:"+music.getLrcPath());

			//			dbhelper.updateData(music.get, times);
			mPlayer.setDataSource(music.getMusicPath());
			mPlayer.prepare();
			mPlayer.start();
			music.setTimes(++times);//设置该首歌曲累计播放次数
			dbhelper.updateData(music.getId()+"", times);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 初始化当前播放music对象
	 */
	private void setAllMusic() {
		Log.d("tag", "---------currentMusic（setMusic）-------->"+currentMusic);
		cursor.moveToPosition(currentMusic);
		music.setMusicPath(cursor.getString(cursor.getColumnIndex(Media.DATA)));
		music.setName(cursor.getString(cursor.getColumnIndex(Media.TITLE)));
		music.setSinger(cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
		music.setDuration(cursor.getString(cursor.getColumnIndex(Media.DURATION)));
		music.setLrcPath(lrcParentPath+music.getName());
	}
	/**
	 * 根据爱好初始化music
	 */
	private void setLoveMusic(){
		//		Log.d("tag", "==========当前第几首==========>"+currentMusic);
		cursor.moveToPosition(currentMusic);
		music.setName(cursor.getString(cursor.getColumnIndex(Dbhelper.MUSIC_NAME)));
		music.setSinger(cursor.getString(cursor.getColumnIndex(Dbhelper.SINGER)));
		music.setMusicPath(cursor.getString(cursor.getColumnIndex(Dbhelper.MUSIC_PATH)));
		music.setDuration(cursor.getString(cursor.getColumnIndex(Dbhelper.DURATION)));
		//music.setTimes(cursor.getInt(cursor.getColumnIndex(Dbhelper.TIMES)));
		music.setId(cursor.getInt(cursor.getColumnIndex(Dbhelper.ID)));
		music.setLrcPath(lrcParentPath+music.getName());
	}
	/**
	 * 继续或者暂停播放音乐
	 * @author Administrator
	 *
	 */
	public void continueOrStopMusic(){
		if(mPlayer.isPlaying()){
			mPlayer.pause();
		}else{
			mPlayer.start();
		}
	}
	/**
	 * 播放下一曲
	 * @return
	 */
	public boolean playNextMusic(){
		Log.d("tag", "下一曲方法内部："+currentMusic);
		if(++currentMusic>=cursor.getCount()){
			currentMusic=0;
		}	
		isStart=false;
		mPlayer.reset();
		playMusic();
		return true;
	}
	/**
	 * 播放上一曲
	 * @return
	 */
	public boolean playPrevMusic(){
		if(--currentMusic<0){
			currentMusic=cursor.getCount()-1;
		}	
		isStart=false;
		mPlayer.reset();
		playMusic();
		return true;

	}
	/**
	 * 停止
	 * @author Administrator
	 *
	 */
	public void over(){
		mPlayer.seekTo(0);
		mPlayer.stop();
		currentMusic--;
	}
	public class MyBinder extends Binder{
		public MusicPlayService getMusicPlayService(){
			return MusicPlayService.this;
		}
	}


}
