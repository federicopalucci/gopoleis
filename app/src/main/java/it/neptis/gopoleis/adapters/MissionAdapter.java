package it.neptis.gopoleis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import it.neptis.gopoleis.R;

public class MissionAdapter extends ArrayAdapter<String> {

    private String[] testo;
    private boolean[] completed;
    private Context context;
    private Holder holder;

    public MissionAdapter(Context context, String[] missionText, boolean[] completed) {
        super(context, R.layout.adapter_mission, missionText);
        this.testo = missionText;
        this.completed = completed;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        View row;

        if (view == null) {
            holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.adapter_mission, null, true);
        } else {
            row = view;
        }

        holder.textView = (TextView) row.findViewById(R.id.adapter_mission_text);
        holder.imageView = (ImageView) row.findViewById(R.id.mission_completed_image);
        holder.textView.setText(testo[position]);
        holder.imageView.setVisibility(View.INVISIBLE);
        if (completed[position])
            holder.imageView.setVisibility(View.VISIBLE);

        return row;
    }
}

class Holder {
    TextView textView;
    ImageView imageView;
}