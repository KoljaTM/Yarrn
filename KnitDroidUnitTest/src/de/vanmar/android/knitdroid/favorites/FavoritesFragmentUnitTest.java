package de.vanmar.android.knitdroid.favorites;

import android.view.inputmethod.EditorInfo;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.listener.RequestListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import de.vanmar.android.knitdroid.FragmentFactory;
import de.vanmar.android.knitdroid.MainActivity;
import de.vanmar.android.knitdroid.MainActivity_;
import de.vanmar.android.knitdroid.projects.ProjectsFragment;
import de.vanmar.android.knitdroid.util.TestUtil;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
public class FavoritesFragmentUnitTest {

    @Mock
    private FragmentFactory fragmentFactory;
    @Mock
    private SpiceManager spiceManager;
    @Mock
    private ProjectsFragment projectsFragment;

    @Captor
    private ArgumentCaptor<ListFavoritesRequest> request;

    private FavoritesFragment favoritesFragment;
    private MainActivity activity;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        TestUtil.mockBackgroundExecutor();
        favoritesFragment = new FavoritesFragment_();
        favoritesFragment.spiceManager = this.spiceManager;
        ActivityController<MainActivity_> activityController = Robolectric.buildActivity(MainActivity_.class).create();
        activity = activityController.get();
        favoritesFragment.prefs = activity.prefs;
        TestUtil.mockValidUser(activity.prefs);
        activity.fragmentFactory = this.fragmentFactory;
        given(fragmentFactory.getProjectsFragment()).willReturn(projectsFragment);
        given(fragmentFactory.getFavoritesFragment()).willReturn(favoritesFragment);
        activityController.start().resume();
        activity.menuMyFavoritesClicked();
    }

    @Test
    public void shouldLoadAndDisplayList() {
        // when (starting)

        // then
        verify(spiceManager).execute(any(ListFavoritesRequest.class), anyString(), anyInt(), any(RequestListener.class));
    }

    @Test
    public void shouldSearchForQuery() {
        // given
        reset(spiceManager);

        // when
        favoritesFragment.query.setText("Doctor");
        favoritesFragment.query.onEditorAction(EditorInfo.IME_ACTION_SEARCH);

        // then
        verify(spiceManager).execute(request.capture(), anyString(), anyInt(), any(RequestListener.class));
        request.getValue().getCacheKey().toString().endsWith("query=Doctor");

        // when
        reset(spiceManager);
        favoritesFragment.searchOptions.setSelection(0);
        favoritesFragment.searchOptions.getOnItemSelectedListener().onItemSelected(favoritesFragment.searchOptions, null, 0, 0);

        // then
        verify(spiceManager).execute(request.capture(), anyString(), anyInt(), any(RequestListener.class));
        request.getValue().getCacheKey().toString().endsWith("tag=Doctor");
    }
}
