package com.example.announcement.announcements;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.announcement.R;
import com.example.announcement.authentication.User;
import com.example.announcement.base_activity.BaseActivity;
import com.example.announcement.custom_fragment.CustomDialogAdapter;
import com.example.announcement.custom_fragment.CustomDialogFragment;
import com.example.announcement.databinding.ActivityFeedBinding;
import com.example.announcement.databinding.BottomSheetBinding;
import com.example.announcement.touch_helper.RecyclerItemTouchHelper;
import com.example.announcement.touch_helper.TouchListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends BaseActivity<ActivityFeedBinding> implements ChannelListener, TouchListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG="FeedActivity";
    BottomSheetDialog sheetDialog;
    FeedViewModel feedViewModel;
    SwipeRefreshLayout mLayout;
    DrawerLayout rootLayout;
    FeedModel deleteItem;
    int position;
    CustomDialogAdapter customDialogAdapter;
    CustomDialogFragment dialogFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feed;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityFeedBinding binding = getViewDataBinding();
        feedViewModel = ViewModelProviders.of(this).get(FeedViewModel.class);
        if (savedInstanceState == null) {
            feedViewModel.init();
        }

        createSheetLayout();
        binding.setFeedModel(feedViewModel);
        binding.feedRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.feedRecyclerview.setAdapter(feedViewModel.getAdapter());
        mLayout = findViewById(R.id.swipeRefreshId);
        mLayout.setOnRefreshListener(this);
       /* binding.feedRecyclerview.setItemAnimator(new DefaultItemAnimator());
        binding.feedRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));*/
        rootLayout = findViewById(R.id.drawer_layout);
        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(item).attachToRecyclerView(binding.feedRecyclerview);


        binding.feedRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                feedViewModel.fetchNewData(recyclerView, dx, dy);
            }
        });
        binding.navView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.bottom_nav_item1) {
                sheetDialog.show();
            } else if (id == R.id.bottom_nav_item2) {
                feedViewModel.fetchData(1, "");
            } else if (id == R.id.bottom_nav_item3) {

                // Toast.makeText(FeedActivity.this, "notification button clicked", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    void createSheetLayout() {
        if (sheetDialog == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            BottomSheetBinding sheetBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet, null, false);
            // View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet, null);  //if you don't want to use data binding
            sheetDialog = new BottomSheetDialog(this);
            View view = sheetBinding.getRoot();
            Context context = sheetBinding.getRoot().getContext();
            sheetDialog.setContentView(view);
            sheetBinding.recyclerId.setLayoutManager(new LinearLayoutManager(context));
            ChannelAdapter ch = new ChannelAdapter(context, feedViewModel.getList());
            sheetBinding.recyclerId.setAdapter(ch);
        }
    }

    @Override
    public void callBack(String clubName) {
        sheetDialog.dismiss();
        feedViewModel.fetchData(3, clubName);
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof FeedAdapter.ViewHolder) {
            this.position = viewHolder.getAdapterPosition();
            deleteItem = feedViewModel.getDataAt(this.position);
            feedViewModel.removeDataAt(this.position);
        }
        Snackbar snackbar = Snackbar.make(rootLayout, "Item removed ", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedViewModel.restoreItem(deleteItem, position);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void showAlertDialog(View view) {
        List<Boolean> values= new ArrayList<>();
        List<String> clubNames=new ArrayList<>();
        DocumentReference reference= FirebaseFirestore.getInstance()
                .collection("announcement/annDocument/usersSubscriptions")
                .document(User.userId);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Object> data;
                data=documentSnapshot.getData();
                for(Object key:data.keySet()){
                    clubNames.add(key.toString());
                    String val=data.get(key).toString();
                    values.add(new Boolean(Boolean.valueOf(val)));
                    Log.d(key.toString(),val);
                }
                Log.d("boolean",values.toString());
                customDialogAdapter = new CustomDialogAdapter(clubNames,values);
                dialogFragment = new CustomDialogFragment(customDialogAdapter);
                dialogFragment.show(getSupportFragmentManager(),"tag");
            }
        });

    }
    public void saveSubscribed(View view){
        List<Boolean> subscribed=customDialogAdapter.subscribed;
        List<String> clubNames=customDialogAdapter.clubNames;
        Map<String,Boolean> subscriptions=new HashMap<>();
        for(int i=0;i<subscribed.size();i++)
            subscriptions.put(clubNames.get(i),subscribed.get(i));
        FirebaseFirestore.getInstance().collection("announcement/annDocument/usersSubscriptions").document(User.userId)
                .set(subscriptions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("feed activity","subscriptions saved");
            }
        });
        dialogFragment.dismiss();
    }
    public void cancelDialog(View view) {
        dialogFragment.dismiss();
    }

    @Override
    public void onRefresh() {
        if (feedViewModel.specific) {
            mLayout.setRefreshing(false);
            return;
        }
        final ArrayList<FeedModel> newData = new ArrayList<>();
        CollectionReference reference = feedViewModel.getReference();
        final String end_key = feedViewModel.getEnd_key();
        Query query1 = reference.orderBy("date", Query.Direction.DESCENDING).limit(15);
        query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                feedViewModel.setMaxData(false);
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    try {
                        FeedModel f = snapshot.toObject(FeedModel.class);
                        newData.add(f);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                    String id = snapshot.getId();
                    if (id.equals(end_key))
                        feedViewModel.setMaxData(true);
                }
                feedViewModel.setFeedDataInAdapter(newData);
                DocumentSnapshot last_node = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                feedViewModel.setLast_node(last_node);
                mLayout.setRefreshing(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLayout.setRefreshing(false);
                Toast.makeText(FeedActivity.this, "sync failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
