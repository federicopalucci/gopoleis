package com.example.anna.neptis.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
import com.example.anna.neptis.defines.GameManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TreasurePortalPag2 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "TreasurePortalPag2";

    private final static int CAMERA_REQUEST_CODE = 1;
    private static final int LOCATION_SETTINGS_CHECK_REQUEST_CODE = 2;
    private static GoogleMap mMap;
    // 2km range
    private static final int RANGE_METERS = 3 * 100;

    String heritageName, heritageInfo, heritageLatitude, heritageLongitude;
    LatLng heritageLatLng;
    String tempTreasureLat, tempTreasureLon, tempTreasureCode;

    String url, url2, urlCheckFound;

    String game1SessionCode;

    private MarkerOptions options = new MarkerOptions();

    LocationRequest mLocationRequest;
    Location playerLocation;
    LatLng playerLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_portal_pag2);

        game1SessionCode = GameManager.getInstance().getGame1SessionCode();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "locationSettingsResponse onSuccess called");
                getPlayerLocation();
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
                            resolvable.startResolutionForResult(TreasurePortalPag2.this,
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        heritageName = getIntent().getExtras().getString("heritageName");
        getHeritageInfo();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RequestQueue queue = Volley.newRequestQueue(this);
        String spaces = heritageName.replace(" ", "%20");
        url2 = getString(R.string.server_url) + "getTreasureElements/" + spaces + "/";
        JsonArrayRequest jsTreasureElements = new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        tempTreasureLat = jsObj.getString("latitude");
                        tempTreasureLon = jsObj.getString("longitude");
                        tempTreasureCode = jsObj.getString("code");
                        options.position(new LatLng(Double.parseDouble(tempTreasureLat), Double.parseDouble(tempTreasureLon)));
                        options.title(tempTreasureCode);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mMap.addMarker(options);
                        mMap.setOnMarkerClickListener(TreasurePortalPag2.this);
                    }
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

        queue.add(jsTreasureElements);

        // Manage camera button
        ImageButton camera = (ImageButton) findViewById(R.id.camera_image);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera, CAMERA_REQUEST_CODE);
            }
        });

        // Manage site information button
        Button siteInformation = (Button) findViewById(R.id.site_information);
        siteInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(), heritageInfo, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void getPlayerLocation() {
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(TreasurePortalPag2.this);
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
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        Log.d(TAG, "getLastLocation onSuccess called");
                        if (location != null) {
                            Log.d(TAG, "player location acquired");
                            playerLocation = location;
                        }
                    }
                });
    }

    private void getHeritageInfo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String heritageNameNoSpaces = heritageName.replace(" ", "%20");
        String url3 = getString(R.string.server_url) + "getHeritageInfo/" + heritageNameNoSpaces + "/";
        JsonArrayRequest heritageInfoRequest = new JsonArrayRequest(Request.Method.GET, url3, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        heritageInfo = jsObj.getString("info");
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

        queue.add(heritageInfoRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RequestQueue queue = Volley.newRequestQueue(TreasurePortalPag2.this);
        String spaces = heritageName.replace(" ", "%20");
        url = getString(R.string.server_url) + "getCoordinatesHeritage/" + spaces + "/";
        JsonArrayRequest jsCoordinates = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        heritageLatitude = jsObj.getString("latitude");
                        heritageLongitude = jsObj.getString("longitude");
                        heritageLatLng = new LatLng(Double.parseDouble(heritageLatitude), Double.parseDouble(heritageLongitude));
                        mMap.addMarker(new MarkerOptions().title("heritageName").position(heritageLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(heritageLatLng).zoom(17).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

        queue.add(jsCoordinates);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.getTitle().equals("heritageName")) return true;

        LatLng treasurePosition = marker.getPosition();
        final String code_treas = marker.getTitle();

        if (playerLocation != null) {
            playerLatLng = new LatLng(playerLocation.getLatitude(), playerLocation.getLongitude());
            // TODO change treasure distance computation parameters
            boolean inRange = SphericalUtil.computeDistanceBetween(treasurePosition, heritageLatLng) <= RANGE_METERS;
            //boolean inRange = SphericalUtil.computeDistanceBetween(playerLatLng, treasurePosition) <= RANGE_METERS;
            if (inRange) {
                RequestQueue queue4 = Volley.newRequestQueue(this);
                urlCheckFound = getString(R.string.server_url) + "checkTreasureFound/" + code_treas + "/" + game1SessionCode + "/";
                JsonArrayRequest jsTreasFound = new JsonArrayRequest(Request.Method.GET, urlCheckFound, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject obj_found;
                        try {
                            obj_found = response.getJSONObject(0);
                            int found = obj_found.getInt("EXISTS(SELECT * from Gt where treasure='" + code_treas + "' AND game1='" + game1SessionCode + "')");
                            if (found == 1) {
                                Intent openTreasureFound = new Intent(TreasurePortalPag2.this, TreasureFound.class);
                                openTreasureFound.putExtra("heritageName", heritageName);
                                openTreasureFound.putExtra("codice_tesoro", code_treas);
                                startActivity(openTreasureFound);
                            } else {
                                Intent openTreasureNotFound = new Intent(TreasurePortalPag2.this, TreasureNotFoundActivity.class);
                                openTreasureNotFound.putExtra("codice_tesoro", code_treas);
                                openTreasureNotFound.putExtra("heritageName", heritageName);
                                startActivity(openTreasureNotFound);
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

                queue4.add(jsTreasFound);
            } else {
                Toast.makeText(TreasurePortalPag2.this, getString(R.string.too_far), Toast.LENGTH_LONG).show();
            }
        }
        else{
            Log.d(TAG, "playerLocation null");
            Toast.makeText(TreasurePortalPag2.this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, getString(R.string.photo_taken), Toast.LENGTH_LONG).show();
        }

        if (requestCode == LOCATION_SETTINGS_CHECK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "location services enabled");
                getPlayerLocation();
            }
            else {
                Log.d(TAG, "location services not enabled");
                Toast.makeText(this, getString(R.string.need_location_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

}