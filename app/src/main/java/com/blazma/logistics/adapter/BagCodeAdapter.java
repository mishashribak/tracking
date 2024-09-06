package com.blazma.logistics.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.BaseActivity;
import com.blazma.logistics.databinding.RowBagCodesListBinding;
import com.blazma.logistics.databinding.RowBarCodesListBinding;
import com.blazma.logistics.model.freezer.BagItemModel;

import java.util.ArrayList;
import java.util.List;

public class BagCodeAdapter extends RecyclerView.Adapter<BagCodeAdapter.MyViewHolder>  {
    private Context context;
    private List<BagItemModel> bagList = new ArrayList<>();

    public BagCodeAdapter(Context context){
        this.context = context;
    }

    public void setData(List<BagItemModel> bagList){
        this.bagList = bagList;
        ((BaseActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public BagCodeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowBagCodesListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_bag_codes_list, parent, false);
        return new BagCodeAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.txtBag.setText(bagList.get(position).bagCode);
        holder.binding.txtValue.setText(bagList.get(position).temperatureType);
    }

    @Override
    public int getItemCount() {
        if(null!=bagList) {
            return this.bagList.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowBagCodesListBinding binding;

        public MyViewHolder(RowBagCodesListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
