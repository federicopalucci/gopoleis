package it.neptis.gopoleis.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.model.GlideApp;

public class HeritageActivity extends AppCompatActivity {

    private static final String TAG = "HeritageActivity";

    private TextView name, structureType, latitude, longitude, province, region, historicalPeriod, description;
    private String heritageCode;
    private FirebaseAuth mAuth;
    private ImageView image;
    private Button submitReviewButton, readReviewsButton;
    private String userReview;
    private boolean hasReviewed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heritage);

        mAuth = FirebaseAuth.getInstance();

        heritageCode = getIntent().getStringExtra("code");

        name = (TextView) findViewById(R.id.heritage_name);
        structureType = (TextView) findViewById(R.id.heritage_structuretype);
        latitude = (TextView) findViewById(R.id.heritage_latitude);
        longitude = (TextView) findViewById(R.id.heritage_longitude);
        province = (TextView) findViewById(R.id.heritage_province);
        region = (TextView) findViewById(R.id.heritage_region);
        historicalPeriod = (TextView) findViewById(R.id.heritage_historicalperiod);
        description = (TextView) findViewById(R.id.heritage_description);
        image = (ImageView) findViewById(R.id.imageView2);
        submitReviewButton = (Button) findViewById(R.id.submit_reviews_button);
        readReviewsButton = (Button) findViewById(R.id.read_reviews_button);

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWriteReviewDialog();
            }
        });

        readReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HeritageActivity.this, ReviewsActivity.class);
                intent.putExtra("code", heritageCode);
                startActivity(intent);
            }
        });

        // TODO Check for medals unlocking

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.heritage);

        getHeritage();
    }

    private void showWriteReviewDialog() {
        if (hasReviewed) {
            Toast.makeText(HeritageActivity.this, "Hai già recensito questo bene culturale", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HeritageActivity.this);
        builder.setTitle(R.string.input_review);
        final EditText reviewEditText = new EditText(HeritageActivity.this);
        reviewEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        reviewEditText.requestFocus();
        reviewEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(reviewEditText);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(StageActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(reviewEditText.getWindowToken(), 0);
                userReview = reviewEditText.getText().toString();
                submitReview();
                Toast.makeText(HeritageActivity.this, "Recensione registrata!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(StageActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(reviewEditText.getWindowToken(), 0);
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void submitReview() {
        RequestQueue queue = Volley.newRequestQueue(this);
        userReview = userReview.replaceAll(" ", "%20");
        String url = getString(R.string.server_url) + "submitReview/" + mAuth.getCurrentUser().getEmail() + "/" + heritageCode + "/" + userReview + "/";
        Log.d(TAG, url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                hasReviewed = true;
                for (int i = 0; i < response.length(); i++) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(HeritageActivity.this);
                    builder.setTitle(R.string.congratulations)
                            .setMessage(R.string.congratulations_mission)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.star_off)
                            .show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(HeritageActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.star_off)
                .show();
    }

    private void getHeritage() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getHeritageInfo/" + heritageCode + "/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsHeritageInfo = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        name.setText(jsObj.getString("name"));
                        structureType.setText(String.format(getString(R.string.structuretype), jsObj.getString("structuretype")));
                        latitude.setText((String.format(getString(R.string.latitude), jsObj.getString("latitude"))));
                        longitude.setText((String.format(getString(R.string.longitude), jsObj.getString("longitude"))));
                        province.setText((String.format(getString(R.string.province), jsObj.getString("province"))));
                        region.setText((String.format(getString(R.string.region), jsObj.getString("region"))));
                        historicalPeriod.setText((String.format(getString(R.string.historicalperiod), jsObj.getString("historicalperiod"))));
                        description.setText(jsObj.getString("description"));
                        hasReviewed = jsObj.getString("hasReviewed").equals("1");
                        GlideApp.with(HeritageActivity.this).load(getString(R.string.server_url) + "images/heritages/" + jsObj.getString("filename")).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(image);

                        if (jsObj.getInt("visited") == 0) {
                            addVisitedHeritage();
                            showDialog(getString(R.string.congratulations), getString(R.string.congratulations_heritage));
                        }
                    }
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

        queue.add(jsHeritageInfo);
    }

    private void addVisitedHeritage() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "addVisitedHeritage/" + mAuth.getCurrentUser().getEmail() + "/" + String.valueOf(heritageCode) + "/";
        JsonArrayRequest jsHeritageInfo = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() != 0) {
                    // Some medal(s)/missions unlocked
                    try {
                        JSONArray jsArray = (JSONArray) response.get(0);
                        for (int j = 0; j < jsArray.length(); j++) {
                            showDialog(getString(R.string.congratulations), getString(R.string.congratulations_medal2));
                        }
                        jsArray = (JSONArray) response.get(1);
                        for (int j = 0; j < jsArray.length(); j++) {
                            showDialog(getString(R.string.congratulations), getString(R.string.congratulations_mission));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        queue.add(jsHeritageInfo);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        setResult(Activity.RESULT_OK);
        finish();
    }

}