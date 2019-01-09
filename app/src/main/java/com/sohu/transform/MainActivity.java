package com.sohu.transform;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sohu.groovytest.Cost;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        groovyTest();
    }

    @Cost
    private void groovyTest() {
        Log.d("wy", "groovyTest");
    }
}
