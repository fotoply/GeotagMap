package student.sdu.dk.geotagmap.image;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ImageStore {
    private HashMap<LatLng, List<String>> imageMap;
    private static final ImageStore ourInstance = new ImageStore();
    private GoogleMap updateMap;

    public static ImageStore getInstance() {
        return ourInstance;
    }

    private ImageStore() {
        imageMap = new HashMap<>();
    }

    public List<String> getImagesFromPosition(LatLng position) {
        return imageMap.getOrDefault(position, new ArrayList<>());
    }

    public Set<LatLng> getPositions() {
        return imageMap.keySet();
    }

    public void storeImage(LatLng position, String image) {
        if(!imageMap.containsKey(position)) {
            imageMap.put(position, new ArrayList<>());
        }
        imageMap.get(position).add(image);
        updateMap.addMarker(new MarkerOptions().position(position));
    }

    public void setUpdateMap(GoogleMap updateMap) {
        this.updateMap = updateMap;
        getPositions().forEach((pos) -> {
            updateMap.addMarker(new MarkerOptions().position(pos));
        });
    }
}
