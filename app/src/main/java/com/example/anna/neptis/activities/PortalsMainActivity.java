package com.example.anna.neptis.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.anna.neptis.R;
import com.example.anna.neptis.defines.GameManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.firebase.ui.auth.provider.FacebookProvider;
import com.firebase.ui.auth.provider.GoogleProvider;
import com.firebase.ui.auth.provider.TwitterProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;


public class PortalsMainActivity extends AppCompatActivity {

    private final static String TAG = "PortalsMainActivity";

    private SharedPreferences prefs;

    TextView loggedUser;
    ImageButton logoutButton;

    Dialog home_dialog, login_dialog;

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portals_main);

        prefs = getSharedPreferences("session", Context.MODE_PRIVATE);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Logout button
        logoutButton = (ImageButton) findViewById(R.id.logout_icon);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutUser(view);
            }
        });

        loggedUser = (TextView) findViewById(R.id.accesso);

        // Yellow Portal
        ImageButton yellowPortalButton = (ImageButton) findViewById(R.id.yellow_portal);
        yellowPortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openYellowPortal = new Intent(PortalsMainActivity.this, TreasurePortalPag1.class);
                startActivity(openYellowPortal);
            }
        });

        // Green Portal
        ImageButton greenPortalButton = (ImageButton) findViewById(R.id.green_portal);
        greenPortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGreenPortal = new Intent(PortalsMainActivity.this, TravelPortalActivity.class);
                startActivity(openGreenPortal);
            }
        });

        // Red Portal
        ImageButton redPortalButton = (ImageButton) findViewById(R.id.red_portal);
        redPortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openRedPortal = new Intent(PortalsMainActivity.this, PuzzlePortal.class);
                startActivity(openRedPortal);
            }
        });

        // Blue Portal
        ImageButton bluePortalButton = (ImageButton) findViewById(R.id.blue_portal);
        bluePortalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(view.getContext(), "Blue portal", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loggedUser.setText(String.format(getString(R.string.logged_in_as), currentUser.getDisplayName()));
            getUserGameCodes();
        } else {
            loggedUser.setText(R.string.please_log_in);
            firebaseLogin();
        }
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
    protected void onResume() {
        super.onResume();

        boolean hasDoneTutorial = prefs.getBoolean("hasDoneTutorial", false);
        if (!hasDoneTutorial) {
            tutorial();
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putBoolean("hasDoneTutorial", true);
            prefsEditor.apply();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
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

                    checkUserAndCreateThenGetGameCodes();
                }
                //startActivity(SignedInActivity.createIntent(this, response));
            } else {
                if (response == null) {
                    // User pressed back button
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.unknown_error), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    public void signOutUser(View v) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.done_log_out), Toast.LENGTH_SHORT);
                        toast.show();
                        loggedUser.setText(R.string.please_log_in);
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

    private void checkUserAndCreateThenGetGameCodes() {
        RequestQueue queue = Volley.newRequestQueue(this);
        if (mAuth.getCurrentUser() == null)
            return;
        String url = getString(R.string.server_url) + "checkUser/" + mAuth.getCurrentUser().getEmail();

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
                    getUserGameCodes();
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
        if (mAuth.getCurrentUser() == null)
            return;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "createUser/" + mAuth.getCurrentUser().getEmail() + "/" + "password" + "/";

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

    private void getUserGameCodes() {
        if (mAuth.getCurrentUser() == null)
            return;
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getUserGameCodes/" + mAuth.getCurrentUser().getEmail() + "/";

        JsonArrayRequest getUserGameCodesRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    GameManager gm = GameManager.getInstance();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        gm.setGame1SessionCode(jsObj.getString("game1"));
                        gm.setGame2SessionCode(jsObj.getString("game2"));
                        gm.setGame3SessionCode(jsObj.getString("game3"));
                        gm.setGame4SessionCode(jsObj.getString("game4"));
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

        queue.add(getUserGameCodesRequest);
    }

    public void tutorial() {
        AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
        miaAlert.setTitle("Benvenuto in GoPoleis!");
        miaAlert.setMessage("Prima di iniziare la nostra avventura volevamo darti delle indicazioni su come muoverti nei portali.\nVuoi avviare il tutorial?");
        miaAlert.setIcon(R.drawable.logo);

        miaAlert.setCancelable(false);
        miaAlert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                home_dialog = new Dialog(PortalsMainActivity.this);
                home_dialog.setCancelable(false);
                home_dialog.setContentView(R.layout.tutorial_portal_main_activity);
                home_dialog.show();

            }
        });

        miaAlert.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = miaAlert.create();
        alert.show();

    }

    public void avanti_home(View view) {
        login_dialog = new Dialog(PortalsMainActivity.this);
        login_dialog.setCancelable(false);
        login_dialog.setContentView(R.layout.tutorial_login);
        home_dialog.cancel();
        login_dialog.show();
    }

    public void avanti_login(View view) {
        login_dialog.cancel();

        AlertDialog.Builder inizia = new AlertDialog.Builder(this);
        inizia.setTitle("Inizia la tua avventura!");
        inizia.setMessage("Effettua il login ed entra in uno dei portali.\nGoPoleis!");
        inizia.setIcon(R.drawable.logo);

        inizia.setCancelable(false);
        inizia.setPositiveButton("Inizia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = inizia.create();
        alert.show();
    }

}