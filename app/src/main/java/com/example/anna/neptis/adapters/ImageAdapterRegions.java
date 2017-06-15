package com.example.anna.neptis.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.anna.neptis.R;

public class ImageAdapterRegions extends BaseAdapter {

    private Context mContext;

    // Constructor
    public ImageAdapterRegions(Context c) {
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(190, 200));
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    //references to or images
    private Integer[] mThumbIds = {R.drawable.lazio, R.drawable.trentino, R.drawable.sicilia, R.drawable.marche};
}