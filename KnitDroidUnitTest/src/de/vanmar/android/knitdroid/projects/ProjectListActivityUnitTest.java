package de.vanmar.android.knitdroid.projects;

import android.content.Intent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class ProjectListActivityUnitTest {

	private ProjectListActivity activity;

	@Before
	public void prepare() {
		ActivityController<ProjectListActivity> projectListActivityActivityController = Robolectric.buildActivity(ProjectListActivity.class);
		projectListActivityActivityController.create();
		activity = projectListActivityActivityController.get();
	}

	@Test
	public void shouldCallProjectActivity() throws Exception {
		// assume
		final int projectId = 17;

		// when
		activity.onProjectSelected(projectId);

		// then
		final Intent intent = new Intent(activity, ProjectDetailActivity_.class);
		intent.putExtra(ProjectDetailActivity.EXTRA_PROJECT_ID, projectId);
		assertTrue(((ShadowActivity.IntentForResult) Robolectric.shadowOf(activity).getNextStartedActivityForResult()).intent.equals(intent));
	}
}
