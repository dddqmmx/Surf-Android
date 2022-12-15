package com.dd.surf;

import static android.widget.Toast.makeText;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Client;
import com.dd.surf.view.util.adapter.AdapterMain;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main extends AppCompatActivity {

    LayoutInflater layoutInflater = null;
    TextView titleText = null;

    int[] icons = {R.drawable.message,R.drawable.contact,R.drawable.person};
    int[] texts = {R.string.message,R.string.friend,R.string.my_info};

    private MyServiceConn conn;
    private TCPService service;

    private ContentReceiver mReceiver;

    public AdapterMain adapterMain;
    public TabLayoutMediator tabLayoutMediator;
    public ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Client client = (Client) getApplication();

        conn=new MyServiceConn();
        bindService(new Intent(Main.this, TCPService.class), conn, BIND_AUTO_CREATE);

        mReceiver = new ContentReceiver();
        //新添代码，在代码中注册广播接收程序
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.dd.surf.service.tcpClient");
        registerReceiver(mReceiver, filter);

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

        viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.footTab);

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(texts[position]);
            tab.setIcon(icons[position]);
        });
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

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((TCPService.LocalBinder) binder).getService();
            Client.setTcpService(service);

            adapterMain = new AdapterMain(Main.this,service);
            viewPager.setAdapter(adapterMain);
            tabLayoutMediator.attach();

            service.getUserInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            service = null;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unbindService(conn);
        if (mReceiver!=null) {
            unregisterReceiver(mReceiver);
        }
    }

    public class ContentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra("command");
            String userName = null;
            String name = null;
            switch (command) {
                case "getUserInfo":
                    User user = Client.getUser(Client.userId);
                    adapterMain.name.setText(user.getName());
                    adapterMain.userName.setText(user.getUserName());
                    service.getGroupList();
                    break;
                case "getGroupList":
                    try {
                        JSONArray groupList = new JSONArray(intent.getStringExtra("groupList"));
                        for (int i = 0; i < groupList.length(); i++) {
                            JSONObject jsonObject = groupList.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String groupName = jsonObject.getString("groupName");
                            adapterMain.addGroup(id,groupName);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    service.getUserFriend();
                    break;
                case "getUserFriendList":
                    try {
                        JSONArray groupList = new JSONArray(intent.getStringExtra("userList"));
                        for (int i = 0; i < groupList.length(); i++) {
                            JSONObject jsonObject = groupList.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            Client.friendsList.add(id);
                            userName = jsonObject.getString("userName");
                            name = jsonObject.getString("name");
                            adapterMain.addFriend(id,name);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }
}