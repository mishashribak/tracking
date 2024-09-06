package com.blazma.logistics.activities;

import android.os.Bundle;
import com.blazma.logistics.R;
import com.blazma.logistics.fragments.schedule.ScheduleListFragment;
import com.blazma.logistics.utilities.FragmentProcess;
import com.blazma.logistics.utilities.LocaleHelper;

public class ScheduleActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        FragmentProcess.replaceFragment(getSupportFragmentManager(), new ScheduleListFragment(), R.id.frameLayout);
    }


    @Override
    protected void onResume() {
        super.onResume();

        LocaleHelper.onAttach(this);
    }
}
