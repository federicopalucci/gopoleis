package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import it.neptis.gopoleis.R;

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
import java.util.List;

public class PuzzlePortalActivity extends AppCompatActivity {

    private static final String TAG = "PuzzlePortalAct";

    private ListView activePuzzlesListView, upcomingPuzzlesListView;
    private List<String> activePuzzlesTitles, upcomingPuzzlesTitles;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_portal);

        mAuth = FirebaseAuth.getInstance();

        getAllPaths();

        activePuzzlesListView = (ListView) findViewById(R.id.list_active);
        activePuzzlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toPathActivity = new Intent(PuzzlePortalActivity.this, PathActivity.class);
                toPathActivity.putExtra("title", activePuzzlesTitles.get(position));
                startActivity(toPathActivity);
            }
        });

        upcomingPuzzlesListView = (ListView) findViewById(R.id.list_incoming);
        upcomingPuzzlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PuzzlePortalActivity.this, R.string.path_coming_soon, Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton achievementsButton = (ImageButton) findViewById(R.id.ib_obiettivi);
        achievementsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAchievements = new Intent(PuzzlePortalActivity.this, AchievementsActivity.class);
                toAchievements.putExtra("game", "game3");
                startActivity(toAchievements);
            }
        });

        ImageButton participateButton = (ImageButton) findViewById(R.id.ib_partecipazioni);
        participateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMyPuzzles = new Intent(PuzzlePortalActivity.this, MyPathsActivity.class);
                startActivity(toMyPuzzles);
            }
        });
    }

    private void getAllPaths() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getAllPaths/";
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    activePuzzlesTitles = new ArrayList<>();
                    upcomingPuzzlesTitles = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        if (jsObj.getString("enabled").equals("true"))
                            activePuzzlesTitles.add(jsObj.getString("title"));
                        else
                            upcomingPuzzlesTitles.add(jsObj.getString("title"));
                    }
                    ArrayAdapter<?> adapter = new ArrayAdapter<Object>(PuzzlePortalActivity.this, android.R.layout.simple_selectable_list_item, activePuzzlesTitles.toArray());
                    activePuzzlesListView.setAdapter(adapter);
                    ArrayAdapter<?> adapter2 = new ArrayAdapter<Object>(PuzzlePortalActivity.this, android.R.layout.simple_selectable_list_item, upcomingPuzzlesTitles.toArray());
                    upcomingPuzzlesListView.setAdapter(adapter2);
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