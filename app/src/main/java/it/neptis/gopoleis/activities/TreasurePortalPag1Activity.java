package it.neptis.gopoleis.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import it.neptis.gopoleis.GopoleisApp;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Heritage;
import it.neptis.gopoleis.defines.Treasure;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TreasurePortalPag1Activity extends AppCompatActivity implements OnItemSelectedListener {

    private static final String TAG = "TreasurePortalPag1";
    public static final int ALL_CARDS_REQUEST_CODE = 100;
    public static final int MY_CARDS_REQUEST_CODE = 200;

    int contLength;
    String[] spinner_options;
    Spinner dropdown;
    ImageView lente = null;
    String item;

    private GopoleisApp gopoleisApp;
    private List<Heritage> heritages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_portal_pag1);

        gopoleisApp = (GopoleisApp) getApplicationContext();

        lente = (ImageView) findViewById(R.id.lens);

        // Spinner configuration
        dropdown = (Spinner) findViewById(R.id.spinner_menu);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(dropdown);
            popupWindow.setHeight(320);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            Toast.makeText(getApplicationContext(), "Errore!", Toast.LENGTH_SHORT).show();
        }

        // Item retrieval and insertion in spinner
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getHeritagesGame1/";
        JsonArrayRequest jsArray = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    contLength = response.length();
                    spinner_options = new String[contLength + 1];
                    spinner_options[0] = "";
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        int code = jsObj.getInt("code");
                        String name = jsObj.getString("name");
                        String description = jsObj.getString("description");
                        String latitude = jsObj.getString("latitude");
                        String longitude = jsObj.getString("longitude");
                        Heritage tempHeritage = new Heritage(code,name,description,latitude,longitude,new ArrayList<Treasure>());
                        heritages.add(tempHeritage);

                        spinner_options[i + 1] = name;
                        ArrayAdapter<?> adapter = new ArrayAdapter<Object>(TreasurePortalPag1Activity.this, android.R.layout.simple_spinner_dropdown_item, spinner_options);
                        dropdown.setAdapter(adapter);
                        dropdown.setOnItemSelectedListener(TreasurePortalPag1Activity.this);
                    }
                    gopoleisApp.setHeritages(heritages);
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

        queue.add(jsArray);

        // Bottom buttons configuration
        ImageButton card_list_image = (ImageButton) findViewById(R.id.cards_list_image);
        card_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openManageCard = new Intent(TreasurePortalPag1Activity.this, ManageCardsActivity.class);
                openManageCard.putExtra("codice", ALL_CARDS_REQUEST_CODE);
                startActivity(openManageCard);
            }
        });

        ImageButton mycards_list_image = (ImageButton) findViewById(R.id.mycards_list_image);
        mycards_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent openManageCard = new Intent(TreasurePortalPag1Activity.this, ManageCardsActivity.class);
                        openManageCard.putExtra("codice", MY_CARDS_REQUEST_CODE);
                        startActivity(openManageCard);
                    }
                }, 1000L);
            }
        });

        ImageButton achivement_list_image = (ImageButton) findViewById(R.id.achieve);
        achivement_list_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAchievements = new Intent(TreasurePortalPag1Activity.this, AchievementsActivity.class);
                openAchievements.putExtra("game", 1);
                startActivity(openAchievements);
            }
        });
    }

    // Handle spinner items clicks
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get selected item's value
        item = parent.getItemAtPosition(position).toString();
        if (!item.equals("")) {
                    Intent openTreasurePage2 = new Intent(TreasurePortalPag1Activity.this, TreasurePortalPag2Activity.class);
                    openTreasurePage2.putExtra("heritageName", item);
                    startActivity(openTreasurePage2);
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {}

    // TODO tutorial
    /*
    Dialog tpp1_dialog,tpp2_dialog;
    public void tutorial(){
        tpp1_dialog=new Dialog(TreasurePortalPag1Activity.this);
        tpp1_dialog.setCancelable(false);
        tpp1_dialog.setContentView(R.layout.tutorial_tpp1);
        tpp1_dialog.show();
    }

    public void avanti_tpp1(View view){
        tpp1_dialog.cancel();
        tpp2_dialog=new Dialog(TreasurePortalPag1Activity.this);
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
