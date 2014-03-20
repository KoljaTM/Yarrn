package de.vanmar.android.yarrn.stashes;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.LinkedList;
import java.util.List;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.PagingListFragment;
import de.vanmar.android.yarrn.components.SearchCriteria;
import de.vanmar.android.yarrn.components.SearchCriteriaDialog;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.dts.StashSearchResult;
import de.vanmar.android.yarrn.ravelry.dts.StashShort;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.SearchStashesRequest;

@EFragment(R.layout.fragment_search_stashes)
@OptionsMenu(R.menu.fragment_menu)
public class StashSearchFragment extends PagingListFragment<StashSearchResult, StashShort> {

    private SearchCriteria searchCriteria;
    private TextView searchCriteriaText;

    public interface StashSearchFragmentListener extends IRavelryActivity {
        void onStashSelected(int stashId, String username);
    }

    @SystemService
    InputMethodManager inputMethodManager;

    @ViewById(R.id.stashlist)
    ListView stashlist;

    @ViewById(R.id.query)
    EditText query;

    @Pref
    YarrnPrefs_ prefs;

    private StashesAdapter adapter;

    private StashSearchFragmentListener listener;

    @AfterViews
    @Override
    public void afterViews() {
        super.afterViews();
        adapter = new StashesAdapter(getActivity()) {

            @Override
            protected void onStashClicked(final StashShort stash) {
                listener.onStashSelected(stash.id, stash.user.username);
            }
        };
        if (searchCriteriaText == null) {
            searchCriteriaText = new TextView(getActivity());
            stashlist.addHeaderView(searchCriteriaText);
        }
        stashlist.setAdapter(adapter);

        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event == null
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    startSearch();
                }
                return true;
            }
        });
    }

    private void startSearch() {
        adapter.clear();
        inputMethodManager.hideSoftInputFromWindow(
                query.getWindowToken(), 0);
        loadData(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.search_stashes_title);
        updateSearchCriteriaDescription();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof StashSearchFragmentListener) {
            listener = (StashSearchFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement StashSearchFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        super.menuRefresh();
    }

    @Click(R.id.search_button)
    public void onSearchButtonClicked() {
        startSearch();
    }

    @Click(R.id.add_search_criteria)
    public void onAddSearchCriteriaClicked() {
        final SearchCriteriaDialog searchCriteriaDialog = new SearchCriteriaDialog(getActivity(), SearchCriteria.SearchContext.STASH, prefs);
        searchCriteriaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                StashSearchFragment.this.searchCriteria = searchCriteriaDialog.getSearchCriteria();
                updateSearchCriteriaDescription();
                startSearch();
            }
        });
        searchCriteriaDialog.show();
    }

    private void updateSearchCriteriaDescription() {
        if (searchCriteria == null) {
            searchCriteriaText.setText("");
            searchCriteriaText.setVisibility(View.GONE);
        } else {
            searchCriteriaText.setText(searchCriteria.getDescription());
            searchCriteriaText.setVisibility(View.VISIBLE);
        }
    }

    protected void displayResult(final StashSearchResult result) {
        super.displayResult(result);
    }

    @Override
    protected ListView getListView() {
        return stashlist;
    }

    @Override
    protected YarrnAdapter<StashShort> getAdapter() {
        return adapter;
    }

    @Override
    protected AbstractRavelryGetRequest<StashSearchResult> getRequest(int page) {
        List<SearchCriteria> searchCriteriaList = new LinkedList<SearchCriteria>();
        if (query.getText().length() > 0) {
            String queryText = query.getText().toString();
            searchCriteriaList.add(new SearchCriteria("query", queryText, "\"" + queryText + "\""));
        }
        if (searchCriteria != null) {
            searchCriteriaList.add(searchCriteria);
        }
        return new SearchStashesRequest(this.getActivity().getApplication(), prefs, searchCriteriaList, page, PAGE_SIZE);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}