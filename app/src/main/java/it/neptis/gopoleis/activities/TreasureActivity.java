package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.LinkedList;
import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.adapters.CardAdapter;
import it.neptis.gopoleis.adapters.ClickListener;
import it.neptis.gopoleis.adapters.RecyclerTouchListener;
import it.neptis.gopoleis.defines.Card;

public class TreasureActivity extends AppCompatActivity {

    private static final String TAG = "TreasureaActivity";

    private String t_lat, t_lon, t_info;
    private String treasure_code;

    private TextView info, latitude, longitude;
    private ImageView coffer;

    private FirebaseAuth mAuth;

    private List<Card> treas_card_list;
    private String c_name, c_cost, c_description, c_code;

    private String[] random_card_codes = new String[5];

    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure);

        mAuth = FirebaseAuth.getInstance();

        Bundle extras = getIntent().getExtras();

        treasure_code = extras.getString("codice_tesoro");

        info = (TextView) findViewById(R.id.treasure_description);
        latitude = (TextView) findViewById(R.id.treasure_latitude);
        longitude = (TextView) findViewById(R.id.treasure_longitude);
        coffer = (ImageView) findViewById(R.id.treasure_image);

        getSetTreasureInfo();

        boolean found = extras.getBoolean("found");

        if (found) {
            treasureFound();
        } else {
            treasureNotFound();
        }

    }

    private void treasureFound() {
        coffer.setImageResource(R.drawable.forziere_aperto);
        View dynamicTreasureView = findViewById(R.id.dynamicTreasureView);
        ViewGroup parent = (ViewGroup) dynamicTreasureView.getParent();
        int index = parent.indexOfChild(dynamicTreasureView);
        parent.removeView(dynamicTreasureView);
        dynamicTreasureView = getLayoutInflater().inflate(R.layout.treasure_found_text, parent, false);
        parent.addView(dynamicTreasureView, index);
    }

    private void treasureNotFound() {
        Button open_treasure = (Button) findViewById(R.id.open_treasure_button);
        open_treasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //una volta cliccato sul bottone open_treasure
                //aggiungo il tesoro a GT e apro di nuovo TreasureInfoActivity passandogli game1SessionCode
                RequestQueue queue = Volley.newRequestQueue(v.getContext());
                String url2 = getString(R.string.server_url) + "addTreasToPlayer/" + mAuth.getCurrentUser().getEmail() + "/" + treasure_code + "/";

                // Request a string response from the provided URL.
                JsonObjectRequest jsAddTreasToGame = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        treasureOpened();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });

                queue.add(jsAddTreasToGame);
            }


        });
    }

    private void treasureOpened() {
        coffer.setImageResource(R.drawable.forziere_aperto);
        View dynamicTreasureView = findViewById(R.id.dynamicTreasureView);
        ViewGroup parent = (ViewGroup) dynamicTreasureView.getParent();
        int index = parent.indexOfChild(dynamicTreasureView);
        parent.removeView(dynamicTreasureView);
        dynamicTreasureView = getLayoutInflater().inflate(R.layout.cards_found, parent, false);
        parent.addView(dynamicTreasureView, index);

        generateCards();

        treas_card_list = new LinkedList<>();

        // ------------------------------------------------------------------------
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.carte_forziere);

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
                Intent openCardDetails = new Intent(TreasureActivity.this, CardDetailsActivity.class);
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
            String url = getString(R.string.server_url) + "getTreasureCardInfo/" + aRandom_card_code + "/";

            JsonArrayRequest jsInfoCardTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
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

        addCardToCollection(random_card_codes);
    }

    private void getSetTreasureInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getInfoTreasure/" + treasure_code + "/";
        JsonArrayRequest jsInfoTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        t_info = jsObj.getString("description");
                        t_lat = jsObj.getString("latitude");
                        t_lon = jsObj.getString("longitude");
                        info.setText(t_info);
                        latitude.setText(String.format(getString(R.string.treasure_latitude),t_lat));
                        longitude.setText(String.format(getString(R.string.treasure_longitude),t_lon));
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

        queue.add(jsInfoTreasure);
    }

    public void generateCards() {
        // TODO duplicate random numbers can occur
        for (int i = 0; i < 5; i++) {
            int j = 1;
            int n = 20 - j;
            int RESULT = (int) (Math.random() * n + j);

            String card_code = String.valueOf(RESULT);
            random_card_codes[i] = card_code;
        }
    }

    public void addCardToCollection(String[] card_codes) {
        for (String aCard_code : card_codes) {
            RequestQueue queue3 = Volley.newRequestQueue(this);
            String url = getString(R.string.server_url) + "addCardToUserCollection/" + mAuth.getCurrentUser().getEmail() + "/" + aCard_code + "/";

            JsonObjectRequest jsAddCardToCollection = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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