package de.vanmar.android.knitdroid.projects;

import android.content.Intent;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.util.ActivityController;

import de.vanmar.android.knitdroid.ravelry.GetAccessTokenActivity_;
import de.vanmar.android.knitdroid.ravelry.RavelryException;
import de.vanmar.android.knitdroid.ravelry.dts.Project;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;
import de.vanmar.android.knitdroid.util.TestUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(RobolectricTestRunner.class)
public class ProjectDetailActivityUnitTest {

    public static final int PROJECT_ID = 10014463;
    private ProjectDetailActivity activity;

    @Mock
    private SpiceManager spiceManager;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        TestUtil.mockBackgroundExecutor();
        ActivityController<ProjectDetailActivity_> projectDetailActivityController = Robolectric.buildActivity(ProjectDetailActivity_.class);
        projectDetailActivityController.create();
        activity = projectDetailActivityController.get();
        activity.projectFragment.spiceManager = this.spiceManager;
    }

    @Test
    public void shouldCallGetAccessTokenIfNoTokenKnown() throws Exception {
        // given
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                RequestListener<ProjectResult> listener = (RequestListener<ProjectResult>) invocationOnMock.getArguments()[1];
                listener.onRequestFailure(new SpiceException(new RavelryException(401)));
                return null;
            }
        }).when(spiceManager).execute(any(SpiceRequest.class), any(RequestListener.class));

        // when
        activity.onProjectSelected(PROJECT_ID);

        // then
        ShadowActivity.IntentForResult startedActivityForResult = Robolectric.shadowOf(activity).getNextStartedActivityForResult();
        assertNotNull("should start activity", startedActivityForResult);
        assertTrue(((Object) startedActivityForResult.intent).equals(new Intent(activity,
                GetAccessTokenActivity_.class)));
    }

    @Test
    public void shouldGetProjectDetails() throws Exception {
        // given
        mockSpiceCall(createProjectResult());

        // when
        activity.onProjectSelected(PROJECT_ID);

        // then
        assertThat(activity.projectFragment.name.getText().toString(),
                is("aqua diva"));
    }

    private void mockSpiceCall(final ProjectResult projectResult) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                RequestListener<ProjectResult> listener = (RequestListener<ProjectResult>) invocationOnMock.getArguments()[1];
                listener.onRequestSuccess(projectResult);
                return null;
            }
        }).when(spiceManager).execute(any(SpiceRequest.class), any(RequestListener.class));
    }

    private ProjectResult createProjectResult() {
        ProjectResult projectResult = new ProjectResult();
        Project project = new Project();
        project.name = "aqua diva";
        projectResult.project = project;
        return projectResult;
    }
}
