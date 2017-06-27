package it.neptis.gopoleis.activities;

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
import it.neptis.gopoleis.adapters.CardAdapter;
import it.neptis.gopoleis.adapters.ClickListener;
import it.neptis.gopoleis.adapters.RecyclerTouchListener;
import it.neptis.gopoleis.defines.Card;
import it.neptis.gopoleis.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageCardsActivity extends AppCompatActivity {

    public static final int ALL_CARDS_REQUEST_CODE = 100;
    public static final int MY_CARDS_REQUEST_CODE = 200;
    List<Card> all_cards;
    String url;
    RecyclerView recyclerView;
    String code, cost, name, description;

    CardAdapter cardAdapter;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView)  findViewById(R.id.recyclerView);

        all_cards = new ArrayList<>();

        // ------------------------------------------------------------------------
        cardAdapter = new CardAdapter(all_cards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Card card = all_cards.get(position);
                Intent openCardDetails = new Intent(ManageCardsActivity.this, CardDetailsActivity.class);
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
        switch (button_code) {
            case ALL_CARDS_REQUEST_CODE:
                url = getString(R.string.server_url) + "getAllCards/";
                break;
            case MY_CARDS_REQUEST_CODE:
                url = getString(R.string.server_url) + "getMyCards/" + mAuth.getCurrentUser().getEmail() + "/";
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
                        all_cards.add(new Card(code, cost, name, description));
                    }
                    cardAdapter.notifyDataSetChanged();
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