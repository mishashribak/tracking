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
import com.blazma.logistics.databinding.RowBarCodesListBinding;
import com.blazma.logistics.databinding.RowFirstStepBarCodesListBinding;
import com.blazma.logistics.model.task.SampleBarCodeModel;

import java.util.ArrayList;
import java.util.List;

public class FirstStepDriverBarCodeAdapter extends RecyclerView.Adapter<FirstStepDriverBarCodeAdapter.MyViewHolder>  {
    private FirstStepDriverBarCodeAdapter.OnItemClickListener onItemClickListener;
    private Context context;
    private boolean isShowDeleteBtn;
    private boolean isShowSampleType;
    private List<SampleBarCodeModel> barCodesList = new ArrayList<>();

    public FirstStepDriverBarCodeAdapter(Context context, boolean isDeletable, boolean isShowSampleType, FirstStepDriverBarCodeAdapter.OnItemClickListener onItemClickListener){
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
    public FirstStepDriverBarCodeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowFirstStepBarCodesListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_first_step_bar_codes_list, parent, false);
        return new FirstStepDriverBarCodeAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstStepDriverBarCodeAdapter.MyViewHolder holder, int position) {

        holder.binding.txtCode.setText(this.barCodesList.get(position).code);
        holder.binding.txtType.setText(this.barCodesList.get(position).container);

        if(isShowSampleType){
            holder.binding.txtSampleType.setVisibility(View.VISIBLE);
            holder.binding.txtSampleType.setText(this.barCodesList.get(position).sampleType);
        } else {
            holder.binding.txtSampleType.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if(null!=barCodesList) {
            return this.barCodesList.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowFirstStepBarCodesListBinding binding;

        public MyViewHolder(RowFirstStepBarCodesListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener{
        void onClickDelete(int position);
    }
}
