package com.transform.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.transform.demo.okhttp.OkHttpTest;
import com.transform.demo.timing.TestTiming;

public class MainActivity extends Activity {

    private Button timingTestButton;

    private Button okhttpTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView() {
        timingTestButton = (Button) findViewById(R.id.test_timing);
        okhttpTestButton = (Button) findViewById(R.id.test_okhttp);
    }

    private void initListener() {
        timingTestButton.setOnClickListener(clickListener);
        okhttpTestButton.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.test_okhttp:
                    OkHttpTest.okHttpTest();
                    break;
                case R.id.test_timing:
                    TestTiming.sleepTimingTest(3000);
                    break;
            }
        }
    };
}
