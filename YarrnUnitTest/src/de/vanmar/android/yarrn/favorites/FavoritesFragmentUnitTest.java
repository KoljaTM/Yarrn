package de.vanmar.android.yarrn.favorites;

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
import org.robolectric.util.ActivityController;

import de.vanmar.android.yarrn.FragmentFactory;
import de.vanmar.android.yarrn.MainActivity;
import de.vanmar.android.yarrn.MainActivity_;
import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.projects.ProjectsFragment;
import de.vanmar.android.yarrn.requests.ListFavoritesRequest;
import de.vanmar.android.yarrn.util.MyRobolectricTestRunner;
import de.vanmar.android.yarrn.util.TestUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

@RunWith(MyRobolectricTestRunner.class)
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

    @Test
    public void shouldEmptyQueryOnDelete() {
        // given
        favoritesFragment.query.setText("Doctor");

        // when
        favoritesFragment.getView().findViewById(R.id.delete).performClick();

        // then
        assertEquals("", favoritesFragment.query.getText().toString());
    }
}
