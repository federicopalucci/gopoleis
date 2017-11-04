package it.neptis.gopoleis.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.adapters.CardAdapter;
import it.neptis.gopoleis.adapters.ClickListener;
import it.neptis.gopoleis.adapters.RecyclerTouchListener;
import it.neptis.gopoleis.model.Card;

public class ManageCardsActivity extends AppCompatActivity {

    //private static final String TAG = "ManageCards";
    public static final int ALL_CARDS_REQUEST_CODE = 100;
    public static final int MY_CARDS_REQUEST_CODE = 200;

    private List<Card> all_cards;
    private String url;
    private String code, rarity, name, description;
    private CardAdapter cardAdapter;
    private LinearLayout cardsContainerLayout;
    private TextView noCardsText;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_cards);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.cards);

        cardsContainerLayout = (LinearLayout) findViewById(R.id.cards_container_layout);
        noCardsText = new TextView(ManageCardsActivity.this);
        noCardsText.setText(R.string.no_cards);
        noCardsText.setTextSize(30);

        mAuth = FirebaseAuth.getInstance();

        setRecyclerView();

        getCards();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        all_cards = new ArrayList<>();

        cardAdapter = new CardAdapter(ManageCardsActivity.this, all_cards);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Card card = all_cards.get(position);
                Intent openCardDetails = new Intent(ManageCardsActivity.this, CardDetailsActivity.class);
                openCardDetails.putExtra("cardName", card.getName());
                openCardDetails.putExtra("cardRarity", card.getRarity());
                openCardDetails.putExtra("cardDescription", card.getDescription());
                openCardDetails.putExtra("cardCode", card.getCode());
                startActivity(openCardDetails);
            }
        }));
    }

    private void getCards() {
        int button_code = getIntent().getExtras().getInt("codice");

        // Retrieve cards
        int requestMethod = 0;
        switch (button_code) {
            case ALL_CARDS_REQUEST_CODE:
                url = getString(R.string.server_url) + "getAllCards/";
                requestMethod = Request.Method.POST;
                break;
            case MY_CARDS_REQUEST_CODE:
                //noinspection ConstantConditions
                url = getString(R.string.server_url) + "getMyCards/" + mAuth.getCurrentUser().getEmail() + "/";
                requestMethod = Request.Method.GET;
                break;
            default:
                break;
        }

        final JsonArrayRequest cardsRequest = new JsonArrayRequest(requestMethod, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        code = jsObj.getString("code");
                        rarity = jsObj.getString("rName");
                        name = jsObj.getString("name");
                        description = jsObj.getString("description");
                        String filename = jsObj.getString("filename");
                        all_cards.add(new Card(code, rarity, name, description, getString(R.string.server_url) + "images/cards/" + filename));
                    }
                    cardAdapter.notifyDataSetChanged();

                    if (all_cards.isEmpty()) {
                        cardsContainerLayout.addView(noCardsText, 0);
                    } else {
                        cardsContainerLayout.removeView(noCardsText);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(ManageCardsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(cardsRequest);
    }

}