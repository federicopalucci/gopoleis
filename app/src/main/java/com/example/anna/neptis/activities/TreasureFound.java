package com.example.anna.neptis.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.anna.neptis.defines.ObjTesoro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anna on 08/11/2016.
 */

public class TreasureFound extends AppCompatActivity {

    TextView info;
    TextView latitude;
    TextView longitude;

    Button go_to_map;

    Intent openTreasPortalPag2;

    String user;
    String game;
    String heritage;
    String treasure_code;

    List treasure_list;
    String t_lat, t_lon, t_info;//attributi di Tesoro
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treasure_found);

        info = (TextView) findViewById(R.id.t_info_found);
        latitude = (TextView) findViewById(R.id.t_lat_val_found);
        longitude = (TextView) findViewById(R.id.t_lon_val_found);

        user = getIntent().getExtras().getString("user");
        treasure_code = getIntent().getExtras().getString("codice_tesoro");
        game = getIntent().getExtras().getString("game1SessionCode");
        heritage = getIntent().getExtras().getString("heritageName");


        go_to_map = (Button)findViewById(R.id.go_to_map);
        go_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTreasPortalPag2 = new Intent(v.getContext(),TreasurePortalPag2.class);
                openTreasPortalPag2.putExtra("user",user);
                openTreasPortalPag2.putExtra("heritageName",heritage);
                openTreasPortalPag2.putExtra("game1SessionCode",game);
                startActivity(openTreasPortalPag2);
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
                        t_lat = jsObj.getString("heritageLatitude");
                        t_lon = jsObj.getString("heritageLongitude");

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
