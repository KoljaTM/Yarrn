package de.vanmar.android.yarrn.patterns;

import java.util.Collection;

import com.androidquery.AQuery;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.ravelry.dts.PatternShort;

public abstract class PatternsAdapter extends ArrayAdapter<PatternShort> implements YarrnAdapter<PatternShort> {
    private final Activity context;

    private class ViewHolder {
        private ImageView thumb;
        private TextView name;
        private TextView authorName;
    }

    public PatternsAdapter(final Activity context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public void addAllItems(Collection<? extends PatternShort> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            setNotifyOnChange(false);
            clear();
            for (PatternShort pattern : collection) {
                add(pattern);
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
                    R.layout.patternlist_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.authorName = (TextView) view
                    .findViewById(R.id.author_name);
            holder.thumb = (ImageView) view.findViewById(R.id.thumb);
            view.setTag(holder);
        }
        final PatternShort pattern = getItem(position);

        holder.name.setText(pattern.name);
        holder.authorName.setText(pattern.patternAuthor == null ? null : pattern.patternAuthor.name);

        String imageUrl = null;
        if (pattern.firstPhoto != null) {
            imageUrl = pattern.firstPhoto.getSquareUrl();
        }
        new AQuery(view).id(holder.thumb).image(imageUrl);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                onPatternClicked(pattern.id);
            }
        });

        return view;
    }

    protected abstract void onPatternClicked(int patternId);
}
