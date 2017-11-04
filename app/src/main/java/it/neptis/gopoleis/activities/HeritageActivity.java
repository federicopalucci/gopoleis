package it.neptis.gopoleis.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.model.GlideApp;

public class HeritageActivity extends AppCompatActivity {

    //private static final String TAG = "HeritageActivity";

    private TextView name, structureType, coordinates, province_region, historicalPeriod, description;
    private String heritageCode;
    private FirebaseAuth mAuth;
    private ImageView image;
    private String userReview;
    private boolean hasReviewed = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heritage);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();

        heritageCode = getIntent().getStringExtra("code");

        name = (TextView) findViewById(R.id.heritage_name);
        structureType = (TextView) findViewById(R.id.heritage_structuretype);
        coordinates = (TextView) findViewById(R.id.heritage_coordinates);
        province_region = (TextView) findViewById(R.id.heritage_province_region);
        historicalPeriod = (TextView) findViewById(R.id.heritage_historicalperiod);
        description = (TextView) findViewById(R.id.heritage_description);
        image = (ImageView) findViewById(R.id.imageView2);
        Button submitReviewButton = (Button) findViewById(R.id.submit_reviews_button);
        Button readReviewsButton = (Button) findViewById(R.id.read_reviews_button);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.heritage);

        getHeritage();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getHeritage() {
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getHeritageInfo/" + heritageCode + "/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest heritageInfoRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsObj = (JSONObject) response.get(0);

                    name.setText(jsObj.getString("name"));
                    structureType.setText(String.format(getString(R.string.structuretype), jsObj.getString("structuretype")));
                    coordinates.setText((String.format(getString(R.string.ne_coordinates), jsObj.getString("latitude"), jsObj.getString("longitude"))));
                    province_region.setText((String.format(getString(R.string.heritage_province_region), jsObj.getString("province"), jsObj.getString("region"))));
                    historicalPeriod.setText((String.format(getString(R.string.historicalperiod), jsObj.getString("historicalperiod"))));
                    description.setText(jsObj.getString("description"));
                    hasReviewed = jsObj.getString("hasReviewed").equals("1");
                    GlideApp.with(HeritageActivity.this).load(getString(R.string.server_url) + "images/heritages/" + jsObj.getString("filename")).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(image);

                    if (jsObj.getInt("visited") == 0) {
                        addVisitedHeritage();
                    } else
                        progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(HeritageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(heritageInfoRequest);
    }

    private void addVisitedHeritage() {
        final String[] idToken = new String[1];
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken[0] = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            String url = getString(R.string.server_url) + "player/addVisitedHeritage/" + mUser.getEmail() + "/" + String.valueOf(heritageCode) + "/";
                            JsonArrayRequest addHeritageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    progressDialog.dismiss();

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

                                    showDialog(getString(R.string.congratulations), getString(R.string.congratulations_heritage));
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(HeritageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            RequestQueueSingleton.getInstance(HeritageActivity.this).addToRequestQueue(addHeritageRequest);
                        } else {
                            // Handle error -> task.getException();
                            progressDialog.dismiss();
                            Toast.makeText(HeritageActivity.this, "There was an error with your request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showWriteReviewDialog() {
        if (hasReviewed) {
            Toast.makeText(HeritageActivity.this, R.string.heritage_already_reviewed, Toast.LENGTH_LONG).show();
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        final String[] idToken = new String[1];
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken[0] = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            userReview = userReview.replaceAll(" ", "%20");
                            String url = getString(R.string.server_url) + "player/submitReview/" + mUser.getEmail() + "/" + heritageCode + "/" + userReview + "/";
                            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    progressDialog.dismiss();
                                    Toast.makeText(HeritageActivity.this, R.string.review_submitted, Toast.LENGTH_SHORT).show();

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
                                    progressDialog.dismiss();
                                    Toast.makeText(HeritageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            RequestQueueSingleton.getInstance(HeritageActivity.this).addToRequestQueue(jsonArrayRequest);
                        } else {
                            // Handle error -> task.getException();
                            progressDialog.dismiss();
                            Toast.makeText(HeritageActivity.this, "There was an error with your request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
    }

}