package de.vanmar.android.knitdroid.favorites;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryResultListener;
import de.vanmar.android.knitdroid.ravelry.dts.FavoritesResult;
import de.vanmar.android.knitdroid.ravelry.dts.Paginator;
import de.vanmar.android.knitdroid.requests.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.requests.ListFavoritesRequest;

import static de.vanmar.android.knitdroid.requests.ListFavoritesRequest.SearchOption;

@EFragment(R.layout.fragment_favorites)
public class FavoritesFragment extends Fragment {

    public static final int PAGE_SIZE = 25;
    protected SpiceManager spiceManager;
    private Paginator paginator;
    private boolean isLoading = false;
    private View listFooter;

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

    @SystemService
    InputMethodManager inputMethodManager;

    @ViewById(R.id.favoritelist)
    ListView favoritelist;

    @ViewById(R.id.query)
    EditText query;

    @ViewById(R.id.search_options)
    Spinner searchOptions;

    @Pref
    KnitdroidPrefs_ prefs;

    private FavoritesAdapter adapter;

    private FavoritesFragmentListener listener;
    private String searchQuery = "";

    private SearchOption searchOption;

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
        listFooter = getActivity().getLayoutInflater().inflate(R.layout.loading_indicator, favoritelist, false);
        favoritelist.addFooterView(listFooter);
        favoritelist.setAdapter(adapter);
        favoritelist.setOnScrollListener(new FavoritesScrollListener());

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    setSearchQuery(query.getText().toString());
                    inputMethodManager.hideSoftInputFromWindow(query.getWindowToken(), 0);
                    query.clearFocus();
                }
                return true;
            }
        });
        searchOptions.setOnItemSelectedListener(null);
        searchOptions.setSelection(prefs.favoriteSearchOption().get());
        searchOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSearchOption(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSearchQuery(String newQuery) {
        if (!searchQuery.equals(newQuery)) {
            searchQuery = newQuery;
            requestFavorites(1);
        }
    }

    private void setSearchOption(int position) {
        SearchOption newOption = SearchOption.values()[position];
        if (searchOption != newOption) {
            searchOption = newOption;
            requestFavorites(1);
            prefs.favoriteSearchOption().put(position);
        }
    }

    @UiThread
    protected void displayFavorites(final FavoritesResult result) {
        if (result.paginator.page == 1) {
            adapter.clear();
        }
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

        requestFavorites(1);
    }

    private void requestFavorites(int page) {
        loadingStarted();
        ListFavoritesRequest request = new ListFavoritesRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE, searchQuery, searchOption);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<FavoritesResult>(FavoritesFragment.this.listener) {
            @Override
            public void onRequestSuccess(FavoritesResult favoritesResult) {
                displayFavorites(favoritesResult);
                FavoritesFragment.this.paginator = favoritesResult.paginator;
                loadingFinished();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                loadingFinished();
                super.onRequestFailure(spiceException);
            }
        });
    }


    private void loadingStarted() {
        ((TextView) listFooter.findViewById(R.id.loading_text)).setText(R.string.loading);
        listFooter.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
        listFooter.setVisibility(View.VISIBLE);
        isLoading = true;
    }

    private void loadingFinished() {
        ((TextView) listFooter.findViewById(R.id.loading_text)).setText(R.string.load_more);
        listFooter.findViewById(R.id.loading_progress).setVisibility(View.GONE);
        listFooter.setVisibility((paginator != null && paginator.page == paginator.lastPage) ? View.GONE : View.VISIBLE);
        isLoading = false;
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    private class FavoritesScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!isLoading && firstVisibleItem + visibleItemCount >= totalItemCount) {
                if (paginator != null && paginator.page < paginator.pageCount) {
                    requestFavorites(paginator.page + 1);
                }
            }
        }
    }
}