package com.rwz.music_player;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MusicListFragment extends Fragment{
	private View view;
	private ListView listview;
	public MusicListFragment() {
		super();
	}
	
	public ListView getListview() {
		return listview;
	}

	public void setListview(ListView listview) {
		this.listview = listview;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.fragment_music_list, null);
		listview=(ListView) view.findViewById(R.id.lv_music_list);
		/*getMusicCursor();
		ListAdapter adapter=new MusicAdapter(cursor, view.getContext());
		listview.setAdapter(adapter);*/
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	/*void getMusicCursor(){
		ContentResolver resolver=view.getContext().getContentResolver();
		Uri uri=Media.EXTERNAL_CONTENT_URI;
		cursor = resolver.query(uri, null, null, null, null);
	}*/
}
