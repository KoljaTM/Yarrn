package de.vanmar.android.yarrn.components;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.YarrnSpiceService;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.RavelryResultListener;
import de.vanmar.android.yarrn.ravelry.dts.Paginator;
import de.vanmar.android.yarrn.ravelry.dts.Paging;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;

/**
 * Created by Kolja on 23.02.14.
 */
public abstract class PagingListFragment<RESULT extends Paging<ITEM>, ITEM> extends SherlockFragment {
    protected static final int PAGE_SIZE = 25;
    public SpiceManager spiceManager; // public for testing
    private Paginator paginator;
    private boolean isLoading = false;
    private View listFooter;

    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(YarrnSpiceService.class);
        }
        ListView listView = getListView();
        if (listFooter == null) {
            listFooter = getActivity().getLayoutInflater().inflate(R.layout.loading_indicator, listView, false);
            listView.addFooterView(listFooter);
        }
        listView.setOnScrollListener(new ScrollListener());
    }

    protected abstract ListView getListView();

    protected void displayResult(final RESULT result) {
        YarrnAdapter<ITEM> adapter = getAdapter();
        if (result.getPaginator().page == 1) {
            adapter.clear();
        }
        adapter.addAllItems(result.getItems());
    }

    protected abstract YarrnAdapter<ITEM> getAdapter();

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(1);
    }

    protected void loadData(int page) {
        loadingStarted();
        AbstractRavelryGetRequest<RESULT> request = getRequest(page);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<RESULT>(getRavelryActivity()) {
            @Override
            public void onRequestSuccess(RESULT result) {
                displayResult(result);
                PagingListFragment.this.paginator = result.getPaginator();
                loadingFinished();
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                loadingFinished();
                super.onRequestFailure(spiceException);
            }
        });
    }

    protected abstract IRavelryActivity getRavelryActivity();

    protected abstract AbstractRavelryGetRequest<RESULT> getRequest(int page);

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

    public void menuRefresh() {
        loadData(1);
    }

    private class ScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!isLoading && firstVisibleItem + visibleItemCount >= totalItemCount) {
                if (paginator != null && paginator.page < paginator.pageCount) {
                    loadData(paginator.page + 1);
                }
            }
        }
    }
}
