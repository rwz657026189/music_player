package com.rwz.music_player;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MusicLrcFragment extends Fragment{
	private LrcTextView mLtv;
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_music_lrc, null);
		mLtv=(LrcTextView) (view.findViewById(R.id.tv_music_lrc));
		return view;
	}
	public LrcTextView getTextView(){
		return mLtv;
	}
}
