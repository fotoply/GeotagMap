package student.sdu.dk.geotagmap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import student.sdu.dk.geotagmap.image.ImageLoader;
import student.sdu.dk.geotagmap.image.ImageStore;
import student.sdu.dk.geotagmap.image.ImageViewerFragment;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        initMapSettings(googleMap);
        startImageLoading();
        ImageStore.getInstance().setUpdateMap(mMap);
    }

    private void startImageLoading() {
        ImageLoader loader = new ImageLoader();
        loader.acquirePermissions(this);
        loader.setOnFinishLoading(this::onImagesFinishLoading);
        Thread imageLoaderThread = new Thread(() -> {
            loader.loadImageData(this);
        });
        imageLoaderThread.start();
    }

    private void initMapSettings(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this::onMarkerClick);
        mMap.setOnMapClickListener(this::onMapClick);
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    private void onImagesFinishLoading() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if(ImageStore.getInstance().getNonTaggedImages().size() > 0) {
            fab.show();
            fab.setOnClickListener(this::untaggedButtonClicked);
        } else {
            fab.hide();
        }
    }

    private void onMapClick(LatLng latLng) {
        //TODO Implement tagging action
    }

    private void untaggedButtonClicked(View view) {
        //TODO Implement tagging menu
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ImageViewerFragment imageViewerFragment  = ImageViewerFragment.newInstance(marker.getPosition());
        imageViewerFragment.show(getFragmentManager(), "imageDialog");
        return false;
    }
}
