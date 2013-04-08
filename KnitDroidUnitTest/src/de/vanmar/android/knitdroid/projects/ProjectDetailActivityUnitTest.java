package de.vanmar.android.knitdroid.projects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.matchers.StartedMatcher;

import android.content.Intent;
import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.knitdroid.util.TestUtil;

@RunWith(RobolectricTestRunner.class)
public class ProjectDetailActivityUnitTest {

	private ProjectDetailActivity activity;

	@Before
	public void prepare() {
		TestUtil.mockBackgroundExecutor();
		activity = new ProjectDetailActivity_();
		activity.setIntent(new Intent());
		activity.onCreate(null);
	}

	@Test
	public void shouldCallGetAccessTokenIfNoTokenKnown() throws Exception {
		// assume
		final int projectId = 17;

		// when
		activity.onProjectSelected(projectId);

		// then
		final Intent intent = new Intent(activity,
				GetAccessTokenActivity_.class);
		assertThat(activity, new StartedMatcher(intent));
	}

	@Test
	public void shouldGetProjectDetails() throws Exception {
		// assume
		final int projectId = 17;

		// given
		activity.prefs.accessToken().put("token");

		// when
		activity.onProjectSelected(projectId);

		// then
		// TODO: add working mocks to put some project detail data in
		assertThat(activity.projectFragment.name.getText().toString(), is(""));
	}
}
