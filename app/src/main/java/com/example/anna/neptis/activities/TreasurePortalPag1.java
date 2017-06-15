package com.example.anna.neptis.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
//import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;


public class TreasurePortalPag1 extends AppCompatActivity implements OnItemSelectedListener {

    int contLength;
    String [] spinner_options;
    Spinner dropdown;
    private Animation lens_anim = null;
    //private Animation lens_anim2 = null;
    private ImageView lente = null;
    String item;
    String user;

    SharedPreferences prefs;
    String pre;
    String game;

    //SharedPreferences pref_tutorial;
    int flag_tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_portal_pag1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        getGameCode();
        user = getIntent().getExtras().getString("user");

        flag_tutorial = getIntent().getExtras().getInt("Tutorial");


        if(flag_tutorial == 1){
            //tutorial();
        }


        lente = (ImageView)findViewById(R.id.lens);
        //lente.setImageAlpha(100);
        /*******inizio configurazione dello spinner*******/
        dropdown = (Spinner)findViewById(R.id.spinner_menu);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(dropdown);

            // Set popupWindow height to 500px
            popupWindow.setHeight(320);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            Toast.makeText(getApplicationContext(),"Errore!",Toast.LENGTH_SHORT).show();
        }

        //inserimento item nello spinner da database
        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url)+"getHeritagesGame1/";

        // Request a string response from the provided URL.
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                //Log.d("Response is: ", response.toString());
                try{
                    contLength = response.length();
                    spinner_options = new String[contLength+1];
                    spinner_options[0] = "";
                    for(int i = 0;i< contLength;i++){
                        JSONObject jsObj = (JSONObject)response.get(i);
                        String value = jsObj.getString("heritage");
                        spinner_options[i+1] = value;
                        //Log.d("Spinner: ",spinner_options[i]+ "\n");
                        ArrayAdapter<?>adapter = new ArrayAdapter<Object>(TreasurePortalPag1.this,android.R.layout.simple_spinner_dropdown_item,spinner_options);
                        //applico l'adapter allo spinner
                        dropdown.setAdapter(adapter);
                        dropdown.setOnItemSelectedListener(TreasurePortalPag1.this);
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
        //***********_______END TEMPLATE JSON REQUEST________**********
        /*******fine configurazione dello spinner*******/

        /******inizio configurazione bottoni cards_list e achievement_list******/
        ImageButton card_list_image = (ImageButton) findViewById(R.id.cards_list_image);
        card_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openManageCard = new Intent(TreasurePortalPag1.this,ManageCards.class);
                openManageCard.putExtra("codice",100);
                startActivity(openManageCard);

            }});

        ImageButton mycards_list_image = (ImageButton)findViewById(R.id.mycards_list_image);
        mycards_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent openManageCard = new Intent(TreasurePortalPag1.this,ManageCards.class);
                        openManageCard.putExtra("codice",200);
                        openManageCard.putExtra("game_code",game);
                        startActivity(openManageCard);
                    }
                }, 1000L);

            }});


        ImageButton achivement_list_image= (ImageButton) findViewById(R.id.achieve);
        achivement_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAchievements = new Intent(TreasurePortalPag1.this,Achievements.class);
                //per inviare parametri all'activity Achievement
                openAchievements.putExtra("game","game1");
                startActivity(openAchievements);
            }
        });
        /******fine configurazione bottoni cards_list e achievement_list******/

    }


    @Override
    protected void onResume() {
        super.onResume();

        game = getIntent().getExtras().getString("game");
        user = getIntent().getExtras().getString("user");

        if(flag_tutorial == 1){
            //tutorial();
        }



        lente = (ImageView)findViewById(R.id.lens);
        //lente.setImageAlpha(100);
        /*******inizio configurazione dello spinner*******/
        dropdown = (Spinner)findViewById(R.id.spinner_menu);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(dropdown);

            // Set popupWindow height to 500px
            popupWindow.setHeight(320);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            Toast.makeText(getApplicationContext(),"Errore!",Toast.LENGTH_SHORT).show();
        }

        //inserimento item nello spinner da database

        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url)+"getHeritagesGame1/";

        // Request a string response from the provided URL.
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                //Log.d("Response is: ", response.toString());
                try{
                    contLength = response.length();
                    spinner_options = new String[contLength+1];
                    spinner_options[0] = "";
                    for(int i = 0;i< contLength;i++){
                        JSONObject jsObj = (JSONObject)response.get(i);
                        String value = jsObj.getString("heritage");
                        spinner_options[i+1] = value;
                        //Log.d("Spinner: ",spinner_options[i]+ "\n");
                        ArrayAdapter<?>adapter = new ArrayAdapter<Object>(TreasurePortalPag1.this,android.R.layout.simple_spinner_dropdown_item,spinner_options);
                        //applico l'adapter allo spinner
                        dropdown.setAdapter(adapter);
                        dropdown.setOnItemSelectedListener(TreasurePortalPag1.this);
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
        //***********_______END TEMPLATE JSON REQUEST________**********
        /*******fine configurazione dello spinner*******/

        /******inizio configurazione bottoni cards_list e achievement_list******/
        ImageButton card_list_image = (ImageButton) findViewById(R.id.cards_list_image);
        card_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openManageCard = new Intent(TreasurePortalPag1.this,ManageCards.class);
                openManageCard.putExtra("codice",100);
                startActivity(openManageCard);

            }});

        ImageButton mycards_list_image = (ImageButton)findViewById(R.id.mycards_list_image);
        mycards_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent openManageCard = new Intent(TreasurePortalPag1.this,ManageCards.class);
                        openManageCard.putExtra("codice",200);
                        openManageCard.putExtra("game_code",game);
                        startActivity(openManageCard);
                    }
                }, 1000L);

            }});


        ImageButton achivement_list_image= (ImageButton) findViewById(R.id.achieve);
        achivement_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAchievements = new Intent(TreasurePortalPag1.this,Achievements.class);
                //per inviare parametri all'activity Achievement
                openAchievements.putExtra("game","game1");
                startActivity(openAchievements);
            }
        });
        /******fine configurazione bottoni cards_list e achievement_list******/

    }





    /********************gestione click sugli elementi dello spinner*********************/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //prendo il valore dell'elemento selezionato
        item = parent.getItemAtPosition(position).toString();

        Log.d("ITEM SELEZIONATO", item);

        if(!item.equals("")){
            lens_anim = AnimationUtils.loadAnimation(TreasurePortalPag1.this,R.anim.lens_animation);
            lente.startAnimation(lens_anim);

            //l'activity si apre dopo un certo tempo (dopo che è terminata l'animazione)
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    //start your activity here
                    Intent openTreasurePage2 = new Intent(TreasurePortalPag1.this,TreasurePortalPag2.class);
                    openTreasurePage2.putExtra("heritage",item);
                    openTreasurePage2.putExtra("user",user);
                    openTreasurePage2.putExtra("game",game);//passo il parametro game a TPP2
                    startActivity(openTreasurePage2);
                }

            }, 1100L);

        }
    }

    @Override
    public void onBackPressed() {
        Intent openParentActivity = getParentActivityIntent();
        startActivity(openParentActivity);
    }




    public void onNothingSelected(AdapterView<?> arg0) {

    }

    /********************fine gestione click sugli elementi dello spinner*********************/


    public String getGameCode(){
        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        pre = prefs.getString("current_session", "");
        Log.d("Pref salvate create: ",pre);


        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url)+"getGame1FromSession/" + pre + "/";

        // Request a string response from the provided URL.
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // Display the first 500 characters of the response string.
                //Log.d("Response is: ", response.toString());
                try{

                    JSONObject jsObj = (JSONObject)response.get(0);
                    game = jsObj.getString("game1");


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
        //***********_______END TEMPLATE JSON REQUEST________**********
        return game;
    }

    /*
    Dialog tpp1_dialog,tpp2_dialog;
    public void tutorial(){
        tpp1_dialog=new Dialog(TreasurePortalPag1.this);
        tpp1_dialog.setCancelable(false);
        tpp1_dialog.setContentView(R.layout.tutorial_tpp1);
        tpp1_dialog.show();
    }

    public void avanti_tpp1(View view){
        tpp1_dialog.cancel();
        tpp2_dialog=new Dialog(TreasurePortalPag1.this);
        tpp2_dialog.setCancelable(false);
        tpp2_dialog.setContentView(R.layout.tutorial_tpp2);
        tpp2_dialog.show();
    }

    public void avanti_tpp2(View view){

        tpp2_dialog.cancel();
        AlertDialog.Builder inizia = new AlertDialog.Builder(this);
        inizia.setTitle("Inizia la tua avventura!");
        inizia.setMessage("Ora tocca a te! Troviamo più tesori possibili per collezionare il maggior numero di carte!\nGoPoleis!");
        inizia.setIcon(R.drawable.logo);

        inizia.setCancelable(false);
        inizia.setPositiveButton("Inizia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });


        AlertDialog alert = inizia .create();
        alert.show();
    }

    */



}
