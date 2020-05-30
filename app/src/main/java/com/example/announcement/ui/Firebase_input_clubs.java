package com.example.announcement.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.announcement.R;
import com.example.announcement.announcements.FeedModel;
import com.example.announcement.notifications.ApiClient;
import com.example.announcement.notifications.ApiInterFace;
import com.example.announcement.notifications.Data;
import com.example.announcement.notifications.MyResponse;
import com.example.announcement.notifications.Sender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Firebase_input_clubs extends AppCompatActivity {

    FirebaseFirestore db;
    CollectionReference reference,reference2,reference3;
    String notification_title,notification_body,notification_club;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_input_clubs);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText club_notification_year,club_venue,club_heading,club_details,club_date;
        Button club_submit_btn;
        final FeedModel member;
        final Spinner spinner=findViewById(R.id.club_name_id);
        final ArrayList<String> channels=new ArrayList<>();
        channels.add("club name");

        club_notification_year=findViewById(R.id.club_notification_years);
        club_date=findViewById(R.id.clubDate);
        club_venue=findViewById(R.id.clubVenue);
        club_heading=findViewById(R.id.clubHeading);
        club_details=findViewById(R.id.clubDetails);
        club_submit_btn=findViewById(R.id.club_submit_button);
        db=FirebaseFirestore.getInstance();
        reference2=db.collection("announcement").document("annDocument").collection("channels");

        member=new FeedModel();
        reference2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    channels.add(snapshot.get("name").toString());
                }
                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(Firebase_input_clubs.this
                        ,android.R.layout.simple_list_item_1,channels);
                spinner.setAdapter(arrayAdapter);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String channel_name=parent.getItemAtPosition(position).toString();
                member.setClubName(channel_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        club_submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(club_notification_year.getText().length()==0 || club_heading.getText().length()==0
                || club_details.getText().length()==0 ||club_venue.getText().length()==0 || club_date.getText().length()==0
                || club_date.getText().length()==0){
                    Toast.makeText(Firebase_input_clubs.this,"All fields are necessary",Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog dialog=new ProgressDialog(Firebase_input_clubs.this);
                dialog.setTitle("Sending");
                dialog.setMessage("Please wait...");
                dialog.show();
                dialog.setCancelable(false);
                reference= db.collection("announcement").document("annDocument").collection("users");
                reference3= db.collection("announcement").document("annDocument").collection("usersData");
                if(member.getClubName().equals("club name")){
                    Toast.makeText(Firebase_input_clubs.this,"please select club name",Toast.LENGTH_SHORT).show();
                    return;
               }
                member.setClubNotificationYear(club_notification_year.getText().toString());
                member.setClubHeading(club_heading.getText().toString());
                member.setClubDetails(club_details.getText().toString());
                member.setClubVenue(club_venue.getText().toString());
                member.setClubDate(club_date.getText().toString());
                member.setDate(new Date());
                notification_title=member.getClubHeading();
                notification_body=member.getClubDetails();
                notification_club=member.getClubName();
               reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       int size=queryDocumentSnapshots.size();
                       Task t[]=new Task[size];
                       int i=0;
                       for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                           String s=snapshot.getString("userId");
                           Log.d("message",snapshot.toString());
                           Log.d("message","loop no "+i);
                           t[i]=reference3.document(s).collection("data").document().set(member);
                           i++;
                       }
                       Task<List<QuerySnapshot>> allTasks=Tasks.whenAllSuccess((Collection)Arrays.asList(t));
                       allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
                           @Override
                           public void onSuccess(List<QuerySnapshot> querySnapshots) {
                               Toast.makeText(Firebase_input_clubs.this,"succeeded for all",Toast.LENGTH_SHORT).show();
                               dialog.dismiss();
                               sendMessageToEveryone();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Firebase_input_clubs.this,"not succeeded for all",Toast.LENGTH_SHORT).show();
                               dialog.dismiss();
                           }
                       });
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(Firebase_input_clubs.this,"failed",Toast.LENGTH_SHORT).show();
                       dialog.dismiss();
                   }
               });
            }
        });


    }
    public  void sendMessageToEveryone(){
        List<String> tokens=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("announcement/annDocument/usersDeviceTokens")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot snapshot:queryDocumentSnapshots){
                    if(snapshot.getBoolean(notification_club)==null || !snapshot.getBoolean(notification_club) )
                        continue;
                    String token=snapshot.get("token").toString();
                    if(!token.isEmpty())
                        tokens.add(token);
                    Log.d("token of user",token);
                    //Toast.makeText(Firebase_input_clubs.this,token,Toast.LENGTH_SHORT).show();
                }
                start(tokens);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Firebase_input_clubs.this,"Unable to find tokens",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*public void send(View view) {
        sendMessageToEveryone();
    }*/
    public void start(List<String> tokens){
        ApiInterFace interFace= ApiClient.getRetrofit().create(ApiInterFace.class);
        Data data=new Data("user_ID",R.drawable.ic_notifications,notification_body,notification_title,"sent");
        for(String token:tokens){

            Sender sender=new Sender(data,token);
            interFace.sendNotification(sender)
                    .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.isSuccessful()){
                                if(response.body().success!=1){
                                    Toast.makeText(Firebase_input_clubs.this,"notification failed to send",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(Firebase_input_clubs.this,"notification sent successfully",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Toast.makeText(Firebase_input_clubs.this,"notification send failure",Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }
}
