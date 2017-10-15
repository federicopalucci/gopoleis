package it.neptis.gopoleis.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
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

import it.neptis.gopoleis.HurlStackProvider;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.model.Question;
import it.neptis.gopoleis.model.Stage;

public class StageActivity extends AppCompatActivity {

    private static final String TAG = "StageActivity";

    private Stage stage;
    private String userAnswer;
    private FirebaseAuth mAuth;
    private boolean isCompleted;
    private ImageButton answerButton;
    private String stageCode;
    private TextView titleTextView, curiosityTextView, questionTextView;
    private ImageButton hintOnSiteButton, hintByPayingButton;
    private boolean solved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        stageCode = getIntent().getStringExtra("code");

        solved = false;

        titleTextView = (TextView) findViewById(R.id.stage_title);
        curiosityTextView = (TextView) findViewById(R.id.stage_curiosity);
        questionTextView = (TextView) findViewById(R.id.stage_question);
        hintOnSiteButton = (ImageButton) findViewById(R.id.hintOnSiteButton);
        hintByPayingButton = (ImageButton) findViewById(R.id.hintByPayingButton);
        answerButton = (ImageButton) findViewById(R.id.answerButton);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.stage);

        getStage();
    }

    private void submitStageAnswer() {
        final String[] idToken = new String[1];
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken[0] = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            RequestQueue queue = Volley.newRequestQueue(StageActivity.this, HurlStackProvider.getHurlStack());
                            String formattedUserAnswer = userAnswer.trim().replaceAll("\\s+", " ").replace(" ", "%20").toUpperCase();
                            String url = getString(R.string.server_url) + "player/submitStageAnswer/" + stage.getCode() + "/" + mAuth.getCurrentUser().getEmail() + "/" + formattedUserAnswer + "/";
                            JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        if (response.length() == 0)
                                            Toast.makeText(StageActivity.this, R.string.wrong_stage_answer, Toast.LENGTH_SHORT).show();
                                        else {
                                            // stage completed
                                            showDialog(getString(R.string.stage_completed));
                                            solved = true;

                                            boolean pathCompleted = response.getBoolean(0);
                                            if (pathCompleted)
                                                showDialog(getString(R.string.path_completed));
                                            isCompleted = true;

                                            for (int i = 0; i < ((JSONArray) response.get(1)).length(); i++) {
                                                showDialog(getString(R.string.congratulations_mission));
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
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            queue.add(jsArray);
                        } else {
                            // Handle error -> task.getException();
                            Log.d(TAG, task.getException().toString());
                            Toast.makeText(StageActivity.this, "There was an error with your request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showDialog(final String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(StageActivity.this);
        builder.setTitle(R.string.congratulations)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (message.equals(getString(R.string.stage_completed)))
                            onBackPressed();
                    }
                })
                .setIcon(android.R.drawable.star_off)
                .show();
    }

    private void getStage() {
        RequestQueue queue = Volley.newRequestQueue(this, HurlStackProvider.getHurlStack());
        String url = getString(R.string.server_url) + "getStageByCode/" + stageCode + "/" + mAuth.getCurrentUser().getEmail();
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        stage = new Stage(jsObj.getInt("stagecode"), jsObj.getString("title"), jsObj.getString("curiosity"), new LatLng(Double.parseDouble(jsObj.getString("latitude")), Double.parseDouble(jsObj.getString("longitude"))), new Question(jsObj.getInt("questioncode"), jsObj.getString("question"), jsObj.getString("hintonsite"), jsObj.getString("hintbypaying"), jsObj.getString("answer")), null, jsObj.getString("isfinal").equals("true"), jsObj.getInt("hintUnlocked") == 1);

                        setUIData();

                        isCompleted = jsObj.getInt("completed") == 1;
                        answerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isCompleted)
                                    Toast.makeText(StageActivity.this, R.string.stage_already_completed, Toast.LENGTH_SHORT).show();
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(StageActivity.this);
                                    builder.setTitle(R.string.input_answer);
                                    final EditText answerEditText = new EditText(StageActivity.this);
                                    answerEditText.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    answerEditText.requestFocus();
                                    answerEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                                    builder.setView(answerEditText);
                                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(StageActivity.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(answerEditText.getWindowToken(), 0);
                                            userAnswer = answerEditText.getText().toString();
                                            submitStageAnswer();
                                        }
                                    });
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(StageActivity.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(answerEditText.getWindowToken(), 0);
                                            dialog.cancel();
                                        }
                                    });
                                    builder.show();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
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

    private void setUIData() {
        titleTextView.setText(stage.getTitle());
        curiosityTextView.setText(String.format(getString(R.string.stage_curiosity), stage.getCuriosity()));
        questionTextView.setText(stage.getQuestion().getQuestion());

        hintOnSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StageActivity.this, stage.getQuestion().getHintOnSite(), Toast.LENGTH_SHORT).show();
            }
        });

        hintByPayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage.isHintUnlocked()) {
                    Toast.makeText(StageActivity.this, stage.getQuestion().getHintByPaying(), Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(StageActivity.this);
                builder.setMessage("Questo indizio ti costerà 1 coin, vuoi sbloccarlo?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final String[] idToken = new String[1];
                                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                                mUser.getIdToken(true)
                                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                if (task.isSuccessful()) {
                                                    idToken[0] = task.getResult().getToken();
                                                    // Send token to your backend via HTTPS
                                                    RequestQueue queue = Volley.newRequestQueue(StageActivity.this, HurlStackProvider.getHurlStack());
                                                    String url = getString(R.string.server_url) + "player/buyHint/" + stage.getCode() + "/" + mAuth.getCurrentUser().getEmail() + "/";
                                                    JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                                        @Override
                                                        public void onResponse(JSONArray response) {
                                                            if (response.length() == 0) {
                                                                Toast.makeText(StageActivity.this, stage.getQuestion().getHintByPaying(), Toast.LENGTH_SHORT).show();
                                                                stage.setHintUnlocked(true);
                                                            }
                                                            else
                                                                Toast.makeText(StageActivity.this, R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Log.d(TAG, error.toString());
                                                        }
                                                    }) {
                                                        @Override
                                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("MyToken", idToken[0]);
                                                            return params;
                                                        }
                                                    };

                                                    queue.add(jsArray);
                                                } else {
                                                    // Handle error -> task.getException();
                                                    Log.d(TAG, task.getException().toString());
                                                    Toast.makeText(StageActivity.this, "There was an error with your request", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (solved) {
            Intent backToMainActivity = new Intent();
            backToMainActivity.putExtra("code", stageCode);
            setResult(Activity.RESULT_OK, backToMainActivity);
        } else
            setResult(Activity.RESULT_CANCELED);
        finish();
    }

}