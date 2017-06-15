package com.example.anna.neptis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
import com.example.anna.neptis.adapters.CardAdapter;
import com.example.anna.neptis.defines.ObjCard;
import com.example.anna.neptis.defines.ObjTesoro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anna on 29/10/2016.
 */

public class TreasureInfoActivity extends AppCompatActivity {

    String treasure_code;//= getIntent().getExtras().getString("heritage")
    List treasure_list; //lista de tesori presenti nell'heritage passato come parametro
    List treas_card_list;//lista delle carte appartenenti al tesoro passato come parametro
    String url;
    String url2;
    String url3;
    String url4;
    String t_lat, t_lon, t_info;//attributi di Tesoro
    TextView info;
    TextView latitude;
    TextView longitude;
    String c_name, c_cost, c_description,c_code;//attributi della carte
    ListView carte_tesori;

    String user;
    int set_trovato;
    ImageButton ok;

    String game;
    String heritage;

    String[] random_card_code = new String[5];//codici carte generati randomicamente

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_in_treasure);

        user = getIntent().getExtras().getString("user");
        game = getIntent().getExtras().getString("game");
        heritage = getIntent().getExtras().getString("heritage");
        treasure_code = getIntent().getExtras().getString("codice_tesoro");

        info = (TextView) findViewById(R.id.t_info);
        latitude = (TextView) findViewById(R.id.t_lat_val);
        longitude = (TextView) findViewById(R.id.t_lon_val);
        ok = (ImageButton)findViewById(R.id.ok);




        //heritage_tres =(TextView)findViewById(R.id.heritage_treas);

        carte_tesori = (ListView) findViewById(R.id.carte_forziere);

        treasure_list = new LinkedList<ObjTesoro>();
        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.

        //set_trovato = Integer.parseInt(getIntent().getExtras().getString("trovato"));

        RequestQueue queue = Volley.newRequestQueue(this);
        url = getString(R.string.server_url)+"getInfoTreasure/" + treasure_code + "/";

        //Log.d("codice tesoro: ", treasure_code);

        Log.d("url= ", url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsInfoTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        t_info = jsObj.getString("info");
                        t_lat = jsObj.getString("latitude");
                        t_lon = jsObj.getString("longitude");

                        info.setText(t_info);
                        latitude.setText(t_lat);
                        longitude.setText(t_lon);

                        treasure_list.add(new ObjTesoro(treasure_code, t_lat, t_lon, t_info, user,set_trovato));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!", error.toString());
            }
        });
        queue.add(jsInfoTreasure);

        /***********_______END TEMPLATE JSON REQUEST________**********/


        generaCarte();


        for(int i =0; i < random_card_code.length;i++ ){
            Log.d("carte array",random_card_code[i]);
        }



        treas_card_list = new LinkedList<ObjCard>();//dovrebbe diventare una lista di oggetti Card

        for(int i = 0;i < random_card_code.length;i++) {
            //***********_______TEMPLATE JSON REQUEST________**********
            // Instantiate the RequestQueue.
            RequestQueue queue2 = Volley.newRequestQueue(this);
            url2 = getString(R.string.server_url)+"getTreasureCardInfo/" + random_card_code[i]  + "/";

            Log.d("url= ", url2);

            // Request a string response from the provided URL.
            JsonArrayRequest jsInfoCardTreasure = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        int contLength = response.length();
                        for (int i = 0; i < contLength; i++) {
                            JSONObject jsObj = (JSONObject) response.get(i);
                            c_code =  jsObj.getString("code");
                            c_name = jsObj.getString("name");
                            c_cost = jsObj.getString("cost");
                            c_description = jsObj.getString("description");

                            treas_card_list.add(new ObjCard(c_code,c_cost, c_name, c_description));


                            CardAdapter adapter = new CardAdapter(TreasureInfoActivity.this, R.layout.adapter_card, treas_card_list);
                            carte_tesori.setAdapter(adapter);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.d("That didn't work GTCI!", error.toString());
                }
            });



            // Add the request to the RequestQueue.
            queue2.add(jsInfoCardTreasure);

            /***********_______END TEMPLATE JSON REQUEST________**********/

        }

        addCardToTreasure(random_card_code,treasure_code);
        addCardToCollection(random_card_code,game); //aggiungere game prendendolo dall'activity padre



        //quando l'utente fa la back da questa pagina, aspetto qualche secondo prima di passare nell'activity principale
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent returnToPag2 = new Intent(TreasureInfoActivity.this, TreasurePortalPag2.class);
                returnToPag2.putExtra("user",user);
                returnToPag2.putExtra("heritage",heritage);
                    returnToPag2.putExtra("game",game);
                startActivity(returnToPag2);
                }
            });
            }

        }, 1500L);


    }


    //funzione random che permette di generare 5 carte da inserire nel tesoro trovato dall'utente
    public void generaCarte(){
        ///////////////////FUNZIONE RANDOM PROVA///////////////////////////////

        for(int i = 0;i <5;i++) {
            int j = 1;
            int n = 20 - j;
            int RESULT = (int) (Math.random() * n + j);
            Log.d("NUMERO RANDOM:", Integer.toString(RESULT));//debug

            String card_code = "card00";
            if(RESULT < 10){
                card_code = card_code+"0"+RESULT;
            }
            else {
                card_code = card_code+RESULT;
            }
            random_card_code[i] = card_code;

            ///////////////////FUNZIONE RANDOM PROVA///////////////////////////////
        }

    }



    //metodo che aggiunge le carte generate nella relazione TC, associandole al tesoro passato come parametro
    public void addCardToTreasure(String [] card_code,String treas_code){
        for(int i = 0;i<card_code.length;i++) {

            //***********_______TEMPLATE JSON REQUEST________**********
            // Instantiate the RequestQueue.
            RequestQueue queue3 = Volley.newRequestQueue(this);
            url3 = getString(R.string.server_url)+"addCardToTreasure/" + treas_code + "/" + card_code[i] + "/";//modificare con getCardInfo

            Log.d("url= ", url3);

            // Request a string response from the provided URL.
            JsonArrayRequest jsAddCard = new JsonArrayRequest(Request.Method.GET, url3, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("That didn't work!", error.toString());
                }
            });

            queue3.add(jsAddCard);
            /***********_______END TEMPLATE JSON REQUEST________**********/
        }
    }


    //metodo che aggiunge le carte generate nella relazione G1C, associandole al game passato come parametro
    public void addCardToCollection(String [] card_code,String game){
        for(int i = 0;i<card_code.length;i++) {

            //***********_______TEMPLATE JSON REQUEST________**********
            // Instantiate the RequestQueue.
            RequestQueue queue3 = Volley.newRequestQueue(this);
            url4 = getString(R.string.server_url)+"addCardToUserCollection/" + game + "/" + card_code[i] + "/";//modificare con getCardInfo

            Log.d("url= ", url4);

            // Request a string response from the provided URL.
            JsonArrayRequest jsAddCardToCollection = new JsonArrayRequest(Request.Method.GET, url4, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("That didn't work!", error.toString());
                }
            });

            queue3.add(jsAddCardToCollection);
            /***********_______END TEMPLATE JSON REQUEST________**********/
        }
    }







}
