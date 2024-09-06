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
import com.blazma.logistics.databinding.RowTaskListSwapBinding;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.swap.SwapTask;
import com.blazma.logistics.model.swap.SwapTaskData;
import com.blazma.logistics.utilities.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class SwapTaskListAdapter extends RecyclerView.Adapter<SwapTaskListAdapter.MyViewHolder>{

    private OnSwapTaskItemClickListener clickListener;
    private Context context;
    private List<SwapTaskData> mTaskList = new ArrayList<>();

    public SwapTaskListAdapter(Context context, List<SwapTaskData> taskList, OnSwapTaskItemClickListener onItemClickListener){
        this.context = context;
        this.clickListener = onItemClickListener;
        this.mTaskList = taskList;
    }

    @NonNull
    @Override
    public SwapTaskListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTaskListSwapBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_task_list_swap, parent, false);
        return new SwapTaskListAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SwapTaskListAdapter.MyViewHolder holder, int position) {
        SwapTaskData taskData = mTaskList.get(position);

        // Display the task information
        holder.binding.tvFrom.setText(taskData.task.fromLocationName);
        holder.binding.tvTo.setText(taskData.task.toLocationName);

        holder.binding.tvDate.setText(taskData.task.taskDate);
        holder.binding.tvTime.setText(taskData.task.taskTime);

//        if(UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_NEW)){
//            holder.binding.viewBagNumbers.setVisibility(View.GONE);
//        } else {
//            holder.binding.viewBagNumbers.setVisibility(View.VISIBLE);
//            holder.binding.tvBags.setText(String.format(context.getResources().getString(R.string.bags), task.numberOfBags));
//        }

        holder.binding.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go Map Screen
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

    public void setData(List<SwapTaskData> data){
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
        private RowTaskListSwapBinding binding;

        public MyViewHolder(RowTaskListSwapBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnSwapTaskItemClickListener{
        void onClick(SwapTaskData task);
    }
}
