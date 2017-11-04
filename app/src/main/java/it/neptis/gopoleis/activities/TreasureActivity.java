package it.neptis.gopoleis.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.adapters.CardAdapter;
import it.neptis.gopoleis.adapters.ClickListener;
import it.neptis.gopoleis.adapters.RecyclerTouchListener;
import it.neptis.gopoleis.model.Card;

public class TreasureActivity extends AppCompatActivity {

    //private static final String TAG = "TreasureaActivity";

    private String treasureCode;
    private TextView info, coordinates;
    private FirebaseAuth mAuth;
    private List<Card> treas_card_list;
    private String c_name, c_rarity, c_description, c_code;
    private String[] random_card_codes = new String[5];
    private CardAdapter cardAdapter;
    private boolean opened;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();

        opened = false;

        treasureCode = getIntent().getStringExtra("code");

        info = (TextView) findViewById(R.id.treasure_description);
        coordinates = (TextView) findViewById(R.id.treasure_coordinates);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.booster_pack);

        getSetTreasureInfo();
    }

    private void getSetTreasureInfo() {
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getInfoTreasure/" + treasureCode + "/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest treasureRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsObj = (JSONObject) response.get(0);
                    info.setText(jsObj.getString("description"));
                    coordinates.setText(String.format(getString(R.string.ne_coordinates), jsObj.getString("latitude"), jsObj.getString("longitude")));

                    setupOpenTreasureButton();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(TreasureActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(treasureRequest);
    }

    private void setupOpenTreasureButton() {
        final Button open_treasure = (Button) findViewById(R.id.open_treasure_button);

        open_treasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(TreasureActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();

                final String[] idToken = new String[1];
                final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                assert mUser != null;
                mUser.getIdToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    idToken[0] = task.getResult().getToken();
                                    // Send token to your backend via HTTPS
                                    String url = getString(R.string.server_url) + "player/addTreasToPlayer/" + mUser.getEmail() + "/" + treasureCode + "/";
                                    JsonArrayRequest addTreasureRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                        @Override
                                        public void onResponse(JSONArray response) {
                                            // First element contains unlocked missions
                                            try {
                                                JSONArray unlockedMissionsJSONArray = (JSONArray) response.get(0);
                                                for (int i = 0; i < unlockedMissionsJSONArray.length(); i++) {
                                                    if (!unlockedMissionsJSONArray.getString(i).equals("null")) {
                                                        AlertDialog.Builder builder;
                                                        builder = new AlertDialog.Builder(TreasureActivity.this);
                                                        builder.setTitle(R.string.congratulations)
                                                                .setMessage(R.string.congratulations_mission)
                                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                    }
                                                                })
                                                                .setIcon(android.R.drawable.star_off)
                                                                .show();
                                                    }
                                                }

                                                JSONArray cardsJSONArray = (JSONArray) response.get(1);
                                                for (int i = 0; i < cardsJSONArray.length(); i++) {
                                                    random_card_codes[i] = cardsJSONArray.getString(i);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            treasureOpened();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progressDialog.dismiss();
                                            Toast.makeText(TreasureActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<>();
                                            params.put("MyToken", idToken[0]);
                                            return params;
                                        }
                                    };

                                    RequestQueueSingleton.getInstance(TreasureActivity.this).addToRequestQueue(addTreasureRequest);
                                } else {
                                    // Handle error -> task.getException();
                                    progressDialog.dismiss();
                                    Toast.makeText(TreasureActivity.this, "There was an error with your request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void treasureOpened() {
        opened = true;
        View dynamicTreasureView = findViewById(R.id.dynamicTreasureView);
        ViewGroup parent = (ViewGroup) dynamicTreasureView.getParent();
        int index = parent.indexOfChild(dynamicTreasureView);
        parent.removeView(dynamicTreasureView);
        dynamicTreasureView = getLayoutInflater().inflate(R.layout.cards_found, parent, false);
        parent.addView(dynamicTreasureView, index);

        treas_card_list = new LinkedList<>();

        // ------------------------------------------------------------------------
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.carte_forziere);

        cardAdapter = new CardAdapter(TreasureActivity.this, treas_card_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Card card = treas_card_list.get(position);
                Intent openCardDetails = new Intent(TreasureActivity.this, CardDetailsActivity.class);
                openCardDetails.putExtra("cardName", card.getName());
                openCardDetails.putExtra("cardRarity", card.getRarity());
                openCardDetails.putExtra("cardDescription", card.getDescription());
                openCardDetails.putExtra("cardCode", card.getCode());
                startActivity(openCardDetails);
            }
        }));
        // ------------------------------------------------------------------------


        String url = getString(R.string.server_url) + "getFiveTreasureCardsInfo/";
        for (String tempString : random_card_codes)
            url += tempString + "/";

        JsonArrayRequest cardsInfoRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        c_code = jsObj.getString("code");
                        c_name = jsObj.getString("name");
                        c_rarity = jsObj.getString("rarity");
                        c_description = jsObj.getString("description");
                        String filename = jsObj.getString("filename");
                        treas_card_list.add(new Card(c_code, c_rarity, c_name, c_description, getString(R.string.server_url) + "images/cards/" + filename));
                    }
                    cardAdapter.notifyDataSetChanged();

                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(TreasureActivity.this);
                    builder.setTitle(R.string.congratulations)
                            .setMessage(R.string.congratulations_treasure)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.star_off)
                            .show();

                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(TreasureActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueueSingleton.getInstance(this).addToRequestQueue(cardsInfoRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (opened) {
            Intent backToTreasurePortal = new Intent();
            backToTreasurePortal.putExtra("code", treasureCode);
            setResult(Activity.RESULT_OK, backToTreasurePortal);
        } else
            setResult(Activity.RESULT_CANCELED);
        finish();
    }

}