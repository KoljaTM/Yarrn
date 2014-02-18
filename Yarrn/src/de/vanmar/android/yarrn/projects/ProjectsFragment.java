package de.vanmar.android.yarrn.projects;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockFragment;
import com.octo.android.robospice.SpiceManager;

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
import de.vanmar.android.yarrn.ravelry.dts.ProjectShort;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListProjectsRequest;

@EFragment(R.layout.fragment_projects)
@OptionsMenu(R.menu.fragment_menu)
public class ProjectsFragment extends SherlockFragment {

    protected SpiceManager spiceManager;

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
        projectlist.setAdapter(adapter);

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
        loadProjects();
    }

    @UiThread
    protected void displayProjects(final ProjectsResult result) {
        adapter.clear();
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
        loadProjects();
    }

    void loadProjects() {
        ListProjectsRequest request = new ListProjectsRequest(this.getActivity().getApplication(), prefs);
        spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new RavelryResultListener<ProjectsResult>(ProjectsFragment.this.listener) {
            @Override
            public void onRequestSuccess(ProjectsResult projectsResult) {
                displayProjects(projectsResult);
            }
        });
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
        loadProjects();
    }
}