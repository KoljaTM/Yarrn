package de.vanmar.android.yarrn.projects;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import de.vanmar.android.yarrn.FragmentFactory;
import de.vanmar.android.yarrn.MainActivity;
import de.vanmar.android.yarrn.MainActivity_;
import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.requests.ListProjectsRequest;
import de.vanmar.android.yarrn.util.MyRobolectricTestRunner;
import de.vanmar.android.yarrn.util.TestUtil;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MyRobolectricTestRunner.class)
public class ProjectsFragmentUnitTest {

    @Mock
    private FragmentFactory fragmentFactory;
    @Mock
    private SpiceManager spiceManager;

    @Captor
    private ArgumentCaptor<ListProjectsRequest> request;

    private ProjectsFragment projectsFragment;
    private MainActivity activity;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        TestUtil.mockBackgroundExecutor();
        projectsFragment = new ProjectsFragment_();
        projectsFragment.spiceManager = this.spiceManager;
        ActivityController<MainActivity_> activityController = Robolectric.buildActivity(MainActivity_.class).create();
        activity = activityController.get();
        projectsFragment.prefs = activity.prefs;
        TestUtil.mockValidUser(activity.prefs);
        activity.fragmentFactory = this.fragmentFactory;
        given(fragmentFactory.getProjectsFragment()).willReturn(projectsFragment);
        activityController.start().resume();
    }

    @Test
    public void shouldLoadAndDisplayList() {
        // when (starting)

        // then
        verify(spiceManager).execute(any(SpiceRequest.class), anyString(), anyInt(), any(RequestListener.class));
    }

    @Test
    public void shouldAcceptSortOrders() {
        // given
        reset(spiceManager);
        assertTrue(activity.getResources().getStringArray(R.array.project_sort_option_values)[2].equals("happiness"));

        // when
        projectsFragment.sort.setSelection(2);
        projectsFragment.sort.getOnItemSelectedListener().onItemSelected(projectsFragment.sort, null, 2, 0);

        // then
        verify(spiceManager).execute(request.capture(), anyString(), anyInt(), any(RequestListener.class));
        assertTrue(request.getValue().getCacheKey().toString().contains("sort=happiness"));

        // when
        reset(spiceManager);
        projectsFragment.sortReverse.setChecked(true);

        // then
        verify(spiceManager).execute(request.capture(), anyString(), anyInt(), any(RequestListener.class));
        assertTrue(request.getValue().getCacheKey().toString().contains("sort=happiness_"));
    }
}
