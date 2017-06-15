package com.example.anna.neptis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    EditText reg_email;
    EditText reg_password;
    EditText confirm_password;
    String email;
    String password;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reg_email = (EditText) findViewById(R.id.regEmail);
        reg_password = (EditText) findViewById(R.id.regPassword);
        confirm_password = (EditText) findViewById(R.id.regConfirmPass);

        //gestione click su bottone Register
        Button bRegister = (Button)findViewById(R.id.bRegister);
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            email = reg_email.getText().toString();
            password = reg_password.getText().toString();


            /***********_______START TEMPLATE JSON REQUEST________**********/

            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
            url =getString(R.string.server_url)+"createUser/"+email+"/"+password+"/";
            // Request a string response from the provided URL.

            Log.d("url= ",url);

            JsonObjectRequest jsObject = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Toast.makeText(RegisterActivity.this,"Registrazione avvenuta con successo!",Toast.LENGTH_LONG).show();
                    Intent returnPageLogin = new Intent(RegisterActivity.this,LoginDialogActivity.class);
                    startActivity(returnPageLogin);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegisterActivity.this,"Errore di registrazione!",Toast.LENGTH_LONG).show();
                }
            });
            // Add the request to the RequestQueue.
            queue.add(jsObject);
            /***********_______END TEMPLATE JSON REQUEST________**********/

            }
        });




    }


}
