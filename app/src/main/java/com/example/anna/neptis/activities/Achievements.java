package com.example.anna.neptis.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.adapters.ArrayAdapterAchievement;
import com.example.anna.neptis.defines.ObjAchievement;
import com.example.anna.neptis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Achievements extends AppCompatActivity {

    TextView title;
    ListView list_achieve;

    String id = "";
    List<ObjAchievement> list_active = new LinkedList<>();
    String url;
    String url2;
    String achiev_code;
    private String code;
    String achiev_name;
    String achiev_description;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        id = getIntent().getExtras().getString("game");
        setContentView(R.layout.activity_achievements);

        title = (TextView) findViewById(R.id.l_achievements_title);
        list_achieve = (ListView) findViewById(R.id.list_achievements);

        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        switch(id){
            case "game1":
                url = getString(R.string.server_url) + "getAchievementGame1/"; break;

            case "game2":
                url = getString(R.string.server_url) + "getAchievementGame2/"; break;

            case "game3":
                url = getString(R.string.server_url) + "getAchievementGame1/"; break;

            case "game4":
                url = getString(R.string.server_url) + "getAchievementGame1/"; break;

            default: break;
        }

        //***********_______START TEMPLATE JSON REQUEST________**********
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                int contLength = response.length();
                try{
                    for(int i = 0;i< contLength;i++){
                        JSONObject jsObj = (JSONObject)response.get(i);

                        code = jsObj.getString("achievement");
                        Log.d("Code: ",code);
                        url2 = getString(R.string.server_url) +"getAchievementElements/" + code + "/";
                        Log.d("url2: ",url2);
                        getAchievElements(code,url2);
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
        queue.add(jsArray);
        //***********_______END TEMPLATE JSON REQUEST________**********

    }


    public void getAchievElements(final String code,String url2){
        JsonArrayRequest jsAchievElements = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                try {
                    Log.d("code achiev: ",code);
                    JSONObject jsObj =  response.getJSONObject(0);
                    achiev_name = jsObj.getString("name");
                    Log.d("name achiev: ",achiev_name);
                    achiev_description = jsObj.getString("description");
                    Log.d("description achiev: ",achiev_description);

                    achiev_code = code;

                    list_active.add(new ObjAchievement(achiev_code, achiev_name, achiev_description));

                    ArrayAdapterAchievement adapter = new ArrayAdapterAchievement(Achievements.this, android.R.layout.simple_list_item_1, list_active);
                    list_achieve.setAdapter(adapter);

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
        Volley.newRequestQueue(getApplicationContext()).add(jsAchievElements);
    }

}
