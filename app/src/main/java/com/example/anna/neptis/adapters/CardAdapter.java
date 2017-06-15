package com.example.anna.neptis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.anna.neptis.defines.ObjCard;
import com.example.anna.neptis.R;

import java.util.List;

/**
 * Created by Anna on 23/10/2016.
 */


public class CardAdapter extends ArrayAdapter<ObjCard> {

    public CardAdapter(Context context, int textViewResourceId, List<ObjCard> list){
        super(context,textViewResourceId,list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.adapter_card, null);
        ImageView card = (ImageView) convertView.findViewById(R.id.card_icon);

        TextView code = (TextView)convertView.findViewById(R.id.card_code);
        TextView cost = (TextView)convertView.findViewById(R.id.valore_costo);
        TextView name = (TextView)convertView.findViewById(R.id.nome);

        ScrollView s  = (ScrollView)convertView.findViewById(R.id.ScrollView);
        TextView des = (TextView)convertView.findViewById(R.id.d);

        ObjCard t = getItem(position);

        code.setText(t.getCode());
        cost.setText(t.getCost());
        name.setText(t.getName());
        des.setText(t.getDescription());
        s.setNestedScrollingEnabled(true);

        String card_code = t.getCode();

        //Log.d("CARD CODE ADAPTER",card_code);
        card.setImageResource(t.getCardImage(card_code));

        return convertView;
    }


}
