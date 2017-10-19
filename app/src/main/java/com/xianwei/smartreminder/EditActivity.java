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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/18/2017.
 */

public class EditActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_edit)
    Toolbar toolbar;

    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_edit)
                .setSwipeBackView(R.layout.swipeback_customization);
        ButterKnife.bind(this);
        this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

        int pageId;
        Intent intent = getIntent();
        if (intent.hasExtra("pageId")) {
            pageId = intent.getIntExtra("pageId", 0);
            setupFragment(pageId);
            toolbar.setTitle("New Task");
        }
    }

    private void setupFragment(int pageId) {
        if (pageId == 0) {
            fragment = new EditTimeFragment();
        } else {
            fragment = new EditLocationFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.swipeback_stack_to_front,
                R.anim.swipeback_stack_right_out);
    }
}
