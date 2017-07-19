package it.neptis.gopoleis.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neptis.gopoleis.GopoleisApp;
import it.neptis.gopoleis.MyLocationManager;
import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Heritage;

public class TreasurePortalPag2Activity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "TreasurePortalPag2";

    private static final int LOCATION_SETTINGS_CHECK_REQUEST_CODE = 2;
    private static final int TREASURE_REQUEST_CODE = 3;
    private static GoogleMap mMap;
    private List<Marker> markers;
    // 2km range
    private static final int RANGE_METERS = 3 * 100;

    private String heritageName;
    private LatLng heritageLatLng;

    private FirebaseAuth mAuth;
    private Heritage heritage;

    private MyLocationManager myLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_portal_pag2);

        mAuth = FirebaseAuth.getInstance();
        markers = new ArrayList<>();

        heritageName = getIntent().getExtras().getString("heritageName");

        myLocationManager = MyLocationManager.getInstance(this);
        myLocationManager.checkLocationSettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Manage site information button
        Button siteInformation = (Button) findViewById(R.id.site_information);
        siteInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(), heritage.getDescription(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void getTreasureElements() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String spaces = heritageName.replace(" ", "%20");
        String url = getString(R.string.server_url) + "getTreasureElements/" + spaces + "/" + mAuth.getCurrentUser().getEmail() + "/";
        JsonArrayRequest jsTreasureElements = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length() - 1; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        String tempTreasureLat = jsObj.getString("latitude");
                        String tempTreasureLon = jsObj.getString("longitude");
                        String tempTreasureCode = jsObj.getString("code");
                        String tempTreasureDescription = jsObj.getString("description");
                        MarkerOptions options = new MarkerOptions();
                        options.position(new LatLng(Double.parseDouble(tempTreasureLat), Double.parseDouble(tempTreasureLon)));
                        options.title(tempTreasureCode);
                        if (jsObj.getString("found").equals("0")) {
                            options.snippet("0");
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        } else {
                            options.snippet("1");
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        }
                        Marker tempMarker = mMap.addMarker(options);
                        tempMarker.setTag(new String[]{tempTreasureDescription, tempTreasureLat, tempTreasureLon});
                        markers.add(tempMarker);
                        mMap.setOnMarkerClickListener(TreasurePortalPag2Activity.this);
                    }
                    JSONObject jsObj = (JSONObject) response.get(response.length() - 1);
                    heritage = new Heritage(jsObj.getInt("code"), jsObj.getString("name"),jsObj.getString("description"), jsObj.getString("latitude"), jsObj.getString("longitude"));
                    heritageLatLng = new LatLng(Double.parseDouble(heritage.getLatitude()), Double.parseDouble(heritage.getLongitude()));
                    mMap.addMarker(new MarkerOptions().title("heritageName").position(heritageLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(heritageLatLng).zoom(17).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

        queue.add(jsTreasureElements);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getTreasureElements();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTitle().equals("heritageName")) return true;

        LatLng treasurePosition = marker.getPosition();
        String code_treas = marker.getTitle();

        if (myLocationManager.getCurrentLatLng() != null) {
            // TODO change treasure distance computation parameters
            boolean inRange = SphericalUtil.computeDistanceBetween(treasurePosition, heritageLatLng) <= RANGE_METERS;
            //boolean inRange = SphericalUtil.computeDistanceBetween(myLocationManager.getCurrentLatLng(), treasurePosition) <= RANGE_METERS;
            if (inRange) {
                Intent toTreasureActivity = new Intent(TreasurePortalPag2Activity.this, TreasureActivity.class);
                toTreasureActivity.putExtra("code", code_treas);
                String[] tag = (String[]) marker.getTag();
                toTreasureActivity.putExtra("description", tag[0]);
                toTreasureActivity.putExtra("latitude", tag[1]);
                toTreasureActivity.putExtra("longitude", tag[2]);
                if (marker.getSnippet().equals("1"))
                    toTreasureActivity.putExtra("found", true);
                else
                    toTreasureActivity.putExtra("found", false);
                startActivityForResult(toTreasureActivity, TREASURE_REQUEST_CODE);
            } else {
                Toast.makeText(TreasurePortalPag2Activity.this, getString(R.string.too_far), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "playerLocation null");
            Toast.makeText(TreasurePortalPag2Activity.this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_SETTINGS_CHECK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "location services enabled");
            } else {
                Log.d(TAG, "location services not enabled");
                Toast.makeText(this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
                finish();
            }
        }

        if (requestCode == TREASURE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String tempCode = data.getStringExtra("code");
                for (Marker tempMarker : markers) {
                    if (tempMarker.getTitle().equals(tempCode)) {
                        tempMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        tempMarker.setSnippet("1");
                        break;
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

}