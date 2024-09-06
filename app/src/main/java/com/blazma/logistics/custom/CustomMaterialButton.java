package com.blazma.logistics.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.blazma.logistics.R;
import com.google.android.material.button.MaterialButton;

public class CustomMaterialButton extends MaterialButton {

    private final String TAG = this.getClass().getSimpleName();

    public CustomMaterialButton(Context context) {
        super(context);
    }

    public CustomMaterialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomMaterialButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
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
                asset = ctx.getResources().getString(R.string.Nuinito_Bold);
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}