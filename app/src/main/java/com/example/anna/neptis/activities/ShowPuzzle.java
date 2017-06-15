package com.example.anna.neptis.activities;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowPuzzle extends AppCompatActivity {

    String nome;
    String url;
    String url2;
    String url3;
    String risposta;
    String indizio;
    TextView nome_titolo;
    TextView descrizione;
    ImageButton solve;
    ImageButton hint;
    String puzzle_code;
    String game_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_puzzle);

        String temp = getIntent().getExtras().getString("name");
        game_code = getIntent().getExtras().getString("game_code");


        nome_titolo = (TextView) findViewById(R.id.l_puzzle_name);
        nome_titolo.setText(temp);

        nome = temp.replace(" ","%20");

        descrizione = (TextView) findViewById(R.id.l_test_puzzle_descr);

        ///

        solve = (ImageButton) findViewById(R.id.ib_solve);
        solve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_answer = new Intent(ShowPuzzle.this,PuzzleAnswerActivity.class);
                go_to_answer.putExtra("answer",risposta);
                startActivityForResult(go_to_answer,100);
            }
        });

        hint = (ImageButton) findViewById(R.id.ib_hint);
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowPuzzle.this,indizio,Toast.LENGTH_LONG).show();
            }
        });

        /***********_______START TEMPLATE JSON REQUEST________**********/

        RequestQueue queue = Volley.newRequestQueue(ShowPuzzle.this);
        url = getString(R.string.server_url)+"getPuzzleDescription/"+nome+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url);

        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj_desc = response.getJSONObject(0);
                    descrizione.setText(obj_desc.getString("description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error:", error.toString());
                Toast.makeText(ShowPuzzle.this,"No Description...",Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsArray);
        /***********_______END TEMPLATE JSON REQUEST________**********/

        /***********_______START TEMPLATE JSON REQUEST________**********/

        RequestQueue queue2 = Volley.newRequestQueue(ShowPuzzle.this);
        url2 = getString(R.string.server_url)+"getPuzzleAnswer/"+nome+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url2);

        JsonArrayRequest jsArray2 = new JsonArrayRequest(Request.Method.GET, url2,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj_desc = response.getJSONObject(0);
                    risposta = obj_desc.getString("answer");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error:", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue2.add(jsArray2);
        /***********_______END TEMPLATE JSON REQUEST________**********/

        /***********_______START TEMPLATE JSON REQUEST________**********/

        RequestQueue queue3 = Volley.newRequestQueue(ShowPuzzle.this);
        url3 = getString(R.string.server_url)+"getPuzzleHint/"+nome+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url3);

        JsonArrayRequest jsArray3 = new JsonArrayRequest(Request.Method.GET, url3,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj_desc = response.getJSONObject(0);
                    indizio = obj_desc.getString("hint");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error:", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue3.add(jsArray3);
        /***********_______END TEMPLATE JSON REQUEST________**********/

        /***********_______START TEMPLATE JSON REQUEST________**********/

        RequestQueue queue4 = Volley.newRequestQueue(ShowPuzzle.this);
        String url4 = getString(R.string.server_url)+"getPuzzleCode/"+nome+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url4);

        JsonArrayRequest jsArray4 = new JsonArrayRequest(Request.Method.GET, url4,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj_desc = response.getJSONObject(0);
                    puzzle_code = obj_desc.getString("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error:", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue4.add(jsArray4);
        /***********_______END TEMPLATE JSON REQUEST________**********/
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {

                Toast.makeText(ShowPuzzle.this,"THAT WAS EASY!!",Toast.LENGTH_LONG).show();

                /***********_______START TEMPLATE JSON REQUEST________**********/

                RequestQueue queue5 = Volley.newRequestQueue(ShowPuzzle.this);
                String url5 = getString(R.string.server_url)+"acquirePuzzle/"+game_code+"/"+puzzle_code+"/";
                // Request a string response from the provided URL.
                Log.d("url= ",url5);

                JsonArrayRequest jsArray5 = new JsonArrayRequest(Request.Method.GET, url5,null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Acquisisci: ",response.toString());
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Volley error:", error.toString());
                        finish();
                    }
                });
                // Add the request to the RequestQueue.
                queue5.add(jsArray5);
                /***********_______END TEMPLATE JSON REQUEST________**********/


        }
        }
    }


}
