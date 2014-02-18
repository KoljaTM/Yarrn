package de.vanmar.android.yarrn.projects;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;

import java.util.Collection;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.ravelry.dts.Photo;

public class PhotoAdapter extends ArrayAdapter<Photo> {

    private class ViewHolder {
        private ImageView photo;
    }

    private final Activity context;

    public PhotoAdapter(final Activity context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public void addAll(Collection<? extends Photo> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            setNotifyOnChange(false);
            clear();
            for (Photo photo : collection) {
                add(photo);
            }
            // this sets notifyOnChange to true, regardless of former state
            notifyDataSetChanged();
        }
    }


    @Override
    public View getView(final int position, final View convertView,
                        final ViewGroup parent) {
        final View view;
        final ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = context.getLayoutInflater().inflate(
                    R.layout.photogallery_item, parent, false);
            holder = new ViewHolder();
            holder.photo = (ImageView) view.findViewById(R.id.photo);
            view.setTag(holder);
        }
        final Photo photo = getItem(position);
        new AQuery(view).id(holder.photo).image(photo.squareUrl);

        return view;
    }
}