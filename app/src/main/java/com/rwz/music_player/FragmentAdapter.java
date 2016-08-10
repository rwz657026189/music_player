package com.rwz.music_player;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter{
	private List<Fragment> list;
	public FragmentAdapter(FragmentManager fm,List<Fragment> list) {
		super(fm);
		this.list=list;
	}
	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}
	@Override
	public int getCount() {
		return list.size();
	}

}
