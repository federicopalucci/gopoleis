package it.neptis.gopoleis.activities;

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
import it.neptis.gopoleis.adapters.ArrayAdapterAchievement;
import it.neptis.gopoleis.defines.Achievement;
import it.neptis.gopoleis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private static final String TAG = "AchievementsActivity";

    TextView title;
    ListView list_achieve;

    List<Achievement> list_active = new LinkedList<>();
    String url;
    String url2;
    String achiev_code;
    private String code;
    String achiev_name;
    String achiev_description;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        title = (TextView) findViewById(R.id.l_achievements_title);
        list_achieve = (ListView) findViewById(R.id.list_achievements);

        // Retrieve achievements
        RequestQueue queue = Volley.newRequestQueue(this);
        switch (getIntent().getIntExtra("game", 0)) {
            case 1:
                url = getString(R.string.server_url) + "getAchievementGame1/";
                break;
            case 2:
                url = getString(R.string.server_url) + "getAchievementGame2/";
                break;
            case 3:
                url = getString(R.string.server_url) + "getAchievementGame1/";
                break;
            case 4:
                url = getString(R.string.server_url) + "getAchievementGame1/";
                break;
            default:
                break;
        }

        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int contLength = response.length();
                try {
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        code = jsObj.getString("achievement");
                        url2 = getString(R.string.server_url) + "getAchievementElements/" + code + "/";
                        getAchievementElements(code, url2);
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

        queue.add(jsArray);
    }

    public void getAchievementElements(final String code, String url2) {
        // TODO should implement a column "unlocked" in DB
        JsonArrayRequest jsAchievElements = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsObj = response.getJSONObject(0);
                    achiev_name = jsObj.getString("name");
                    achiev_description = jsObj.getString("description");
                    achiev_code = code;
                    list_active.add(new Achievement(achiev_code, achiev_name, achiev_description));
                    ArrayAdapterAchievement adapter = new ArrayAdapterAchievement(AchievementsActivity.this, android.R.layout.simple_list_item_1, list_active);
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

        Volley.newRequestQueue(getApplicationContext()).add(jsAchievElements);
    }

}