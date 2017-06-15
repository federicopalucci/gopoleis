package com.example.anna.neptis.activities;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
import com.example.anna.neptis.defines.ObjTesoro;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreasurePortalPag2 extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener {

    private final static int CAMERA_REQUEST_CODE = 1;
    private static GoogleMap mMap;

    String heritage;//= getIntent().getExtras().getString("heritage")
    String url,url2;
    String latitudine;
    String longitudine;
    String code,lat,lon,info;//attributi di ObjTesoro
    //int found;//per i tesori
    List list; //lista dei tesori presenti nell'heritage passato come parametro

    String user;
    String game;




    private MarkerOptions options = new MarkerOptions();
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    ListView tesori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_portal_pag2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

       // getGameCode();
        String game = getIntent().getExtras().getString("game");
    }


    @Override
    protected void onResume(){
        super.onResume();

       // getGameCode();
        Log.d("ON RESUME: ","ok");
        user = getIntent().getExtras().getString("user");
        game = getIntent().getExtras().getString("game");
        heritage = getIntent().getExtras().getString("heritage");

        //Log.d("CODICE GAME: ",game);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        /*___________________________gestione TESORI all'interno della scrollbar______________________*/
        //tesori = (ListView) findViewById(R.id.list_treasures);



        list = new LinkedList<ObjTesoro>();

        //***********_______TEMPLATE JSON REQUEST________**********
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String spaces = heritage.replace(" ","%20");
        url2 = getString(R.string.server_url)+"getTreasureElements/" + spaces + "/";

        Log.d("url= ",url2);

        // Request a string response from the provided URL.
        JsonArrayRequest jsTreasureElements = new JsonArrayRequest(Request.Method.GET, url2,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for(int i = 0;i< contLength;i++) {
                        //int j = 1;
                        JSONObject jsObj = (JSONObject) response.get(i);
                        code = jsObj.getString("code");
                        lat = jsObj.getString("latitude");
                        lon = jsObj.getString("longitude");
                        info = jsObj.getString("info");
                        //found = jsObj.getInt("found");
                        Log.d("CODICE TESORO INIZIO: ",code);
                        //Log.d("TROVATO: ",Integer.toString(found));


                        latlngs.add(new LatLng(Double.parseDouble(lat),Double.parseDouble(lon))); //some latitude and logitude value

                        LatLng point = latlngs.get(i);


                        options.position(point);

                        options.title(code);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(options);
                        mMap.setOnMarkerClickListener(TreasurePortalPag2.this);//click su marker



////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        /*list.add(new ObjTesoro(code,lat,lon,info,user));//aggiungere found(value)
                        TreasureAdapter adapter = new TreasureAdapter(TreasurePortalPag2.this, R.layout.adapter_treasure, list);
                        tesori.setAdapter(adapter);*/
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
        queue.add(jsTreasureElements);


        /***********_______END TEMPLATE JSON REQUEST________**********/


            /*__________________________fine gestione gridView all'interno della scrollbar________________________*/


        //gestione click su fotocamera
        ImageButton camera = (ImageButton)findViewById(R.id.camera_image);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera,CAMERA_REQUEST_CODE);
                    /*Toast toast = Toast.makeText(view.getContext(),"Camera ImageButton",Toast.LENGTH_SHORT);
                    toast.show();*/


            }});

        /*__________________gestione bottone SITE INFORMATION____________________*/
        Button siteInformation = (Button)findViewById(R.id.site_information);
        siteInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast toast = Toast.makeText(view.getContext(),"Site Information Button",Toast.LENGTH_SHORT);
                toast.show();


            }});
        /*__________________fine gestione bottone SITE INFORMATION____________________*/




    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    LatLng herit;
    @Override
    public void onMapReady(GoogleMap googleMap) {

        /***********_______START TEMPLATE JSON REQUEST________**********/
        mMap = googleMap;
        RequestQueue queue = Volley.newRequestQueue(TreasurePortalPag2.this);
        String spaces = heritage.replace(" ","%20");
        url = getString(R.string.server_url)+ "getCoordinatesHeritage/"+spaces+"/";

        Log.d("url= ",url);

        JsonArrayRequest jsCoordinates = new JsonArrayRequest(Request.Method.GET, url,null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        int contLength = response.length();
                        for(int i = 0;i< contLength;i++) {
                            JSONObject jsObj = (JSONObject) response.get(i);
                            latitudine = jsObj.getString("latitude");
                            longitudine = jsObj.getString("longitude");
                            //Log.d("VERIFICA LATITUDINE ",latitudine);
                            //Log.d("VERIFICA LONGITUDINE ",longitudine);

                            herit = new LatLng(Double.parseDouble(latitudine),Double.parseDouble(longitudine));
                            //mMap.addMarker(new MarkerOptions().position(herit).title(heritage).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))); //aggiungere

                            mMap.addMarker(new MarkerOptions().position(herit).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))); //aggiungere
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(herit).zoom(17).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        queue.add(jsCoordinates);

        /***********_______END TEMPLATE JSON REQUEST________**********/
    }

    private static final int RANGE_METERS = 3 * 100;//raggio di 2km
    String urlCheckFound;
    LatLng pos;
    Intent openTreasureFound;


    //implementazione click sui marker relativi ai tesori (da escludere il marker dell'heritage-renderlo non cliccabile)
    @Override
    public boolean onMarkerClick(final Marker marker) {
        pos = marker.getPosition();
        final String code_treas = marker.getTitle();

        //NOTA:la distanza bisogna calcolarla tra posizione utente e marker cliccato non tra herit e marker cliccato
        //modificare dopo aver implementato la ricerca della posizione utente
        boolean inRange = SphericalUtil.computeDistanceBetween(pos, herit) < RANGE_METERS;

        //se il marker rientra nel range e il forziere non è stato ancora trovato, apre il forziere e mostra le carte
        if (inRange) {

            RequestQueue queue4 = Volley.newRequestQueue(this);
            urlCheckFound = getString(R.string.server_url)+"checkTreasureFound/" + code_treas + "/" + game + "/";

            Log.d("url= ", urlCheckFound);

            // Request a string response from the provided URL.
            JsonArrayRequest jsTreasFound = new JsonArrayRequest(Request.Method.GET, urlCheckFound,null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    JSONObject obj_found = null;
                    int found = 0;
                    try {
                        obj_found = response.getJSONObject(0);
                        Log.d("found result: ",obj_found.toString());
                        found = obj_found.getInt("EXISTS(SELECT * from Gt where treasure='"+code_treas+"' AND game1='"+ game +"')");
                        Log.d("FOUND? ",Integer.toString(found));

                        if(found == 1){//se il tesoro è in GT(trovato-posseduto dallo user)
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            openTreasureFound = new Intent(TreasurePortalPag2.this, TreasureFound.class);//cambiare activity
                            openTreasureFound.putExtra("user", user);
                            openTreasureFound.putExtra("heritage",heritage);
                            openTreasureFound.putExtra("codice_tesoro",code_treas);
                            openTreasureFound.putExtra("game",game);
                            startActivity(openTreasureFound);

                        }else{//se il tesoro non è posseduto dallo user

                            Intent openTreasNotFound = new Intent(TreasurePortalPag2.this,TreasureNotFoundActivity.class);
                            openTreasNotFound.putExtra("user",user);
                            openTreasNotFound.putExtra("codice_tesoro",code_treas);
                            openTreasNotFound.putExtra("game",game);
                            openTreasNotFound.putExtra("heritage",heritage);
                            startActivity(openTreasNotFound);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error: ",error.toString());

                }
            });
            // Add the request to the RequestQueue.
            queue4.add(jsTreasFound);
            /***********_______END TEMPLATE JSON REQUEST________**********/

        } else {
            Toast.makeText(TreasurePortalPag2.this, "Sei troppo lontano dal tesoro. Avvicinati!", Toast.LENGTH_LONG).show();
        }

        return true;

    }

    @Override
    public void onBackPressed() {
        Intent openParentActivity = getParentActivityIntent();
        openParentActivity.putExtra("user",user);
        openParentActivity.putExtra("game",game);
        startActivity(openParentActivity);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {

        if (requestCode == CAMERA_REQUEST_CODE && resultCode== RESULT_OK){
            Toast.makeText(this,"camera ok!",Toast.LENGTH_LONG).show();
        }
    }


    //stava in onMarkerClick dopo intent ch apre treasureInfo Activity
    /***********_______START TEMPLATE JSON REQUEST________**********/
    /*RequestQueue queue = Volley.newRequestQueue(TreasurePortalPag2.this);
    //Log.d("CODICE MARKER:",code_treas);
    urlUpdate = "http://10.0.2.2:8000/updateFoundTreas/" + code_treas + "/" + user + "/";

    Log.d("url= ", urlUpdate);
    JsonObjectRequest jsFoundTreas = new JsonObjectRequest(Request.Method.GET, urlUpdate, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            Toast.makeText(TreasurePortalPag2.this, "Trovato nuovo tesoro!", Toast.LENGTH_SHORT).show();


        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("Volley error:", error.toString());
        }
    });
    // Add the request to the RequestQueue.
    queue.add(jsFoundTreas);*/


}
