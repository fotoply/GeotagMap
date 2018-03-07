package student.sdu.dk.geotagmap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageStore {
    private HashMap<LatLng, List<String>> imageMap;
    private static final ImageStore ourInstance = new ImageStore();

    public static ImageStore getInstance() {
        return ourInstance;
    }

    private ImageStore() {
        imageMap = new HashMap<>();
    }

    public List<String> getImagesFromPosition(LatLng position) {
        return imageMap.getOrDefault(position, new ArrayList<>());
    }

    public void storeImage(LatLng position, String image) {
        if(!imageMap.containsKey(position)) {
            imageMap.put(position, new ArrayList<>());
        }
        imageMap.get(position).add(image);
    }
}
