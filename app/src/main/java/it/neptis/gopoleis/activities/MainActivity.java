package it.neptis.gopoleis.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.neptis.gopoleis.MyLocationManager;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.ClusterMarker;
import it.neptis.gopoleis.defines.CustomClusterRenderer;
import it.neptis.gopoleis.defines.Heritage;
import it.neptis.gopoleis.defines.Treasure;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ClusterManager.OnClusterClickListener<ClusterMarker>, ClusterManager.OnClusterItemClickListener<ClusterMarker> {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_SETTINGS_CHECK_REQUEST_CODE = 2;
    private static final int TREASURE_REQUEST_CODE = 3;
    private static final int STAGE_REQUEST_CODE = 4;

    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private ClusterManager<ClusterMarker> mClusterManager;
    private CustomClusterRenderer customClusterRenderer;
    // 2km range
    private static final int RANGE_METERS = 3 * 100;
    private MyLocationManager myLocationManager;
    private List<ClusterMarker> heritageClusterMarkers;
    private List<ClusterMarker> treasureClusterMarkers;
    private List<ClusterMarker> stageClusterMarkers;
    private ClusterMarker tempClusterMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        myLocationManager = MyLocationManager.getInstance(this);
        myLocationManager.checkLocationSettings(this);

        heritageClusterMarkers = new ArrayList<>();
        treasureClusterMarkers = new ArrayList<>();
        stageClusterMarkers = new ArrayList<>();

        // Wait for UI to load
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView playerIcon = (ImageView) findViewById(R.id.player_icon_drawer);
                try {
                    Glide.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(playerIcon);
                } catch (Exception e) {
                    playerIcon.setImageResource(R.drawable.default_user);
                }

                TextView playerName = (TextView) findViewById(R.id.player_name_drawer);
                playerName.setText(mAuth.getCurrentUser().getDisplayName());
                TextView playerEmail = (TextView) findViewById(R.id.player_email_drawer);
                playerEmail.setText(mAuth.getCurrentUser().getEmail());

                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(MainActivity.this);
            }
        }, 1000);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_all);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description */
                R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            new SelectMarkers().execute("all");
        } else if (id == R.id.nav_heritages) {
            new SelectMarkers().execute("heritage");
        } else if (id == R.id.nav_treasures) {
            new SelectMarkers().execute("treasure");
        } else if (id == R.id.nav_paths) {
            new SelectMarkers().execute("path");
        } else if (id == R.id.nav_my_cards) {
            startActivity(new Intent(this, ManageCardsActivity.class).putExtra("codice", 200));
        } else if (id == R.id.nav_all_cards) {
            startActivity(new Intent(this, ManageCardsActivity.class).putExtra("codice", 100));
        } else if (id == R.id.nav_medals) {
            startActivity(new Intent(this, MedalsActivity.class));
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocationManager.getCurrentLatLng()).zoom(15).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMyLocationEnabled(true);
        mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);
        //mClusterManager.setAlgorithm(new GridBasedAlgorithm<AbstractClusterMarker>());
        mClusterManager.setRenderer(new CustomClusterRenderer(this, mMap, mClusterManager));
        customClusterRenderer = (CustomClusterRenderer) mClusterManager.getRenderer();
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        getAllHeritages();
    }

    public void getAllHeritages() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getAllHeritages/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        int code = jsObj.getInt("code");
                        String latitude = jsObj.getString("latitude");
                        String longitude = jsObj.getString("longitude");
                        boolean visited = jsObj.getString("visited").equals("1");
                        ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(latitude), Double.parseDouble(longitude), String.valueOf(code), "heritage", visited);
                        mClusterManager.addItem(tempClusterMarker);
                        heritageClusterMarkers.add(tempClusterMarker);
                    }
                    getAllTreasures();
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

        queue.add(jsTotal);
    }

    public void getAllTreasures() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getAllTreasures/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        int code = jsObj.getInt("code");
                        String latitude = jsObj.getString("latitude");
                        String longitude = jsObj.getString("longitude");
                        boolean found = jsObj.getInt("found") == 1;
                        ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(latitude), Double.parseDouble(longitude), String.valueOf(code), "treasure", found);
                        mClusterManager.addItem(tempClusterMarker);
                        treasureClusterMarkers.add(tempClusterMarker);
                    }
                    getAllStages();
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

        queue.add(jsTotal);
    }

    public void getAllStages() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getActiveStages/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        int code = jsObj.getInt("stagecode");
                        String latitude = jsObj.getString("latitude");
                        String longitude = jsObj.getString("longitude");
                        boolean completed = jsObj.getInt("completed") == 1;
                        ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(latitude), Double.parseDouble(longitude), String.valueOf(code), "stage", completed);
                        mClusterManager.addItem(tempClusterMarker);
                        stageClusterMarkers.add(tempClusterMarker);
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

        queue.add(jsTotal);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_SETTINGS_CHECK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "location services enabled");
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocationManager.getCurrentLatLng()).zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                Log.d(TAG, "location services not enabled");
                Toast.makeText(this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        else if (requestCode == TREASURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setObtainedMarkerIcon(tempClusterMarker);
            }
        }

        else if (requestCode == STAGE_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                setObtainedMarkerIcon(tempClusterMarker);
            }
        }
    }

    @Override
    public boolean onClusterClick(Cluster<ClusterMarker> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        return true;
    }

    @Override
    public boolean onClusterItemClick(ClusterMarker clusterMarker) {
        if (myLocationManager.getCurrentLatLng() != null) {
            // TODO Implement validity areas
            //boolean inRange = SphericalUtil.computeDistanceBetween(playerLatLng, getLatLngByHeritageCode(Integer.parseInt(marker.getTitle()))) <= RANGE_METERS;
            boolean inRange = true;
            if (inRange) {
                switch (clusterMarker.getSnippet()) {
                    case "heritage":
                        Intent toHeritageActivity = new Intent(MainActivity.this, HeritageActivity.class);
                        if (!clusterMarker.isObtained()) {
                            setObtainedMarkerIcon(clusterMarker);
                        }
                        toHeritageActivity.putExtra("code", clusterMarker.getTitle());
                        startActivity(toHeritageActivity);
                        break;
                    case "treasure":
                        Intent toTreasureActivity = new Intent(MainActivity.this, TreasureActivity.class);
                        toTreasureActivity.putExtra("code", clusterMarker.getTitle());
                        tempClusterMarker = clusterMarker;
                        startActivityForResult(toTreasureActivity, TREASURE_REQUEST_CODE);
                        break;
                    case "stage":
                        Intent toStageActivity = new Intent(MainActivity.this, StageActivity.class);
                        toStageActivity.putExtra("code", clusterMarker.getTitle());
                        tempClusterMarker = clusterMarker;
                        startActivityForResult(toStageActivity, STAGE_REQUEST_CODE);
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.too_far), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "playerLocation null");
            Toast.makeText(MainActivity.this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void setObtainedMarkerIcon(ClusterMarker marker) {
        marker.setObtained(true);
        Collection<Marker> markerCollection = mClusterManager.getMarkerCollection().getMarkers();
        for (Marker tempMarker : markerCollection) {
            if (tempMarker.getTitle().equals(marker.getTitle()) && tempMarker.getSnippet().equals(marker.getSnippet()))
                customClusterRenderer.setObtainedMarkerIcon(tempMarker);
        }
    }

    private class SelectMarkers extends AsyncTask<String, Integer, Void> {
        ProgressDialog progDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDialog = new ProgressDialog(MainActivity.this);
            progDialog.setMessage(getString(R.string.loading));
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();
        }

        @Override
        protected Void doInBackground(String... markerType) {
            mClusterManager.clearItems();
            switch (markerType[0]) {
                case "all":
                    for (ClusterMarker marker : treasureClusterMarkers)
                        mClusterManager.addItem(marker);
                    for (ClusterMarker marker : heritageClusterMarkers)
                        mClusterManager.addItem(marker);
                    for (ClusterMarker marker : stageClusterMarkers)
                        mClusterManager.addItem(marker);
                    break;
                case "heritage":
                    for (ClusterMarker marker : heritageClusterMarkers)
                        mClusterManager.addItem(marker);
                    break;
                case "treasure":
                    for (ClusterMarker marker : treasureClusterMarkers)
                        mClusterManager.addItem(marker);
                    break;
                case "path":
                    for (ClusterMarker marker : stageClusterMarkers)
                        mClusterManager.addItem(marker);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            mClusterManager.cluster();
            progDialog.dismiss();
        }

    }

}