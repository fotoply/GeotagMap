package student.sdu.dk.geotagmap.image;

import android.app.DialogFragment;
import android.app.Fragment;
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

public class ImageContextMenu extends DialogFragment {

    public ImageContextMenu() {
        // Required empty public constructor
    }
    private Marker marker;
    private Button deleteButton;
    private Uri image;

    public static ImageContextMenu newInstance(Uri image) {
        ImageContextMenu f = new ImageContextMenu();
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
        View view = inflater.inflate(R.layout.image_context_menu, container, false);
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
            for (Fragment fragement: this.getFragmentManager().getFragments()) {

            }
            this.getFragmentManager().getFragments().forEach((f)->
                    this.getFragmentManager().beginTransaction().remove(f).commit());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
