package com.xianwei.smartreminder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TableLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tableLayout;
    @BindView(R.id.fab_add)
    FloatingActionButton fab;

    private ReminderPagerAdapter reminderPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle("Smart Reminder");

        reminderPagerAdapter = new ReminderPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(reminderPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tableLayout.getTabAt(0).setIcon(R.drawable.ic_time_white);
        tableLayout.getTabAt(1).setIcon(R.drawable.ic_location_white);
    }

    @OnClick(R.id.fab_add)
    void addReminder(){
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("pageId", viewPager.getCurrentItem());
        startActivity(intent);
    }
}
