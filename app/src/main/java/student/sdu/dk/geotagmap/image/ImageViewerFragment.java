package student.sdu.dk.geotagmap.image;


import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import student.sdu.dk.geotagmap.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageViewerFragment extends DialogFragment {

    private int currentImage = 0;
    private Marker marker;

    public ImageViewerFragment() {
        // Required empty public constructor
    }

    public static ImageViewerFragment newInstance(LatLng position) {
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
        ImageSwitcher imageView = view.findViewById(R.id.imageView);
        double latitude = getArguments().getDouble("lat", 0);
        double longitude = getArguments().getDouble("long", 0);
        LatLng position = new LatLng(latitude, longitude);
        Log.i("Test", position.toString());
        String pathName = ImageStore.getInstance().getImagesFromPosition(position).get(0);
        List<String> others = ImageStore.getInstance().getImagesFromPosition(position);
        imageView.setImageURI(Uri.parse(pathName));
        GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.i("IMAGES", "Image swapped");
                if (others.size() == 1) {
                    return false;
                }

                currentImage++;
                if (currentImage > others.size() - 1) currentImage = 0;
                imageView.setImageURI(Uri.parse(others.get(currentImage)));
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Uri  a = Uri.parse(others.get(currentImage));
                ImageContextMenu imageContextMenu = ImageContextMenu.newInstance(a);
                imageContextMenu.setMarker(marker);
                imageContextMenu.show(getFragmentManager(), "imageDialog");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        imageView.setOnTouchListener((touchedView, touch) -> gestureDetector.onTouchEvent(touch));
        //GlideApp.with(view.getContext()).load(pathName).into();
        return view;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

}
