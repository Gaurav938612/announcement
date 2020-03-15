package com.example.announcement.custom_fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.announcement.R;
import com.example.announcement.databinding.ClubListWithCheckboxBinding;

import java.util.List;

public class CustomDialogAdapter extends RecyclerView.Adapter<CustomDialogAdapter.MyViewHolder> {

    public List<String> clubNames;
    public List<Boolean> subscribed;
    public CustomDialogAdapter( List<String> clubNames,List<Boolean> subscribed){
        this.clubNames=clubNames;
        this.subscribed=subscribed;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        ClubListWithCheckboxBinding binding= DataBindingUtil.inflate(inflater, R.layout.club_list_with_checkbox,parent,false);
        return new  MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.setClubName(clubNames.get(position));
        Boolean a=subscribed.get(position);
        CheckBox checkBox=holder.binding.checkbox;
        checkBox.setChecked(subscribed.get(position));
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribed.set(position,checkBox.isChecked());
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ClubListWithCheckboxBinding binding;
        public MyViewHolder(@NonNull ClubListWithCheckboxBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
