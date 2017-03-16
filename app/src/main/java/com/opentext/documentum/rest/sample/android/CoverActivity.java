/*
 * Copyright (c) 2017. OpenText Corporation. All Rights Reserved.
 */

package com.opentext.documentum.rest.sample.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CoverActivity extends AppCompatActivity {

    @BindView(R.id.start_button)
    Button startButton;

    @BindView(R.id.welcome_msg_id)
    TextView welcomeView;

    private static String getDay() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            return "Good Morning!";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            return "Good Afternoon!";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            return "Good Evening!";
        } else {
            return "Good Night!";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        ButterKnife.bind(this);
        setupViews();
    }

    private void setupViews() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoverActivity.this.setResult(Activity.RESULT_OK);
                CoverActivity.this.finish();
                Intent intent = new Intent(CoverActivity.this, LoginActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_LOGIN);
            }
        });
        welcomeView.setText(getDay());
    }
}
