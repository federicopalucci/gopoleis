package it.neptis.gopoleis.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import it.neptis.gopoleis.MyLocationManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class TravelPortalActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<ClusterMarker>, ClusterManager.OnClusterItemClickListener<ClusterMarker> {

    private static final String TAG = "TravelPortalAct";
    private static final int LOCATION_SETTINGS_CHECK_REQUEST_CODE = 2;

    private int totalHeritagesCount, visitedHeritagesCount;
    private TextView visitedHeritagesCounter, totalHeritagesCounter;
    private FirebaseAuth mAuth;
    private List<Heritage> heritages;
    private GoogleMap mMap;
    private boolean firstTimeZoom = true;
    private ClusterManager<ClusterMarker> mClusterManager;
    // 2km range
    private static final int RANGE_METERS = 3 * 100;
    private MyLocationManager myLocationManager;

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

        myLocationManager = MyLocationManager.getInstance(this);
        myLocationManager.checkLocationSettings(this);

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
            ClusterMarker tempClusterMarker = new ClusterMarker(Double.parseDouble(tempHeritage.getLatitude()), Double.parseDouble(tempHeritage.getLongitude()), String.valueOf(tempHeritage.getCode()), null, tempHeritage.isVisited());
            mClusterManager.addItem(tempClusterMarker);
        }
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
        if (myLocationManager.getCurrentLatLng() != null) {
            // TODO Implement validity areas
            //boolean inRange = SphericalUtil.computeDistanceBetween(playerLatLng, getLatLngByHeritageCode(Integer.parseInt(marker.getTitle()))) <= RANGE_METERS;
            boolean inRange = true;
            if (inRange) {
                Intent toHeritageActivity = new Intent(TravelPortalActivity.this, HeritageActivity.class);
                if (!clusterMarker.isObtained()) {
                    toHeritageActivity.putExtra("firstTime", true);
                    visitedHeritagesCount++;
                    clusterMarker.setObtained(true);
                    visitedHeritagesCounter.setText(String.valueOf(visitedHeritagesCount));
                    repaintMarkerGreen(clusterMarker.getTitle());
                }
                toHeritageActivity.putExtra("code", clusterMarker.getTitle());
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
        for (Marker tempMarker : markerCollection) {
            if (tempMarker.getTitle().equals(title))
                tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
    }

}