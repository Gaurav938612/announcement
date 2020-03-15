package com.example.announcement.announcements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.announcement.R;
import java.util.ArrayList;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.MyViewHolder>  {

    private ArrayList<String> list;
    private Context context;
    ChannelListener listener;
    public  ChannelAdapter(Context context,ArrayList<String> list){
        this.context=context;
        this.list=list;
        this.listener=(ChannelListener)context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.channel_item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.textView.setText(list.get(position));
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name=holder.textView.getText().toString();
                    listener.callBack(name);
                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.channel_id);
            layout=itemView.findViewById(R.id.layout_id);
        }
    }

}
