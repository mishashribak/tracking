package com.blazma.logistics.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blazma.logistics.R;
import com.blazma.logistics.utilities.LocaleHelper;

import java.util.List;

public class SpinCommonAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> values;
    private String lang;

    public SpinCommonAdapter(Context context, int textViewResourceId,
                             List<String>  values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        lang = LocaleHelper.getPersistedData(context, "ar");
    }

    @Override
    public int getCount(){
        return values.size();
    }

    @Override
    public String getItem(int position){
        return values.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        if(lang.equals("en")){
            label.setText(values.get(position));
        }else{
            label.setText(values.get(position));
        }
        return label;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        if(lang.equals("en")){
            label.setText(values.get(position));
        }else{
            label.setText(values.get(position));
        }
        label.setTextColor(context.getResources().getColor(R.color.colorTextGrayDark));

        return label;
    }
}
