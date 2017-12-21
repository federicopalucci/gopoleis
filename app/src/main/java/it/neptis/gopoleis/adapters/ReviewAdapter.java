package it.neptis.gopoleis.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.RequestQueueSingleton;
import it.neptis.gopoleis.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    //private static final String TAG = "ReviewAdapter";

    private List<Review> reviewsList;
    private Context context;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView review, upvotes, downvotes;
        ImageButton thumbUpButton, thumbDownButton;

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
        if (review.wasVoted() && review.wasVotedPositively()) {
            holder.thumbUpButton.setImageResource(R.drawable.ic_thumb_up_green_24dp);
            holder.thumbUpButton.setClickable(false);
            holder.thumbDownButton.setClickable(false);
        } else if (review.wasVoted() && !review.wasVotedPositively()) {
            holder.thumbDownButton.setImageResource(R.drawable.ic_thumb_down_red_24dp);
            holder.thumbUpButton.setClickable(false);
            holder.thumbDownButton.setClickable(false);
        } else {
            holder.thumbUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //noinspection ConstantConditions
                    voteReview(mAuth.getCurrentUser().getEmail(), review.getCode(), true);
                    holder.upvotes.setText(String.valueOf(Integer.parseInt(holder.upvotes.getText().toString()) + 1));
                    holder.thumbUpButton.setImageResource(R.drawable.ic_thumb_up_green_24dp);
                    holder.thumbUpButton.setClickable(false);
                    holder.thumbDownButton.setClickable(false);
                }
            });
            holder.thumbDownButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //noinspection ConstantConditions
                    voteReview(mAuth.getCurrentUser().getEmail(), review.getCode(), false);
                    holder.downvotes.setText(String.valueOf(Integer.parseInt(holder.downvotes.getText().toString()) + 1));
                    holder.thumbDownButton.setImageResource(R.drawable.ic_thumb_down_red_24dp);
                    holder.thumbUpButton.setClickable(false);
                    holder.thumbDownButton.setClickable(false);
                }
            });
        }
    }

    private void voteReview(final String email, final String code, final boolean thumbUp) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.show();

        final String[] idToken = new String[1];
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken[0] = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            String url = "https://neptis-poleis.diag.uniroma1.it/player/voteReview/" + code + "/" + email + "/" + thumbUp;
                            final JsonObjectRequest jsReviews = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "Voto registrato!", Toast.LENGTH_SHORT).show();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(context, "There was an error with your request", Toast.LENGTH_SHORT).show();
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("MyToken", idToken[0]);
                                    return params;
                                }
                            };

                            RequestQueueSingleton.getInstance(context).addToRequestQueue(jsReviews);
                        } else {
                            // Handle error -> task.getException();
                            progressDialog.dismiss();
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