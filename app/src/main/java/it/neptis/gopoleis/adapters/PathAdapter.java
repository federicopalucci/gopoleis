package it.neptis.gopoleis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.Achievement;
import it.neptis.gopoleis.defines.Path;

public class PathAdapter extends ArrayAdapter<Path> {

    public PathAdapter(Context context, Path[] paths) {
        super(context, 0, paths);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Path path = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.path_row, parent, false);
        }
        TextView titleTV = (TextView) convertView.findViewById(R.id.path_row_title);
        ImageView iconIV = (ImageView) convertView.findViewById(R.id.path_row_icon);
        titleTV.setText(path.getTitle());
        if (path.isCompleted()){
            iconIV.setImageResource(android.R.drawable.star_off);
        }
        return convertView;
    }
}