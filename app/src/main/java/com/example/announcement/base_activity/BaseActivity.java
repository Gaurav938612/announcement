package com.example.announcement.base_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.announcement.R;
import com.example.announcement.announcements.FeedActivity;
import com.example.announcement.authentication.LoginActivity;
import com.example.announcement.authentication.User;
import com.example.announcement.ui.Firebase_input_clubs;
import com.example.announcement.ui.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    DrawerLayout drawer;
    private T mViewDataBinding;
    protected abstract int getLayoutId();
    protected abstract Activity getActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewDataBinding= DataBindingUtil.setContentView(this,getLayoutId());
        Toolbar toolbar=findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.feed_title_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawer = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar,  R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
        }
        navigationView=findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);
        Log.d("baseActivity","onCreate called");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_drawer,menu);
        return false;
    }
    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        if(id==R.id.nav_item1){
            Intent intent=new Intent(this,Firebase_input_clubs.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.nav_item2){
            Intent intent=new Intent(this,FeedActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.nav_item4){
            signOut();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void signOut(){
        LoadingDialog.startLoading(this,"Signing Out");
        FirebaseAuth.getInstance().signOut();
        User.getmGoogleSignInClient(this).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseFirestore.getInstance().collection("announcement/annDocument/usersDeviceTokens").document(User.userId)
                        .update("token","").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        User.userId=null;
                        User.email=null;
                        User.name=null;
                        Toast.makeText(getApplicationContext(),"You are logged out...", Toast.LENGTH_SHORT).show();
                        LoadingDialog.hideLoading();
                        Intent intent=new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to logout", Toast.LENGTH_SHORT).show();
                        LoadingDialog.hideLoading();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                LoadingDialog.hideLoading();
                Toast.makeText(getApplicationContext(),"Failed to logout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public T getViewDataBinding() {
        return mViewDataBinding;
    }
}
