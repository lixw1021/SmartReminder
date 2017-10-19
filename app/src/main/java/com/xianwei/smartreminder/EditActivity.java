package com.xianwei.smartreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xianwei li on 10/18/2017.
 */

public class EditActivity extends AppCompatActivity {

    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);

        int pageId;
        Intent intent = getIntent();
        if (intent.hasExtra("pageId")) {
            pageId = intent.getIntExtra("pageId", 0);
            setupFragment(pageId);
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
}
