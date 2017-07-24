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
import it.neptis.gopoleis.defines.SearchResult;

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {

    public SearchResultAdapter(Context context, SearchResult[] data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        SearchResult searchResult = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_result_row, parent, false);
        }
        TextView titleTV = (TextView) convertView.findViewById(R.id.search_result_row_text);
        ImageView iconIV = (ImageView) convertView.findViewById(R.id.search_result_row_image);
        titleTV.setText(searchResult.getName());
        switch (searchResult.getType()){
            case "heritage":
                iconIV.setImageResource(R.drawable.ic_heritage);
                break;
            case "stage":
                iconIV.setImageResource(R.drawable.ic_puzzle);
                break;
        }
        return convertView;
    }
}