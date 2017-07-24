package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import it.neptis.gopoleis.R;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.card_details);
    }
    
}
