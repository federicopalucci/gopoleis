package it.neptis.gopoleis.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.ClusterMarker;
import it.neptis.gopoleis.defines.CustomClusterRenderer;
import it.neptis.gopoleis.defines.Heritage;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class TravelPortalActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<ClusterMarker>, ClusterManager.OnClusterItemClickListener<ClusterMarker> {

    private static final String TAG = "TravelPortalAct";
    private static final int LOCATION_SETTINGS_CHECK_REQUEST_CODE = 2;

    private int totalHeritagesCount, visitedHeritagesCount;
    private TextView visitedHeritagesCounter, totalHeritagesCounter;
    private FirebaseAuth mAuth;
    private List<Heritage> heritages;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mRequestingLocationUpdates = false;
    private LatLng playerLatLng;
    private boolean firstTimeZoom = true;
    private ClusterManager<ClusterMarker> mClusterManager;
    // 2km range
    private static final int RANGE_METERS = 3 * 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_portal);

        mAuth = FirebaseAuth.getInstance();
        heritages = new ArrayList<>();

        visitedHeritagesCounter = (TextView) findViewById(R.id.visited_heritages_counter);
        totalHeritagesCounter = (TextView) findViewById(R.id.total_heritages_counter);

        visitedHeritagesCount = 0;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TravelPortalActivity.this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location lastLocation = locationResult.getLastLocation();
                playerLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                Log.d(TAG, "player location has changed!");
                if (firstTimeZoom) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(playerLatLng).zoom(15).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    firstTimeZoom = false;
                }
            }
        };

        // Location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 20);
        mLocationRequest.setFastestInterval(1000 * 10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "locationSettingsResponse onSuccess called");
                mRequestingLocationUpdates = true;
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        Log.d(TAG, "need resolution");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(TravelPortalActivity.this,
                                    LOCATION_SETTINGS_CHECK_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });

        // Achievements button
        ImageButton achievement = (ImageButton) findViewById(R.id.achievement);
        achievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openAchivement = new Intent(TravelPortalActivity.this, AchievementsActivity.class);
                startActivity(openAchivement);
            }
        });

        // Medals button
        ImageButton medals_button = (ImageButton) findViewById(R.id.Medals);
        medals_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMedalsActivity = new Intent(TravelPortalActivity.this, MedalsActivity.class);
                startActivity(openMedalsActivity);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
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
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
        Log.d(TAG, "requesting location updates");
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        Log.d(TAG, "stopping location updates");
    }

    public void getHeritagesGame2() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getHeritagesGame2/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    totalHeritagesCount = response.length();
                    totalHeritagesCounter.setText(String.valueOf(totalHeritagesCount));
                    for (int i = 0; i < totalHeritagesCount; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        int code = jsObj.getInt("code");
                        String name = jsObj.getString("name");
                        String description = jsObj.getString("description");
                        String latitude = jsObj.getString("latitude");
                        String longitude = jsObj.getString("longitude");
                        String province = jsObj.getString("province");
                        String region = jsObj.getString("region");
                        String structureType = jsObj.getString("structuretype");
                        String historicalPeriod = jsObj.getString("historicalperiod");
                        boolean visited = jsObj.getString("visited").equals("1");
                        if (visited) visitedHeritagesCount++;
                        Heritage tempHeritage = new Heritage(code, name, description, latitude, longitude, province, region, historicalPeriod, structureType, visited);
                        heritages.add(tempHeritage);
                    }
                    visitedHeritagesCounter.setText(String.valueOf(visitedHeritagesCount));
                    placeMarkers();
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnMarkerClickListener(this);
        mClusterManager = new ClusterManager<ClusterMarker>(this, mMap);
        mClusterManager.setRenderer(new CustomClusterRenderer(this, mMap, mClusterManager));
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
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
        mMap.setMyLocationEnabled(true);
        getHeritagesGame2();
    }

    private void placeMarkers() {
        for (Heritage tempHeritage : heritages) {
            /*
            mMap.addMarker(new MarkerOptions()
                    .position(tempLatLng)
                    .title(String.valueOf(tempHeritage.getCode()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    */
            ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(tempHeritage.getLatitude()), Double.parseDouble(tempHeritage.getLongitude()), String.valueOf(tempHeritage.getCode()), null, tempHeritage);
            mClusterManager.addItem(tempClusterMarker);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_SETTINGS_CHECK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "location services enabled");
                mRequestingLocationUpdates = true;
                startLocationUpdates();
            } else {
                Log.d(TAG, "location services not enabled");
                Toast.makeText(this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    private LatLng getLatLngByHeritageCode(int code) {
        for (Heritage tempHeritage : heritages) {
            if (tempHeritage.getCode() == code) {
                return new LatLng(Double.parseDouble(tempHeritage.getLatitude()), Double.parseDouble(tempHeritage.getLongitude()));
            }
        }
        return null;
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
            // TODO Implement validity areas
            //boolean inRange = SphericalUtil.computeDistanceBetween(playerLatLng, getLatLngByHeritageCode(Integer.parseInt(marker.getTitle()))) <= RANGE_METERS;
            boolean inRange = true;
            if (inRange) {
                Intent toHeritageActivity = new Intent(TravelPortalActivity.this, HeritageActivity.class);
                if (!clusterMarker.getHeritage().isVisited()) {
                    toHeritageActivity.putExtra("firstTime", true);
                    visitedHeritagesCount++;
                    clusterMarker.getHeritage().setVisited(true);
                    visitedHeritagesCounter.setText(String.valueOf(visitedHeritagesCount));
                    repaintMarkerGreen(clusterMarker.getTitle());
                }
                toHeritageActivity.putExtra("code", clusterMarker.getHeritage().getCode());
                toHeritageActivity.putExtra("name", clusterMarker.getHeritage().getName());
                toHeritageActivity.putExtra("description", clusterMarker.getHeritage().getDescription());
                toHeritageActivity.putExtra("latitude", clusterMarker.getHeritage().getLatitude());
                toHeritageActivity.putExtra("longitude", clusterMarker.getHeritage().getLongitude());
                toHeritageActivity.putExtra("historicalperiod", clusterMarker.getHeritage().getHistoricalPeriod());
                toHeritageActivity.putExtra("structuretype", clusterMarker.getHeritage().getStructureType());
                toHeritageActivity.putExtra("province", clusterMarker.getHeritage().getProvince());
                toHeritageActivity.putExtra("region", clusterMarker.getHeritage().getProvince());
                startActivity(toHeritageActivity);
            } else {
                Toast.makeText(TravelPortalActivity.this, getString(R.string.too_far), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "playerLocation null");
            Toast.makeText(TravelPortalActivity.this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    private void repaintMarkerGreen(String title) {
        Collection<Marker> markerCollection = mClusterManager.getMarkerCollection().getMarkers();
        for (Marker tempMarker : markerCollection){
            if (tempMarker.getTitle().equals(title))
                tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
    }

}