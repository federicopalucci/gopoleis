package it.neptis.gopoleis.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.adapters.RankingAdapter;
import it.neptis.gopoleis.model.RankingRow;

public class RankingFragment extends Fragment {

    //private static final String TAG = "RankingFragment";

    private List<RankingRow> rankingRows;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ranking_layout, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.ranking_listview);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        getRankingByCoins(listView);

        return rootView;
    }

    private void getRankingByCoins(final ListView listView) {
        final String[] idToken = new String[1];
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken[0] = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            String url = getString(R.string.server_url) + "player/getRankingByCoins/";
                            JsonArrayRequest rankingRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        rankingRows = new ArrayList<>();
                                        for (int i = 0; i < response.length(); i++) {
                                            JSONObject jsObj = (JSONObject) response.get(i);
                                            rankingRows.add(new RankingRow(jsObj.getString("email"), jsObj.getInt("coins")));
                                        }
                                        listView.setAdapter(new RankingAdapter(getContext(), rankingRows.toArray(new RankingRow[rankingRows.size()])));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    progressDialog.dismiss();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(rankingRequest);
                        } else {
                            // Handle error -> task.getException();
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "There was an error with your request", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }
                });
    }

}