package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.adapters.CardAdapter;
import it.neptis.gopoleis.adapters.ClickListener;
import it.neptis.gopoleis.adapters.RecyclerTouchListener;
import it.neptis.gopoleis.defines.Card;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class TreasureInfoActivity extends AppCompatActivity {

    private static final String TAG = "TreasureInfoAct";

    String treasure_code;
    List<Card> treas_card_list;
    String url, url2, url3, url4;
    String t_lat, t_lon, t_info;
    TextView info;
    TextView latitude, longitude;
    String c_name, c_cost, c_description, c_code;

    String heritage;

    String[] random_card_codes = new String[5];

    RecyclerView recyclerView;
    CardAdapter cardAdapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_in_treasure);

        mAuth = FirebaseAuth.getInstance();

        heritage = getIntent().getExtras().getString("heritageName");
        treasure_code = getIntent().getExtras().getString("codice_tesoro");
        t_info = getIntent().getExtras().getString("info");
        t_lat = getIntent().getExtras().getString("latitude");
        t_lon = getIntent().getExtras().getString("longitude");

        info = (TextView) findViewById(R.id.t_info);
        latitude = (TextView) findViewById(R.id.t_lat_val);
        longitude = (TextView) findViewById(R.id.t_lon_val);

        info.setText(t_info);
        latitude.setText(t_lat);
        longitude.setText(t_lon);

        generateCards();

        treas_card_list = new LinkedList<>();

        // ------------------------------------------------------------------------
        recyclerView = (RecyclerView)  findViewById(R.id.carte_forziere);

        cardAdapter = new CardAdapter(treas_card_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Card card = treas_card_list.get(position);
                Intent openCardDetails = new Intent(TreasureInfoActivity.this, CardDetailsActivity.class);
                openCardDetails.putExtra("cardName", card.getName());
                openCardDetails.putExtra("cardCost", card.getCost());
                openCardDetails.putExtra("cardDescription", card.getDescription());
                // TODO add card image
                startActivity(openCardDetails);
            }
        }));
        // ------------------------------------------------------------------------

        for (String aRandom_card_code : random_card_codes) {
            RequestQueue queue2 = Volley.newRequestQueue(this);
            url2 = getString(R.string.server_url) + "getTreasureCardInfo/" + aRandom_card_code + "/";

            JsonArrayRequest jsInfoCardTreasure = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsObj = (JSONObject) response.get(i);
                            c_code = jsObj.getString("code");
                            c_name = jsObj.getString("name");
                            c_cost = jsObj.getString("cost");
                            c_description = jsObj.getString("description");
                            treas_card_list.add(new Card(c_code, c_cost, c_name, c_description));
                        }
                        cardAdapter.notifyDataSetChanged();
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

            queue2.add(jsInfoCardTreasure);
        }

        // TODO useless relation Tc
        // addCardToTreasure(random_card_codes, treasure_code);

        addCardToCollection(random_card_codes);
    }

    //funzione random che permette di generare 5 carte da inserire nel tesoro trovato dall'utente
    public void generateCards() {
        for (int i = 0; i < 5; i++) {
            int j = 1;
            int n = 20 - j;
            int RESULT = (int) (Math.random() * n + j);

            String card_code = String.valueOf(RESULT);
            random_card_codes[i] = card_code;
        }
    }

    //metodo che aggiunge le carte generate nella relazione G1C, associandole al game1SessionCode passato come parametro
    public void addCardToCollection(String[] card_codes) {
        for (String aCard_code : card_codes) {
            RequestQueue queue3 = Volley.newRequestQueue(this);
            url4 = getString(R.string.server_url) + "addCardToUserCollection/" + mAuth.getCurrentUser().getEmail() + "/" + aCard_code + "/";
            Log.d(TAG, url4);

            JsonObjectRequest jsAddCardToCollection = new JsonObjectRequest(Request.Method.GET, url4, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            });

            queue3.add(jsAddCardToCollection);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}