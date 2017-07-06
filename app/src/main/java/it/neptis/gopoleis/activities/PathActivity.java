package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neptis.gopoleis.GopoleisApp;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Path;
import it.neptis.gopoleis.defines.Question;
import it.neptis.gopoleis.defines.Stage;

public class PathActivity extends AppCompatActivity {

    private static final String TAG = "PathActivity";

    private ListView stagesListView;
    private String[] stages;
    private String title;
    private Path path;
    private GopoleisApp gopoleisApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        gopoleisApp = (GopoleisApp) getApplicationContext();

        TextView titleTextView = (TextView) findViewById(R.id.path_title);
        title = getIntent().getStringExtra("title");
        titleTextView.setText(title);

        stagesListView = (ListView) findViewById(R.id.path_stages_list_view);
        stagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toStageActivity = new Intent(PathActivity.this, StageActivity.class);
                startActivity(toStageActivity);
            }
        });

        getPathDetails();
    }

    private void getPathStages() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String titleNoSpaces = title.replace("  - coming soon!","").replace(" ","%20");
        String url = getString(R.string.server_url) + "getPathStagesByTitle/" + titleNoSpaces + "/";
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    stages = new String[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        path.getStages().add(new Stage(jsObj.getInt("stagecode"), jsObj.getString("title"), jsObj.getString("curiosity"), null, new Question(jsObj.getInt("questioncode"), jsObj.getString("question"), jsObj.getString("hintonsite"), jsObj.getString("hintbypaying"), jsObj.getString("answer")), path));

                        stages[i] = jsObj.getString("title");
                        ArrayAdapter<?> adapter = new ArrayAdapter<>(PathActivity.this, android.R.layout.simple_selectable_list_item, stages);
                        stagesListView.setAdapter(adapter);
                    }
                    gopoleisApp.setPath(path);
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

    private void getPathDetails() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String titleNoSpaces = title.replace("  - coming soon!","").replace(" ","%20");
        String url = getString(R.string.server_url) + "getPathByTitle/" + titleNoSpaces + "/";
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        path = new Path(jsObj.getInt("pathCode"), jsObj.getString("title"), new Question(jsObj.getInt("questionCode"), jsObj.getString("question"), jsObj.getString("hintonsite"), jsObj.getString("hintbypaying"), jsObj.getString("answer")), null, new ArrayList<Stage>());
                    }
                    getPathStages();
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
