package de.vanmar.android.knitdroid;

import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.FragmentById;

import de.vanmar.android.knitdroid.projects.ProjectFragment;
import de.vanmar.android.knitdroid.projects.ProjectFragment.ProjectFragmentListener;

@EActivity(resName = "activity_project_detail")
public class ProjectDetailActivity extends AbstractRavelryActivity implements
		ProjectFragmentListener {

	public static final String EXTRA_PROJECT_ID = "ProjectDetailActivity.extra.project_id";

	@FragmentById(R.id.projectFragment)
	ProjectFragment projectFragment;

	@Extra(EXTRA_PROJECT_ID)
	protected int projectId;

	@Override
	protected void onResume() {
		super.onResume();

		onProjectSelected(projectId);
	}

	public void onProjectSelected(final int projectId) {
		Toast.makeText(this, "Project " + projectId + " selected!",
				Toast.LENGTH_LONG).show();
		projectFragment.onProjectSelected(projectId);
	}
}
