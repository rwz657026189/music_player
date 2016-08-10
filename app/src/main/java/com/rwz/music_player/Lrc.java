package com.rwz.music_player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Lrc {
	private List<String> timeMusicStr;
	private List<String> lrcMusic;
	private List<Integer> timeMusic;
	
	public Lrc() {
		super();
		timeMusicStr=new ArrayList<String>();
		lrcMusic=new ArrayList<String>();
		timeMusic=new ArrayList<Integer>();
	}
	public List<String> getLrcMusic() {
		return lrcMusic;
	}
	public void setLrcMusic(List<String> lrcMusic) {
		this.lrcMusic = lrcMusic;
	}
	public List<Integer> getTimeMusic() {
		return timeMusic;
	}
	public void setTimeMusic(List<Integer> timeMusic) {
		this.timeMusic = timeMusic;
	}
	/**
	 * 从本地获取Lrc文件并存入集合
	 * @param uriStr
	 * @return
	 */
	private  boolean getLrc(String uriStr){
		File lrcFile=new File(uriStr);
		if(!lrcFile.exists()){
			return false;
		}
		BufferedReader br=null;
		try {
			br=new BufferedReader(new FileReader(lrcFile));
			String line;
			String[] split;
			while((line=br.readLine())!=null){
				split = line.split("]");
				if(split[1]!=null){
					lrcMusic.add(split[1]);
					timeMusicStr.add(split[0].substring(1));
				}
			}
			if(timeMusicStr!=null){
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	/**
	 * 解析时间字符串为毫秒数
	 * @return
	 */
	public boolean parseTime(String uriStr){
		timeMusicStr.clear();
		timeMusic.clear();
		lrcMusic.clear();
//		Log.d("tag", "uriStr"+uriStr);
		if(!getLrc2(uriStr)){
			return false;
		}
		String[] split;
		int size=timeMusicStr.size();
		int tem=0;
		for(int i=0;i<size;i++){
			split = timeMusicStr.get(i).split(":");
			if(split[0].matches("[0-9]{1,2}")){
//				Log.d("tag", "匹配成功");
				int minute = Integer.parseInt(split[0]);
				double second = Double.parseDouble(split[1]);
				timeMusic.add((int)((minute*60+second)*1000));
			}else{
				//时间解析不成功则移除对应的歌词
				lrcMusic.remove(i-tem);
				tem++;
			}
		}
		return true;
	}
	private  boolean getLrc2(String uriStr){
		//Log.d("tag", uriStr);
		String[] splits = uriStr.split("\\[");
		for (int i = 0; i < splits.length; i++) {
			String[] split = splits[i].split("\\]");
			if(split.length==2 && split[1]!=""){
				//Log.d("tag", "lrcMusic子元素:"+split[1]);
				lrcMusic.add(split[1]);
				timeMusicStr.add(split[0].substring(1));
			}
		}
		if(timeMusicStr!=null){
			
			return true;
		}
		return false;
	}
	public int getMusicLrcLineNum(){
		return lrcMusic.size();
	}
}
