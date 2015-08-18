package de.vanmar.android.yarrn.stashes;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.Collection;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.ravelry.dts.StashShort;

public abstract class StashesAdapter extends ArrayAdapter<StashShort> implements YarrnAdapter<StashShort> {
    private final Activity context;

    private class ViewHolder {
        private ImageView thumb;
        private TextView name;
        private TextView location;
    }

    public StashesAdapter(final Activity context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public void addAllItems(Collection<? extends StashShort> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            setNotifyOnChange(false);
            clear();
            for (StashShort stash : collection) {
                add(stash);
            }
            // this sets notifyOnChange to true, regardless of former state
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        final ViewHolder holder;
        if (convertView != null) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = context.getLayoutInflater().inflate(
                    R.layout.stashlist_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.location = (TextView) view
                    .findViewById(R.id.location);
            holder.thumb = (ImageView) view.findViewById(R.id.thumb);
            view.setTag(holder);
        }
        final StashShort stash = getItem(position);

        holder.name.setText(stash.name);
        holder.location.setText(stash.location);

        String imageUrl = null;
        if (stash.firstPhoto != null) {
            imageUrl = stash.firstPhoto.squareUrl;
        }
        new AQuery(view).id(holder.thumb).image(imageUrl);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                onStashClicked(stash);
            }
        });

        return view;
    }

    protected abstract void onStashClicked(StashShort project);
}
