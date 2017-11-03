package com.xianwei.smartreminder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xianwei.smartreminder.adapter.ReminderPagerAdapter;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT =10000 ;
    @BindView(R.id.navigation_drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tableLayout;
    @BindView(R.id.fab_add)
    FloatingActionButton fab;
    @BindView(R.id.ib_voice_input)
    ImageButton voiceInputBtn;

    private ReminderPagerAdapter reminderPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupToolbar();

        reminderPagerAdapter = new ReminderPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(reminderPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        initNavigationDrawer();
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.toolbar_title_smart_reminder));
        toolbar.setNavigationIcon(R.drawable.ic_dehaze);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    private void setupTabIcons() {
        tableLayout.getTabAt(0).setIcon(R.drawable.ic_time_white);
        tableLayout.getTabAt(1).setIcon(R.drawable.ic_location_white);
    }

    @OnClick(R.id.fab_add)
    void addReminder() {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_PAGE_ID, viewPager.getCurrentItem());
        startActivity(intent);
    }

    @OnClick(R.id.ib_voice_input)
    void voidInput() {
        promptSpeechInput();
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    launchEditFragment(result.get(0));
                }
                break;
            }

        }
    }

    private void launchEditFragment(String viceInput) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_VOICE_INPUT, viceInput);
        intent.putExtra(EditActivity.EXTRA_PAGE_ID, viewPager.getCurrentItem());
        startActivity(intent);
    }

    private void initNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_done:
                        launchFragment(NaviActivity.EXTRA_TASK_DONE);
                        break;
                    case R.id.menu_settings:
                        launchFragment(NaviActivity.EXTRA_SETTING);
                        break;
                    case R.id.menu_remove_ads:
                        launchFragment(NaviActivity.EXTRA_REMOVE_AD);
                        break;
                    case R.id.menu_feedback:
                        launchFragment(NaviActivity.EXTRA_FEEDBACK);
                        break;
                    case R.id.menu_about:
                        launchFragment(NaviActivity.EXTRA_ABOUT);
                        break;
                    default:
                        break;
                }

                if (item.isChecked()) {
                    item.setChecked(false);
                }

                drawerLayout.closeDrawer(Gravity.START);
                return false;
            }
        });
    }

    private void launchFragment(String fragmentName) {
        Intent intent = new Intent(this, NaviActivity.class);
        intent.putExtra(NaviActivity.EXTRA_FRAGMENT_NAME, fragmentName);
        startActivity(intent);
    }
}
