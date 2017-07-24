package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

import it.neptis.gopoleis.GopoleisApp;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Path;
import it.neptis.gopoleis.defines.Question;
import it.neptis.gopoleis.defines.Stage;

public class PathActivity extends AppCompatActivity {

    private static final String TAG = "PathActivity";

    private ListView stagesListView;
    private String[] stagesTitles;
    private String title;
    private Path path;
    private TextView pathAlreadyCompleted;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        mAuth = FirebaseAuth.getInstance();

        TextView titleTextView = (TextView) findViewById(R.id.path_title);
        title = getIntent().getStringExtra("title");
        titleTextView.setText(title);

        pathAlreadyCompleted = (TextView) findViewById(R.id.path_already_completed);

        stagesListView = (ListView) findViewById(R.id.path_stages_list_view);
        stagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent toStageActivity = new Intent(PathActivity.this, StageActivity.class);
                toStageActivity.putExtra("code", String.valueOf(path.getStages().get(position).getCode()));
                startActivity(toStageActivity);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.path);

        getPathAndStages();
    }

    private void getPathAndStages() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String titleNoSpaces = title.replace(" ","%20");
        String url = getString(R.string.server_url) + "getPathStagesByTitle/" + titleNoSpaces + "/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    stagesTitles = new String[response.length()];
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        if (path == null)
                            path = new Path(jsObj.getInt("pathcode"), jsObj.getString("pathtitle"), null, new ArrayList<Stage>(), jsObj.getInt("completed") == 1);

                        String isFinalString = jsObj.getString("isfinal");
                        boolean isFinal = isFinalString.equals("true");
                        path.getStages().add(new Stage(jsObj.getInt("stagecode"), jsObj.getString("stagetitle"), jsObj.getString("curiosity"), null, new Question(jsObj.getInt("questioncode"), jsObj.getString("question"), jsObj.getString("hintonsite"), jsObj.getString("hintbypaying"), jsObj.getString("answer")), path, isFinal));

                        stagesTitles[i] = jsObj.getString("stagetitle");
                    }
                    ArrayAdapter<?> adapter = new ArrayAdapter<>(PathActivity.this, android.R.layout.simple_selectable_list_item, stagesTitles);
                    stagesListView.setAdapter(adapter);
                    if (path.isCompleted())
                        pathAlreadyCompleted.setText(R.string.path_already_completed);
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