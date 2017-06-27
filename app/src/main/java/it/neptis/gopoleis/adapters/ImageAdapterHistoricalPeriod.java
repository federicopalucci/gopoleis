package it.neptis.gopoleis.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import it.neptis.gopoleis.R;

public class ImageAdapterHistoricalPeriod extends BaseAdapter {

    private Context hContext;
    //references to or images
    private Integer[] hThumbIds = {R.drawable.barocco, R.drawable.grecia, R.drawable.romani,R.drawable.medioevo};

    // Constructor
    public ImageAdapterHistoricalPeriod(Context c) {
        hContext = c;
    }

    @Override
    public int getCount() {
        return hThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return hThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(hContext);
        imageView.setImageResource(hThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(190, 200));
        imageView.setPadding(2, 2, 2, 2);
        imageView.setAdjustViewBounds(true);
        return imageView;
    }
}
