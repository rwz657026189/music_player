package com.rwz.music_player;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Scroller;
import android.widget.TextView;

public class LrcTextView extends TextView{
	private Scroller mScroller;
	private Paint paint;
	public LrcTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public LrcTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public LrcTextView(Context context) {
		super(context);
		init();
	}
	private void init() {
		mScroller=new Scroller(getContext());
		paint=new Paint();
		paint.setTextSize(20);
	}
	
	public void smoothScroll(int dstX,int dstY,int duration){
		mScroller.startScroll(getScrollX(), getScrollY(), dstX-getScrollX(),
				dstY-getScrollY(),duration);
		invalidate();
	}
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
		}
		invalidate();
	}
}
