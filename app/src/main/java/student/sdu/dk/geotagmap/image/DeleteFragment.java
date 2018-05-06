package student.sdu.dk.geotagmap.image;

import android.app.DialogFragment;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;import android.annotation.SuppressLint;
import android.location.Location;
import android.media.ExifInterface;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;

import student.sdu.dk.geotagmap.R;

/**
 * Created by Joachim on 22-04-2018.
 */

public class DeleteFragment extends DialogFragment {

    public DeleteFragment() {
        // Required empty public constructor
    }

    private Button deleteButton;
    private Button declineButton;
    private Uri image;

    public static DeleteFragment newInstance(Uri image) {
        DeleteFragment f = new DeleteFragment();
        Bundle args = new Bundle();
        args.putString("imageString", image.toString());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog, container, false);
        image = Uri.parse(getArguments().getString("imageString", null));
        deleteButton = view.findViewById(R.id.delButton);
        deleteButton.setOnClickListener(v -> {
            deleteButtonAction(v);
        });
        return view;
    }

    public void deleteButtonAction(View v) {
        MarkGeoTagImage(image.toString());
    }

    public void MarkGeoTagImage(String imagePath)
    {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, null);
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
