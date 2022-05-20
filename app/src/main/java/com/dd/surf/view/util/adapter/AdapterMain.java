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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.dd.surf.Developers;
import com.dd.surf.R;
import com.dd.surf.entity.Message;
import com.dd.surf.util.Control;
import com.google.android.material.tabs.TabLayout;

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

        List<Message> messageList = control.getMessageList(control.getUserName(),control.getPassword());
        for (Message message : messageList) {
            View messageView = layoutInflater.inflate(R.layout.view_message,null);
            messageListLayout.addView(messageView);
        }


        View friendList = layoutInflater.inflate(R.layout.view_friend_list,null);

        View myInfo = layoutInflater.inflate(R.layout.view_my_info,null);
        TextView userName = myInfo.findViewById(R.id.user_name);
        userName.setText(control.getUserName());
        TextView name = myInfo.findViewById(R.id.name);
        name.setText(control.getName(control.getUserName()));

        viewList.add(messagesList);
        viewList.add(friendList);
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