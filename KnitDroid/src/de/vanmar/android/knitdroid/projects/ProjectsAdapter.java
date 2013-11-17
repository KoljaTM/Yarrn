package de.vanmar.android.knitdroid.projects;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectShort;

public abstract class ProjectsAdapter extends ArrayAdapter<ProjectShort> {
	private final Activity context;

	private class ViewHolder {
		private ImageView thumb;
		private TextView name;
		private TextView patternName;
		private LinearLayout progress;
	}

	public ProjectsAdapter(final Activity context) {
		super(context, 0);
		this.context = context;
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
		final ProjectShort project = getItem(position);

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
				onProjectClicked(project);
			}
		});

		return view;
	}

	protected abstract void onProjectClicked(ProjectShort project);
}
