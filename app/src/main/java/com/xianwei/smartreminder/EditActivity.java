package com.xianwei.smartreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_edit)
                .setSwipeBackView(R.layout.swipeback_customization);
        ButterKnife.bind(this);
        this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);




        Intent intent = getIntent();
        if (intent.hasExtra("pageId") && savedInstanceState == null) {
            toolbar.setTitle("New Task");
            setupFragment(intent);
        } else if (intent.hasExtra("itemId") && savedInstanceState == null) {
            toolbar.setTitle("Edit Task");
            setupEditFragment(intent);
        }
        setSupportActionBar(toolbar);
    }

    private void setupFragment(Intent intent) {
        int pageId = intent.getIntExtra("pageId", 0);
        fragment = (pageId == 0) ? new EditTimeFragment() : new EditLocationFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_fragment_container, fragment)
                .commit();
    }

    private void setupEditFragment(Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.i("12345bundle=null", String.valueOf(bundle == null));
        EditTimeFragment fragment = new EditTimeFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.edit_fragment_container, new EditTimeFragment())
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
