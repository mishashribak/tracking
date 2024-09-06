package com.blazma.logistics.utilities;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blazma.logistics.R;

public class FragmentProcess {
    public static void addFragment(FragmentManager supportFragment, Fragment mFragment, int container) {
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.add(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void addFragment(FragmentManager supportFragment, Fragment mFragment, Bundle bundle, int container) {
        if (bundle != null) {
            mFragment.setArguments(bundle);
        }
        FragmentTransaction transaction = supportFragment.beginTransaction();
        transaction.add(container, mFragment, mFragment.getClass().getSimpleName());
        transaction.addToBackStack(mFragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(FragmentManager supportFragment, Fragment fragment, Bundle bundle, int layout) {
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction transaction =supportFragment.beginTransaction();
        transaction.replace(layout, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragment(FragmentManager supportFragment, Fragment fragment, int layout) {
        FragmentTransaction transaction =supportFragment.beginTransaction();
        transaction.replace(layout, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void replaceFragmentWithoutBackStack(FragmentManager supportFragment, Fragment fragment, int layout) {
        FragmentTransaction transaction =supportFragment.beginTransaction();
        transaction.replace(layout, fragment, fragment.getClass().getSimpleName());
        transaction.commitAllowingStateLoss();
    }

    public static void removeCurrentFragment(FragmentManager supportFragment) {
        supportFragment.popBackStack(supportFragment.getBackStackEntryAt(supportFragment.getBackStackEntryCount()-1).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void clearBackStack(FragmentManager supportFragment){
        supportFragment.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void moveToSpecifyFragment(FragmentManager supportFragment, String fragmentSimpleName){
        for (int i = supportFragment.getBackStackEntryCount() - 1; i > 0; i--){
            if (!fragmentSimpleName.equals(supportFragment.getBackStackEntryAt(i).getName())) {
                supportFragment.popBackStack();
            }
            else
            {
                break;
            }
        }
    }
}
