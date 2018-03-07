package student.sdu.dk.geotagmap;


import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewerFragment extends DialogFragment {

    public ImageViewerFragment() {
        // Required empty public constructor
    }

    static ImageViewerFragment newInstance(LatLng position) {
        ImageViewerFragment f = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", position.latitude);
        args.putDouble("long", position.longitude);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        double latitude = getArguments().getDouble("lat", 0);
        double longitude = getArguments().getDouble("long", 0);
        LatLng position = new LatLng(latitude, longitude);
        Log.i("Test", position.toString());
        String pathName = ImageStore.getInstance().getImagesFromPosition(position).get(0);
        GlideApp.with(view.getContext()).load(pathName).into(imageView);
        return view;
    }

}
