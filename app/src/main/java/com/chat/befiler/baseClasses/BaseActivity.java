package com.chat.befiler.baseClasses;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.chat.befiler.activities.MainActivity;
import com.example.signalrtestandroid.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context context;
    private  MainActivity mainActivity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
    }

    public void ReplaceFragmentWithBackStack(Fragment fragment) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.main_content, fragment);
        trans.addToBackStack(fragment.getClass().getName());
        trans.commitAllowingStateLoss();
    }

    public MainActivity getMainActivity() {

        if (mainActivity != null)
            return mainActivity;
        else {
            if (context instanceof MainActivity) {
                mainActivity = ((MainActivity) context);
            } else
                return null;
        }
        return mainActivity;
    }


    public void ReplaceFragment(Fragment fragment, boolean animation, Bundle arguments ,boolean withBundle) {
        if(getSupportFragmentManager()!=null) {
            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if(withBundle){
                    fragment.setArguments(arguments);
                }
                if (fragment.isAdded()) {
                    clearBackStack();
                    ft.add(R.id.main_content, fragment);
                } else {
                    ft.replace(R.id.main_content, fragment);
                }
                ft.addToBackStack(null);
                ft.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ReplaceFragmentWithoutClearBackStack(Fragment fragment, boolean animation, Bundle argument,boolean withBundle) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        if (withBundle){
            fragment.setArguments(argument);
        }
        if (animation){
             //trans.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_right);
        }
        trans.addToBackStack(fragment.getClass().getName());
        trans.replace(R.id.main_content, fragment);
        trans.commitAllowingStateLoss();
    }

    public void ReplaceFragmentWithBundleBackStack(Fragment fragment ,Bundle bundle) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        trans.replace(R.id.main_content, fragment);
        trans.addToBackStack(fragment.getClass().getName());
        trans.commit();
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.main_content);
    }

    protected void popBackStackTillEntry(int entryIndex) {
        if (getSupportFragmentManager() == null)
            return;
        if (getSupportFragmentManager().getBackStackEntryCount() <= entryIndex)
            return;
        FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(entryIndex);
        if (entry != null) {
            getSupportFragmentManager().popBackStack(entry.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void popFragment() {
        if (getSupportFragmentManager() == null)
            return;
        getSupportFragmentManager().popBackStack();
    }


    protected boolean isTextNullOrEmpty(String text) {
        if (text == null) {
            return true;
        }
        if (text.isEmpty()) {
            return true;
        }
        if (text.trim().equalsIgnoreCase("")) {
            return true;
        }
        return text.trim().equalsIgnoreCase("null");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void clearBackStack() {
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
