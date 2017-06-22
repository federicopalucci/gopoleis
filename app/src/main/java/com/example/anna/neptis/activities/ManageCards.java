package com.example.anna.neptis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.adapters.CardAdapter2;
import com.example.anna.neptis.adapters.ClickListener;
import com.example.anna.neptis.adapters.RecyclerTouchListener;
import com.example.anna.neptis.defines.ObjCard;
import com.example.anna.neptis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageCards extends AppCompatActivity {

    List<ObjCard> all_cards;
    String url;
    RecyclerView recyclerView;
    String code, cost, name, description;

    CardAdapter2 cardAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_manage_cards);
        setContentView(R.layout.activity_manage_cards_2);

        recyclerView = (RecyclerView)  findViewById(R.id.recyclerView);

        all_cards = new ArrayList<>();

        // ------------------------------------------------------------------------
        cardAdapter2 = new CardAdapter2(all_cards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter2);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ObjCard card = all_cards.get(position);
                Intent openCardDetails = new Intent(ManageCards.this, CardDetailsActivity.class);
                openCardDetails.putExtra("cardName", card.getName());
                openCardDetails.putExtra("cardCost", card.getCost());
                openCardDetails.putExtra("cardDescription", card.getDescription());
                // TODO add card image
                startActivity(openCardDetails);
            }
        }));
        // ------------------------------------------------------------------------

        int button_code = getIntent().getExtras().getInt("codice");

        // Retrieve cards
        RequestQueue queue = Volley.newRequestQueue(this);
        String game1SessionCode = getIntent().getExtras().getString("game_code");
        switch (button_code) {
            case 100:
                url = getString(R.string.server_url) + "getAllCards/";
                break;
            case 200:
                url = getString(R.string.server_url) + "getMyCards/" + game1SessionCode + "/";
                break;
            default:
                break;
        }
        JsonArrayRequest jsCardCodes = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        code = jsObj.getString("code");
                        cost = jsObj.getString("cost");
                        name = jsObj.getString("name");
                        description = jsObj.getString("description");
                        all_cards.add(new ObjCard(code, cost, name, description));
                        //recyclerView.setAdapter(new CardAdapter(ManageCards.this, R.layout.adapter_card, all_cards));
                    }
                    cardAdapter2.notifyDataSetChanged();
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

        queue.add(jsCardCodes);
    }

}