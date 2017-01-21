package com.zdd.accessbilityservicedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zdd.accessbilityservicedemo.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    private EditText mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxt = ((EditText) findViewById(R.id.my_txt));
        ((Button) findViewById(R.id.my_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.i("btn onclick");
                mTxt.setText("change after");
            }
        });
    }
}
