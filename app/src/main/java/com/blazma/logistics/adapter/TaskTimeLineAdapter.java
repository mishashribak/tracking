package com.blazma.logistics.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.ItemTaskTimelineBinding;
import com.blazma.logistics.databinding.RowTaskListBinding;

public class TaskTimeLineAdapter extends RecyclerView.Adapter<TaskTimeLineAdapter.TimeLineViewHolder>{

    private Context mContext;
    private int currentTaskStatus = 0;
    private int screenWidth = 0;

    private int ACTIVE_ITEM_INCREASE_WIDTH = 0;

    public TaskTimeLineAdapter(Context context, int taskStatus, int width){
        mContext = context;
        currentTaskStatus = taskStatus;
        screenWidth = width;

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        ACTIVE_ITEM_INCREASE_WIDTH = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, metrics);
    }

    @NonNull
    @Override
    public TimeLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskTimelineBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_task_timeline, parent, false);
        return new TimeLineViewHolder(binding, screenWidth);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeLineViewHolder holder, int position) {
        if (position >= currentTaskStatus){
            holder.mBinding.indicator.setImageResource(R.drawable.bg_circle_gray_7);
            holder.mBinding.progressBg.setBackgroundColor(mContext.getColor(R.color.colorProgressGray));

            holder.mBinding.thumb.setVisibility(View.GONE);
        }
        else{
            holder.mBinding.indicator.setImageResource(R.drawable.bg_circle_blue_7);
            holder.mBinding.progressBg.setBackgroundColor(mContext.getColor(R.color.colorMainBgBlue));

            if (position == currentTaskStatus - 1) {
                holder.mBinding.thumb.setVisibility(View.VISIBLE);

                // Set the width of item container
                ViewGroup.LayoutParams lParams = holder.mBinding.timelineElementLayout.getLayoutParams();
                lParams.width = lParams.width + ACTIVE_ITEM_INCREASE_WIDTH;
                holder.mBinding.timelineElementLayout.setLayoutParams(lParams);

            }
            else
                holder.mBinding.thumb.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {
        private ItemTaskTimelineBinding mBinding;

        public TimeLineViewHolder(ItemTaskTimelineBinding binding, int screenWidth){
            super(binding.getRoot());
            mBinding = binding;

            // Set the width of item container
            ViewGroup.LayoutParams lParams = mBinding.timelineElementLayout.getLayoutParams();
            lParams.width = (screenWidth - ACTIVE_ITEM_INCREASE_WIDTH) / 3;
            mBinding.timelineElementLayout.setLayoutParams(lParams);
        }
    }
}
