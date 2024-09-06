package com.blazma.logistics.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;

import com.blazma.logistics.R;

public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    private final String TAG = this.getClass().getSimpleName();

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
        setUnderline(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
        setUnderline(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        String customFont = a.getString(R.styleable.CustomTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            if(asset == null)
                asset = ctx.getResources().getString(R.string.Nuinito_Regular);
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }
        setTypeface(tf);
        return true;
    }

    private void setUnderline(Context ctx, AttributeSet attrs){
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        boolean underline = a.getBoolean(R.styleable.CustomTextView_underline, false);
        if(underline){
            SpannableString content = new SpannableString(getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            this.setText(content);
            a.recycle();
        }
    }
}