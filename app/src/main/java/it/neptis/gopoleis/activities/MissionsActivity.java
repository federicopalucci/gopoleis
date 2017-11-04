package it.neptis.gopoleis.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.adapters.MissionAdapter;

public class MissionsActivity extends AppCompatActivity {

    //private static final String TAG = "MissionsActivity";

    private MissionAdapter adapter;
    private String[] missions;
    private boolean[] completed;
    private FirebaseAuth mAuth;
    private ListView listview;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missions);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.missions);

        mAuth = FirebaseAuth.getInstance();

        listview = (ListView) findViewById(R.id.missions_listview);

        getMissions();
    }

    private void getMissions() {
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getMissions/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest missionsRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    missions = new String[response.length()];
                    completed = new boolean[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        missions[i] = jsObj.getString("title");
                        completed[i] = jsObj.getString("completed").equals("1");
                    }
                    adapter = new MissionAdapter(MissionsActivity.this, missions, completed);
                    listview.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MissionsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(missionsRequest);
    }

}