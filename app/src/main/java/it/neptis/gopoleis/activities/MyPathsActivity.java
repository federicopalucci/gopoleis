package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.adapters.PathAdapter;
import it.neptis.gopoleis.defines.Path;

public class MyPathsActivity extends AppCompatActivity {

    private static final String TAG = "MyPathsActivity";

    private Path[] myPaths;
    private ListView myPathsListView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_paths);

        mAuth = FirebaseAuth.getInstance();

        myPathsListView = (ListView) findViewById(R.id.my_paths_listview);
        myPathsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toPathActivity = new Intent(MyPathsActivity.this, PathActivity.class);
                toPathActivity.putExtra("title", myPaths[position].getTitle());
                startActivity(toPathActivity);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.my_paths);

        getMyPaths();
    }

    private void getMyPaths() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getMyPathsTitles/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    myPaths = new Path[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        myPaths[i] = new Path(0, jsObj.getString("title"), null, null, jsObj.getInt("completed") == 1);
                    }
                    PathAdapter adapter = new PathAdapter(MyPathsActivity.this, myPaths);
                    myPathsListView.setAdapter(adapter);
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

        queue.add(jsArray);
    }

}