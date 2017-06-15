package com.example.anna.neptis.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.anna.neptis.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {
    //utilizziamo queste 2 variabili per individuare gli eventi sul bottone di loginFacebook
   /* private LoginButton loginButton;
    private CallbackManager callbackManager;
    public static boolean flag_login_session = false;
    private boolean flag_log = false;*/
    //public static boolean flag_welcome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

       /* final EditText username = (EditText)findViewById(R.id.regUsername);
        final EditText password = (EditText)findViewById(R.id.regPassword);
        final TextView registerLink = (TextView)findViewById(R.id.registerHere);*/

        /*if(flag_welcome == false) {
            Intent openDialogPage = new Intent(LoginActivity.this, DialogActivity.class);
            startActivity(openDialogPage);
        }*/


        /*******gestione click bottone Login with facebook********/
        /*callbackManager = CallbackManager.Factory.create();
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
        });*/
        /*******fine gestione click bottone Login with facebook********/





        /*******gestione click sui bottoni Login e Cancel********/
       /* Button bLogin = (Button)findViewById(R.id.bLogin);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*/

                //istruzioni da eseguire appena viene cliccato il bottone Login
                /*if(RegisterActivity.getUser() == null || RegisterActivity.getPassword() == null){
                    Toast erroreLogin = Toast.makeText(getApplicationContext(),"User inesistente! Effettuare la registrazione..",Toast.LENGTH_SHORT);
                    erroreLogin.show();
                }else if(username.getText().toString().equals(RegisterActivity.getUser())&& password.getText().toString().equals(RegisterActivity.getPassword())){
                   Toast openHomePage = Toast.makeText(getApplicationContext(),"Open Home Page",Toast.LENGTH_SHORT);
                    openHomePage.show();
                    flag_login_session = true;
                    Intent openHomePage = new Intent(LoginActivity.this,PortalsMainActivity.class);
                    startActivity(openHomePage);
                }else{
                    Toast erroreLogin = Toast.makeText(getApplicationContext(),"Username o password errate! Riprovare..",Toast.LENGTH_SHORT);
                    erroreLogin.show();
                }*/

            }
       // });

        //bottone Cancel --> utilizzato per resettare i dati inseriti
       /* Button bCancel = (Button)findViewById(R.id.bCancel);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setText("");
                password.setText("");
            }
        });*/
        /*******fine gestione click sui bottoni Login e Cancel********/


        /*
        Button bFacebook = (Button)findViewById(R.id.face_login);

        bFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openFacebookLoginActivity = new Intent(LoginActivity.this,FacebookLoginActivity.class);
                startActivity(openFacebookLoginActivity);
            }
        });
        */



        /*******gestione click su TextView Register here********/
        /*registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRegisterPage = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(openRegisterPage);
            }
        });*/
        /*******fine gestione click su TextView Register here********/


        /*gestione database*/
        /*try {
            URL url = new URL("http://www.android.com/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);*/

                /*post nome utente*/
                /*OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write("Nome utente");
                writer.flush();
                writer.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));


                //readStream(in);
            } finally {
                urlConnection.disconnect();
            }

        }catch(Exception e){}

    }

    private void goMainScreen() {
        flag_login_session = true;
        Intent openPortalPage = new Intent(LoginActivity.this,PortalsMainActivity.class);
        openPortalPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(openPortalPage);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public static boolean getFlagLogin(){
        return flag_login_session;
    }*/











//}
