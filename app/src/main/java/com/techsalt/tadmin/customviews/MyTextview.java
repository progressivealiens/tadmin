package com.techsalt.tadmin.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextview extends TextView {


    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public MyTextview(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public MyTextview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, textStyle);
        setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        switch (textStyle) {
            case Typeface.BOLD: // bold
                return FontCache.getTypeface("aver_bold.ttf", context);

            case Typeface.ITALIC: // italic
                return FontCache.getTypeface("aver_italic.ttf", context);

            case Typeface.BOLD_ITALIC: // bold italic
                return FontCache.getTypeface("aver_bold_italic.ttf", context);

            case Typeface.NORMAL: // regular
            default:
                return FontCache.getTypeface("aver_regular.ttf", context);
        }

    }
}

