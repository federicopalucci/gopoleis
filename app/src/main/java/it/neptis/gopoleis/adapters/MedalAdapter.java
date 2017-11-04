package it.neptis.gopoleis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.model.GlideApp;
import it.neptis.gopoleis.model.Medal;

public class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.MyViewHolder> {

    private List<Medal> medals;
    private Context context;

    public MedalAdapter(Context context, List<Medal> medals) {
        this.medals = medals;
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        ImageView obtainedMarker;

        MyViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.regions_medal_image);
            obtainedMarker = (ImageView) view.findViewById(R.id.medal_obtained_image);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_medal, parent, false);

        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        GlideApp.with(context).load(medals.get(position).getFilePath()).placeholder(R.drawable.progress_animation).error(R.drawable.noimage).into(holder.image);
        if (medals.get(position).isObtained())
            holder.obtainedMarker.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return medals.size();
    }

}