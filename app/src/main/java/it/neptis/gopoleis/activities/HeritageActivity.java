package it.neptis.gopoleis.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Heritage;

public class HeritageActivity extends AppCompatActivity {

    private static final String TAG = "HeritageActivity";

    private TextView name, structureType, latitude, longitude, province, region, historicalPeriod, description;
    private int heritageCode;
    private FirebaseAuth mAuth;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heritage);

        mAuth = FirebaseAuth.getInstance();

        heritageCode = getIntent().getIntExtra("code", 0);
        firstTime = getIntent().getBooleanExtra("firstTime", false);

        name = (TextView) findViewById(R.id.heritage_name);
        structureType = (TextView) findViewById(R.id.heritage_structuretype);
        latitude = (TextView) findViewById(R.id.heritage_latitude);
        longitude = (TextView) findViewById(R.id.heritage_longitude);
        province = (TextView) findViewById(R.id.heritage_province);
        region = (TextView) findViewById(R.id.heritage_region);
        historicalPeriod = (TextView) findViewById(R.id.heritage_historicalperiod);
        description = (TextView) findViewById(R.id.heritage_description);

        // TODO Check for medals unlocking

        setHeritageInfo();

        if (firstTime) {
            addVisitedHeritage();
            showDialog(getString(R.string.congratulations), getString(R.string.congratulations_heritage));
        }
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(HeritageActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.star_off)
                .show();
    }

    private void setHeritageInfo() {
        Bundle bundle = getIntent().getExtras();
        name.setText(bundle.getString("name"));
        structureType.setText(String.format(getString(R.string.structuretype), bundle.getString("structuretype")));
        latitude.setText((String.format(getString(R.string.latitude), bundle.getString("latitude"))));
        longitude.setText((String.format(getString(R.string.longitude), bundle.getString("longitude"))));
        province.setText((String.format(getString(R.string.province), bundle.getString("province"))));
        region.setText((String.format(getString(R.string.region), bundle.getString("region"))));
        historicalPeriod.setText((String.format(getString(R.string.historicalperiod), bundle.getString("historicalperiod"))));
        description.setText(bundle.getString("description"));
    }

    private void addVisitedHeritage() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "addVisitedHeritage/" + mAuth.getCurrentUser().getEmail() + "/" + String.valueOf(heritageCode) + "/";
        JsonArrayRequest jsHeritageInfo = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() != 0) {
                    // Some medal(s) unlocked
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsObj = (JSONObject) response.get(i);
                            showDialog(getString(R.string.congratulations), String.format(getString(R.string.congratulations_medal), jsObj.getString("name")));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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