package com.example.anna.neptis.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginDialogActivity extends Activity {
    //utilizziamo queste 2 variabili per individuare gli eventi sul bottone di loginFacebook
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    public static boolean flag_login_session = false;
    private String url;
    private String user;
    private String pass;
    private String session_result="";
    private final static String SESSION = "session";
    private final static String CURRENT_SESSION = "current_session";

    //private User u;//current_user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText username = (EditText)findViewById(R.id.regEmail);
        final EditText password = (EditText)findViewById(R.id.regPassword);
        final TextView registerLink = (TextView)findViewById(R.id.registerHere);


        /*******gestione click bottone Login with facebook********/
        /*
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                flag_login_session = true;
                goMainScreen();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.cancel_login, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.error_login, Toast.LENGTH_SHORT).show();
            }
        });
        */
        /*******fine gestione click bottone Login with facebook********/



        /*******gestione click sui bottoni Login e Cancel********/
        Button bLogin = (Button)findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = username.getText().toString();
                pass = password.getText().toString();

               // u= new User(user);

                /***********_______START TEMPLATE JSON REQUEST________**********/

                RequestQueue queue = Volley.newRequestQueue(LoginDialogActivity.this);
                url = getString(R.string.server_url) + "checkUser/"+user+"/"+pass+"/";
                // Request a string response from the provided URL.

                Log.d("url= ",url);

                JsonArrayRequest jsObject = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject obj_log = null;
                        int isLog = 0;
                        try {
                            obj_log = response.getJSONObject(0);
                            Log.d("log result: ",obj_log.toString());
                            isLog = obj_log.getInt("EXISTS (SELECT * from User where email='"+user+"' and password='"+ pass+"')");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isLog==1) {
                            Toast.makeText(LoginDialogActivity.this, "Autenticazione avvenuta con successo!", Toast.LENGTH_LONG).show();


                            // REGISTRA CHIAVE DI SESSIONE
                            createSessionToken(user,pass);

                            //l'activity si apre dopo un certo tempo (dopo che è terminato l'insert della chiave di sessione)
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //start your activity here
                                    getSessionToken(user);
                                }

                            }, 1000L);

                        }else {Toast.makeText(LoginDialogActivity.this, "User inesistente! Ricontrollare i dati inseriti.", Toast.LENGTH_LONG).show();}
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error: ",error.toString());
                        Toast.makeText(LoginDialogActivity.this,"Errore di autenticazione",Toast.LENGTH_LONG).show();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsObject);
                /***********_______END TEMPLATE JSON REQUEST________**********/


            }
        });

        //bottone Cancel --> utilizzato per resettare i dati inseriti
        Button bCancel = (Button)findViewById(R.id.bCancel);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("");
                password.setText("");
            }
        });
        /*******fine gestione click sui bottoni Login e Cancel********/




        /*******gestione click su TextView Register here********/
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRegisterPage = new Intent(LoginDialogActivity.this,RegisterActivity.class);
                startActivity(openRegisterPage);
            }
        });
        /*******fine gestione click su TextView Register here********/


    }

    private void goMainScreen() {
        flag_login_session = true;
        Intent openPortalPage = new Intent();
        openPortalPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        setResult(Activity.RESULT_OK,openPortalPage);
        finish();

    }

    private void createSessionToken(String e, String p){
        RequestQueue queue = Volley.newRequestQueue(LoginDialogActivity.this);
        url = getString(R.string.server_url) +"createSession/"+e+"/"+p+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url);
        JsonObjectRequest jsObject = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error:", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsObject);
        /***********_______END TEMPLATE JSON REQUEST________**********/
    }

    private void getSessionToken(String e){
        RequestQueue queue = Volley.newRequestQueue(LoginDialogActivity.this);
        url = getString(R.string.server_url) + "getSession/"+e+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",url);
        JsonArrayRequest jsObject = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject obj_sess = response.getJSONObject(0);
                    session_result = obj_sess.getString("session");
                    Log.d("get session token :",session_result);

                    //////////////////
                    //SALVA CHIAVE SESSIONE IN PREFERENZE
                    //////////////////
                    SharedPreferences prefs = getSharedPreferences(SESSION, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(CURRENT_SESSION, session_result);
                    editor.apply();

                    Intent returnMain = new Intent(LoginDialogActivity.this, PortalsMainActivity.class);
                    //setResult(RESULT_OK,returnMain);
                    startActivity(returnMain);
                    //finish();

                    //returnMain.putExtra("user",u.getEmail());
                    //returnMain.putExtra("password",u.getPassword());
                    //returnMain.putExtra("accedi",Boolean.toString(true));

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley error:", error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(jsObject);
        /***********_______END TEMPLATE JSON REQUEST________**********/
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    /*public static boolean getFlagLogin(){
        return flag_login_session;
    }*/

}