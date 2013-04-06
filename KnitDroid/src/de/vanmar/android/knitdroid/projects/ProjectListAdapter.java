package de.vanmar.android.knitdroid.projects;

import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;

import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.util.JSONAdapter;

public abstract class ProjectListAdapter extends JSONAdapter {

	private class ViewHolder {
		private ImageView thumb;
		private TextView name;
		private TextView patternName;
		private ProgressBar progress;
	}

	private final Activity context;

	public ProjectListAdapter(final Activity context) {
		this.context = context;
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
					R.layout.projectlist_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.patternName = (TextView) view.findViewById(R.id.patternName);
			holder.progress = (ProgressBar) view.findViewById(R.id.progress);
			holder.thumb = (ImageView) view.findViewById(R.id.thumb);
			view.setTag(holder);
		}
		final JSONObject projectJson = getObject(position);
		holder.name.setText(projectJson.optString("name"));
		holder.patternName.setText(projectJson.optString("pattern_name"));
		holder.progress.setProgress(projectJson.optInt("progress"));
		final JSONObject photo = projectJson.optJSONObject("first_photo");
		String imageUrl = null;
		if (photo != null) {
			imageUrl = photo.optString("square_url");
		}
		new AQuery(view).id(holder.thumb).image(imageUrl);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				onProjectClicked(projectJson);
			}
		});

		return view;
	}

	protected abstract void onProjectClicked(JSONObject projectJson);
}