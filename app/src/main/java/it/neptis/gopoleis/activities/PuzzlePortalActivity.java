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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PuzzlePortalActivity extends AppCompatActivity {

    ListView list_attivi;
    ListView list_incoming;
    ImageButton ib_achievements;
    ImageButton ib_partecipa;
    ImageButton ib_cerca;
    String[] list_item;
    String[] list_item2;
    private String user;
    private String game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_portal);

        list_attivi = (ListView) findViewById(R.id.list_active);

        user = getIntent().getExtras().getString("user");


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url)+"getEnabledPuzzle/";
        // Request a string response from the provided URL.

        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                //Log.d("Response is: ", response.toString());
                try {
                    int contLength = response.length();
                    list_item = new String[contLength];
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        String value = jsObj.getString("name");
                        list_item[i] = value;

                        ArrayAdapter<?> adapter = new ArrayAdapter<Object>(PuzzlePortalActivity.this, android.R.layout.simple_selectable_list_item, list_item);
                        list_attivi.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsArray);
        //***********_______END TEMPLATE JSON REQUEST________**********

        list_incoming = (ListView) findViewById(R.id.list_incoming);

        RequestQueue queue2 = Volley.newRequestQueue(this);
        String url2 = getString(R.string.server_url)+"getSoonPuzzle/";
        // Request a string response from the provided URL.

        JsonArrayRequest jsArray2 = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    list_item2 = new String[contLength];
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        String value = jsObj.getString("name") + "  - coming soon!";
                        list_item2[i] = value;
                        ArrayAdapter<?> adapter = new ArrayAdapter<Object>(PuzzlePortalActivity.this, android.R.layout.simple_selectable_list_item, list_item2);
                        list_incoming.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue2.add(jsArray2);
        //***********_______END TEMPLATE JSON REQUEST________**********

        RequestQueue queue3 = Volley.newRequestQueue(PuzzlePortalActivity.this);
        String url3 = getString(R.string.server_url)+"getGame3FromUser/"+user+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url3);
        JsonArrayRequest jsArray3 = new JsonArrayRequest(Request.Method.GET, url3,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    int contLength = response.length();
                    for(int i = 0;i< contLength;i++){
                        JSONObject jsObj = (JSONObject)response.get(i);
                        game = jsObj.getString("game3");
                        Log.d("Game puzzle: ",game);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!",error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue3.add(jsArray3);
        /***********_______END TEMPLATE JSON REQUEST________**********/

        list_attivi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goto_show_puzzle = new Intent(PuzzlePortalActivity.this, ShowPuzzleActivity.class);
                Object selected_puzzle = list_attivi.getItemAtPosition(position);
                String extra_nome = selected_puzzle.toString();
                goto_show_puzzle.putExtra("name", extra_nome);
                goto_show_puzzle.putExtra("game_code", game);
                startActivity(goto_show_puzzle);
            }
        });

        list_incoming.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "This puzzle will be available soon!", Toast.LENGTH_SHORT).show();
                ;
            }
        });


        ib_achievements = (ImageButton) findViewById(R.id.ib_obiettivi);
        ib_achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goto_a = new Intent(PuzzlePortalActivity.this, AchievementsActivity.class);
                goto_a.putExtra("game1SessionCode", "game3");
                startActivity(goto_a);
            }
        });

        ib_partecipa = (ImageButton) findViewById(R.id.ib_partecipazioni);
        ib_partecipa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goto_mypuzzle = new Intent(PuzzlePortalActivity.this,ShowMyPuzzlesActivity.class);
                goto_mypuzzle.putExtra("game_code", game);
                startActivity(goto_mypuzzle);
            }
        });

        /*
        ib_cerca = (ImageButton) findViewById(R.id.ib_cerca_enigmi);
        ib_cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
    }
}