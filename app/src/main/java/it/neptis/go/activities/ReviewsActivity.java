package it.neptis.go.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.neptis.go.R;
import it.neptis.go.adapters.ReviewAdapter;
import it.neptis.go.model.Review;

public class ReviewsActivity extends AppCompatActivity {

    private static final String TAG = "ReviewsActivity";

    RecyclerView recyclerView;
    List<Review> all_reviews;
    ReviewAdapter reviewAdapter;
    String heritageCode;
    private FirebaseAuth mAuth;
    LinearLayout reviewsContainerLayout;
    private TextView noReviewsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.reviews);

        reviewsContainerLayout = (LinearLayout) findViewById(R.id.reviews_container_layout);
        noReviewsText = new TextView(ReviewsActivity.this);
        noReviewsText.setText(R.string.no_reviews);
        noReviewsText.setTextSize(30);

        mAuth = FirebaseAuth.getInstance();

        heritageCode = getIntent().getStringExtra("code");

        recyclerView = (RecyclerView) findViewById(R.id.reviews_recyclerView);

        all_reviews = new ArrayList<>();

        // ------------------------------------------------------------------------
        reviewAdapter = new ReviewAdapter(ReviewsActivity.this, all_reviews);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.setNestedScrollingEnabled(true);
        // ------------------------------------------------------------------------

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.server_url) + "getReviews/" + heritageCode + "/" + mAuth.getCurrentUser().getEmail() + "/";
        final JsonArrayRequest jsReviews = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int contLength = response.length();
                    for (int i = 0; i < contLength; i++) {
                        JSONObject jsObj = (JSONObject) response.get(i);
                        String code = jsObj.getString("code");
                        String review = jsObj.getString("review");
                        String likes = jsObj.getString("likes");
                        String disklikes = jsObj.getString("dislikes");
                        String player = jsObj.getString("player");
                        boolean wasVoted = !jsObj.isNull("positiveVote");
                        boolean wasVotedPositively = false;
                        if (wasVoted)
                            wasVotedPositively = Boolean.parseBoolean(jsObj.getString("positiveVote"));
                        all_reviews.add(new Review(code, player, heritageCode, review, Integer.parseInt(likes), Integer.parseInt(disklikes), wasVoted, wasVotedPositively));
                    }
                    Collections.sort(all_reviews, new Comparator<Review>() {
                        @Override
                        public int compare(Review r1, Review r2) {
                            int likes1 = r1.getLikes(), likes2 = r2.getLikes();
                            return Integer.compare(likes1, likes2) * -1;
                        }
                    });
                    reviewAdapter.notifyDataSetChanged();

                    if (all_reviews.isEmpty()) {
                        reviewsContainerLayout.addView(noReviewsText, 0);
                        Log.d(TAG, "added");
                    } else {
                        reviewsContainerLayout.removeView(noReviewsText);
                        Log.d(TAG, "not");
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

        queue.add(jsReviews);
    }
}
