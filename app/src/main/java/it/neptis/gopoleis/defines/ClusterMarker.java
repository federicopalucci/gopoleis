package it.neptis.gopoleis.defines;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private boolean mObtained;

    public ClusterMarker(double lat, double lng, String title, String snippet, boolean obtained) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mObtained = obtained;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public boolean isObtained() {
        return mObtained;
    }

    public void setObtained(boolean mObtained) {
        this.mObtained = mObtained;
    }
}