package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import it.neptis.gopoleis.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TreasureNotFoundActivity extends AppCompatActivity {

    private static final String TAG = "TreasureNotFoundAct";

    String url, url2;
    //attributi di Tesoro
    String t_lat, t_lon, t_info;
    String treasure_code;
    String heritageName;

    TextView info, latitude, longitude;
    Button open_treasure;

    Intent openTreasInfoActivity;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasure_not_found);

        mAuth = FirebaseAuth.getInstance();

        treasure_code = getIntent().getExtras().getString("codice_tesoro");
        heritageName = getIntent().getExtras().getString("heritageName");

        info = (TextView) findViewById(R.id.t_info_not_found);
        latitude = (TextView) findViewById(R.id.t_lat_val_not_found);
        longitude = (TextView) findViewById(R.id.t_lon_val_not_found);

        //bottone openTreasure
        open_treasure = (Button) findViewById(R.id.open_treas);
        open_treasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //una volta cliccato sul bottone open_treasure
                //aggiungo il tesoro a GT e apro di nuovo TreasureInfoActivity passandogli game1SessionCode
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                url2 = getString(R.string.server_url) + "addTreasToPlayer/" + mAuth.getCurrentUser().getEmail() + "/" + treasure_code + "/";

                // Request a string response from the provided URL.
                JsonObjectRequest jsAddTreasToGame = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });

                queue.add(jsAddTreasToGame);

                openTreasInfoActivity = new Intent(v.getContext(), TreasureInfoActivity.class);

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openTreasInfoActivity.putExtra("codice_tesoro", treasure_code);
                        openTreasInfoActivity.putExtra("heritageName", heritageName);
                        openTreasInfoActivity.putExtra("info", t_info);
                        openTreasInfoActivity.putExtra("latitude", t_lat);
                        openTreasInfoActivity.putExtra("longitude", t_lon);
                        startActivity(openTreasInfoActivity);
                        finish();
                    }
                }, 1000L);
            }


        });

        // Get treasure info
        RequestQueue queue = Volley.newRequestQueue(this);
        url = getString(R.string.server_url) + "getInfoTreasure/" + treasure_code + "/";

        JsonArrayRequest jsInfoTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        t_info = jsObj.getString("description");
                        t_lat = jsObj.getString("latitude");
                        t_lon = jsObj.getString("longitude");
                        info.setText(t_info);
                        latitude.setText(t_lat);
                        longitude.setText(t_lon);
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

        queue.add(jsInfoTreasure);
    }

}