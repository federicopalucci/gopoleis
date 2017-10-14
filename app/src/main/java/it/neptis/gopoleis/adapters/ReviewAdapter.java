package it.neptis.gopoleis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
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
import it.neptis.gopoleis.model.Card;
import it.neptis.gopoleis.model.GlideApp;
import it.neptis.gopoleis.model.RankingRow;
import it.neptis.gopoleis.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private static final String TAG = "ReviewAdapter";

    private List<Review> reviewsList;
    private Context context;
    private FirebaseAuth mAuth;

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView review, upvotes, downvotes;
        public ImageButton thumbUpButton, thumbDownButton;

        MyViewHolder(View view) {
            super(view);
            review = (TextView) view.findViewById(R.id.adapter_review_text);
            upvotes = (TextView) view.findViewById(R.id.upvotes);
            downvotes = (TextView) view.findViewById(R.id.downvotes);
            thumbUpButton = (ImageButton) view.findViewById(R.id.review_thumb_up);
            thumbDownButton = (ImageButton) view.findViewById(R.id.review_thumb_down);
        }
    }

    public ReviewAdapter(Context context, List<Review> reviewsList) {
        this.reviewsList = reviewsList;
        this.context = context;

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_review, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Review review = reviewsList.get(position);
        holder.review.setText(review.getPlayer().toUpperCase() + ": " + review.getReview());
        holder.upvotes.setText(String.valueOf(review.getLikes()));
        holder.downvotes.setText(String.valueOf(review.getDislikes()));
        Log.d(TAG, review.isWasVoted() + " " + review.isWasVotedPositively());
        if (review.isWasVoted() && review.isWasVotedPositively()) {
            holder.thumbUpButton.setImageResource(R.drawable.ic_thumb_up_green_24dp);
            holder.thumbUpButton.setClickable(false);
            holder.thumbDownButton.setClickable(false);
        } else if (review.isWasVoted() && !review.isWasVotedPositively()) {
            holder.thumbDownButton.setImageResource(R.drawable.ic_thumb_down_red_24dp);
            holder.thumbUpButton.setClickable(false);
            holder.thumbDownButton.setClickable(false);
        } else {
            holder.thumbUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    voteReview(mAuth.getCurrentUser().getEmail(), review.getCode(), true);
                    holder.upvotes.setText(String.valueOf(Integer.parseInt(holder.upvotes.getText().toString()) + 1));
                    Toast.makeText(context, "Voto registrato!", Toast.LENGTH_SHORT).show();
                    holder.thumbUpButton.setImageResource(R.drawable.ic_thumb_up_green_24dp);
                    holder.thumbUpButton.setClickable(false);
                    holder.thumbDownButton.setClickable(false);
                }
            });
            holder.thumbDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    voteReview(mAuth.getCurrentUser().getEmail(), review.getCode(), false);
                    holder.downvotes.setText(String.valueOf(Integer.parseInt(holder.downvotes.getText().toString()) + 1));
                    Toast.makeText(context, "Voto registrato!", Toast.LENGTH_SHORT).show();
                    holder.thumbDownButton.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                    holder.thumbUpButton.setClickable(false);
                    holder.thumbDownButton.setClickable(false);
                }
            });
        }
    }

    private void voteReview(final String email, final String code, final boolean thumbUp) {
        final String[] idToken = new String[1];
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken[0] = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            RequestQueue queue = Volley.newRequestQueue(context);
                            String url = "https://77.81.226.246:8000/player/voteReview/" + code + "/" + email + "/" + thumbUp;
                            final JsonArrayRequest jsReviews = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, error.toString());
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            queue.add(jsReviews);
                        } else {
                            // Handle error -> task.getException();
                            Log.d(TAG, task.getException().toString());
                            Toast.makeText(context, "There was an error with your request", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

}