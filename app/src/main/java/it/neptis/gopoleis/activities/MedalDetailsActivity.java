package it.neptis.gopoleis.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.model.GlideApp;

public class MedalDetailsActivity extends AppCompatActivity {

    private static final String TAG = "MedalDetails";

    private int code;
    private TextView name;
    private TextView category;
    private TextView heritages;
    private ImageView image;
    private String filepath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medal_details);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.medal_details);

        code = getIntent().getIntExtra("code", 0);
        filepath = getIntent().getStringExtra("filepath");

        name = (TextView) findViewById(R.id.medal_details_name);
        category = (TextView) findViewById(R.id.medal_details_category);
        heritages = (TextView) findViewById(R.id.medal_details_heritages);
        image = (ImageView) findViewById(R.id.medal_details_image);

        getMedalDetails();
    }

    private void getMedalDetails() {
        String url = getString(R.string.server_url) + "getMedalDetails/" + code + "/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    name.setText(String.format(getString(R.string.medal_details_name), (String) response.get(0)));
                    category.setText(String.format(getString(R.string.medal_details_category), (String) response.get(1)));
                    String heritagesNames = "";
                    for (int i = 2; i < response.length(); i++) {
                        if (i == response.length() - 1)
                            heritagesNames = heritagesNames.concat((String) response.get(i));
                        else
                            heritagesNames = heritagesNames.concat(response.get(i) + ", ");
                    }
                    heritages.setText(String.format(getString(R.string.medal_details_heritages), heritagesNames));
                    progressDialog.dismiss();
                    GlideApp.with(MedalDetailsActivity.this).load(filepath).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(image);
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

        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}