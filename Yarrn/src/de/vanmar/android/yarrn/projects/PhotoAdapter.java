package de.vanmar.android.yarrn.projects;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.androidquery.AQuery;

import java.util.Collection;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.ravelry.dts.Photo;

public class PhotoAdapter extends ArrayAdapter<Photo> {

    interface PhotoAdapterListener {
        void onMoveLeft(int position);

        void onMoveAllLeft(int position);

        void onMoveRight(int position);

        void onMoveAllRight(int position);
    }

    private boolean editable = false;
    private PhotoAdapterListener listener;

    private class ViewHolder {
        private ImageView photo;
        private View edit_overlay;
        private ImageButton move_left;
        private ImageButton move_all_left;
        private ImageButton move_right;
        private ImageButton move_all_right;
    }

    private final Activity context;

    public PhotoAdapter(final Activity context) {
        super(context, 0);
        this.context = context;
    }

    public void setItems(Collection<? extends Photo> collection) {
        clear();
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

    public void setEditable(boolean editable) {
        this.editable = editable;
        notifyDataSetChanged();
    }

    public void setPhotoAdapterListener(PhotoAdapterListener listener) {
        this.listener = listener;
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
            holder.edit_overlay = view.findViewById(R.id.edit_overlay);
            holder.move_left = (ImageButton) view.findViewById(R.id.move_left);
            holder.move_all_left = (ImageButton) view.findViewById(R.id.move_all_left);
            holder.move_right = (ImageButton) view.findViewById(R.id.move_right);
            holder.move_all_right = (ImageButton) view.findViewById(R.id.move_all_right);
            view.setTag(holder);
        }
        final Photo photo = getItem(position);
        new AQuery(view).id(holder.photo).image(photo.squareUrl);
        holder.edit_overlay.setVisibility(editable ? View.VISIBLE : View.GONE);
        holder.move_left.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        holder.move_all_left.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
        holder.move_right.setVisibility(position < getCount() - 1 ? View.VISIBLE : View.GONE);
        holder.move_all_right.setVisibility(position < getCount() - 1 ? View.VISIBLE : View.GONE);
        holder.move_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoveLeft(position);
                }
            }
        });
        holder.move_all_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoveAllLeft(position);
                }
            }
        });
        holder.move_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoveRight(position);
                }
            }
        });
        holder.move_all_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMoveAllRight(position);
                }
            }
        });

        return view;
    }
}