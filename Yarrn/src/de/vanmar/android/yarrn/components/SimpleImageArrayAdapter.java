package de.vanmar.android.yarrn.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.Map;

/**
 * Created by Kolja on 05.03.14.
 */
public class SimpleImageArrayAdapter extends ArrayAdapter {
    private Map<Object, Integer> images;

    public SimpleImageArrayAdapter(Context context, String[] items) {
        super(context, android.R.layout.simple_spinner_item, items);
    }

    public void setImages(Map<Object, Integer> images) {
        this.images = images;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = getImageForPosition(position);
        if (view != null) {
            return view;
        }
        return super.getDropDownView(position, convertView, parent);
    }

    private View getImageForPosition(int position) {
        if (images != null && images.containsKey(getItem(position))) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(images.get(getItem(position)));
            imageView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return imageView;
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = getImageForPosition(position);
        if (view != null) {
            return view;
        }
        return super.getView(position, convertView, parent);
    }
}
