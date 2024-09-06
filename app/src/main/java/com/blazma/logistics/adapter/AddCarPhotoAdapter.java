package com.blazma.logistics.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.blazma.logistics.R;
import com.blazma.logistics.activities.BaseActivity;
import com.blazma.logistics.databinding.RowAddCarBinding;
import com.bumptech.glide.Glide;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddCarPhotoAdapter extends RecyclerView.Adapter<AddCarPhotoAdapter.MyViewHolder> {

    private OnTaskItemClickListener onItemClickListener;
    private Context context;
    private List<String> mPaths;

    public AddCarPhotoAdapter(Context context, List<String> paths, OnTaskItemClickListener onItemClickListener){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.mPaths = paths;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<String> data){
        this.mPaths = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowAddCarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_add_car, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(mPaths.get(position).equals("add")){
            holder.binding.cvPlus.setVisibility(View.VISIBLE);
            holder.binding.cvCar.setVisibility(View.GONE);
        }else{
            holder.binding.cvPlus.setVisibility(View.GONE);
            holder.binding.cvCar.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(mPaths.get(position))
                    .into(holder.binding.imgCar);

//            File imgFile = new  File(mPaths.get(position));
//            if(imgFile.exists()){
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                holder.binding.imgCar.setImageBitmap(myBitmap);
//
//            }
        }

        holder.binding.llAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onAdd();
            }
        });

        holder.binding.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onRemove(mPaths.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null!=mPaths) {
            return mPaths.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowAddCarBinding binding;

        public MyViewHolder(RowAddCarBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnTaskItemClickListener{
        void onAdd();
        void onRemove(String path);
    }
}
