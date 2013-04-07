package de.vanmar.android.knitdroid;

import android.content.Intent;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.EActivity;

import de.vanmar.android.knitdroid.projects.ProjectsFragment.ProjectsFragmentListener;

@EActivity(resName = "activity_project_list")
public class ProjectListActivity extends AbstractRavelryActivity implements
		ProjectsFragmentListener {

	@Override
	public void onProjectSelected(final int projectId) {
		Toast.makeText(this, "Project " + projectId + " selected!",
				Toast.LENGTH_LONG).show();
		final Intent intent = new Intent(this, ProjectDetailActivity_.class);
		intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, projectId);
		startActivity(intent);
	}
}
