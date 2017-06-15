package com.example.anna.neptis.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.anna.neptis.R;

public class ImageAdapterTypology extends BaseAdapter {

    private Context tContext;
    //references to or images
    private Integer[] tThumbIds = {R.drawable.basilica, R.drawable.church, R.drawable.museo,R.drawable.castello};

    // Constructor
    public ImageAdapterTypology(Context c) {
        tContext = c;
    }

    @Override
    public int getCount() {
        return tThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return tThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(tContext);
        imageView.setImageResource(tThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(190, 200));
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }


}
