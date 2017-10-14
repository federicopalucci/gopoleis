package it.neptis.gopoleis.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ClusterMarker implements ClusterItem {

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private boolean mObtained;
    private boolean stageClickable;

    public ClusterMarker(double lat, double lng, String title, String snippet, boolean obtained) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        mObtained = obtained;
    }

    public ClusterMarker(double lat, double lng, String mTitle, String mSnippet, boolean mObtained, boolean stageClickable) {
        mPosition = new LatLng(lat, lng);
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
        this.mObtained = mObtained;
        this.stageClickable = stageClickable;
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

    public boolean isStageClickable() {
        return stageClickable;
    }

    public void setStageClickable(boolean stageClickable) {
        this.stageClickable = stageClickable;
    }

}