package com.example.administrator.xfmovie;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class RightPicClickEditText extends AppCompatEditText {

    public  RightPicOnclickListener rightPicOnclickListener;

    public void setRightPicOnclickListener(RightPicOnclickListener rightPicOnclickListener) {
        this.rightPicOnclickListener = rightPicOnclickListener;
    }
    public RightPicClickEditText(Context context) {
        super(context);
        init();
    }


    public RightPicClickEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RightPicClickEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    protected Drawable mRightDrawable;
    private void init() {
        // // getCompoundDrawables获取是一个数组，数组0,1,2,3,对应着左，上，右，下 这4个位置的图片，如果没有就为null
        mRightDrawable=getCompoundDrawables()[2];
        if (mRightDrawable == null) {
            return;
        }
        mRightDrawable.setBounds(0, 0, mRightDrawable.getIntrinsicWidth(), mRightDrawable.getIntrinsicHeight());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mRightDrawable != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < (getWidth() - getPaddingRight()));
                if (touchable) {
                setFocusable(false);
                setFocusableInTouchMode(false);
                    if (rightPicOnclickListener != null) {
                        rightPicOnclickListener.rightPicClick();
                    }
                }else{
                    setFocusableInTouchMode(true);
                    setFocusable(false);
                }
            }

        }
        return super.onTouchEvent(event);
    }


    public interface RightPicOnclickListener {
        void rightPicClick();
    }
}
