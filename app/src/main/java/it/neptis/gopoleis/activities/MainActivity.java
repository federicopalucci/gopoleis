package it.neptis.gopoleis.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.auth.provider.FacebookProvider;
import com.firebase.ui.auth.provider.GoogleProvider;
import com.firebase.ui.auth.provider.TwitterProvider;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import it.neptis.gopoleis.HurlStackProvider;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.model.ClusterMarker;
import it.neptis.gopoleis.model.CustomClusterRenderer;
import it.neptis.gopoleis.model.GlideApp;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, ClusterManager.OnClusterClickListener<ClusterMarker>, ClusterManager.OnClusterItemClickListener<ClusterMarker> {

    private static final String TAG = "MainActivity";
    private static final int RC_LOCATION_SETTINGS = 2;
    private static final int RC_TREASURE = 3;
    private static final int RC_STAGE = 4;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_HERITAGE = 5;
    // 200mt range
    private static final double RANGE_METERS = 200;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 30;

    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private ClusterManager<ClusterMarker> mClusterManager;
    private CustomClusterRenderer customClusterRenderer;
    private List<ClusterMarker> heritageClusterMarkers;
    private List<ClusterMarker> treasureClusterMarkers;
    private List<ClusterMarker> stageClusterMarkers;
    private ClusterMarker tempClusterMarker;
    private ActionBarDrawerToggle mDrawerToggle;
    private List<Polyline> pathsPolylines;
    private List<PolylineOptions> pathsPolylinesOptions;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng playerLatLng;
    private boolean requestingLocationUpdates;
    private boolean hasZoomedAtStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        try {
            InputStream caInput = getResources().openRawResource(R.raw.gopoleis_server);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();
            HurlStackProvider.init(ca);
        } catch (Exception e) {
            Log.d(TAG, "Error retrieving server certificate");
        }

        if (mAuth.getCurrentUser() == null) {
            firebaseLogin();
        }
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "About to check user");
            checkUserAndCreate();


            ImageView playerIcon = (ImageView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.player_icon_drawer);
            try {
                GlideApp.with(MainActivity.this).load(mAuth.getCurrentUser().getPhotoUrl()).into(playerIcon);
            } catch (Exception e) {
                Log.d(TAG, "Error retrieving player avatar");
            }

            TextView playerName = (TextView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.player_name_drawer);
            playerName.setText(mAuth.getCurrentUser().getDisplayName());
            TextView playerEmail = (TextView) ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.player_email_drawer);
            playerEmail.setText(mAuth.getCurrentUser().getEmail());

            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            Log.d(TAG, "Getting map...");
            if (mapFragment != null)
                mapFragment.getMapAsync(MainActivity.this);

        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.loading_logo);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                playerLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Log.d(TAG, "player location has changed!");
                if (mMap != null && !hasZoomedAtStart) {
                    progressDialog.dismiss();
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(playerLatLng).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    hasZoomedAtStart = true;
                }
            }
        };

        // Location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 20);
        mLocationRequest.setFastestInterval(1000 * 10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        checkLocationSettings();


        heritageClusterMarkers = new ArrayList<>();
        treasureClusterMarkers = new ArrayList<>();
        stageClusterMarkers = new ArrayList<>();
        pathsPolylines = new ArrayList<>();
        pathsPolylinesOptions = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_all);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        /* host Activity *//* DrawerLayout object *//* "open drawer" description *//* "close drawer" description */
        mDrawerToggle = new ActionBarDrawerToggle(
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

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra("searched")) {
            // Launched from search results
            int code = intent.getIntExtra("searched", 0);
            LatLng position = null;
            switch (intent.getStringExtra("type")) {
                case "heritage":
                    for (ClusterMarker marker : heritageClusterMarkers) {
                        if (marker.getTitle().equals(String.valueOf(code))) {
                            position = marker.getPosition();
                        }
                    }
                    break;
                case "stage":
                    for (ClusterMarker marker : stageClusterMarkers) {
                        if (marker.getTitle().equals(String.valueOf(code))) {
                            position = marker.getPosition();
                        }
                    }
                    break;
                default:
                    break;
            }
            if (position != null)
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(position).zoom(20).build()));
        } else if (intent.hasExtra("path")) {
            for (Polyline polyline : pathsPolylines) {
                //noinspection ConstantConditions
                if (polyline.getTag().equals(intent.getStringExtra("path"))) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    Log.d(TAG, polyline.getPoints().toString());
                    for (LatLng point : polyline.getPoints()) {
                        Log.d(TAG, "point added");
                        builder.include(point);
                    }
                    LatLngBounds bounds = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "About to request location updates");
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (requestingLocationUpdates)
            return;
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
        requestingLocationUpdates = true;
        Log.d(TAG, "requesting location updates");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        requestingLocationUpdates = false;
        Log.d(TAG, "stopping location updates");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        ComponentName cn = new ComponentName(this, SearchResultsActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MenuItemCompat.collapseActionView(searchMenuItem);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void firebaseLogin() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(
                        Arrays.asList(
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                .setTosUrl("https://mgrkld.com")
                .setPrivacyPolicyUrl("https://gnjrld.com")
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.logo)
                .setTheme(R.style.FirebaseUILoginTheme)
                .build(), RC_SIGN_IN);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
        } else if (id == R.id.nav_missions) {
            startActivity(new Intent(this, MissionsActivity.class));
        } else if (id == R.id.nav_medals) {
            startActivity(new Intent(this, MedalsActivity.class));
        } else if (id == R.id.nav_rankings) {
            startActivity(new Intent(this, RankingActivity.class));
        } else if (id == R.id.nav_info) {
            showDialog("info");
        } else if (id == R.id.nav_logout) {
            signOutUser();
        }

        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "MAP READY");
        mMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mClusterManager = new ClusterManager<>(this, mMap);
        //mClusterManager.setAlgorithm(new GridBasedAlgorithm<AbstractClusterMarker>());
        mClusterManager.setRenderer(new CustomClusterRenderer(this, mMap, mClusterManager));
        customClusterRenderer = (CustomClusterRenderer) mClusterManager.getRenderer();
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        //mMap.setOnPolylineClickListener(this);
        getAllHeritages();
    }

    public void getAllHeritages() {
        RequestQueue queue = Volley.newRequestQueue(this, HurlStackProvider.getHurlStack());
        Log.d(TAG, "Getting all heritages...");
        //noinspection ConstantConditions
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
        RequestQueue queue = Volley.newRequestQueue(this, HurlStackProvider.getHurlStack());
        //noinspection ConstantConditions
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
                        if (!found) {
                            //noinspection ConstantConditions
                            ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(latitude), Double.parseDouble(longitude), String.valueOf(code), "treasure", found);
                            mClusterManager.addItem(tempClusterMarker);
                            treasureClusterMarkers.add(tempClusterMarker);
                        }
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
        RequestQueue queue = Volley.newRequestQueue(this, HurlStackProvider.getHurlStack());
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getActiveStages/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int prevPathCode = 0;
                    List<LatLng> latLngs = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        int code = jsObj.getInt("stagecode");

                        int pathCode = jsObj.getInt("pathcode");
                        if (pathCode != prevPathCode && prevPathCode != 0) {
                            PolylineOptions options = new PolylineOptions()
                                    .clickable(true)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .color(0xffF57F17)
                                    .jointType(JointType.ROUND)
                                    .addAll(latLngs);
                            Polyline polyline = mMap.addPolyline(options);
                            polyline.setTag(jsObj.getString("pathtitle"));
                            Log.d(TAG, polyline.getPoints().toString());
                            latLngs.clear();
                            pathsPolylines.add(polyline);
                            pathsPolylinesOptions.add(options);
                        }

                        prevPathCode = pathCode;

                        String latitude = jsObj.getString("latitude");
                        String longitude = jsObj.getString("longitude");

                        latLngs.add(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));

                        if (i == response.length() - 1) {
                            PolylineOptions options = new PolylineOptions()
                                    .clickable(true)
                                    .endCap(new RoundCap())
                                    .startCap(new RoundCap())
                                    .color(0xffF57F17)
                                    .jointType(JointType.ROUND)
                                    .addAll(latLngs);
                            Polyline polyline = mMap.addPolyline(options);
                            polyline.setTag(jsObj.getString("pathtitle"));
                            Log.d(TAG, polyline.getPoints().toString());
                            latLngs.clear();
                            pathsPolylines.add(polyline);
                            pathsPolylinesOptions.add(options);
                        }

                        boolean completed = jsObj.getInt("completed") == 1;
                        boolean isStageClickable = (completed || i == 0 || (((JSONObject) response.get(i - 1)).getInt("completed") == 1));
                        ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(latitude), Double.parseDouble(longitude), String.valueOf(code), "stage", completed, isStageClickable);
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
        if (requestCode == RC_LOCATION_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "location services enabled");
            } else {
                Log.d(TAG, "location services not enabled");
                Toast.makeText(this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
                finish();
            }
        } else if (requestCode == RC_TREASURE) {
            if (resultCode == Activity.RESULT_OK) {
                mClusterManager.removeItem(tempClusterMarker);
                mClusterManager.cluster();
                treasureClusterMarkers.remove(tempClusterMarker);
            }
        } else if (requestCode == RC_STAGE) {
            if (resultCode == Activity.RESULT_OK) {
                for (int i = 0; i < stageClusterMarkers.size(); i++) {
                    if (i != stageClusterMarkers.size() - 1 && stageClusterMarkers.get(i).getTitle().equals(tempClusterMarker.getTitle())) {
                        Log.d(TAG, "found stage to be made clickable");
                        stageClusterMarkers.get(i + 1).setStageClickable(true);
                        setNextStageIcon(stageClusterMarkers.get(i + 1));
                    }
                }
                setObtainedMarkerIcon(tempClusterMarker);
            }
        } else if (requestCode == RC_HERITAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult Heritage");
            }
        } else if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Link IDP account with Firebase user
            AuthCredential credential = getAuthCredential(response);

            if (resultCode == ResultCodes.OK) {
                if (mAuth.getCurrentUser() != null) {
                    mAuth.getCurrentUser().linkWithCredential(credential)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "linkWithCredential:success");
                                    } else {
                                        Log.w(TAG, "linkWithCredential:failure", task.getException());
                                    }
                                }
                            });

                    checkUserAndCreate();

                    recreate();
                }
            } else {
                if (response == null) {
                    // User pressed back button
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(getApplicationContext(), getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void signOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        Toast.makeText(getApplicationContext(), getString(R.string.done_log_out), Toast.LENGTH_SHORT).show();
                        firebaseLogin();
                    }
                });
    }

    public AuthCredential getAuthCredential(IdpResponse idpResponse) {
        switch (idpResponse.getProviderType()) {
            case GoogleAuthProvider.PROVIDER_ID:
                return GoogleProvider.createAuthCredential(idpResponse);
            case FacebookAuthProvider.PROVIDER_ID:
                return FacebookProvider.createAuthCredential(idpResponse);
            case TwitterAuthProvider.PROVIDER_ID:
                return TwitterProvider.createAuthCredential(idpResponse);
            default:
                return null;
        }
    }

    private void checkUserAndCreate() {
        RequestQueue queue = Volley.newRequestQueue(this, HurlStackProvider.getHurlStack());
        Log.d(TAG, "Checking if player exists in db");
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "checkPlayer/" + mAuth.getCurrentUser().getEmail();
        JsonArrayRequest jsInfoTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        Iterator<String> iterator = jsObj.keys();
                        while (iterator.hasNext()) {
                            String tempKey = iterator.next();
                            if (jsObj.getInt(tempKey) == 0)
                                createUser();
                        }
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

    private void createUser() {
        RequestQueue queue = Volley.newRequestQueue(this, HurlStackProvider.getHurlStack());
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "createPlayer/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsInfoTreasure = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        Log.d(TAG, jsObj.toString());
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
        if (playerLatLng != null) {
            boolean inRange = SphericalUtil.computeDistanceBetween(playerLatLng, clusterMarker.getPosition()) <= RANGE_METERS;
            inRange = true;
            //noinspection ConstantConditions
            if (inRange) {
                switch (clusterMarker.getSnippet()) {
                    case "heritage":
                        Intent toHeritageActivity = new Intent(MainActivity.this, HeritageActivity.class);
                        if (!clusterMarker.isObtained()) {
                            setObtainedMarkerIcon(clusterMarker);
                        }
                        toHeritageActivity.putExtra("code", clusterMarker.getTitle());
                        tempClusterMarker = clusterMarker;
                        startActivityForResult(toHeritageActivity, RC_HERITAGE);
                        break;
                    case "treasure":
                        Intent toTreasureActivity = new Intent(MainActivity.this, TreasureActivity.class);
                        toTreasureActivity.putExtra("code", clusterMarker.getTitle());
                        tempClusterMarker = clusterMarker;
                        startActivityForResult(toTreasureActivity, RC_TREASURE);
                        break;
                    case "stage":
                        if (!clusterMarker.isStageClickable())
                            break;
                        Intent toStageActivity = new Intent(MainActivity.this, StageActivity.class);
                        toStageActivity.putExtra("code", clusterMarker.getTitle());
                        tempClusterMarker = clusterMarker;
                        startActivityForResult(toStageActivity, RC_STAGE);
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

    private void setNextStageIcon(ClusterMarker marker) {
        Collection<Marker> markerCollection = mClusterManager.getMarkerCollection().getMarkers();
        for (Marker tempMarker : markerCollection) {
            if (tempMarker.getTitle().equals(marker.getTitle()) && tempMarker.getSnippet().equals(marker.getSnippet()))
                customClusterRenderer.setNextStageIcon(tempMarker);
        }
    }

    private void showDialog(String parameter) {
        final AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(MainActivity.this);
        switch (parameter) {
            /*
            case "newTreasure":
                builder.setMessage(R.string.can_advance_in_treasure_ranking)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClusterMarker closestTreasureMarker = getClosestTreasureClusterMarker();
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(closestTreasureMarker.getPosition()).zoom(15).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        })
                        .setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setIcon(android.R.drawable.star_off)
                        .show();
                break;
            */
            /*
            case "newHeritage":
                builder.setMessage(R.string.almost_advance_in_ranking)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ClusterMarker closestHeritageMarker = getClosestHeritageClusterMarker();
                                if (closestHeritageMarker == null) {
                                    Toast.makeText(MainActivity.this, "Hai già visitato tutti i beni culturali disponibili", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(closestHeritageMarker.getPosition()).zoom(20).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }
                        })
                        .setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setIcon(android.R.drawable.star_off)
                        .show();
                break;
                */
            case "info":
                builder.setTitle(R.string.about_gopoleis)
                        .setMessage(R.string.gopoleis_info)
                        .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_info)
                        .show();
                break;
        }
    }

    private class SelectMarkers extends AsyncTask<String, Integer, Void> {
        ProgressDialog progDialog;
        String markerTypeSelected;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDialog = new ProgressDialog(MainActivity.this);
            progDialog.setMessage(getString(R.string.loading));
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();

            Log.d(TAG, "removing polylines...");
            for (Polyline p : pathsPolylines) {
                p.remove();
                Log.d(TAG, p.toString());
            }
        }

        @Override
        protected Void doInBackground(String... markerType) {
            mClusterManager.clearItems();
            markerTypeSelected = markerType[0];
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
            pathsPolylines.clear();
            if (markerTypeSelected.equals("path") || markerTypeSelected.equals("all"))
                for (PolylineOptions po : pathsPolylinesOptions)
                    pathsPolylines.add(mMap.addPolyline(po));
            mClusterManager.cluster();
            progDialog.dismiss();
        }

    }

    public void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "locationSettingsResponse onSuccess called");
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "Need resolution");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, RC_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            Log.d(TAG, "ERROR RESOLVING LOCATION SETTINGS");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, getString(R.string.need_location_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

}