package it.neptis.gopoleis.adapters;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import it.neptis.gopoleis.R;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    //references to or images
    private Integer[]mThumbIds = {
            R.drawable.treasures,R.drawable.treasures,
            R.drawable.treasures,R.drawable.treasures,
            R.drawable.treasures,R.drawable.treasures,
            R.drawable.treasures,R.drawable.treasures
    };

    // Constructor
    public ImageAdapter(Context c){
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
        imageView.setLayoutParams(new GridView.LayoutParams(250, 250));
        return imageView;
    }




    /*
    private class ViewHolder{
        public ImageView image;
        public String info;
        public String place;
    }*/

}