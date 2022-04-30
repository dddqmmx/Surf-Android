package com.dd.surf.view.util.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dd.surf.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewPagerHolder>{

    Activity activity;

    List<View> viewList = new ArrayList<>();
    LayoutInflater layoutInflater;

    public AdapterMain(Activity activity){
        this.activity = activity;
        layoutInflater= LayoutInflater.from(activity);

        View messagesList = layoutInflater.inflate(R.layout.view_message_list,null);
        View friendList = layoutInflater.inflate(R.layout.view_friend_list,null);
        View myInfo = layoutInflater.inflate(R.layout.view_my_info,null);

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