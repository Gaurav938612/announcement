package com.example.announcement.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.announcement.R;
import com.example.announcement.ui.HomeActivity;
import com.example.announcement.ui.LoadingDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity implements LoginViewModel.Callback {
    private static final String TAG = "LoginActivity";
    static final int GOOGLE_SIGN_IN = 123;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.setListener(this);
        getCurrentFirebaseTokens();
    }

    private void getCurrentFirebaseTokens() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                User.TOKEN = task.getResult().getToken();
                Log.d(TAG, User.TOKEN);
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    LoadingDialog.startLoading(LoginActivity.this,"authenticating");
                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    loginViewModel.updateDeviceTokenInFirebase(firebaseUser,User.TOKEN);
                }
                else
                    setContentView(R.layout.activity_login);
            }

        });
    }

    public void loginStart(View view) {
        LoadingDialog.startLoading(this, "Logging In");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Intent signInIntent=GoogleSignIn.getClient(this,gso).getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null)
                    firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                LoadingDialog.hideLoading();
                Toast.makeText(LoginActivity.this, "Failed to login.. please Try again", Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            loginViewModel.updateDeviceTokenInFirebase(firebaseUser, User.TOKEN);
                        } else {
                            LoadingDialog.hideLoading();
                            customeToast("Network Error");
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }



    @Override
    public void customeToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}

