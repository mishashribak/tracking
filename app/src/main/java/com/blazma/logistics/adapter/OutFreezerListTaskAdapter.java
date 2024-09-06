package com.blazma.logistics.adapter;

import static com.blazma.logistics.utilities.AppConstants.TASK_STATUS_NEW;
import static com.blazma.logistics.utilities.AppConstants.TASK_STATUS_OUT_FREEZER;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blazma.logistics.R;
import com.blazma.logistics.activities.BaseActivity;
import com.blazma.logistics.databinding.RowTaskListBinding;
import com.blazma.logistics.databinding.RowTaskListOutfreezerBinding;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.outfreezer.OutFreezerTaskModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.LocaleHelper;
import com.blazma.logistics.utilities.TimeUtil;
import com.google.firebase.installations.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OutFreezerListTaskAdapter extends RecyclerView.Adapter<OutFreezerListTaskAdapter.MyViewHolder>  {

    private OutFreezerListTaskAdapter.OnTaskItemClickListener onItemClickListener;
    private Context context;
    private List<OutFreezerTaskModel> mTaskList = new ArrayList<>();

    public OutFreezerListTaskAdapter(Context context, List<OutFreezerTaskModel> taskList, OutFreezerListTaskAdapter.OnTaskItemClickListener onItemClickListener){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.mTaskList = taskList;
    }

    public void setData(List<OutFreezerTaskModel> data){
        this.mTaskList = data;
        ((BaseActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public OutFreezerListTaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowTaskListOutfreezerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_task_list_outfreezer, parent, false);
        return new OutFreezerListTaskAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OutFreezerListTaskAdapter.MyViewHolder holder, int position) {
        OutFreezerTaskModel task = mTaskList.get(position);

//        if(LocaleHelper.getLanguage(context).equals(AppConstants.LANGUAGE_ENGLISH)){
//            holder.binding.tvClientName.setText(task.enName);
//        } else {
//            holder.binding.tvClientName.setText(task.arName);
//        }

        //hide the accept button, if already accepted by driver
        if(UserInfo.getInstance().selectedTaskType.equals(TASK_STATUS_OUT_FREEZER)) {
            if (task.isConfirmedByDriver()) {
                holder.binding.btnAccept.setVisibility(View.GONE);
                holder.binding.tvAccepted.setVisibility(View.VISIBLE);
            } else {
                holder.binding.btnAccept.setVisibility(View.VISIBLE);
                holder.binding.tvAccepted.setVisibility(View.GONE);
            }
        }

        holder.binding.tvClientName.setText(task.clientName);
        holder.binding.txtNumberOfBag.setText(String.format(context.getResources().getString(R.string.number_of_bags), task.bagIds.size()));
        holder.binding.txtNumberOfTask.setText(String.format(context.getResources().getString(R.string.number_of_tasks), task.taskIds.size()));
        holder.binding.tvWaitingTime.setText(String.format(context.getString(R.string.waiting_time_format), TimeUtil.convertToHoursAndMinutes(task.dropOffWaitingTime)));

        // Select the item
        holder.binding.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //drop off samples - confirmation validation
                if(UserInfo.getInstance().selectedTaskType.equals(TASK_STATUS_OUT_FREEZER)) {
                    if (task.isConfirmedByDriver()) {
                        // Go Map Screen
                        onItemClickListener.onClick(task);
                    }
                }
                //samples pull out - no need confirmation validation
                else{
                    onItemClickListener.onClick(task);
                }
            }
        });

        // Select the location icon

        holder.binding.viewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (task.latitude != null && task.longitude != null) {
                    // Go to google maps
                    try{
                        String uri = String.format(Locale.ENGLISH, "https://www.google.com/maps/search/?api=1&query=%f,%f", task.latitude, task.longitude);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    Log.e("Dropoff", "Location is null...");
                }
            }
        });
        //Accept the task
        holder.binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onAccept(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(null!=mTaskList) {
            return mTaskList.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowTaskListOutfreezerBinding binding;

        public MyViewHolder(RowTaskListOutfreezerBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnTaskItemClickListener{
        void onClick(OutFreezerTaskModel task);

        void onAccept(OutFreezerTaskModel task);
    }
}
