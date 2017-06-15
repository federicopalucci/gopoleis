package com.example.anna.neptis.activities;

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
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
import com.example.anna.neptis.activities.TreasureInfoActivity;
import com.example.anna.neptis.defines.ObjTesoro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anna on 08/11/2016.
 */

public class TreasureNotFoundActivity extends AppCompatActivity {

    String url,url2;
    String t_lat, t_lon, t_info;//attributi di Tesoro

    String user;
    String treasure_code;
    String game;
    String heritage;

    TextView info;
    TextView latitude;
    TextView longitude;
    Button open_treas;
    List treasure_list;

    Intent openTreasInfoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasure_not_found);

        user = getIntent().getExtras().getString("user");
        treasure_code = getIntent().getExtras().getString("codice_tesoro");
        game = getIntent().getExtras().getString("game");
        heritage = getIntent().getExtras().getString("heritage");

        info = (TextView) findViewById(R.id.t_info_not_found);
        latitude = (TextView) findViewById(R.id.t_lat_val_not_found);
        longitude = (TextView) findViewById(R.id.t_lon_val_not_found);



        //bottone openTreasure
        open_treas = (Button)findViewById(R.id.open_treas);
        open_treas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //una volta cliccato sul bottone open_treas
                //aggiungo il tesoro a GT e apro di nuovo TreasureInfoActivity passandogli game e user
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                url2 = getString(R.string.server_url)+"addTreasToGame1/" + treasure_code + "/"+ game+"/";

                Log.d("url= ", url2);

                // Request a string response from the provided URL.
                JsonArrayRequest jsAddTreasToGame = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("That didn't work!", error.toString());
                    }
                });
                queue.add(jsAddTreasToGame);




                openTreasInfoActivity = new Intent(v.getContext(),TreasureInfoActivity.class);

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        openTreasInfoActivity.putExtra("user",user);
                        openTreasInfoActivity.putExtra("game",game);
                        openTreasInfoActivity.putExtra("codice_tesoro",treasure_code);
                        openTreasInfoActivity.putExtra("heritage",heritage);
                        startActivity(openTreasInfoActivity);
                    }
                }, 1000L);


            }


        });



        treasure_list = new LinkedList<ObjTesoro>();
        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.

        //set_trovato = Integer.parseInt(getIntent().getExtras().getString("trovato"));

        RequestQueue queue = Volley.newRequestQueue(this);
        url = getString(R.string.server_url)+"getInfoTreasure/" + treasure_code + "/";

        //Log.d("codice tesoro: ", treasure_code);

        Log.d("url= ", url);

        // Request a string response from the provided URL.
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

                        treasure_list.add(new ObjTesoro(treasure_code, t_lat, t_lon, t_info, user));
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
        queue.add(jsInfoTreasure);

        /***********_______END TEMPLATE JSON REQUEST________**********/


    }
}
