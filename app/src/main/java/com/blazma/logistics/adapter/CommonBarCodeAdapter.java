package com.blazma.logistics.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.BaseActivity;
import com.blazma.logistics.databinding.RowBarCodesListBinding;
import com.blazma.logistics.model.task.SampleBarCodeModel;

import java.util.ArrayList;
import java.util.List;

public class CommonBarCodeAdapter extends RecyclerView.Adapter<CommonBarCodeAdapter.MyViewHolder>  {
    private CommonBarCodeAdapter.OnItemClickListener onItemClickListener;
    private Context context;
    private boolean isShowDeleteBtn;
    private boolean isShowSampleType;
    private List<SampleBarCodeModel> barCodesList = new ArrayList<>();

    public CommonBarCodeAdapter(Context context, boolean isDeletable, boolean isShowSampleType, CommonBarCodeAdapter.OnItemClickListener onItemClickListener){
        this.context = context;
        this.isShowDeleteBtn = isDeletable;
        this.isShowSampleType = isShowSampleType;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<SampleBarCodeModel> barCodes){
        this.barCodesList = barCodes;
        ((BaseActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public CommonBarCodeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowBarCodesListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_bar_codes_list, parent, false);
        return new CommonBarCodeAdapter.MyViewHolder(binding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CommonBarCodeAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.txtCode.setText(this.barCodesList.get(position).code);
        holder.binding.txtType.setText(this.barCodesList.get(position).container);

        if(isShowDeleteBtn){
            holder.binding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.binding.btnDelete.setVisibility(View.GONE);
        }

        if(isShowSampleType){
            holder.binding.txtSampleType.setVisibility(View.VISIBLE);
            holder.binding.txtSampleType.setText(this.barCodesList.get(position).sampleType);
        } else {
            holder.binding.txtSampleType.setVisibility(View.GONE);
        }

        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onClickDelete(position);
            }
        });

        // Set the repeat barcode count
        holder.binding.txtRepeatCount.setText(String.format("x %d", barCodesList.get(position).count));
    }

    @Override
    public int getItemCount() {
        if(null!=barCodesList) {
            return this.barCodesList.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowBarCodesListBinding binding;

        public MyViewHolder(RowBarCodesListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener{
        void onClickDelete(int position);
    }
}
