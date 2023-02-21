package com.dd.surf;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.BitMapUtil;
import com.dd.surf.util.Client;
import com.dd.surf.view.util.adapter.AdapterMain;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

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
                    //service.getUserFriend();
                    break;
                case "getGroupList":
                    try {
                        JSONArray groupList = new JSONArray(intent.getStringExtra("groupList"));
                        for (int i = 0; i < groupList.length(); i++) {
                            JSONObject jsonObject = groupList.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String groupName = jsonObject.getString("groupName");
                            Client.groupList.add(id);
                            adapterMain.addGroup(id,groupName);
                            service.getGroupHead(id);
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
                            int id = groupList.getInt(i);
                            Client.friendsList.add(id);
                            adapterMain.addFriend(id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case "getUserInfoById":
                    int id = intent.getIntExtra("id", 0);
                    user = Client.getUser(id);
                    for (int i = 0 ; i < adapterMain.friendListLayout.getChildCount();i++){
                        View child = adapterMain.friendListLayout.getChildAt(i);
                        if (child.getId() == R.id.message_root){
                            if (child.getContentDescription() == String.valueOf(id)){
                                TextView nameText = child.findViewById(R.id.name);
                                nameText.setText(user.getName());
                                TextView messageText = child.findViewById(R.id.message);
                                messageText.setText(user.getPersonalProfile());
                            }
                        }
                    }
                    break;
                case "agreeRequest":
                    int code = intent.getIntExtra("code",1);
                    if (code == 0){
                        id = intent.getIntExtra("id",0);
                        adapterMain.addFriend(id);
                    }
                    break;
                case "getGroupHead":
                    int groupId = intent.getIntExtra("groupId", 0);
                    for (int i = 0 ; i < adapterMain.messageListLayout.getChildCount();i++){
                        View child = adapterMain.messageListLayout.getChildAt(i);
                        if (child.getId() == R.id.message_root){
                            if (child.getContentDescription() == String.valueOf(groupId)){
                                ImageView head = child.findViewById(R.id.head);
                                head.setImageBitmap(BitMapUtil.openImage(Main.this.getExternalFilesDir("image/group/").getAbsolutePath()+"/"+groupId+".sf"));
                            }
                        }
                    }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_create_group:
                /*Intent chatInteger = new Intent(Main.this, GroupInfo.class);
                chatInteger.putExtra("id",1);
                Main.this.startActivity(chatInteger);*/
                service.getGroupHead(1);
                break;
            case R.id.menu_add_group_or_friend:
                Main.this.startActivity(new Intent(this,AddFriendAndGroup.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }


}