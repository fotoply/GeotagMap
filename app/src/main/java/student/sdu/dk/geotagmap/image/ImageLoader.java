package student.sdu.dk.geotagmap.image;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageLoader {

    public void loadImageData(Context context) {
        Activity activity = (Activity) context;
        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        while(activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<String> images = getAllImages(context);
        Log.i("IMAGES", Arrays.toString(images.toArray()));

        for (String image : images) {
            LatLng latLong = getLatLong(image);
            if(latLong != null) {
                ImageStore.getInstance().storeImage(latLong, image);
            }
        }
    }

    private LatLng getLatLong(String file) {
        LatLng position = null;
        try {
            ExifInterface exifInterface = new ExifInterface(file);

            String latAttribute = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lngAttribute = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

            if(latAttribute == null || lngAttribute == null) {
                return null;
            }

            double lat = convertToDegree(latAttribute);
            double lng = convertToDegree(lngAttribute);

            position = new LatLng(lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return position;
    }

    private Double convertToDegree(String stringDMS) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = Double.valueOf(stringD[0]);
        Double D1 = Double.valueOf(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = Double.valueOf(stringM[0]);
        Double M1 = Double.valueOf(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = Double.valueOf(stringS[0]);
        Double S1 = Double.valueOf(stringS[1]);
        Double FloatS = S0 / S1;

        result = Double.valueOf(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;


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
}
