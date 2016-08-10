package com.rwz.music_player;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter implements OnCheckedChangeListener{
	private Cursor cursor;
	private LayoutInflater inflater;
	private int currentMusic=-1;
	private int playList;//播放列表类型
	private int showList;//界面显示的列表
	private Set<String> pathSet;
	private Set<Music> musicSet;
	public Set<Music> getMusicSet() {
		return musicSet;
	}
	public void setMusicSet(Set<Music> musicSet) {
		this.musicSet = musicSet;
	}
	public int getShowList() {
		return showList;
	}
	public void setShowList(int showList) {
		this.showList = showList;
	}
	public int getPlayList() {
		return playList;
	}
	public void setPlayList(int playList) {
		this.playList = playList;
	}

	public Set<String> getSet() {
		return pathSet;
	}
	public void setSet(Set<String> set) {
		this.pathSet = set;
	}
	public MusicAdapter(Cursor curosr, Context context) {
		super();
		this.cursor = curosr;
		inflater=LayoutInflater.from(context);
		pathSet=new HashSet<String>();
		musicSet=new HashSet<Music>();
	}
	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Cursor getItem(int position) {
		cursor.moveToPosition(position);
		return cursor;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_music_list, null);
			holder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_singer=(TextView) convertView.findViewById(R.id.tv_singer);
			holder.cb_love=(CheckBox) convertView.findViewById(R.id.cb_love);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		cursor.moveToPosition(position);
		//根据显示列表判断是否显示收藏按钮
		if(playList==MusicPlayService.PLAY_ALL){
			holder.tv_name.setText(cursor.getString(cursor.getColumnIndex(Media.TITLE)));
			holder.tv_singer.setText(cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
			holder.cb_love.setVisibility(View.VISIBLE);
			holder.cb_love.setOnCheckedChangeListener(this);
			holder.cb_love.setTag(position);
			String musicPath=cursor.getString(cursor.getColumnIndex(Media.DATA));
			if(pathSet.contains(musicPath)){
				holder.cb_love.setChecked(true);
			}else{
				holder.cb_love.setChecked(false);
			}
		}else if(playList==MusicPlayService.PLAY_LOVE){
			holder.tv_name.setText(cursor.getString(cursor.getColumnIndex(Dbhelper.MUSIC_NAME)));
			holder.tv_singer.setText(cursor.getString(cursor.getColumnIndex(Dbhelper.SINGER)));
			holder.cb_love.setVisibility(View.GONE);
		}
		//突出显示当前播放曲目
		if(playList==showList && position==currentMusic){
			holder.tv_name.setTextColor(0xfffe4beb);
			holder.tv_singer.setTextColor(0x88fe4beb);
			holder.tv_name.setTextSize(30f);
			holder.tv_singer.setTextSize(22f);
		}else{
			holder.tv_name.setTextColor(0xff44eeff);
			holder.tv_singer.setTextColor(0x55ddeeff);
			holder.tv_name.setTextSize(20f);
			holder.tv_singer.setTextSize(16f);
		}
		return convertView;
	}
	public static class ViewHolder{
		TextView tv_name,tv_singer;
		CheckBox cb_love;
	}
	public void reSetData(Cursor cursor,int position){
		//position:当前播放哪一首
		this.cursor=cursor;
		currentMusic=position;
		notifyDataSetChanged();
	}
	public void reSetData(Cursor cursor){
		this.cursor=cursor;
		notifyDataSetChanged();

	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int position=(Integer) buttonView.getTag();
		cursor.moveToPosition(position);
		Music music=new Music();
		String musicPath=cursor.getString(cursor.getColumnIndex(Media.DATA));
		music.setMusicPath(musicPath);
		music.setDuration(cursor.getString(cursor.getColumnIndex(Media.DURATION)));
		music.setSinger(cursor.getString(cursor.getColumnIndex(Media.ARTIST)));
		music.setName(cursor.getString(cursor.getColumnIndex(Media.TITLE)));
		if(isChecked){
			pathSet.add(musicPath);
			musicSet.add(music);
		}else{
			pathSet.remove(musicPath);
			musicSet.remove(music);
		}
	}
}
