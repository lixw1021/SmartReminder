package com.xianwei.smartreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;
import com.xianwei.smartreminder.fragment.EditLocationFragment;
import com.xianwei.smartreminder.fragment.EditTimeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/18/2017.
 */

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_edit)
    Toolbar toolbar;


    public static final String EXTRA_PAGE_ID = "com.xianwei.extra.PAGE_ID";
    public static final String EXTRA_VOICE_INPUT = "com.xianwei.extra.VOICE_INPUT";
    public static final String EXTRA_EDIT_FRAGMENT = "com.xianwei.extra.EDIT_FRAGMENT";
    public static final String EXTRA_TIME_EDIT = "com.xianwei.extra.TIME_EDIT";
    public static final String EXTRA_LOCATION_EDIT = "com.xianwei.extra.LOCATION_EDIT";
    public static final String EXTRA_ITEM_ID = "com.xianwei.extra.ITEM_ID";

    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_edit)
                .setSwipeBackView(R.layout.swipeback_customization);
        ButterKnife.bind(this);
        this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PAGE_ID) && savedInstanceState == null) {
            toolbar.setTitle(getString(R.string.toolbar_title_new_task));
            if (intent.hasExtra(EXTRA_VOICE_INPUT)) {
                setupVoiceInputFragment(intent);
            } else {
                setupFragment(intent);
            }
        } else if (intent.hasExtra(EXTRA_EDIT_FRAGMENT) && savedInstanceState == null) {
            toolbar.setTitle(getString(R.string.toolbar_title_edit_task));
            setupEditFragment(intent);
        }
        setSupportActionBar(toolbar);
    }

    private void setupVoiceInputFragment(Intent intent) {
        int pageId = intent.getIntExtra(EXTRA_PAGE_ID, 0);
        Bundle bundle = intent.getExtras();

        fragment = (pageId == 0) ? new EditTimeFragment() : new EditLocationFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_fragment_container, fragment)
                .commit();
    }

    private void setupFragment(Intent intent) {
        int pageId = intent.getIntExtra(EXTRA_PAGE_ID, 0);
        fragment = (pageId == 0) ? new EditTimeFragment() : new EditLocationFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_fragment_container, fragment)
                .commit();
    }

    private void setupEditFragment(Intent intent) {
        String name = intent.getStringExtra(EXTRA_EDIT_FRAGMENT);
        fragment = (name.equals(EXTRA_TIME_EDIT)) ? new EditTimeFragment() : new EditLocationFragment();
        Bundle bundle = intent.getExtras();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }
}
