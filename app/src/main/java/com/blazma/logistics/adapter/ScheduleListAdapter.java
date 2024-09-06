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
import com.blazma.logistics.databinding.RowScheduleListBinding;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.model.login.Car;
import com.blazma.logistics.model.schedule.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.MyViewHolder> {

    private OnTaskItemClickListener onItemClickListener;
    private Context context;
    private List<Schedule> mScheduleList = new ArrayList<>();
    private Car car;
    public ScheduleListAdapter(Context context, OnTaskItemClickListener onItemClickListener){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.car = UserInfo.getInstance().loginInfo.getCar();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Schedule> data){
        this.mScheduleList = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowScheduleListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_schedule_list, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Schedule task = mScheduleList.get(position);
        holder.binding.tvName.setText(task.client);
        holder.binding.tvCar.setText("Car No: "+car.getPlateNumber()+"\n"+"Model: "+car.getModel());
        holder.binding.tvFromLocation.setText(String.format(context.getString(R.string.from_location_format), task.fromLocation.name));
        holder.binding.tvToLocation.setText(String.format(context.getString(R.string.to_location_format), task.toLocation.name));
        holder.binding.tvFromLocation.setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View view) {

                                                             }
                                                         });
        holder.binding.tvToLocation.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View view) {

                                                           }
                                                       });
                holder.binding.tvNote.setText(task.date);
        holder.binding.tvTime.setText(String.format(context.getString(R.string.from_location_format), task.time));
    }

    @Override
    public int getItemCount() {
        if(null!=mScheduleList) {
            return mScheduleList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RowScheduleListBinding binding;

        public MyViewHolder(RowScheduleListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnTaskItemClickListener{
        void onClick(Schedule task);
    }
}
