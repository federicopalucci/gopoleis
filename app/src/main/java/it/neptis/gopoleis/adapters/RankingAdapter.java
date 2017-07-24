package it.neptis.gopoleis.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import it.neptis.gopoleis.R;
import it.neptis.gopoleis.defines.RankingRow;

public class RankingAdapter extends ArrayAdapter<RankingRow> {

    public RankingAdapter(Context context, RankingRow[] data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        RankingRow rankingRow = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ranking_row, parent, false);
        }
        TextView playerTV = (TextView) convertView.findViewById(R.id.ranking_row_player);
        TextView amountTV = (TextView) convertView.findViewById(R.id.ranking_row_amount);
        //noinspection ConstantConditions
        playerTV.setText(rankingRow.getPlayer());
        amountTV.setText(String.valueOf(rankingRow.getAmount()));
        return convertView;
    }

}