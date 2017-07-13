package it.neptis.gopoleis.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

import it.neptis.gopoleis.GopoleisApp;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Stage;

public class StageActivity extends AppCompatActivity {

    private static final String TAG = "StageActivity";

    private Stage stage;
    private String userAnswer;
    private FirebaseAuth mAuth;
    private boolean isCompleted;
    private ImageButton answerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);

        GopoleisApp gopoleisApp = (GopoleisApp) getApplicationContext();
        stage = gopoleisApp.getStage();

        mAuth = FirebaseAuth.getInstance();

        TextView titleTextView, curiosityTextView, questionTextView;
        ImageButton hintOnSiteButton, hintByPayingButton;
        titleTextView = (TextView) findViewById(R.id.stage_title);
        curiosityTextView = (TextView) findViewById(R.id.stage_curiosity);
        questionTextView = (TextView) findViewById(R.id.stage_question);
        hintOnSiteButton = (ImageButton) findViewById(R.id.hintOnSiteButton);
        hintByPayingButton = (ImageButton) findViewById(R.id.hintByPayingButton);
        answerButton = (ImageButton) findViewById(R.id.answerButton);
        titleTextView.setText(stage.getTitle());
        curiosityTextView.setText(String.format(getString(R.string.stage_curiosity), stage.getCuriosity()));
        questionTextView.setText(stage.getQuestion().getQuestion());

        // TODO Check position
        hintOnSiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StageActivity.this, stage.getQuestion().getHintOnSite(), Toast.LENGTH_SHORT).show();
            }
        });

        // TODO Implement coins
        hintByPayingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StageActivity.this, stage.getQuestion().getHintByPaying(), Toast.LENGTH_SHORT).show();
            }
        });

        checkIfCompleted();
    }

    private void submitStageAnswer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        // TODO format userAnswer to all caps and trim spaces
        String url = getString(R.string.server_url) + "submitStageAnswer/" + stage.getCode() + "/" + mAuth.getCurrentUser().getEmail() + "/" + userAnswer.replace(" ", "%20");
        JsonObjectRequest jsArray = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.length() == 0)
                        Toast.makeText(StageActivity.this, R.string.wrong_stage_answer, Toast.LENGTH_SHORT).show();
                    else {
                        boolean stageCompleted = response.getBoolean("stagecompleted");
                        boolean pathCompleted = response.getBoolean("pathcompleted");
                        if (pathCompleted)
                            showDialog(getString(R.string.path_completed));
                        if (stageCompleted)
                            showDialog(getString(R.string.stage_completed));
                        isCompleted = true;
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

        queue.add(jsArray);
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(StageActivity.this);
        builder.setTitle(R.string.congratulations)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.star_off)
                .show();
    }

    private void checkIfCompleted() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "checkStageCompleted/" + stage.getCode() + "/" + mAuth.getCurrentUser().getEmail();
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                isCompleted = response.length() != 0;
                answerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isCompleted)
                            Toast.makeText(StageActivity.this, R.string.stage_already_completed, Toast.LENGTH_SHORT).show();
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(StageActivity.this);
                            builder.setTitle(R.string.input_answer);
                            final EditText answerEditText = new EditText(StageActivity.this);
                            answerEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(answerEditText);
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userAnswer = answerEditText.getText().toString();
                                    submitStageAnswer();
                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    }
                });
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