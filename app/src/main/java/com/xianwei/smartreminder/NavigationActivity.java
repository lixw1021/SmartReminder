package com.xianwei.smartreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.xianwei.smartreminder.fragment.AboutFragment;
import com.xianwei.smartreminder.fragment.FeedbackFragment;
import com.xianwei.smartreminder.fragment.RemoveAdFragment;
import com.xianwei.smartreminder.fragment.SettingFragment;
import com.xianwei.smartreminder.fragment.TaskDoneFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavigationActivity extends AppCompatActivity {
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_navgation)
                .setSwipeBackView(R.layout.swipeback_customization);
        ButterKnife.bind(this);
        this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

        Intent intent = getIntent();
        if (intent.hasExtra("fragmentName")) {
            launchFragment(intent.getStringExtra("fragmentName"));
        }
    }

    private void launchFragment(String fragmentName) {
        switch (fragmentName) {
            case "taskDone":
                fragment = new TaskDoneFragment();
                break;
            case "setting":
                fragment = new SettingFragment();
                break;
            case "removeAd":
                fragment = new RemoveAdFragment();
                break;
            case "feedback":
                fragment = new FeedbackFragment();
                break;
            case "about":
                fragment = new AboutFragment();
                break;
            default:
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.navigation_fragment_container , fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }
}
