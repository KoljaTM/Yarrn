package de.vanmar.android.knitdroid.projects;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;

import static org.junit.Assert.assertNotNull;
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
        ShadowActivity.IntentForResult startedActivityForResult = Robolectric.shadowOf(activity).getNextStartedActivityForResult();
        assertNotNull("should start activity", startedActivityForResult);
        assertTrue(((Object) startedActivityForResult.intent).equals(new Intent(activity,
                GetAccessTokenActivity_.class)));
    }
}
