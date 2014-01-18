package de.vanmar.android.knitdroid.projects;

import android.content.Intent;

import org.androidannotations.annotations.EActivity;

import de.vanmar.android.knitdroid.AbstractRavelryActivity;
import de.vanmar.android.knitdroid.projects.ProjectsFragment.ProjectsFragmentListener;

@EActivity(resName = "activity_project_list")
public class ProjectListActivity extends AbstractRavelryActivity implements
		ProjectsFragmentListener {

	@Override
	public void onProjectSelected(final int projectId) {
		final Intent intent = new Intent(this, ProjectDetailActivity_.class);
		intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, projectId);
		startActivity(intent);
	}
}
