package com.dhu777.tagalbum.ui;


import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 抽象类,针对应用中Activity通用的初始化行为,对{@link AppCompatActivity}的函数进行重载.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public void notifyMsg( String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
