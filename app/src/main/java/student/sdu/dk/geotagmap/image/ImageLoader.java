package student.sdu.dk.geotagmap.image;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {

    private Runnable onFinishLoading;

    public void loadImageData(Context context) {
        List<String> images = getAllImages(context);
        storeImages(images);

        Log.i("IMAGES", "Loaded " + ImageStore.getInstance().getPositions().size() + " images with geo tag"); //  Not precise, but precise enough
        Log.i("IMAGES", "Loaded " + ImageStore.getInstance().getNonTaggedImages().size() + " without geo tag");

        if (onFinishLoading != null) {
            onFinishLoading.run();
        }
    }

    private void storeImages(List<String> images) {
        for (String image : images) {
            LatLng latLong = getLatLong(image);
            if (latLong != null) {
                ImageStore.getInstance().storeImage(latLong, image);
            } else {
                ImageStore.getInstance().storeNonTaggedImage(image);
                Log.i("testing", image);
            }
        }
    }

    public void acquirePermissions(Activity context) {
        Activity activity = context;
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        while (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private LatLng getLatLong(String file) {
        Location location = exif2Loc(file);
        if(location == null) return null;

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        return position;
    }

    public Location exif2Loc(String flNm) {
        String sLat = "", sLatR = "", sLon = "", sLonR = "";
        try {
            ExifInterface ef = new ExifInterface(flNm);
            sLat = ef.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            sLon = ef.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            sLatR = ef.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            sLonR = ef.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        } catch (IOException e) {
            return null;
        }

        if(sLat == null || sLatR == null || sLon == null || sLonR == null) return null;

        double lat = stringToDouble(sLat);
        if (lat > 180.0) return null;
        double lon = stringToDouble(sLon);
        if (lon > 180.0) return null;

        lat = sLatR.contains("S") ? -lat : lat;
        lon = sLonR.contains("W") ? -lon : lon;

        Location loc = new Location("exif");
        loc.setLatitude(lat);
        loc.setLongitude(lon);
        return loc;
    }

    public static double stringToDouble(String sDMS) {
        double dRV = 999.0;
        try {
            String[] DMSs = sDMS.split(",", 3);
            String s[] = DMSs[0].split("/", 2);
            dRV = (new Double(s[0]) / new Double(s[1]));
            s = DMSs[1].split("/", 2);
            dRV += ((new Double(s[0]) / new Double(s[1])) / 60);
            s = DMSs[2].split("/", 2);
            dRV += ((new Double(s[0]) / new Double(s[1])) / 3600);
        } catch (Exception e) {
        }
        return round(dRV, 5);
    }


    private static List<String> getAllImages(Context context) {
        final String[] projection = {MediaStore.Images.Media.DATA};
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.FLOOR);
        return bd.doubleValue();
    }

    public void setOnFinishLoading(Runnable callback) {
        this.onFinishLoading = callback;
    }
}
