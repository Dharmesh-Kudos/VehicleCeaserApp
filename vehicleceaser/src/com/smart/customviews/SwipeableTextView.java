package com.smart.customviews;

import android.animation.*;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;

import com.smart.framework.SmartApplication;

/**
 * This Class Contains All Method Related To IjoomerTextView.
 *
 * @author tasol
 */
public class SwipeableTextView extends AppCompatTextView {


    private Context context;

    public SwipeableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SwipeableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeableTextView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(Context mContext) {

        try {
            if (SmartApplication.REF_SMART_APPLICATION.FONT != null) {
                if (getTypeface() != null) {
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT, getTypeface().getStyle());
                } else {
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                }
            } else {
                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.FONT_NAME);
                if (getTypeface() != null) {
                    setTypeface(tf, getTypeface().getStyle());
                } else {
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                }
                SmartApplication.REF_SMART_APPLICATION.FONT=tf;
            }
        } catch (Throwable e) {
        }

        setSwipeableListener();
    }

    private void setSwipeableListener() {

        this.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {

                animate()
                        .translationX(-SwipeableTextView.this.getWidth())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                super.onAnimationEnd(animation);
                                setVisibility(View.GONE);
                            }
                        });
            }

            @Override
            public void onSwipeRight() {

                animate()
                        .translationX(SwipeableTextView.this.getWidth())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(android.animation.Animator animation) {
                                super.onAnimationEnd(animation);
                                setVisibility(View.GONE);
                            }
                        });


            }
        });
    }

    @Override
    public void setVisibility(int visibility) {

        super.setVisibility(visibility);

        if(getVisibility()==View.VISIBLE){
            setTranslationX(0);
            setAlpha(1.0f);
        }


    }
}
