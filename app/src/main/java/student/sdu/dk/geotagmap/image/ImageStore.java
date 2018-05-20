package student.sdu.dk.geotagmap.image;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageStore {
    private HashMap<LatLng, List<String>> imageMap;
    private Set<String> nonTaggedImages;
    private static final ImageStore ourInstance = new ImageStore();
    private UpdatableMap updateMap;


    private LatLng test = null;
    public static ImageStore getInstance() {
        return ourInstance;
    }

    private ImageStore() {
        imageMap = new HashMap<>();
        nonTaggedImages = new HashSet<>();
    }

    public Set<String> getNonTaggedImages() {
        return nonTaggedImages;
    }

    public void storeNonTaggedImage(String image) {
        nonTaggedImages.add(image);
    }

    public List<String> getImagesFromPosition(LatLng position) {
        return imageMap.getOrDefault(position, new ArrayList<>());
    }

    public void removeTaggedImage(String image, Marker marker) {
        List<String> a =  imageMap.get(marker.getPosition());
        try {
            a.remove(image);
            if(imageMap.get(marker.getPosition()).isEmpty()) {
                imageMap.remove(marker.getPosition());
                marker.remove();
            }
        } catch (NullPointerException e) {
            Log.i("RemoveImage", "Position does not exist");
        }
    }


    public Set<LatLng> getPositions() {
        return imageMap.keySet();
    }

    public void storeImage(LatLng position, String image) {
        if (!imageMap.containsKey(position)) {
            imageMap.put(position, new ArrayList<>());
        }
        imageMap.get(position).add(image);
        addMarker(position);
    }

    private void addMarker(LatLng position) {
        if (updateMap != null) {
            updateMap.addMarker(new MarkerOptions().position(position));
            test = position;
            Log.i("Marker", "New marker added");
        }
    }

    public void setUpdateMap(UpdatableMap updateMap) {
        this.updateMap = updateMap;
        getPositions().forEach(this::addMarker);
    }
}
