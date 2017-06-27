package it.neptis.gopoleis.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import it.neptis.gopoleis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TreasureFoundActivity extends AppCompatActivity {

    private static final String TAG = "TreasureFoundAct";

    TextView info, latitude, longitude;
    String t_lat, t_lon, t_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasure_found);

        info = (TextView) findViewById(R.id.t_info_found);
        latitude = (TextView) findViewById(R.id.t_lat_val_found);
        longitude = (TextView) findViewById(R.id.t_lon_val_found);

        String treasure_code = getIntent().getExtras().getString("codice_tesoro");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getInfoTreasure/" + treasure_code + "/";

        JsonArrayRequest jsInfoTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        t_info = jsObj.getString("info");
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