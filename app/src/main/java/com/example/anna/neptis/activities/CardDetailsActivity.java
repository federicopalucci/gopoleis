package com.example.anna.neptis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.anna.neptis.R;

public class CardDetailsActivity extends AppCompatActivity {

    TextView name, cost, description;

    // TODO add card image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        name = (TextView) findViewById(R.id.card_details_name);
        cost = (TextView) findViewById(R.id.card_details_cost);
        description = (TextView) findViewById(R.id.card_details_description);

        Intent launchingIntent = getIntent();
        name.setText(launchingIntent.getStringExtra("cardName"));
        cost.setText(String.format(getString(R.string.card_details_cost), launchingIntent.getStringExtra("cardCost")));
        description.setText(launchingIntent.getStringExtra("cardDescription"));
    }
    
}
