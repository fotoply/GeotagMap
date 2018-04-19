package student.sdu.dk.geotagmap.image;

import android.net.Uri;
import android.widget.*;

import java.util.*;

import android.graphics.*;
import android.view.*;
import android.content.*;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<String> imagelist;

    public ImageAdapter(Context context, Set<String> bitmapList) {
        this.context = context;
        this.imagelist = new ArrayList<>(bitmapList);
    }

    public int getCount() {
        return this.imagelist.size();
    }

    public Uri getItem(int position) {
        return Uri.parse(imagelist.get(position));
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(115, 115));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(Uri.parse(imagelist.get(position)));
        return imageView;
    }

}