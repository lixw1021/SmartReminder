package com.xianwei.smartreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.xianwei.smartreminder.fragment.AboutFragment;
import com.xianwei.smartreminder.fragment.FeedbackFragment;
import com.xianwei.smartreminder.fragment.RemoveAdFragment;
import com.xianwei.smartreminder.fragment.SettingFragment;
import com.xianwei.smartreminder.fragment.TaskDoneFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NaviActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_navigation)
    Toolbar toolbar;

    public static final String EXTRA_FRAGMENT_NAME = "com.xianwei.extra.FRAGMENT_NAME";
    public static final String EXTRA_TASK_DONE = "com.xianwei.extra.TASK_DONE";
    public static final String EXTRA_SETTING = "com.xianwei.extra.SETTING";
    public static final String EXTRA_REMOVE_AD = "com.xianwei.extra.REMOVE_AD";
    public static final String EXTRA_FEEDBACK = "com.xianwei.extra.FEEDBACK";
    public static final String EXTRA_ABOUT = "com.xianwei.extra.ABOUT";
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
        if (intent.hasExtra(EXTRA_FRAGMENT_NAME) && savedInstanceState == null) {
            launchFragment(intent.getStringExtra(EXTRA_FRAGMENT_NAME));
        }
        setSupportActionBar(toolbar);
    }

    private void launchFragment(String fragmentName) {
        switch (fragmentName) {
            case EXTRA_TASK_DONE:
                fragment = new TaskDoneFragment();
                toolbar.setTitle(getString(R.string.men_done));
                break;
            case EXTRA_SETTING:
                fragment = new SettingFragment();
                toolbar.setTitle(R.string.men_settings);
                break;
            case EXTRA_REMOVE_AD:
                fragment = new RemoveAdFragment();
                toolbar.setTitle(R.string.menu_remove_ads);
                break;
            case EXTRA_FEEDBACK:
                fragment = new FeedbackFragment();
                toolbar.setTitle(R.string.menu_feedback);
                break;
            case EXTRA_ABOUT:
                fragment = new AboutFragment();
                toolbar.setTitle(R.string.men_about);
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
