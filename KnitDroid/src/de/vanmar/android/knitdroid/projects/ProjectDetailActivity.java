package de.vanmar.android.knitdroid.projects;

import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.FragmentById;

import de.vanmar.android.knitdroid.AbstractRavelryActivity;
import de.vanmar.android.knitdroid.R;
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
		projectFragment.onProjectSelected(projectId);
	}
}
