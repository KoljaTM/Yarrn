package de.vanmar.android.knitdroid.favorites;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.Collection;

import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.dts.BookmarkShort;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectShort;

public abstract class FavoritesAdapter extends ArrayAdapter<BookmarkShort> {
    private final Activity context;

    private class ViewHolder {
        private ImageView thumb;
        private TextView name;
        private TextView patternName;
        private LinearLayout progress;
    }

    public FavoritesAdapter(final Activity context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public void addAll(Collection<? extends BookmarkShort> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            setNotifyOnChange(false);
            clear();
            for (BookmarkShort favorite : collection) {
                add(favorite);
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
                    R.layout.projectlist_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.patternName = (TextView) view
                    .findViewById(R.id.pattern_name);
            holder.progress = (LinearLayout) view.findViewById(R.id.progress);
            holder.thumb = (ImageView) view.findViewById(R.id.thumb);
            view.setTag(holder);
        }
        final BookmarkShort favorite = getItem(position);
        final ProjectShort project = favorite.project;

        holder.name.setText(project.name);
        holder.patternName.setText(project.patternName);
        holder.progress.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, project.progress));

        String imageUrl = null;
        if (project.firstPhoto != null) {
            imageUrl = project.firstPhoto.squareUrl;
        }
        new AQuery(view).id(holder.thumb).image(imageUrl);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                onProjectClicked(project.id, favorite.project.user.username);
            }
        });

        return view;
    }

    protected abstract void onProjectClicked(int projectId, String username);
}
