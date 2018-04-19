package student.sdu.dk.geotagmap.image;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import student.sdu.dk.geotagmap.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageChooserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageChooserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageChooserFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    public ImageChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImageChooserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageChooserFragment newInstance() {
        ImageChooserFragment fragment = new ImageChooserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_chooser, container, false);
        GridView grid = view.findViewById(R.id.imageChooserGrid);
        ImageAdapter adapter = new ImageAdapter(getActivity(), ImageStore.getInstance().getNonTaggedImages());
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent, clickedView, position, id) -> {
            onImageChosen(adapter.getItem(position));
        });
        return view;
    }

    public void onImageChosen(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri, this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri, DialogFragment fragment);
    }
}
