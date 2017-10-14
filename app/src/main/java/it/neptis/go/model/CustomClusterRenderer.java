package it.neptis.go.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import it.neptis.go.R;

public class CustomClusterRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private Context context;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarker item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        switch (item.getSnippet()) {
            case "heritage":
                Drawable heritageDrawable;
                if (!item.isObtained())
                    heritageDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.heritage, null);
                else
                    heritageDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.heritage_visited, null);
                markerOptions.icon(getMarkerIconFromDrawable(heritageDrawable));
                break;
            case "treasure":
                Drawable treasureDrawable;
                treasureDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.cards_icon, null);
                markerOptions.icon(getMarkerIconFromDrawable(treasureDrawable));
                break;
            case "stage":
                Drawable stageDrawable;
                if (item.isObtained())
                    stageDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.stage_completed, null);
                else if (!item.isStageClickable())
                    stageDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.stage, null);
                else
                    stageDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.stage_next, null);
                markerOptions.icon(getMarkerIconFromDrawable(stageDrawable));
                break;
        }
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarker> cluster) {
        return cluster.getSize() > 3;
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 60, 60, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    public void setObtainedMarkerIcon(Marker marker) {
        if (marker.getSnippet().equals("heritage"))
            marker.setIcon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.heritage_visited, null)));
        if (marker.getSnippet().equals("stage"))
            marker.setIcon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.stage_completed, null)));
    }

    public void setNextStageIcon(Marker marker) {
        marker.setIcon(getMarkerIconFromDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.stage_next, null)));
    }

}