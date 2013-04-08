package de.vanmar.android.knitdroid.projects;

import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.matchers.StartedMatcher;

import android.content.Intent;

@RunWith(RobolectricTestRunner.class)
public class ProjectListActivityUnitTest {

	private ProjectListActivity activity;

	@Before
	public void prepare() {
		activity = new ProjectListActivity();
		activity.onCreate(null);
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
		assertThat(activity, new StartedMatcher(intent));
	}
}
