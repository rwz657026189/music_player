package com.rwz.music_player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rwz.music_player.MusicPlayService.MyBinder;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements OnClickListener, 
OnSeekBarChangeListener, OnCheckedChangeListener, OnPageChangeListener {
	public static final String MUSIC_COMPLECTION = "com.bwf.music_player.music_complection";
	public static final String MUSIC_ERROR = "com.bwf.music_player.music_error";
	protected static final int SET_MUSIC_LRC = 0;
	protected static final int NULL_MUSIC_LRC = 1;
	protected static final int START_MUSIC_LRC = 2;
	protected static final int TEXT_FONT = 20;
	private Button btnStart,btnNext,btnPrev,btnStop;
	private SeekBar sbProgress;
	private TextView tvLrc;//�б������ʾ
	private LrcTextView tvAllLrc;//��ʽ�������ʾ
	private ViewPager viewPager;
	private CheckBox cbCollect;
	private TextView tvTitle;

	private MusicPlayService mService;
	private boolean isTouch=false;
	private boolean isFirstPlay=true;
	private MusicAdapter adapter;
	private Lrc lrc;

	private Map<String,Integer> map;

	private Intent service;
	private Handler handler = new Handler(new Handler.Callback() {
		int appendNum=10;
		@Override
		public boolean handleMessage(Message msg) {
			if(msg.what==SET_MUSIC_LRC){
				//Log.d("tag", "��ʣ�"+lrc.getLrcMusic().get(msg.arg1));
				tvLrc.setText(lrc.getLrcMusic().get(msg.arg1));
				/*if(isFirst){
					for (int i = 0; i <appendNum; i++) {
						tvAllLrc.append("\n"+lrc.getLrcMusic().get(msg.arg1+i));
					}
					isFirst=false;
				}else if(lrc.getMusicLrcLineNum()>msg.arg1+appendNum){
					tvAllLrc.append("\n"+lrc.getLrcMusic().get(msg.arg1+appendNum));
				}*/
				int lineHeight=(int) tvAllLrc.getTextSize()+6;
				tvAllLrc.smoothScroll(0, tvAllLrc.getScrollY()+lineHeight, 1000);
			}else if(msg.what==NULL_MUSIC_LRC){
				tvLrc.setText("δ�ܻ�ȡ�����ظ��");
				tvAllLrc.setText("δ�ܻ�ȡ�����ظ��");
			}else if(msg.what==START_MUSIC_LRC){
				tvAllLrc.setText("");
				for (int i = 0; i < appendNum; i++) {
					tvAllLrc.append("\n");
				}
				for (int i = 0; i < lrc.getMusicLrcLineNum(); i++) {
					tvAllLrc.append("\n"+lrc.getLrcMusic().get(i));
				}
			}
			
			
			return false;
		}
	});
	private BroadcastReceiver receiver;
	private FragmentManager fm;
	private ListView listView;
	private MusicListFragment musicListFragment;
	private boolean isGetLrcSuccess;
	private MusicLrcFragment musicLrcFragment;
	//private boolean changedPlayList;//�����б�ı�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		addMusicLrc();
		initView();
		service = new Intent(this,MusicPlayService.class);
		startService(service);
		bindMusciPlayService();
		FileUtil.copyAllFile();
		lrc=new Lrc();

		receiver = new MusicBroadcastReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(MUSIC_COMPLECTION);
		registerReceiver(receiver, filter);


	}
	private void addMusicLrc() {
		map=new HashMap<String, Integer>();
		map.put("����", R.string.lrc_aq);
		map.put("����", R.string.lrc_bugua);
		map.put("�����˵", R.string.lrc_knbks);
		map.put("ƽ��֮·", R.string.lrc_pfzl);
		map.put("���Գ���", R.string.lrc_sych);
		map.put("�ҵĺ��ֵ�", R.string.lrc_wdhxd);
		map.put("ϲ����", R.string.lrc_xhn);
		map.put("��ɽ��", R.string.lrc_nsn);
		map.put("Ĭ", R.string.lrc_mo);
	}
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	/**
	 * �󶨷���
	 */
	private void bindMusciPlayService() {
		ServiceConnection conn=new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
			}
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				MyBinder mBinder=(MyBinder) service;
				mService = mBinder.getMusicPlayService();
				adapter = new MusicAdapter(mService.getCursor(), MainActivity.this);
				//Log.d("tag", "-----listView------>"+listView+"");
				//���ڴ˴���Ϊ������onCreateView()ִ�����
				listView = musicListFragment.getListview();
				tvAllLrc=musicLrcFragment.getTextView();
				tvAllLrc.setTextSize(TEXT_FONT);
				addDataToSet(mService.getLoveMusicCursor());
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						position--;
						/*if(changedPlayList){
							changedPlayList=false;
						}*/
						//						Log.d("tag", "------------------->�����item");
						mService.setCurrentMusic(position);
						mService.playNextMusic();
						initOnMusicChanged();
						changePlayToShow();
					}
				});
				listView.setAdapter(adapter);
			}
		};
		bindService(service, conn, Context.BIND_AUTO_CREATE);
	}
	/**
	 * ui�ؼ���ʼ��
	 */
	private void initView() {
		findView();
		setLinstener();
		fm=getSupportFragmentManager();
		List<Fragment> list=new ArrayList<Fragment>();
		musicListFragment = new MusicListFragment();
		musicLrcFragment=new MusicLrcFragment();
		list.add(musicListFragment);
		list.add(musicLrcFragment);


		viewPager.setAdapter(new FragmentAdapter(fm,list));
		viewPager.setOnPageChangeListener(this);
	}
	/**
	 * �ҿؼ�
	 */
	private void findView() {
		btnStart=(Button) findViewById(R.id.btn_start);
		btnNext=(Button) findViewById(R.id.btn_next);
		btnPrev=(Button) findViewById(R.id.btn_prev);
		btnStop=(Button) findViewById(R.id.btn_stop);
		tvLrc=(TextView) findViewById(R.id.tv_lrc);
		sbProgress=(SeekBar) findViewById(R.id.sb_progress);
		viewPager=(ViewPager) findViewById(R.id.viewpager);
		cbCollect=(CheckBox) findViewById(R.id.cb_collect);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
	}
	/**
	 * ���ü�����
	 */
	private void setLinstener() {
		btnStart.setOnClickListener(this);
		btnNext.setOnClickListener(this);
		btnPrev.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		sbProgress.setOnSeekBarChangeListener(this);
		cbCollect.setOnCheckedChangeListener(this);

	}
	/**
	 * ��������
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_start:
			if(isFirstPlay){
				btnStart.setBackgroundResource(R.drawable.selector_btn_stop);
				mService.playMusic();
				initOnMusicChanged();
				isFirstPlay=false;
			}else{
				if(mService.getmPlayer().isPlaying()){
					btnStart.setBackgroundResource(R.drawable.selector_btn_play);
				}else{
					btnStart.setBackgroundResource(R.drawable.selector_btn_stop);
				}
				mService.continueOrStopMusic();
			}
			break;
		case R.id.btn_next:
			changePlayToShow();
			mService.playNextMusic();
			initOnMusicChanged();
			break;
		case R.id.btn_prev:
			changePlayToShow();
			mService.playPrevMusic();
			initOnMusicChanged();
			break;
		case R.id.btn_stop:
			mService.over();
			//isFirstPlay=true;
			break;
		}
	}
	/**
	 * �����Ž����������ʾ����
	 */
	private void changePlayToShow() {
		if(adapter.getPlayList()!=adapter.getShowList()){
			adapter.setShowList(adapter.getPlayList());
		}
	}
	/**
	 * �����������䶯ʱ�����б�Ҫ�ĳ�ʼ��
	 */
	private void initOnMusicChanged() {
		sbProgress.setMax(Integer.parseInt(mService.getMusic().getDuration()));
		setSeekBarProgress();
		
		adapter.reSetData(mService.getCursor(), mService.getCurrentMusic());
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if(seekBar==sbProgress){
			mService.getmPlayer().seekTo(sbProgress.getProgress());
			isTouch=false;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		if(seekBar==sbProgress){
			isTouch=true;
		}
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	}
	/**
	 * ����һ�����߳�ʱʱ���½�����
	 */
	int count;
	private CharSequence tem = "";
	private void setSeekBarProgress() {
		if(mService.isStart()){
			return;
		}
		mService.setStart(true);
		new Thread(){
			@Override
			public void run() {
				Integer id=map.get(mService.getMusic().getName());
				if(id!=null){
					String uri=getResources().getString(id);
					isGetLrcSuccess=lrc.parseTime(uri);
				}else{
					isGetLrcSuccess=false;
				}
				count=0;
				handler.sendEmptyMessage(START_MUSIC_LRC);
				while(mService.isStart()){
					if(!isTouch ){
						/*
						 * ��������һ������ʱ��ִ��reset()����������getCurrentPosition()����error (-38,0)
						 * ����������onCompletion���������������Բ�����Ϊʲô��
						 */
						sbProgress.setProgress(mService.getmPlayer().getCurrentPosition());
					}
					if(isGetLrcSuccess){
						//��ʾ���
						if(lrc.getMusicLrcLineNum()>count && sbProgress.getProgress()>=lrc.getTimeMusic().get(count)){
							Message msg=Message.obtain();
							msg.what=SET_MUSIC_LRC;
							msg.arg1=count;
							handler.sendMessage(msg);
							count++;
						}
					}else{
						//tvLrc.setText("δ�ܻ�ȡ�����ظ��");
						handler.sendEmptyMessage(NULL_MUSIC_LRC);
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	private class MusicBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(MUSIC_COMPLECTION.equals(intent.getAction())){
				initOnMusicChanged();
			}
		}

	}
	/**
	 * ���ղظ����б����ı�ʱ
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		Cursor cursor=null;
		if(isChecked){
			Set<Music> set=adapter.getMusicSet();
			mService.getDbhelper().deleteAllData();
			for (Music m : set) {
				//�����ݿ�Ϊд��������
				if(m!=null){
					mService.getDbhelper().insertData(m);
				}
			}
			//�����ݿ��ȡ����
			cursor = mService.getLoveMusicCursor();
			addDataToSet(cursor);
			mService.setPlayList(MusicPlayService.PLAY_LOVE);
			adapter.setPlayList(MusicPlayService.PLAY_LOVE);
			adapter.reSetData(cursor);
			tem = tvTitle.getText();
			tvTitle.setText("�ղ��б�");
		}else{
			mService.setPlayList(MusicPlayService.PLAY_ALL);
			adapter.setPlayList(MusicPlayService.PLAY_ALL);
			cursor = mService.getAllMusicCursor();
			tvTitle.setText(tem);
			
		}
		mService.setCursor(cursor);
		adapter.reSetData(cursor);
	}
	/**
	 * �����ݿ��ȡ���ݵ�����
	 * @param cursor
	 */
	private void addDataToSet(Cursor cursor) {
		while(cursor.moveToNext()){
			Music music=new Music();
			String musicPath=cursor.getString(cursor.getColumnIndex(Dbhelper.MUSIC_PATH));
			music.setMusicPath(musicPath);
			music.setDuration(cursor.getString(cursor.getColumnIndex(Dbhelper.DURATION)));
			music.setSinger(cursor.getString(cursor.getColumnIndex(Dbhelper.SINGER)));
			music.setName(cursor.getString(cursor.getColumnIndex(Dbhelper.MUSIC_NAME)));
			adapter.getMusicSet().add(music);
			adapter.getSet().add(musicPath);
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(arg0==1){

		}
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int arg0) {
		if(arg0 == 0){
			tvTitle.setText("�����б�");
		}else{
			tvTitle.setText("���չʾ");
		}
		
	}

}
