package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.model.GlideApp;

public class CardDetailsActivity extends AppCompatActivity {

    private static final String TAG = "CardDetailsActivity";
    TextView name, rarity, description;
    ImageView image;
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        name = (TextView) findViewById(R.id.card_details_name);
        rarity = (TextView) findViewById(R.id.card_details_rarity);
        description = (TextView) findViewById(R.id.card_details_description);
        image = (ImageView) findViewById(R.id.imageView3);

        Intent launchingIntent = getIntent();
        code = launchingIntent.getStringExtra("cardCode");
        name.setText(launchingIntent.getStringExtra("cardName"));
        rarity.setText(String.format(getString(R.string.card_details_rarity), launchingIntent.getStringExtra("cardRarity")));
        description.setText(launchingIntent.getStringExtra("cardDescription"));

        getSetCardImage();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.card_details);
    }

    private void getSetCardImage() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getCard/" + code + "/";
        Log.d(TAG, url);
        JsonArrayRequest jsHeritageInfo = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        Log.d(TAG, jsObj.getString("filename"));
                        GlideApp.with(CardDetailsActivity.this).load(getString(R.string.server_url) + "images/cards/" + jsObj.getString("filename")).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(image);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsHeritageInfo);
    }
    
}