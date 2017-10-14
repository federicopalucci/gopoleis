package it.neptis.go.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import it.neptis.go.R;
import it.neptis.go.model.GlideApp;
import it.neptis.go.model.Medal;

public class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.MyViewHolder> {

    private List<Medal> medals;
    private int[] mThumbIds;
    private Context context;

    public MedalAdapter(Context context, List<Medal> medals) {
        this.medals = medals;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public ImageView obtainedMarker;

        MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.regions_medal_image);
            obtainedMarker = (ImageView) view.findViewById(R.id.medal_obtained_image);
        }
    }

    public MedalAdapter(int[] thumbsID) {
        mThumbIds = thumbsID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_medal, parent, false);

        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (medals != null) {
            GlideApp.with(context).load(medals.get(position).getFilePath()).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(holder.image);
            if (medals.get(position).isObtained())
                holder.obtainedMarker.setVisibility(View.VISIBLE);
        } else
            holder.image.setImageResource(mThumbIds[position]);
    }

    @Override
    public int getItemCount() {
        if (medals != null)
            return medals.size();
        return mThumbIds.length;
    }

}