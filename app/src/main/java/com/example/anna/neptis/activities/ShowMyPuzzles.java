package com.example.anna.neptis.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowMyPuzzles extends AppCompatActivity {

    String[] list_item;
    ListView puzzles;
    TextView title;
    String game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_puzzles);

        title = (TextView) findViewById(R.id.l_title_mypuzzles);
        puzzles = (ListView) findViewById(R.id.lw_mypuzzle);

        game = getIntent().getExtras().getString("game_code");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url)+"getMyPuzzles/"+game+"/";
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

                        ArrayAdapter<?> adapter = new ArrayAdapter<Object>(ShowMyPuzzles.this, android.R.layout.simple_selectable_list_item, list_item);
                        puzzles.setAdapter(adapter);
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
    }
}
