package com.dd.surf.view.util.adapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dd.surf.Chat;
import com.dd.surf.Developers;
import com.dd.surf.Main;
import com.dd.surf.R;
import com.dd.surf.TestActivity;
import com.dd.surf.UserInfo;
import com.dd.surf.entity.Message;
import com.dd.surf.pojo.User;
import com.dd.surf.service.TCPService;
import com.dd.surf.util.Control;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewPagerHolder>{

    Activity activity;

    List<View> viewList = new ArrayList<>();
    LayoutInflater layoutInflater;

    public TextView userName;
    public TextView name;

    public LinearLayout messageListLayout;
    public LinearLayout friendListLayout;

    public AdapterMain(Activity activity, TCPService service){
        this.activity = activity;


        /*Control control = (Control) activity.getApplication();*/

        layoutInflater= LayoutInflater.from(activity);

        View messagesList = layoutInflater.inflate(R.layout.view_message_list,null);

        messageListLayout = messagesList.findViewById(R.id.message_list);
        View friendsList = layoutInflater.inflate(R.layout.view_friend_list,null);
        friendListLayout = friendsList.findViewById(R.id.friend_list);

        View myInfo = layoutInflater.inflate(R.layout.view_my_info,null);
        userName = myInfo.findViewById(R.id.user_name);
        name = myInfo.findViewById(R.id.name);

        View developers = myInfo.findViewById(R.id.developers);
        developers.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, Developers.class));
        });

        View quit = myInfo.findViewById(R.id.quit);
        quit.setOnClickListener(v -> {
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        });

        viewList.add(messagesList);
        viewList.add(friendsList);
        viewList.add(myInfo);
    }

    public void addGroup(int id,String groupName){
        View messageView = layoutInflater.inflate(R.layout.view_message,null);
        TextView nameText = messageView.findViewById(R.id.name);
        nameText.setText(groupName);
        messageListLayout.addView(messageView);
        messageView.setOnClickListener((view)->{
            Intent chatInteger = new Intent(activity, Chat.class);
            chatInteger.putExtra("type",1);
            chatInteger.putExtra("id",id);
            activity.startActivity(chatInteger);
        });
    }

    public void addFriend(int id,String name){
        View friendView = layoutInflater.inflate(R.layout.view_message,null);
        friendView.setOnClickListener((view)->{
            activity.startActivity(new Intent(activity, UserInfo.class));
        });
        TextView nameText = friendView.findViewById(R.id.name);
        nameText.setText(name);
        friendListLayout.addView(friendView);
    }

    @NonNull
    @Override
    public ViewPagerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewPagerHolder(layoutInflater.inflate(R.layout.view_base_pager, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerHolder holder, int position) {
        holder.base_pager.removeAllViews();
        holder.base_pager.addView(viewList.get(position));
    }
    @Override
    public int getItemCount() {
        return viewList.size();
    }

    static class ViewPagerHolder extends RecyclerView.ViewHolder{
        ScrollView base_pager;
        public ViewPagerHolder(@NonNull View itemView) {
            super(itemView);
            base_pager = itemView.findViewById(R.id.list);
        }
    }
}