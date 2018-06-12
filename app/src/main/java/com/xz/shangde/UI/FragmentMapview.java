package com.xz.shangde.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.xz.shangde.Field;
import com.xz.shangde.R;
import com.xz.shangde.ShangdeApplication;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;

/**
 * @author zxz
 * 地块地图的fragment，显示在FieldsActivity中
 */

public class FragmentMapview extends Fragment {
    private ImageView iv_fullscreen;
    private MapView mapView;

    private ArrayList<Field> fields;

    public static final OnlineTileSourceBase GoogleHybrid = new XYTileSource("Google-Hybrid",
            0, 19, 256, ".png", new String[]{
            "http://mt0.google.cn",
            "http://mt1.google.cn",
            "http://mt2.google.cn",
            "http://mt3.google.cn",

    }) {
        @Override
        public String getTileURLString(long pTileIndex) {
            return getBaseUrl() + "/vt/lyrs=y&x=" + MapTileIndex.getX(pTileIndex) + "&y=" +
                    MapTileIndex.getY(pTileIndex) + "&z=" + MapTileIndex.getZoom(pTileIndex);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_mapview,container,false);

        iv_fullscreen=view.findViewById(R.id.iv_fullscreen);
        mapView =view.findViewById(R.id.mapview);

        mapView.setTileSource(GoogleHybrid);

        //add default zoom buttons, and ability to zoom with 2 fingers (multi-touch)
        mapView.setBuiltInZoomControls(false);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint startPoint = new GeoPoint(39.9035849157, 116.3977047882);
        mapController.setCenter(startPoint);

        ShangdeApplication application = (ShangdeApplication) getActivity().getApplication();
            fields = application.getFields();


            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                Polygon polygon = new Polygon();
                polygon.setPoints(field.getField_Boundary());
                mapView.getOverlayManager().add(polygon);
            }
            mapView.invalidate();


        iv_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<GeoPoint> points = new ArrayList<>();
                for (int i = 0; i < fields.size(); i++) {
                    points.addAll(fields.get(i).getField_Boundary());
                }
                BoundingBox boundingBox = BoundingBox.fromGeoPoints(points);
                mapView.zoomToBoundingBox(boundingBox, true);
            }
        });

        return view;
    }
}
