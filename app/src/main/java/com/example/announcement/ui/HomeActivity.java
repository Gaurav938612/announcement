package com.example.announcement.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.announcement.R;
import com.example.announcement.announcements.FeedActivity;
import com.example.announcement.authentication.LoginActivity;
import com.example.announcement.authentication.User;
import com.example.announcement.base_activity.BaseActivity;
import com.example.announcement.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {
    private NavigationView navigationView;
    ProgressDialog dialog;
    TextView userName,email;
    MenuItem signOut;
    User user;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding=getViewDataBinding();
        navigationView = findViewById(R.id.nav_drawer_view);
        Menu nav_menu=navigationView.getMenu();
        signOut=nav_menu.findItem(R.id.nav_item4);

        userName=navigationView.getHeaderView(0).findViewById(R.id.userNameId);
        email=navigationView.getHeaderView(0).findViewById(R.id.emailId);
        if (FirebaseAuth.getInstance() != null) {
            if(userName!=null) {
                Log.d("user name ",User.name);
                Log.d("email ",User.email);
                userName.setText(User.name);
                email.setText(User.email);
            }
        }

    }
}
