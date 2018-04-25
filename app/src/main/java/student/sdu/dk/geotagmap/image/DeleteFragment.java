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

import com.google.android.gms.maps.model.LatLng;

import student.sdu.dk.geotagmap.R;

/**
 * Created by Joachim on 22-04-2018.
 */

public class DeleteFragment extends DialogFragment {

    public DeleteFragment() {
        // Required empty public constructor
    }

    public static DeleteFragment newInstance(Uri image) {
        DeleteFragment f = new DeleteFragment();
        Bundle args = new Bundle();
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
        Button deleteButton = view.findViewById(R.id.delButton);
        Button declineButton = view.findViewById(R.id.declineButton);



        return view;
    }

}
