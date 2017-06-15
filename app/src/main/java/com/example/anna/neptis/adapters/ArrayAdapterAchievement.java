package com.example.anna.neptis.adapters;

/**
 * Created by lorpe on 03/10/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anna.neptis.defines.ObjAchievement;
import com.example.anna.neptis.R;

import java.util.List;

public class ArrayAdapterAchievement extends ArrayAdapter<ObjAchievement>{

    public ArrayAdapterAchievement(Context context, int textViewResourceId,
                         List<ObjAchievement> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.adapter_achievements, null);
        TextView nome = (TextView)convertView.findViewById(R.id.l_achievement_title);
        TextView descrizione = (TextView)convertView.findViewById(R.id.l_achievement_descr);
        ImageView stellina = (ImageView)convertView.findViewById(R.id.img_empty_star) ;
        ObjAchievement c = getItem(position);
        nome.setText(c.getNome());
        descrizione.setText(c.getDescrizione());
        return convertView;
    }

}
