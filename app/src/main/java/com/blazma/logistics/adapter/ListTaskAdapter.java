package com.blazma.logistics.adapter;

import static com.blazma.logistics.utilities.AppConstants.TASK_STATUS_NEW;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.BaseActivity;
import com.blazma.logistics.databinding.RowTaskListBinding;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.task.SampleBarCodeModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ListTaskAdapter extends RecyclerView.Adapter<ListTaskAdapter.MyViewHolder> {

    private OnTaskItemClickListener onItemClickListener;
    private Context context;
    private List<DriverTask> mTaskList = new ArrayList<>();
    private boolean isTaskConfirmPending;

    public ListTaskAdapter(Context context, List<DriverTask> taskList,boolean isTaskConfirmPending, OnTaskItemClickListener onItemClickListener){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.mTaskList = taskList;
        this.isTaskConfirmPending=isTaskConfirmPending;
    }

    public void setData(List<DriverTask> data,boolean isTaskConfirmPending){
        this.mTaskList = data;
        this.isTaskConfirmPending= isTaskConfirmPending;
        ((BaseActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTaskListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_task_list, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DriverTask task = mTaskList.get(position);

        // Display the task information
        holder.binding.tvFrom.setText(task.getFromLocationName());
        holder.binding.tvTo.setText(task.getToLocationName());

        holder.binding.tvDate.setText(task.getTaskDate());
        holder.binding.tvTime.setText(task.getTaskTime());

//        //hide the accept button, if already accepted by driver
//        if(UserInfo.getInstance().selectedTaskType.equals(TASK_STATUS_NEW)) {
//            if (task.isConfirmedByDriver()) {
//                holder.binding.btnAccept.setVisibility(View.GONE);
//                holder.binding.tvAccepted.setVisibility(View.VISIBLE);
//            } else {
//                holder.binding.btnAccept.setVisibility(View.VISIBLE);
//                holder.binding.tvAccepted.setVisibility(View.GONE);
//            }
//        }

        if(UserInfo.getInstance().selectedTaskType.equals(TASK_STATUS_NEW)){
            holder.binding.viewBagNumbers.setVisibility(View.GONE);
            holder.binding.tvWaitingTime.setText(String.format(context.getString(R.string.waiting_time_format), TimeUtil.convertToHoursAndMinutes(task.getPickupWaitingTime())));

        } else {
            holder.binding.viewBagNumbers.setVisibility(View.VISIBLE);
            holder.binding.tvBags.setText(String.format(context.getResources().getString(R.string.bags), task.numberOfBags));
        }

        if(UserInfo.getInstance().selectedTaskType.equals(AppConstants.TASK_STATUS_OUT_FREEZER)){
            holder.binding.viewBags.setVisibility(View.GONE);
        } else {
            holder.binding.viewBags.setVisibility(View.VISIBLE);

            holder.binding.txtRtBag.setText(String.format(context.getResources().getString(R.string.rtCount), task.rtCount));
            holder.binding.txtRefBag.setText(String.format(context.getResources().getString(R.string.refCount), task.refCount));
            holder.binding.txtFrzBag.setText(String.format(context.getResources().getString(R.string.frzCount), task.frzCount));
        }

        // Task type
        holder.binding.txtTaskType.setText(task.taskType);

        holder.binding.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTaskConfirmPending){
                    Toast.makeText(context,context.getString(R.string.accept_tasks),Toast.LENGTH_SHORT).show();
                }else{
                    onItemClickListener.onClick(task);
                }
            }
        });

        // Swap
        holder.binding.ivSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the swap
                onItemClickListener.onClickSwap(task);
            }
        });
//        //Accept the task
//        holder.binding.btnAccept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            onItemClickListener.onAccept(task);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if(null!=mTaskList) {
            return mTaskList.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowTaskListBinding binding;

        public MyViewHolder(RowTaskListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnTaskItemClickListener{
        void onClick(DriverTask task);
        void onClickSwap(DriverTask task);
        void onAccept(DriverTask task);
    }
}
