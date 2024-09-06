package com.blazma.logistics.fragments.main.money;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.databinding.DataBindingUtil;

import com.blazma.logistics.R;
import com.blazma.logistics.databinding.FragmentMoneyTransferFinishBinding;
import com.blazma.logistics.fragments.BaseFragment;
import com.blazma.logistics.global.EventBusMessage;
import com.blazma.logistics.global.UserInfo;

import org.greenrobot.eventbus.EventBus;

public class MoneyTransferFinishFragment extends BaseFragment {
    private FragmentMoneyTransferFinishBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_money_transfer_finish, container, false);

        initViews();
        initActions();

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void initViews(){
        // Hide the Back button
        mBinding.btnBack.setVisibility(View.GONE);

        // Set the description
        mBinding.txtDescription.setText(R.string.verify_complete);
    }

    private void initActions() {
        // Done Button
        mBinding.btnDone.setOnClickListener(v -> {
            // Go to MoneyTransfer task list
            EventBus.getDefault().post(new EventBusMessage(EventBusMessage.MessageType.GO_TO_MONEY_TRANSFER));

            UserInfo.getInstance().selectedMoneyTask = null;
        });
    }
}
