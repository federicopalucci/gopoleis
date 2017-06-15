package com.example.anna.neptis.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.defines.ObjCard;
import com.example.anna.neptis.R;
import com.example.anna.neptis.adapters.CardAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class ManageCards extends AppCompatActivity {

    List<ObjCard> all_cards;
    String url;
    ListView list;
    String code,cost,name,description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);

        list = (ListView) findViewById(R.id.listView);
        all_cards = new ArrayList<>();


        int button_code = getIntent().getExtras().getInt("codice");

        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String game = getIntent().getExtras().getString("game_code");

        switch(button_code){
            case 100:
                url = getString(R.string.server_url) +"getAllCards/";
                break;

            case 200:

                //Log.d("game_ code",game);
                url = getString(R.string.server_url) + "getMyCards/"+ game + "/";
                break;

            default:break;
        }

        Log.d("url= ",url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsCardCodes = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for(int i= 0;i< contLength;i++) {
                        JSONObject jsObj = (JSONObject)response.get(i);
                        code = jsObj.getString("code");
                        cost = jsObj.getString("cost");
                        name = jsObj.getString("name");
                        description = jsObj.getString("description");


                        all_cards.add(new ObjCard(code,cost,name,description));
                        list.setAdapter(new CardAdapter(ManageCards.this, R.layout.adapter_card , all_cards));//android.R.layout.simple_list_item_1

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
        queue.add(jsCardCodes);

        /***********_______END TEMPLATE JSON REQUEST________**********/

    }





}



