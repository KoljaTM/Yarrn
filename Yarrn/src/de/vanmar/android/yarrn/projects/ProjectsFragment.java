package de.vanmar.android.yarrn.projects;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.YarrnAdapter;
import de.vanmar.android.yarrn.YarrnPrefs_;
import de.vanmar.android.yarrn.components.PagingListFragment;
import de.vanmar.android.yarrn.ravelry.IRavelryActivity;
import de.vanmar.android.yarrn.ravelry.dts.ProjectShort;
import de.vanmar.android.yarrn.ravelry.dts.ProjectsResult;
import de.vanmar.android.yarrn.requests.AbstractRavelryGetRequest;
import de.vanmar.android.yarrn.requests.ListProjectsRequest;

@EFragment(R.layout.fragment_projects)
@OptionsMenu(R.menu.fragment_menu)
public class ProjectsFragment extends PagingListFragment<ProjectsResult, ProjectShort> {

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

    private void applySort() {
        prefs.projectSort().put(sort.getSelectedItemPosition());
        prefs.projectSortReverse().put(sortReverse.isChecked());
        loadData(1);
    }


    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        super.menuRefresh();
    }

    protected void displayResult(final ProjectsResult result) {
        super.displayResult(result);
        getActivity().setTitle(R.string.my_projects_title);
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
        return new ListProjectsRequest(this.getActivity().getApplication(), prefs, page, PAGE_SIZE);
    }

    @Override
    protected IRavelryActivity getRavelryActivity() {
        return listener;
    }
}