package it.neptis.gopoleis.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.adapters.RankingAdapter;
import it.neptis.gopoleis.defines.ClusterMarker;
import it.neptis.gopoleis.defines.RankingRow;

public class RankingFragment extends Fragment {

    private static final String TAG = "RankingFragment";
    private List<RankingRow> rankingRows;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ranking_layout, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.ranking_listview);
        Bundle args = getArguments();

        switch (args.getInt("number")) {
            case 0:
                break;
            case 1:
                getRankingByCards(listView);
                break;
            case 2:
                getRankingByMedals(listView);
                break;
            case 3:
                getRankingByPaths(listView);
                break;
        }

        return rootView;
    }

    private void getRankingByCards(final ListView listView) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getRankingByCards/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    rankingRows = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        rankingRows.add(new RankingRow(jsObj.getString("player"), jsObj.getInt("cards")));
                    }
                    listView.setAdapter(new RankingAdapter(getContext(), rankingRows.toArray(new RankingRow[rankingRows.size()])));
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

    private void getRankingByMedals(final ListView listView) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getRankingByMedals/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    rankingRows = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        rankingRows.add(new RankingRow(jsObj.getString("player"), jsObj.getInt("medals")));
                    }
                    listView.setAdapter(new RankingAdapter(getContext(), rankingRows.toArray(new RankingRow[rankingRows.size()])));
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

    private void getRankingByPaths(final ListView listView) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //noinspection ConstantConditions
        String url = getString(R.string.server_url) + "getRankingByPaths/";
        JsonArrayRequest jsTotal = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    rankingRows = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        rankingRows.add(new RankingRow(jsObj.getString("player"), jsObj.getInt("paths")));
                    }
                    listView.setAdapter(new RankingAdapter(getContext(), rankingRows.toArray(new RankingRow[rankingRows.size()])));
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

}