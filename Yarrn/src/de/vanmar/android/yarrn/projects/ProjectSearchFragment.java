package de.vanmar.android.yarrn.projects;

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
import de.vanmar.android.yarrn.ravelry.dts.ProjectShort;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.SearchProjectsRequest;

@EFragment(R.layout.fragment_search_projects)
@OptionsMenu(R.menu.fragment_menu)
public class ProjectSearchFragment extends PagingListFragment<ProjectsResult, ProjectShort> {

    private SearchCriteria searchCriteria;
    private TextView searchCriteriaText;

    public interface ProjectSearchFragmentListener extends IRavelryActivity {
        /**
         * Project with projectId was selected, 0 if no project selected
         */
        void onProjectSelected(int projectId, String username);
    }

    @SystemService
    InputMethodManager inputMethodManager;

    @ViewById(R.id.projectlist)
    ListView projectlist;

    @ViewById(R.id.query)
    EditText query;

    @Pref
    YarrnPrefs_ prefs;

    private ProjectsAdapter adapter;

    private ProjectSearchFragmentListener listener;

    @AfterViews
    @Override
    public void afterViews() {
        super.afterViews();
        adapter = new ProjectsAdapter(getActivity()) {

            @Override
            protected void onProjectClicked(final ProjectShort project) {
                listener.onProjectSelected(project.id, prefs.username().get());
            }

        };
        if (searchCriteriaText == null) {
            searchCriteriaText = new TextView(getActivity());
            projectlist.addHeaderView(searchCriteriaText);
        }
        projectlist.setAdapter(adapter);

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
        getActivity().setTitle(R.string.search_projects_title);
        updateSearchCriteriaDescription();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ProjectSearchFragmentListener) {
            listener = (ProjectSearchFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ProjectSearchFragmentListener");
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
        final SearchCriteriaDialog searchCriteriaDialog = new SearchCriteriaDialog(getActivity(), prefs);
        searchCriteriaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ProjectSearchFragment.this.searchCriteria = searchCriteriaDialog.getSearchCriteria();
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

    protected void displayResult(final ProjectsResult result) {
        super.displayResult(result);
    }

    @Override
    protected ListView getListView() {
        return projectlist;
    }

    @Override
    protected YarrnAdapter<ProjectShort> getAdapter() {
        return adapter;
    }

    @Override
    protected AbstractRavelryGetRequest<ProjectsResult> getRequest(int page) {
        List<SearchCriteria> searchCriteriaList = new LinkedList<SearchCriteria>();
        if (query.getText().length() > 0) {
            String queryText = query.getText().toString();
            searchCriteriaList.add(new SearchCriteria("query", queryText, "\"" + queryText + "\""));
        }
        if (searchCriteria != null) {
            searchCriteriaList.add(searchCriteria);
        }
        return new SearchProjectsRequest(this.getActivity().getApplication(), prefs, searchCriteriaList, page, PAGE_SIZE);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}