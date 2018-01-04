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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.model.Question;
import it.neptis.gopoleis.model.Stage;

public class StageActivity extends AppCompatActivity {

    //private static final String TAG = "StageActivity";

    private Stage stage;
    private String userAnswer;
    private FirebaseAuth mAuth;
    private boolean isCompleted;
    private ImageButton answerButton;
    private String stageCode;
    private TextView titleTextView, curiosityTextView, questionTextView;
    private ImageButton hintOnSiteButton, hintByPayingButton;
    private boolean solved;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

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
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.stage);

        getStage();
    }

    private void getStage() {
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getStageByCode/" + stageCode + "/" + mAuth.getCurrentUser().getEmail();
        JsonArrayRequest stageRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsObj = (JSONObject) response.get(0);
                    stage = new Stage(jsObj.getInt("stagecode"), jsObj.getString("title"), jsObj.getString("curiosity"), new LatLng(Double.parseDouble(jsObj.getString("latitude")), Double.parseDouble(jsObj.getString("longitude"))), new Question(jsObj.getInt("questioncode"), jsObj.getString("question"), jsObj.getString("hintonsite"), jsObj.getString("hintbypaying"), jsObj.getString("answer")), jsObj.getString("isfinal").equals("true"), jsObj.getInt("hintUnlocked") == 1);

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
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(StageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(stageRequest);
    }

    private void setUIData() {
        titleTextView.setText(stage.getTitle());
        curiosityTextView.setText(String.format(getString(R.string.stage_curiosity), stage.getCuriosity()));
        questionTextView.setText(stage.getQuestion().getQuestion());

        progressDialog.dismiss();

        hintOnSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(StageActivity.this, stage.getQuestion().getHintOnSite(), Toast.LENGTH_SHORT).show();
                showHint(stage.getQuestion().getHintOnSite());
            }
        });

        hintByPayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage.isHintUnlocked()) {
                    //Toast.makeText(StageActivity.this, stage.getQuestion().getHintByPaying(), Toast.LENGTH_SHORT).show();
                    showHint(stage.getQuestion().getHintByPaying());
                    return;
                }

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(StageActivity.this);
                builder.setMessage(R.string.hint_will_cost)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog = new ProgressDialog(StageActivity.this);
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
                                                    String url = getString(R.string.server_url) + "player/buyHint/" + stage.getCode() + "/" + mUser.getEmail() + "/";
                                                    JsonArrayRequest buyHintRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                                        @Override
                                                        public void onResponse(JSONArray response) {
                                                            progressDialog.dismiss();

                                                            if (response.length() == 0) {
                                                                //Toast.makeText(StageActivity.this, stage.getQuestion().getHintByPaying(), Toast.LENGTH_SHORT).show();
                                                                showHint(stage.getQuestion().getHintByPaying());
                                                                stage.setHintUnlocked(true);
                                                            } else
                                                                Toast.makeText(StageActivity.this, R.string.not_enough_coins, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(StageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }) {
                                                        @Override
                                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("MyToken", idToken[0]);
                                                            return params;
                                                        }
                                                    };

                                                    RequestQueueSingleton.getInstance(StageActivity.this).addToRequestQueue(buyHintRequest);
                                                } else {
                                                    // Handle error -> task.getException();
                                                    progressDialog.dismiss();
                                                    Toast.makeText(StageActivity.this, "There was an error with your request", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
    }

    private void submitStageAnswer() {
        if (! userAnswer.trim().replaceAll("\\s+", " ").toUpperCase().equals(stage.getQuestion().getAnswer().toUpperCase())) {
            Toast.makeText(this, R.string.wrong_stage_answer, Toast.LENGTH_SHORT).show();
            return;
        }

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
                            String formattedUserAnswer = userAnswer.trim().replaceAll("\\s+", " ").replace(" ", "%20").toUpperCase();
                            String url = getString(R.string.server_url) + "player/submitStageAnswer/" + stage.getCode() + "/" + mUser.getEmail() + "/" + formattedUserAnswer + "/";
                            JsonArrayRequest submitAnswerRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    progressDialog.dismiss();
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
                                        Toast.makeText(StageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(StageActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            RequestQueueSingleton.getInstance(StageActivity.this).addToRequestQueue(submitAnswerRequest);
                        } else {
                            // Handle error -> task.getException();
                            progressDialog.dismiss();
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

    private void showHint(final String hint) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(StageActivity.this);
        builder.setTitle(R.string.hint)
                .setMessage(hint)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_info)
                .show();
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