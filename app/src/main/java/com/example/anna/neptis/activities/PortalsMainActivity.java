package com.example.anna.neptis.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
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
import com.example.anna.neptis.defines.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class PortalsMainActivity extends AppCompatActivity {


    final static int RQ_CODE = 1;
    private SharedPreferences prefs;
    private String pre;

    //private SharedPreferences pref_tutorial;
    //private SharedPreferences read_pref_tutorial;
    private static final String TUTORIAL = "Tutorial";
    private static final String FLAG_TUTORIAL = "Flag";

    private User current_user;
    TextView utente_loggato;

    String urlToken;

    Dialog home_dialog,login_dialog;
    private int flag_tutorial = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portals_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        /*
        pref_tutorial = getSharedPreferences(TUTORIAL,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_tutorial.edit();
        editor.putInt(FLAG_TUTORIAL, 1);
        editor.apply();

        read_pref_tutorial = getSharedPreferences("Tutorial", Context.MODE_PRIVATE);
        flag_tutorial = pref_tutorial.getInt("Flag", 2);
        Log.d("flag salvato PMA: ",Integer.toString(flag_tutorial));

        */

        utente_loggato = (TextView) findViewById(R.id.nome_user);
        utente_loggato.setText("Eseguire l'accesso");

        //DEBUG CONTROLLO PREFERENZE//
        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        pre = prefs.getString("current_session", "");
        Log.d("Pref salvate create: ",pre);
        //////////////////////////////

        //DEBUG URL
        Log.d("server URL: ",getString(R.string.server_url));


        /****__________________bottoni dei 4 portali____________________*****/

        /**
         *
         * MANAGEMENT YELLOW PORTAL
         *
         */
        ImageButton yellowPortalButton = (ImageButton)findViewById(R.id.yellow_portal);
        yellowPortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pre == "") {
                    Intent openYellowPortal = new Intent(PortalsMainActivity.this, LoginDialogActivity.class);
                    startActivityForResult(openYellowPortal, RQ_CODE);

                } else {
                    Intent openYellowPortal = new Intent(PortalsMainActivity.this, TreasurePortalPag1.class);
                    openYellowPortal.putExtra("user",current_user.getEmail());
                    openYellowPortal.putExtra("tutorial",flag_tutorial);
                    startActivity(openYellowPortal);
                }
            }
            // }

        });
        /**
         *
         * MANAGEMENT GREEN PORTAL
         *
         */
        ImageButton greenPortalButton = (ImageButton)findViewById(R.id.green_portal);
        greenPortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pre == "") {
                    Intent openGreenPortal = new Intent(PortalsMainActivity.this,LoginDialogActivity.class);
                    startActivityForResult(openGreenPortal, RQ_CODE);

                } else {
                    Intent openGreenPortal = new Intent(PortalsMainActivity.this, TravelPortalActivity.class);
                    openGreenPortal.putExtra("user",current_user.getEmail());
                    startActivity(openGreenPortal);
                }
            }
        });
        /**
         *
         * MANAGEMENT RED PORTAL
         *
         */
        ImageButton redPortalButton = (ImageButton)findViewById(R.id.red_portal);
        redPortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pre == "") {
                    Intent openRedPortal = new Intent(PortalsMainActivity.this,LoginDialogActivity.class);
                    startActivityForResult(openRedPortal, RQ_CODE);
                } else {
                    Intent openRedPortal = new Intent(PortalsMainActivity.this, PuzzlePortal.class);
                    openRedPortal.putExtra("user",current_user.getEmail());
                    startActivity(openRedPortal);
                }
            }
        });
        /**
         *
         * MANAGEMENT BLUE PORTAL
         *
         */
        ImageButton bluePortalButton = (ImageButton)findViewById(R.id.blue_portal);
        bluePortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(),"Blue portal",Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        /*____________________fine gestione imageButton dei 4 portali_______________________*/


    }


    public void getUserByToken(String pre){
        RequestQueue queue = Volley.newRequestQueue(PortalsMainActivity.this);
        urlToken = getString(R.string.server_url)+"getUserFromSession/"+pre+"/";
        // Request a string response from the provided URL.
        Log.d("url= ",urlToken);
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, urlToken,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try{
                    int contLength = response.length();
                    for(int i = 0;i< contLength;i++){
                        JSONObject jsObj = (JSONObject)response.get(i);
                        String user = jsObj.getString("email");

                        Log.d("utente userBy: ",user);
                        current_user = new User(user);
                        utente_loggato.setText(current_user.getEmail());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!",error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsArray);
        /***********_______END TEMPLATE JSON REQUEST________**********/


    }



    @Override
    protected void onResume(){
        super.onResume();

        //DEBUG CONTROLLO PREFERENZE//
        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        pre = prefs.getString("current_session", "");
        Log.d("Pref salvat on resume: ",pre);
        //////////////////////////////

        getUserByToken(pre);
        if(pre.equals("")) {
            tutorial();
        }

    }


    private void goLoginScreen() {
        Intent openLoginPage = new Intent(this,LoginDialogActivity.class);
        openLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(openLoginPage);
    }

    //revoke permession
    void deleteFacebookApplication(){
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                boolean isSuccess = false;
                try {
                    isSuccess = response.getJSONObject().getBoolean("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (isSuccess && response.getError()==null){
                    // Application deleted from Facebook account
                }

            }
        }).executeAsync();
    }


    public void logout(View view) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("current_session", "");
        editor.apply();

        utente_loggato.setText("Eseguire l'accesso");


        Toast.makeText(view.getContext(),"LogOutEffettuato",Toast.LENGTH_SHORT).show();
        Intent logout = new Intent(PortalsMainActivity.this,LoginDialogActivity.class);
        startActivityForResult(logout, RQ_CODE);
        /*
        if(LoginDialogActivity.flag_login_session == true) {
            deleteFacebookApplication();
            LoginManager.getInstance().logOut();
            accedi = false;
            Intent openPagLogin = new Intent(PortalsMainActivity.this,LoginDialogActivity.class);
            startActivity(openPagLogin);
            //goLoginScreen();
            //Per uscire dall'app appena viene premuto il tasto dil logout
        */
            /*moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);*/
        /*}else{
            Toast.makeText(view.getContext(),"Impossibile effettuare il logout. Utente non loggato..",Toast.LENGTH_SHORT).show();
        }
        */

        //NB: non viene chiusa l'app, rimane aperta in background
        /*Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
    }

    @Override
   protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RQ_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("Accedi ok: ", "Accedi ok");
                getUserByToken(pre);
            }
        }
    }


    public void tutorial(){
        /*
        pref_tutorial = getSharedPreferences(TUTORIAL,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref_tutorial.edit();
        editor.putInt(FLAG_TUTORIAL, 1);
        editor.apply();
        */

        AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
        miaAlert.setTitle("Benvenuto in GoPoleis!");
        miaAlert.setMessage("Prima di iniziare la nostra avventura volevamo darti delle indicazioni su come muoverti nei portali.\nVuoi avviare il tutorial?");
        miaAlert.setIcon(R.drawable.logo);

        miaAlert.setCancelable(false);
        miaAlert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                home_dialog = new Dialog(PortalsMainActivity.this);
                home_dialog.setCancelable(false);
                home_dialog.setContentView(R.layout.tutorial_portal_main_activity);
                home_dialog.show();

            }
        });

        miaAlert.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*
                read_pref_tutorial = getSharedPreferences("Tutorial", Context.MODE_PRIVATE);
                flag_tutorial = pref_tutorial.getInt("Flag", 2);
                Log.d("flag salvato: ",Integer.toString(flag_tutorial));

                if(flag_tutorial == 1){
                    Log.d("if skip button","sono entrato");

                    SharedPreferences.Editor editor = pref_tutorial.edit();
                    editor.putInt("Flag", 0);
                    editor.apply();

                    dialog.cancel();
                    flag_tutorial = 0;
                }else{
                    dialog.cancel();
                    flag_tutorial = 0;
                }
                */
                dialog.cancel();
            }
        });



        AlertDialog alert = miaAlert.create();
        alert.show();

    }


    public void avanti_home(View view) {
        login_dialog=new Dialog(PortalsMainActivity.this);
        login_dialog.setCancelable(false);
        login_dialog.setContentView(R.layout.tutorial_login);
        home_dialog.cancel();
        login_dialog.show();
    }


    public void avanti_login(View view) {
        login_dialog.cancel();

        AlertDialog.Builder inizia = new AlertDialog.Builder(this);
        inizia.setTitle("Inizia la tua avventura!");
        inizia.setMessage("Effettua il login ed entra in uno dei portali.\nGoPoleis!");
        inizia.setIcon(R.drawable.logo);

        inizia.setCancelable(false);
        inizia.setPositiveButton("Inizia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        flag_tutorial = 0;
        AlertDialog alert = inizia .create();
        alert.show();


    }


}