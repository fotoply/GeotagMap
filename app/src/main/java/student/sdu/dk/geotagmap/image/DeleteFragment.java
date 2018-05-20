package student.sdu.dk.geotagmap.image;

import android.app.DialogFragment;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;

import student.sdu.dk.geotagmap.R;

/**
 * Created by Joachim on 22-04-2018.
 */

public class DeleteFragment extends DialogFragment {

    public DeleteFragment() {
        // Required empty public constructor
    }
    private Marker marker;
    private Button deleteButton;
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

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void deleteButtonAction(View v) {
        imageRemoveGeoTag(image.toString());
    }

    public void imageRemoveGeoTag(String imagePath)
    {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, null);
            exif.saveAttributes();
            ImageStore.getInstance().getNonTaggedImages().add(imagePath);
            //TODO make list with nonTagged Images reappear
            ImageStore.getInstance().removeTaggedImage(imagePath, marker);
            this.getFragmentManager().beginTransaction().remove(this).commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
