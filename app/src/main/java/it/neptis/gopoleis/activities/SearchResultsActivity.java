package it.neptis.gopoleis.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.adapters.SearchResultAdapter;
import it.neptis.gopoleis.model.SearchResult;

public class SearchResultsActivity extends AppCompatActivity {

    //private static final String TAG = "SearchResults";

    private ListView listView;
    private List<Integer> codes;
    private List<SearchResult> searchResults;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        listView = (ListView) findViewById(R.id.search_results_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchResultsActivity.this, MainActivity.class);
                intent.putExtra("searched", codes.get(position));
                intent.putExtra("type", searchResults.get(position).getType());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.results);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }

    public void search(String query) {
        query = query.trim().replaceAll("\\s+", " ").replace(" ", "%20").toLowerCase();
        query = query.substring(0, 1).toUpperCase() + query.substring(1);
        String url = getString(R.string.server_url) + "search/" + query + "/";
        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    codes = new ArrayList<>();
                    searchResults = new ArrayList<>();
                    JSONArray jsArray = (JSONArray) response.get("heritage");
                    for (int i = 0; i < jsArray.length(); i++) {
                        JSONObject jsObj = (JSONObject) jsArray.get(i);
                        codes.add(jsObj.getInt("code"));
                        searchResults.add(new SearchResult(jsObj.getString("name"), "heritage"));
                    }
                    jsArray = (JSONArray) response.get("stage");
                    for (int i = 0; i < jsArray.length(); i++) {
                        JSONObject jsObj = (JSONObject) jsArray.get(i);
                        codes.add(jsObj.getInt("code"));
                        searchResults.add(new SearchResult(jsObj.getString("title"), "stage"));
                    }
                    SearchResult[] searchResultsArray = searchResults.toArray(new SearchResult[searchResults.size()]);
                    SearchResultAdapter adapter = new SearchResultAdapter(SearchResultsActivity.this, searchResultsArray);
                    listView.setAdapter(adapter);

                    if (searchResults.isEmpty()) {
                        finish();
                        Toast.makeText(SearchResultsActivity.this, getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(SearchResultsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(searchRequest);
    }

}