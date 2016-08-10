package com.rwz.music_player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileUtil {
	public static void copyAllFile(){
		//File srcFile=new File("/mnt/shell/emulated/0/Download/other/");
		File srcFile=new File(Environment.getExternalStorageDirectory().getPath()
				+"/Tecent/QQfile_recv/卜卦.txt");
		if(!srcFile.exists()){
			Log.d("tag", "目录文件不存在");
			return;
		}
		File[] files = srcFile.listFiles();
		for (File file : files) {
			if(file.isFile()){
				copyLrcFile(file);
			}
		}
		
	}
	public static void copyLrcFile(File srcFile){
		if(!srcFile.exists()){
			Log.d("tag", "目录文件不存在2");
			return;
		}
		String fileName=srcFile.getName();
		File dstFile=new File(MusicPlayService.lrcParentPath,fileName);
		if(!dstFile.getParentFile().exists()){
			dstFile.getParentFile().mkdirs();
		}
		Log.d("tag", dstFile.getAbsolutePath()+"-----------");
		BufferedWriter bw=null;
		BufferedReader br=null;
		try {
			bw=new BufferedWriter(new FileWriter(dstFile));
			br=new BufferedReader(new FileReader(srcFile));
			String line;
			while((line=br.readLine())!=null){
				bw.write(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(bw!=null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
}
