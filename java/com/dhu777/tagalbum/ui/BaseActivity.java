package com.dhu777.tagalbum.ui;


import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dhu777.tagalbum.R;

/**
 * 抽象类,针对应用中Activity通用的初始化行为,对{@link AppCompatActivity}的函数进行重载.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    public void notifyMsg( String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    protected void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    public void statusUiVisibility(final boolean show) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                getWindow().getDecorView().setSystemUiVisibility(show ?
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN :
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
    }

    public String stackTAG(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            Log.d("stackTAG", "stackTAG: "+e.getClassName() + "\t"
                    + e.getMethodName() + "\t" + e.getLineNumber());
        }
        StackTraceElement log = stackTrace[1];
        String tag = null;
//        for (int i = 1; i < stackTrace.length; i++) {
//            StackTraceElement e = stackTrace[i];
//            if (!e.getClassName().equals(log.getClassName())) {
//                tag = e.getClassName() + "." + e.getMethodName();
//                break;
//            }
//        }
//        if (tag == null) {
//            tag = log.getClassName() + "." + log.getMethodName();
//
//        }
        return tag;
    }
}
