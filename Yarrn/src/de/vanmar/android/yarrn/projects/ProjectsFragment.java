package de.vanmar.android.yarrn.projects;

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
import de.vanmar.android.yarrn.ravelry.dts.ProjectShort;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListProjectsRequest;

@EFragment(R.layout.fragment_projects)
@OptionsMenu(R.menu.fragment_menu)
public class ProjectsFragment extends SherlockFragment {

    private static final int PAGE_SIZE = 25;
    protected SpiceManager spiceManager;
    private Paginator paginator;
    private boolean isLoading = false;
    private View listFooter;

    public interface ProjectsFragmentListener extends IRavelryActivity {
        /**
         * Project with projectId was selected, 0 if no project selected
         */
        void onProjectSelected(int projectId, String username);
    }

    @ViewById(R.id.projectlist)
    ListView projectlist;

    @ViewById(R.id.sort)
    Spinner sort;

    @ViewById(R.id.sort_reverse)
    CheckBox sortReverse;

    @Pref
    YarrnPrefs_ prefs;

    private ProjectsAdapter adapter;

    private ProjectsFragmentListener listener;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(YarrnSpiceService.class);
        }
        adapter = new ProjectsAdapter(getActivity()) {

            @Override
            protected void onProjectClicked(final ProjectShort project) {
                listener.onProjectSelected(project.id, prefs.username().get());
            }

        };
        if (listFooter == null) {
            listFooter = getActivity().getLayoutInflater().inflate(R.layout.loading_indicator, projectlist, false);
            projectlist.addFooterView(listFooter);
        }
        projectlist.setAdapter(adapter);
        projectlist.setOnScrollListener(new ProjectsScrollListener());

        sort.setOnItemSelectedListener(null);
        sort.setSelection(prefs.projectSort().get(), false);
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
        sortReverse.setChecked(prefs.projectSortReverse().get());
        sortReverse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                applySort();
            }
        });
    }

    private void applySort() {
        prefs.projectSort().put(sort.getSelectedItemPosition());
        prefs.projectSortReverse().put(sortReverse.isChecked());
        loadProjects(1);
    }

    @UiThread
    protected void displayProjects(final ProjectsResult result) {
        if (result.paginator.page == 1) {
            adapter.clear();
        }
        adapter.addAll(result.projects);
        getActivity().setTitle(R.string.my_projects_title);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ProjectsFragmentListener) {
            listener = (ProjectsFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ProjectsFragmentListener");
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
        loadProjects(1);
    }

    void loadProjects(int page) {
        loadingStarted();
        ListProjectsRequest request = new ListProjectsRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<ProjectsResult>(ProjectsFragment.this.listener) {
            @Override
            public void onRequestSuccess(ProjectsResult projectsResult) {
                displayProjects(projectsResult);
                ProjectsFragment.this.paginator = projectsResult.paginator;
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
        loadProjects(1);
    }

    private class ProjectsScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!isLoading && firstVisibleItem + visibleItemCount >= totalItemCount) {
                if (paginator != null && paginator.page < paginator.pageCount) {
                    loadProjects(paginator.page + 1);
                }
            }
        }
    }
}