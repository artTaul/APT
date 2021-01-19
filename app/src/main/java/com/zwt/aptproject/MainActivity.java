package com.zwt.aptproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.zwt.apt_annotation.BindView;
import com.zwt.apt_bindsdk.BindUtil;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text_view)
    TextView textView;

    @BindView(R.id.text_view1)
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindUtil.bindView(this);
        textView.setText("apt demo");
    }
}