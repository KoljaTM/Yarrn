package de.vanmar.android.knitdroid.favorites;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryResultListener;
import de.vanmar.android.knitdroid.ravelry.dts.FavoritesResult;

@EFragment(R.layout.fragment_favorites)
public class FavoritesFragment extends Fragment {

    protected SpiceManager spiceManager;

    public interface FavoritesFragmentListener extends IRavelryActivity {
        /**
         * Project with projectId was selected, 0 if no project selected
         */
        void onProjectSelected(int projectId, String username);

        /**
         * Pattern with patternId was selected, 0 if no pattern selected
         */
        void onPatternSelected(int patternId);
    }

    @ViewById(R.id.favoritelist)
    ListView favoritelist;

    @Pref
    KnitdroidPrefs_ prefs;

    private FavoritesAdapter adapter;

    private FavoritesFragmentListener listener;

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
        }
        adapter = new FavoritesAdapter(getActivity()) {

            @Override
            protected void onProjectClicked(final int id, final String username) {
                listener.onProjectSelected(id, username);
            }

            @Override
            protected void onPatternClicked(int patternId) {
                listener.onPatternSelected(patternId);
            }

        };
        favoritelist.setAdapter(adapter);
    }

    @UiThread
    protected void displayFavorites(final FavoritesResult result) {
        adapter.clear();
        adapter.addAll(result.favorites);
        getActivity().setTitle(R.string.my_favorites_title);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FavoritesFragmentListener) {
            listener = (FavoritesFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement FavoritesFragmentListener");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();

        ListFavoritesRequest request = new ListFavoritesRequest(this.getActivity().getApplication(), prefs);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<FavoritesResult>(FavoritesFragment.this.listener) {
            @Override
            public void onRequestSuccess(FavoritesResult favoritesResult) {
                displayFavorites(favoritesResult);
            }
        });
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }
}