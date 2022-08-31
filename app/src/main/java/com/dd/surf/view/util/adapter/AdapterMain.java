package com.dd.surf.view.util.adapter;

import android.app.Activity;
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
import com.dd.surf.R;
import com.dd.surf.TestActivity;
import com.dd.surf.entity.Message;
import com.dd.surf.util.Control;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewPagerHolder>{

    Activity activity;

    List<View> viewList = new ArrayList<>();
    LayoutInflater layoutInflater;

    public AdapterMain(Activity activity){
        this.activity = activity;

        Control control = (Control) activity.getApplication();

        layoutInflater= LayoutInflater.from(activity);

        View messagesList = layoutInflater.inflate(R.layout.view_message_list,null);

        LinearLayout messageListLayout = messagesList.findViewById(R.id.message_list);

        List<Message> messageList = control.getGroupList(control.getUserName(),control.getPassword());
        for (Message message : messageList) {
            View messageView = layoutInflater.inflate(R.layout.view_message,null);
            TextView nameText = messageView.findViewById(R.id.name);
            nameText.setText(message.getName());
            messageListLayout.addView(messageView);
            messageView.setOnClickListener((view)->{
                Intent chatInteger = new Intent(activity, TestActivity.class);
                chatInteger.putExtra("type",message.getType());
                chatInteger.putExtra("id",message.getId());
                activity.startActivity(chatInteger);
            });
        }


        View friendsList = layoutInflater.inflate(R.layout.view_friend_list,null);
        LinearLayout friendListLayout = friendsList.findViewById(R.id.friend_list);
        JSONArray friendList = control.getFriendList(control.getUserName(),control.getPassword());
        for (int i = 0 ; i < friendList.length(); i++){
            try {
                View friendView = layoutInflater.inflate(R.layout.view_message,null);
                TextView nameText = friendView.findViewById(R.id.name);
                nameText.setText(control.getNameById((int) friendList.get(i)));
                friendListLayout.addView(friendView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        View myInfo = layoutInflater.inflate(R.layout.view_my_info,null);
        TextView userName = myInfo.findViewById(R.id.user_name);
        userName.setText(control.getUserName());
        TextView name = myInfo.findViewById(R.id.name);
        name.setText(control.getNameByUserName(control.getUserName()));

        viewList.add(messagesList);
        viewList.add(friendsList);
        viewList.add(myInfo);
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