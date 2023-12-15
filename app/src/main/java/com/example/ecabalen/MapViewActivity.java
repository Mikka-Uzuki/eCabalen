package com.example.ecabalen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapViewActivity extends AppCompatActivity {

    LinearLayout linearLayoutBack;

    String Location, Name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");

        linearLayoutBack = findViewById(R.id.linearLayoutBack);

        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Location = getIntent().getExtras().getString("Location");
        Name = getIntent().getExtras().getString("Name");

        try {
            MapView map = (MapView) findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setMultiTouchControls(true);

            {
                Geocoder geocoder = new Geocoder(MapViewActivity.this, Locale.getDefault());

                try {
                    List<Address> addresses = geocoder.getFromLocationName(Location, 1);
                    Address obj = addresses.get(0);

                    GeoPoint startPoint = new GeoPoint(obj.getLatitude(), obj.getLongitude());

                    IMapController mapController = map.getController();
                    mapController.setZoom(14);
                    mapController.setCenter(startPoint);


                    Marker startMarker = new Marker(map);
                    startMarker.setPosition(startPoint);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                    startMarker.setIcon(getResources().getDrawable(R.drawable.location_red_icon));
                    startMarker.setTitle(Name);
                    map.getOverlays().add(startMarker);


                    map.invalidate();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(MapViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception err)
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}