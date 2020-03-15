package com.example.announcement.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.announcement.R;

public class LoadingDialog {

    private static AlertDialog dialog;

    public static void startLoading(Activity activity,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        View view=inflater.inflate(R.layout.custom_progress_dialog,null);
        builder.setView(view);
        TextView textView=view.findViewById(R.id.textId);
        textView.setText(message);
        builder.setCancelable(false);
        dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
    public static void hideLoading(){
        if(dialog!=null)
            dialog.dismiss();
    }

}
