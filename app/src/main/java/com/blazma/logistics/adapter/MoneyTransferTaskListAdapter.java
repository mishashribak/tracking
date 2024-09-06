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
import com.blazma.logistics.databinding.RowTaskListMoneyBinding;
import com.blazma.logistics.model.money.MoneyTransferTask;
import com.blazma.logistics.model.swap.SwapTaskData;

import java.util.ArrayList;
import java.util.List;

public class MoneyTransferTaskListAdapter extends RecyclerView.Adapter<MoneyTransferTaskListAdapter.MyViewHolder>{
    private OnMoneyTaskItemClickListener clickListener;
    private Context context;
    private List<MoneyTransferTask> mTaskList = new ArrayList<>();

    public MoneyTransferTaskListAdapter(Context context, List<MoneyTransferTask> taskList, OnMoneyTaskItemClickListener onItemClickListener){
        this.context = context;
        this.clickListener = onItemClickListener;
        this.mTaskList = taskList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        RowTaskListMoneyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_task_list_money, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MoneyTransferTask taskData = mTaskList.get(position);

        // Display the task information
        holder.binding.tvFrom.setText(taskData.fromLocationName);
        holder.binding.tvTo.setText(taskData.toLocationName);
        holder.binding.tvDate.setText(taskData.createdAt);
        holder.binding.tvStatus.setText(taskData.status.toUpperCase());
        holder.binding.tvAmount.setText(taskData.amount);

        holder.binding.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go Otp verify Screen
                clickListener.onClick(taskData);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null!=mTaskList) {
            return mTaskList.size();
        }return 0;
    }

    public void setData(List<MoneyTransferTask> data){
        this.mTaskList = data;
        ((BaseActivity)context).runOnUiThread(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private RowTaskListMoneyBinding binding;
        public MyViewHolder(RowTaskListMoneyBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnMoneyTaskItemClickListener{
        void onClick(MoneyTransferTask task);
    }
}
