package com.example.announcement.authentication;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.announcement.ui.LoadingDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class LoginViewModel extends ViewModel {
    Callback callback;

    public interface Callback {
        void customeToast(String message);
        void startHomeActivity();
    }
    public void setListener(Callback listener){
        callback=listener;
    }

    public void updateDeviceTokenInFirebase(FirebaseUser user, final String token) {
        if(user!=null){
            User.name=user.getDisplayName();
            User.email=user.getEmail();

            final CollectionReference reference= FirebaseFirestore.getInstance()
                    .collection("announcement").document("annDocument").collection("users");
            Query query=reference;
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots ){
                        if(User.email.equals(snapshot.getId())) {
                            User.userId = snapshot.getString("userId");
                            setToken(token);
                           // setDefaultSubscription();
                            break;
                        }
                    }
                    if(User.userId==null){
                        registerNewUser(reference,token);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LoadingDialog.hideLoading();
                    callback.customeToast("data fetching failed");
                }
            });

        }


    }
    private void registerNewUser(CollectionReference reference,String token) {
        User.userId=reference.document().getId();
        Map<String,String> userData=new HashMap<>();
        final Map<String,String> userToken=new HashMap<>();
        userData.put("name",User.name);
        userData.put("email",User.email);
        userData.put("userId",User.userId);
        userToken.put("token",token);
        reference.document(User.email).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setToken(token);
                setDefaultSubscription();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.customeToast("data fetching failed");
            }
        });
    }
    private void setDefaultSubscription() {
        Map<String,Boolean> subscribed=new HashMap<>();
        FirebaseFirestore.getInstance().collection("announcement/annDocument/channels").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                            String name=snapshot.get("name").toString();
                            subscribed.put(name,false);
                        }
                        FirebaseFirestore.getInstance().collection("announcement/annDocument/usersSubscriptions")
                                .document(User.userId).collection("subcribed clubs");

                    }
                });
    }

    private void setToken(String token) {
        final Map<String,String> userToken=new HashMap<>();
        userToken.put("token",token);
        FirebaseFirestore.getInstance().collection("announcement/annDocument/usersDeviceTokens").document(User.userId)
                .set(userToken).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                callback.customeToast("Logged in as"+User.name);
                LoadingDialog.hideLoading();
                callback.startHomeActivity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LoadingDialog.hideLoading();
                callback.customeToast("device not identified");
            }
        });
    }
}
