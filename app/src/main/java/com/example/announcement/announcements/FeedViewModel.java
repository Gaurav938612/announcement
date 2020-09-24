package com.example.announcement.announcements;

import android.app.AlertDialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.announcement.R;
import com.example.announcement.authentication.User;
import com.example.announcement.custom_fragment.CustomDialogAdapter;
import com.example.announcement.custom_fragment.CustomDialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Objects;

public class FeedViewModel extends ViewModel {

    DocumentSnapshot last_node;

    ArrayList<String> list;
    private FeedAdapter adapter;
    CustomDialogAdapter customDialogAdapter;
    private final int item_load_count = 10;
    public boolean isLoading, isMaxData = false,specific=false;
    private String  end_key="",last_key="",clubName="";

    public CollectionReference REFERENCE1, REFERENCE2,REFERENCE3;
    public DocumentReference REFERENCE4;
    public FirebaseFirestore store;
    public CustomDialogFragment dialogFragment;
    public ObservableBoolean startLoading=new ObservableBoolean();



    public ObservableBoolean getStartLoading() {
        return startLoading;
    }
    public void setLast_node(DocumentSnapshot last_node) {
        this.last_node = last_node;
    }
    public void setStartLoading(boolean startLoading) {
        this.startLoading.set(startLoading);
    }

    public FeedAdapter getAdapter() {
        return adapter;
    }

    public void setFeedDataInAdapter(List<FeedModel> feedData,List<String> id) {
        this.adapter.setFeedModel(feedData,id);
        this.adapter.notifyDataSetChanged();
    }
    public ArrayList<String> getList() {
        return list;
    }
    public CollectionReference getReference(){
        return REFERENCE1;
    }


    public void init() {
        setStartLoading(true);
        list=new ArrayList<>();
        getAllReferences();
        adapter = new FeedAdapter(R.layout.feed_recyclerview_itemlist);
        REFERENCE2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    list.add(snapshot.get("name").toString());
                }
            }
        });
        getLastKeyFromDatabase();
        fetchData(1,"");
    }

    private void getAllReferences() {
        store=FirebaseFirestore.getInstance();
        REFERENCE1 = store.collection("announcement").document("annDocument")
                .collection("usersData").document(User.userId).collection("data");     //original reference
        //REFERENCE1 = store.collection("announcement").document("annDocument").collection("feed");
        REFERENCE2 = store.collection("announcement").document("annDocument").collection("channels");
        REFERENCE3=store.collection("announcement").document("annDocument")
                .collection("users");
        REFERENCE4=store.collection("announcement").document("annDocument")
                .collection("users").document(User.userId);
    }

    public void fetchNewData(RecyclerView recyclerView, int dx, int dy) {
        int totalItem = recyclerView.getLayoutManager().getItemCount();
        int lastVisibleItem = ((LinearLayoutManager) (recyclerView.getLayoutManager())).findLastVisibleItemPosition();
        Log.d("condition","isLoading="+isLoading+"  isMaxdata="+isMaxData);
        if (dy > 0) {
            if (!isLoading && totalItem < (lastVisibleItem + item_load_count) && !isMaxData) {
                Log.d("message", "fetch data called ");
                fetchData(2,"");
            }
        }
    }

    private void getLastKeyFromDatabase() {
        REFERENCE1.orderBy("date", Query.Direction.DESCENDING).limitToLast(1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    end_key = snapshot.getId();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("message", "could not found the last key");
            }
        });
    }

    public void setMaxData(boolean maxData) {
        isMaxData = maxData;
    }

    public String getEnd_key() {
        return end_key;
    }

    public void fetchData(int i, String clubName) {
        isLoading = true;
        Query query=null;
        if(i==1) {
            setStartLoading(true);
            isMaxData=false;
            adapter.feedData.clear();
            adapter.notifyDataSetChanged();
            specific=false;
            this.clubName="";
            query = REFERENCE1.orderBy("date", Query.Direction.DESCENDING).limit(15);
        }
        else if(i==2 && !specific)
            query = REFERENCE1.orderBy("date", Query.Direction.DESCENDING).startAfter(last_node).limit(15);
        else if (i==2 && specific) {
            query = REFERENCE1.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("clubName", this.clubName).startAfter(last_node).limit(15);
        }
        else if(i==3) {
            setStartLoading(true);
            isMaxData=false;
            specific=true;
            this.clubName = clubName;
            adapter.feedData.clear();
            adapter.notifyDataSetChanged();
            query = REFERENCE1.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("clubName", clubName).limit(15);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Log.d("filtered","no snapshot received");
                    last_key=snapshot.getId();
                    try{
                        FeedModel feed = snapshot.toObject(FeedModel.class);
                        adapter.addNewData(feed,snapshot.getId());
                    }catch (RuntimeException e){
                        e.printStackTrace();
                    }

                    if(last_key.equals(end_key))
                        isMaxData=true;
                }
                if(queryDocumentSnapshots.size()>0)
                last_node=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
                isLoading=false;
                setStartLoading(false);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setStartLoading(false);
                Log.d("message", "fetching data stopped  ");
                return;
            }
        });

    }

    public FeedModel getDataAt(int adapterPosition) {
        return adapter.feedData.get(adapterPosition);
    }
    public String getIdAt(int position){
        return adapter.documentSnapShot.get(position);
    }

    public void removeDataAt(int adapterPosition) {
        adapter.remove(adapterPosition);
    }

    public void restoreItem(FeedModel deleteItem,String id,int position) {
        adapter.restoreItem(deleteItem,id,position);
    }
}
