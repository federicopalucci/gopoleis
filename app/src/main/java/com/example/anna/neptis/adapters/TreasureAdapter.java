package com.example.anna.neptis.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Anna on 16/10/2016.
 */

/*public class TreasureAdapter extends ArrayAdapter<ObjTesoro> {

    public TreasureAdapter(Context context, int textViewResourceId, List<ObjTesoro> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.adapter_treasure, null);

        ImageView forziere = (ImageView)convertView.findViewById(R.id.treasure_image);

        //TextView c = (TextView)convertView.findViewById(R.id.c);
        ScrollView st  = (ScrollView)convertView.findViewById(R.id.TreasureScrollView);
        TextView info = (TextView)convertView.findViewById(R.id.info_treasure);
        //ImageButton t_card = (ImageButton)convertView.findViewById(R.id.carte_tesoro);


        ObjTesoro t = getItem(position);
        st.setNestedScrollingEnabled(true);
        info.setText(t.getInfo());

        int found = t.isFound();
        forziere.setImageResource(t.getTreasureImage(found));

        //t_card.setFocusableInTouchMode(false);
        //t_card.setFocusable(false);

        final String user = t.getUser();
        final String treasure_code = t.getCode();

        /*
        if(found == 1) {
            t_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TreasureInfoActivity si dovrebbe aprire solo se il tesoro è stato trovato dall'utente-aggiungere controllo sul found!
                    Intent openTreasure = new Intent(v.getContext(), TreasureInfoActivity.class);
                    openTreasure.putExtra("codice_tesoro", treasure_code);
                    openTreasure.putExtra("user", user);
                    v.getContext().startActivity(openTreasure);
                }
            });
        }else Toast.makeText(getContext(),"Tesoro non ancora trovato dall'utente!",Toast.LENGTH_SHORT).show();

        return convertView;
    }


}
 */