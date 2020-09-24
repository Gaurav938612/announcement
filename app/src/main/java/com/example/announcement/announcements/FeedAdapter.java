package com.example.announcement.announcements;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.announcement.databinding.FeedRecyclerviewItemlistBinding;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    public List<FeedModel> feedData=new ArrayList<>();
    public List<String> documentSnapShot=new ArrayList<>();
    private int layoutId;
    public FeedAdapter(@LayoutRes int layoutId){
        this.layoutId=layoutId;
    }
    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        FeedRecyclerviewItemlistBinding binding=DataBindingUtil.inflate(mInflater,i,viewGroup,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(feedData.get(i));
    }

    @Override
    public int getItemCount() {
        return feedData == null ? 0 : feedData.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        return layoutId;
    }
    public void setFeedModel(List<FeedModel> feedData,List<String> documentSnapShot) {
        this.feedData = feedData;
        this.documentSnapShot=documentSnapShot;
    }
    public void remove(int position){
        feedData.remove(position);
        documentSnapShot.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(FeedModel item,String id,int position){
        feedData.add(position,item);
        documentSnapShot.add(position,id);
        notifyItemInserted(position);
    }
    public void addNewData(FeedModel feedModel,String id){
        feedData.add(feedModel);
        documentSnapShot.add(id);
        notifyItemInserted(feedData.size()-1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private FeedRecyclerviewItemlistBinding binding;
        public ViewHolder(@NonNull FeedRecyclerviewItemlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(FeedModel feed_model) {
            binding.setModel(feed_model);
          //  binding.setVariable(BR.model, feed_model);
            binding.executePendingBindings();
        }

    }
}
