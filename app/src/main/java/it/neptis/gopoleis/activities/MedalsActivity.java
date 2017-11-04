package it.neptis.gopoleis.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.adapters.ClickListener;
import it.neptis.gopoleis.adapters.MedalAdapter;
import it.neptis.gopoleis.adapters.RecyclerTouchListener;
import it.neptis.gopoleis.model.Medal;

public class MedalsActivity extends AppCompatActivity {

    //private static final String TAG = "MedalsActivity";

    private FirebaseAuth mAuth;
    private List<Medal> regionMedals, structuretypeMedals, historicalperiodMedals;
    private MedalAdapter regionsMedalAdapter;
    private MedalAdapter historicalPeriodMedalAdapter;
    private MedalAdapter structuretypeMedalAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();

        regionMedals = new ArrayList<>();
        structuretypeMedals = new ArrayList<>();
        historicalperiodMedals = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.medals);

        getPlayerMedals();

        setRecyclerViews();
    }

    private void getPlayerMedals() {
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getPlayerMedals/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest medalsRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        Medal tempMedal = new Medal(jsObj.getInt("code"), jsObj.getString("name"), getString(R.string.server_url) + "images/medals/" + jsObj.getString("filename"), jsObj.getInt("category"), jsObj.getString("obtained").equals("1"));
                        if (tempMedal.getCategory() == 1)
                            regionMedals.add(tempMedal);
                        else if (tempMedal.getCategory() == 2)
                            historicalperiodMedals.add(tempMedal);
                        else if (tempMedal.getCategory() == 3)
                            structuretypeMedals.add(tempMedal);
                    }
                    regionsMedalAdapter.notifyDataSetChanged();
                    historicalPeriodMedalAdapter.notifyDataSetChanged();
                    structuretypeMedalAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MedalsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(medalsRequest);
    }

    private void setRecyclerViews() {
        RecyclerView regionsRecyclerView = (RecyclerView) findViewById(R.id.regionsRecyclerView);
        regionsMedalAdapter = new MedalAdapter(this, regionMedals);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MedalsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        regionsRecyclerView.setLayoutManager(layoutManager);
        regionsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        regionsRecyclerView.setAdapter(regionsMedalAdapter);
        regionsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(MedalsActivity.this, MedalDetailsActivity.class).putExtra("code", regionMedals.get(position).getCode()).putExtra("filepath", regionMedals.get(position).getFilePath()));
            }
        }));

        RecyclerView historicalPeriodRecyclerView = (RecyclerView) findViewById(R.id.historicalPeriodRecyclerView);
        historicalPeriodMedalAdapter = new MedalAdapter(this, historicalperiodMedals);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(MedalsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        historicalPeriodRecyclerView.setLayoutManager(layoutManager2);
        historicalPeriodRecyclerView.setItemAnimator(new DefaultItemAnimator());
        historicalPeriodRecyclerView.setAdapter(historicalPeriodMedalAdapter);
        historicalPeriodRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(MedalsActivity.this, MedalDetailsActivity.class).putExtra("code", historicalperiodMedals.get(position).getCode()).putExtra("filepath", historicalperiodMedals.get(position).getFilePath()));
            }
        }));

        RecyclerView structuretypeRecyclerView = (RecyclerView) findViewById(R.id.typologyRecyclerView);
        structuretypeMedalAdapter = new MedalAdapter(this, structuretypeMedals);
        RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(MedalsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        structuretypeRecyclerView.setLayoutManager(layoutManager3);
        structuretypeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        structuretypeRecyclerView.setAdapter(structuretypeMedalAdapter);
        structuretypeRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(MedalsActivity.this, MedalDetailsActivity.class).putExtra("code", structuretypeMedals.get(position).getCode()).putExtra("filepath", structuretypeMedals.get(position).getFilePath()));
            }
        }));
    }

}