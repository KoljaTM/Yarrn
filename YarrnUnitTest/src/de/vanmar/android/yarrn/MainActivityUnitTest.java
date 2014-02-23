package de.vanmar.android.yarrn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import de.vanmar.android.yarrn.projects.ProjectFragment;
import de.vanmar.android.yarrn.projects.ProjectsFragment;
import de.vanmar.android.yarrn.util.MyRobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(MyRobolectricTestRunner.class)
public class MainActivityUnitTest {

    public static final String USERNAME = "Jillda";
    private MainActivity activity;

    @Mock
    private FragmentFactory fragmentFactory;
    @Mock
    private ProjectsFragment projectsFragment;
    @Mock
    private ProjectFragment projectFragment;
    public static final int PROJECT_ID = 17;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        ActivityController<MainActivity_> activityController = Robolectric.buildActivity(MainActivity_.class).attach().create();
        activity = activityController.get();
        activity.fragmentFactory = this.fragmentFactory;
        given(fragmentFactory.getProjectsFragment()).willReturn(projectsFragment);
        given(fragmentFactory.getProjectFragment()).willReturn(projectFragment);
        activityController.start().resume();
    }

    @Test
    public void shouldStartWithProjectList() {
        // then
        assertCorrectFragmentShown(MainActivity.PROJECTS_TAG, projectsFragment);
    }

    @Test
    public void shouldOpenProjectDetailFragment() throws Exception {
        // assume
        Bundle args = new Bundle();
        args.putInt(ProjectFragment.ARG_PROJECT_ID, PROJECT_ID);
        args.putString(ProjectFragment.ARG_USERNAME, USERNAME);

        // when
        activity.onProjectSelected(PROJECT_ID, USERNAME);

        // then
        verify(projectFragment).setArguments(args);
        verifyNoMoreInteractions(projectFragment);

        assertCorrectFragmentShown(MainActivity.PROJECT_DETAIL_TAG, projectFragment);
    }

    @Test
    public void shouldCallProjectListFromMenu() {
        // when
        activity.onProjectSelected(PROJECT_ID, USERNAME);
        activity.menuMyProjectsClicked();

        // then
        assertCorrectFragmentShown(MainActivity.PROJECTS_TAG, projectsFragment);

        // when
        activity.onBackPressed();

        // then
        assertTrue("activity finished", shadowOf(activity).isFinishing());
    }

    private void assertCorrectFragmentShown(String tag, Fragment fragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        String lastBackStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        assertEquals("Fragment was added", tag, lastBackStackEntry);
        assertEquals("Correct Fragment was added", fragment, fragmentManager.findFragmentByTag(lastBackStackEntry));
    }
}
