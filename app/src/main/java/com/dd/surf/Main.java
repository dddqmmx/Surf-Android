package com.dd.surf;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.dd.surf.view.util.adapter.AdapterMain;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Main extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    TextView titleText = null;

    int[] icons = {R.drawable.message,R.drawable.contact,R.drawable.person};
    int[] texts = {R.string.message,R.string.friend,R.string.my_info};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        layoutInflater = getLayoutInflater();

        View titleBar = layoutInflater.inflate(R.layout.view_title_main,new LinearLayout(this),false);
        titleText = titleBar.findViewById(R.id.title);
        titleText.setText(texts[0]);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(titleBar);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.show();
        }

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        AdapterMain adapterMain = new AdapterMain(this);
        viewPager.setAdapter(adapterMain);
        TabLayout tabLayout = findViewById(R.id.footTab);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(texts[position]);
            tab.setIcon(icons[position]);
        });
        tabLayoutMediator.attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                titleText.setText(texts[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}