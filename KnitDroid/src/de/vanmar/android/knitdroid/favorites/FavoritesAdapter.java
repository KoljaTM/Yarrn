package de.vanmar.android.knitdroid.favorites;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.Collection;

import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.dts.BookmarkShort;
import de.vanmar.android.knitdroid.ravelry.dts.Favorite;

public abstract class FavoritesAdapter extends ArrayAdapter<BookmarkShort> {
    private final Activity context;

    private class ViewHolder {
        private ImageView thumb;
        private TextView name;
        private TextView patternName;
        private TextView comment;
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
                    R.layout.favoritelist_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.patternName = (TextView) view
                    .findViewById(R.id.pattern_name);
            holder.comment = (TextView) view.findViewById(R.id.comment);
            holder.thumb = (ImageView) view.findViewById(R.id.thumb);
            view.setTag(holder);
        }
        final BookmarkShort bookmarkShort = getItem(position);
        final Favorite favorite = bookmarkShort.favorite;

        holder.name.setText(favorite.name);
        holder.patternName.setText(favorite.patternName == null ? null : '(' + favorite.patternName + ')');
        holder.comment.setText(bookmarkShort.comment);

        String imageUrl = null;
        if (favorite.firstPhoto != null) {
            imageUrl = favorite.firstPhoto.squareUrl;
        }
        new AQuery(view).id(holder.thumb).image(imageUrl);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (BookmarkShort.PROJECT.equals(bookmarkShort.type)) {
                    onProjectClicked(favorite.id, favorite.user.username);
                } else if (BookmarkShort.PATTERN.equals(bookmarkShort.type)) {
                    onPatternClicked(favorite.id);
                }
            }
        });

        return view;
    }

    protected abstract void onProjectClicked(int projectId, String username);

    protected abstract void onPatternClicked(int patternId);
}
