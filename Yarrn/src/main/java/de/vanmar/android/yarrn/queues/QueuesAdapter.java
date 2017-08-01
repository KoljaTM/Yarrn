package de.vanmar.android.yarrn.queues;

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
import de.vanmar.android.yarrn.ravelry.dts.QueuedProjectSmall;

public abstract class QueuesAdapter extends ArrayAdapter<QueuedProjectSmall> implements YarrnAdapter<QueuedProjectSmall> {
    private final Activity context;

    private class ViewHolder {
        private ImageView thumb;
        private TextView name;
        private TextView note;
    }

    public QueuesAdapter(final Activity context) {
        super(context, 0);
        this.context = context;
    }

    @Override
    public void addAllItems(Collection<? extends QueuedProjectSmall> collection) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.addAll(collection);
        } else {
            setNotifyOnChange(false);
            clear();
            for (QueuedProjectSmall project : collection) {
                add(project);
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
                    R.layout.queuelist_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.note = (TextView) view
                    .findViewById(R.id.note);
            holder.thumb = (ImageView) view.findViewById(R.id.thumb);
            view.setTag(holder);
        }
        final QueuedProjectSmall project = getItem(position);

        holder.name.setText(project.name);
        holder.note.setText(project.notes);

        String imageUrl = null;
        if (project.bestPhoto != null) {
            imageUrl = project.bestPhoto.getSquareUrl();
        }
        new AQuery(view).id(holder.thumb).image(imageUrl);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                onProjectClicked(project);
            }
        });

        return view;
    }

    protected abstract void onProjectClicked(QueuedProjectSmall project);
}
