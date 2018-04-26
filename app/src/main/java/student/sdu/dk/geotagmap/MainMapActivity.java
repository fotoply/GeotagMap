package student.sdu.dk.geotagmap;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;

import student.sdu.dk.geotagmap.image.ImageChooserFragment;
import student.sdu.dk.geotagmap.image.ImageLoader;
import student.sdu.dk.geotagmap.image.ImageStore;
import student.sdu.dk.geotagmap.image.ImageViewerFragment;

public class MainMapActivity extends FragmentActivity implements OnMapReadyCallback, ImageChooserFragment.OnFragmentInteractionListener {


    private GoogleMap mMap;
    private Uri imageGettingTagged;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Fragment settingFragment = SettingsFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.nav_view, settingFragment).commit();
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
        ImageStore.getInstance().setUpdateMap((marker) -> runOnUiThread(() -> mMap.addMarker(marker)));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                setFABStatus();
            }
        };

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private void startImageLoading() {
        ImageLoader loader = new ImageLoader();
        loader.acquirePermissions(this);
        loader.setOnFinishLoading(this::setFABStatus);
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

    private void setFABStatus() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if(preferences.getBoolean("hideUntaggedImages", false)) {
            fab.hide();
            return;
        }
        if (ImageStore.getInstance().getNonTaggedImages().size() > 0) {
            fab.show();
            fab.setOnClickListener(this::untaggedButtonClicked);
        } else {
            fab.hide();
        }
    }

    private void onMapClick(LatLng latLng) {
        if (this.imageGettingTagged == null) return;
        try {
            ExifInterface exifInterface = new ExifInterface(imageGettingTagged.getPath());
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, doubleToDmsString(latLng.latitude));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, doubleToDmsString(latLng.longitude));

            if (latLng.latitude > 0) {
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N");
            } else {
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }

            if (latLng.longitude > 0) {
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");
            } else {
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }
            exifInterface.saveAttributes();

            ImageStore.getInstance().getNonTaggedImages().remove(imageGettingTagged.getPath());
            ImageStore.getInstance().storeImage(latLng, imageGettingTagged.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageGettingTagged = null;
        if(ImageStore.getInstance().getNonTaggedImages().size() == 0) {
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.hide();
        }
    }

    String doubleToDmsString(double coord) {
        coord = coord > 0 ? coord : -coord;
        String sOut = Integer.toString((int) coord) + "/1,";
        coord = (coord % 1) * 60;
        sOut = sOut + Integer.toString((int) coord) + "/1,";
        coord = (coord % 1) * 60000;
        sOut = sOut + Integer.toString((int) coord) + "/1000";
        return sOut;
    }

    private void untaggedButtonClicked(View view) {
        ImageChooserFragment imageChooserFragment = ImageChooserFragment.newInstance();
        imageChooserFragment.onAttach(this);
        imageChooserFragment.show(getFragmentManager(), "imageChooserDialog");
    }

    public boolean onMarkerClick(Marker marker) {
        if (imageGettingTagged != null) {
            onMapClick(marker.getPosition());
            return false;
        }
        ImageViewerFragment imageViewerFragment = ImageViewerFragment.newInstance(marker.getPosition());
        imageViewerFragment.show(getFragmentManager(), "imageDialog");
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri, DialogFragment fragment) {
        this.imageGettingTagged = uri;
        fragment.dismiss();
        Snackbar.make(findViewById(R.id.drawer_layout), R.string.snackbar_place, Snackbar.LENGTH_LONG).show();
    }
}
