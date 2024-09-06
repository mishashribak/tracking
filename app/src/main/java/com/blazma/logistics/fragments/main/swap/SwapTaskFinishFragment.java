package com.blazma.logistics.fragments.main.swap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentSwapFinishBinding;
import com.blazma.logistics.databinding.FragmentTaskStatusBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;
import com.blazma.logistics.utilities.LocaleHelper;
import org.greenrobot.eventbus.EventBus;

public class SwapTaskFinishFragment extends BaseFragment {
    private final String TAG = SwapTaskFinishFragment.class.getSimpleName();
    private FragmentSwapFinishBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_swap_finish, container, false);

        initViews();
        initActions();

        return mBinding.getRoot();
    }

    private void initViews(){
//        if(LocaleHelper.getLanguage(mActivity).equals("ar")){
//            mBinding.btnBack.setRotation(180);
//        }

        // Hide the Back button
        mBinding.btnBack.setVisibility(View.GONE);

        // Set the description
        mBinding.txtDescription.setText(R.string.task_swapped_successfully);
    }

    private void initActions() {
        // Done Button
        mBinding.btnDone.setOnClickListener(v -> {
            // Go to Swap task list
            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_SWAP_TASK));

            UserInfo.getInstance().selectedSwapTask = null;
        });
    }
}
