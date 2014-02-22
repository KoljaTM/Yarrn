package de.vanmar.android.yarrn.stashes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.YarrnSpiceService;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.RavelryResultListener;
import de.vanmar.android.yarrn.ravelry.dts.Paginator;
import de.vanmar.android.yarrn.ravelry.dts.StashShort;
import de.vanmar.android.yarrn.ravelry.dts.StashesResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListStashesRequest;

@EFragment(R.layout.fragment_stashes)
@OptionsMenu(R.menu.stashes_menu)
public class StashesFragment extends SherlockFragment {

    private static final int PAGE_SIZE = 25;
    protected SpiceManager spiceManager;
    private Paginator paginator;
    private boolean isLoading = false;
    private View listFooter;

    public interface StashesFragmentListener extends IRavelryActivity {
        /**
         * Stash with stashId was selected, 0 if no stash selected
         */
        void onStashSelected(int stashId, String username);
    }

    @ViewById(R.id.stashlist)
    ListView stashlist;

    @ViewById(R.id.sort)
    Spinner sort;

    @ViewById(R.id.sort_reverse)
    CheckBox sortReverse;

    @Pref
    YarrnPrefs_ prefs;

    private StashesAdapter adapter;

    private StashesFragmentListener listener;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(YarrnSpiceService.class);
        }
        adapter = new StashesAdapter(getActivity()) {

            @Override
            protected void onStashClicked(final StashShort stash) {
                listener.onStashSelected(stash.id, prefs.username().get());
            }

        };
        if (listFooter == null) {
            listFooter = getActivity().getLayoutInflater().inflate(R.layout.loading_indicator, stashlist, false);
            stashlist.addFooterView(listFooter);
        }
        stashlist.setAdapter(adapter);
        stashlist.setOnScrollListener(new StashesScrollListener());


        sort.setOnItemSelectedListener(null);
        sort.setSelection(prefs.stashSort().get(), false);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySort();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sortReverse.setOnCheckedChangeListener(null);
        sortReverse.setChecked(prefs.stashSortReverse().get());
        sortReverse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applySort();
            }
        });
    }

    private void applySort() {
        prefs.stashSort().put(sort.getSelectedItemPosition());
        prefs.stashSortReverse().put(sortReverse.isChecked());
        loadStashes(1);
    }

    @UiThread
    protected void displayStashes(final StashesResult result) {
        if (result.paginator.page == 1) {
            adapter.clear();
        }
        adapter.addAll(result.stashes);
        getActivity().setTitle(R.string.my_stashes_title);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof StashesFragmentListener) {
            listener = (StashesFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement StashesFragmentListener");
        }
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
        loadStashes(1);
    }

    void loadStashes(int page) {
        loadingStarted();
        ListStashesRequest request = new ListStashesRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<StashesResult>(StashesFragment.this.listener) {
            @Override
            public void onRequestSuccess(StashesResult stashesResult) {
                displayStashes(stashesResult);
                StashesFragment.this.paginator = stashesResult.paginator;
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
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        loadStashes(1);
    }

    private class StashesScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!isLoading && firstVisibleItem + visibleItemCount >= totalItemCount) {
                if (paginator != null && paginator.page < paginator.pageCount) {
                    loadStashes(paginator.page + 1);
                }
            }
        }
    }
}