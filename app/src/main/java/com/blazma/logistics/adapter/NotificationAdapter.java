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
import com.blazma.logistics.databinding.ItemNotificationListBinding;
import com.blazma.logistics.databinding.RowTaskListBinding;
import com.blazma.logistics.model.login.DriverTask;
import com.blazma.logistics.model.notification.NotificationModel;
import com.blazma.logistics.utilities.AppConstants;
import com.blazma.logistics.utilities.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    private List<NotificationModel> mList = new ArrayList<>();

    public NotificationAdapter(Context context, List<NotificationModel> notificationList){
        this.context = context;
        this.mList = notificationList;
    }

    public void setData(List<NotificationModel> data){
        this.mList = data;
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
        ItemNotificationListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_notification_list, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NotificationModel item = mList.get(position);

        if(LocaleHelper.getLanguage(context).equals(AppConstants.LANGUAGE_ENGLISH)){
            holder.binding.tvTitle.setText(item.enTitle);
            holder.binding.tvMessage.setText(item.enDescription);
            holder.binding.tvTime.setText(item.enAgo);
        } else {
            holder.binding.tvTitle.setText(item.arabicTitle);
            holder.binding.tvMessage.setText(item.arabicDescription);
            holder.binding.tvTime.setText(item.arabicAgo);
        }

        switch (item.taskType){
            case 1:
                holder.binding.tvNotificationType.setText(context.getResources().getString(R.string.new_task));
                holder.binding.tvNotificationType.setTextColor(context.getResources().getColor(R.color.colorMainTextBlue));
                holder.binding.ivNotificationType.setImageResource(R.drawable.ic_new_tasklist);
                break;
            case 2:
                holder.binding.tvNotificationType.setText(context.getResources().getString(R.string.samples_in_freezer));
                holder.binding.tvNotificationType.setTextColor(context.getResources().getColor(R.color.colorYellow));
                holder.binding.ivNotificationType.setImageResource(R.drawable.ic_time);
                break;
            case 3:
            case 5:
                holder.binding.tvNotificationType.setText(context.getResources().getString(R.string.task_completed));
                holder.binding.tvNotificationType.setTextColor(context.getResources().getColor(R.color.colorGreen));
                holder.binding.ivNotificationType.setImageResource(R.drawable.ic_done_tasklist);
                break;
            case 4:
                holder.binding.tvNotificationType.setText(context.getResources().getString(R.string.sample_collected));
                holder.binding.tvNotificationType.setTextColor(context.getResources().getColor(R.color.colorPink));
                holder.binding.ivNotificationType.setImageResource(R.drawable.ic_collected);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(null!=mList) {
            return mList.size();
        }return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ItemNotificationListBinding binding;

        public MyViewHolder(ItemNotificationListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}