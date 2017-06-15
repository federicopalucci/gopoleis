package com.example.anna.neptis.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.anna.neptis.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        Button welcome_button = (Button)findViewById(R.id.go_to_login);
        welcome_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLoginActivity = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(openLoginActivity);
            }
        });
    }
}
