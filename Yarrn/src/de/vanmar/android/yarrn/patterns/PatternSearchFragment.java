package de.vanmar.android.yarrn.patterns;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.PagingListFragment;
import de.vanmar.android.yarrn.components.SearchCriteria;
import de.vanmar.android.yarrn.components.SearchCriteriaDialog;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.dts.PatternShort;
import de.vanmar.android.yarrn.ravelry.dts.PatternsResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.SearchPatternsRequest;

@EFragment(R.layout.fragment_search_patterns)
@OptionsMenu(R.menu.fragment_menu)
public class PatternSearchFragment extends PagingListFragment<PatternsResult, PatternShort> {

    private Map<SearchCriteria.SearchType, SearchCriteria> searchCriteria = new HashMap<SearchCriteria.SearchType, SearchCriteria>();
    private TextView searchCriteriaText;

    public interface PatternSearchFragmentListener extends IRavelryActivity {
        /**
         * Pattern with patternId was selected, 0 if no pattern selected
         */
        void onPatternSelected(int patternId);
    }

    @SystemService
    InputMethodManager inputMethodManager;

    @ViewById(R.id.patternlist)
    ListView patternlist;

    @ViewById(R.id.query)
    EditText query;

    @Pref
    YarrnPrefs_ prefs;

    private PatternsAdapter adapter;

    private PatternSearchFragmentListener listener;

    @AfterViews
    @Override
    public void afterViews() {
        super.afterViews();
        adapter = new PatternsAdapter(getActivity()) {

            @Override
            protected void onPatternClicked(int patternId) {
                listener.onPatternSelected(patternId);
            }

        };
        if (searchCriteriaText == null) {
            searchCriteriaText = new TextView(getActivity());
            patternlist.addHeaderView(searchCriteriaText);
        }
        patternlist.setAdapter(adapter);

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
        getActivity().setTitle(R.string.search_patterns_title);
        updateSearchCriteriaDescription();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PatternSearchFragmentListener) {
            listener = (PatternSearchFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement PatternSearchFragmentListener");
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
        final SearchCriteriaDialog searchCriteriaDialog = new SearchCriteriaDialog(getActivity(), SearchCriteria.SearchContext.PATTERN, prefs);
        searchCriteriaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SearchCriteria.SearchType type = searchCriteriaDialog.getSearchCriteriaType();
                SearchCriteria criteria = searchCriteriaDialog.getSearchCriteria();
                if (criteria == null) {
                    searchCriteria.remove(type);
                } else {
                    searchCriteria.put(type, criteria);
                }
                updateSearchCriteriaDescription();
                startSearch();
            }
        });
        searchCriteriaDialog.show();
    }

    private void updateSearchCriteriaDescription() {
        if (searchCriteria.isEmpty()) {
            searchCriteriaText.setText("");
            searchCriteriaText.setVisibility(View.GONE);
        } else {
            StringBuilder sb = new StringBuilder();
            for (SearchCriteria criteria : searchCriteria.values()) {
                sb.append(criteria.getDescription()).append(" ");
            }
            searchCriteriaText.setText(sb.toString());
            searchCriteriaText.setVisibility(View.VISIBLE);
        }
    }

    protected void displayResult(final PatternsResult result) {
        super.displayResult(result);
    }

    @Override
    protected ListView getListView() {
        return patternlist;
    }

    @Override
    protected YarrnAdapter<PatternShort> getAdapter() {
        return adapter;
    }

    @Override
    protected AbstractRavelryGetRequest<PatternsResult> getRequest(int page) {
        Collection<SearchCriteria> searchCriteriaList = searchCriteria.values();
        if (query.getText().length() > 0) {
            String queryText = query.getText().toString();
            searchCriteriaList.add(SearchCriteria.byQuery(queryText));
        }
        return new SearchPatternsRequest(this.getActivity().getApplication(), prefs, searchCriteriaList, page, PAGE_SIZE);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}