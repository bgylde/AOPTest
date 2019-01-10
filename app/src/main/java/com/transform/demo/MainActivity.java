package com.transform.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.sohu.agent.Cost;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        groovyTest();
    }

    @Cost
    private void groovyTest() {
//        TimeCache.setStartTime("123", System.nanoTime());
        Log.d("wy", "groovyTest");
//        TimeCache.setEndTime("123", System.nanoTime());
//        TimeCache.getCostTime("123");
    }
}
