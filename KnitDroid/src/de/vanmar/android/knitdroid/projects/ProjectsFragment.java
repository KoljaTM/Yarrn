package de.vanmar.android.knitdroid.projects;

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
import de.vanmar.android.knitdroid.ravelry.dts.ProjectShort;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectsResult;

@EFragment(R.layout.fragment_projects)
public class ProjectsFragment extends Fragment {

    protected SpiceManager spiceManager;

    public interface ProjectsFragmentListener extends IRavelryActivity {
        /**
         * Project with projectId was selected, 0 if no project selected
         */
        void onProjectSelected(int projectId);
    }

    @ViewById(R.id.projectlist)
    ListView projectlist;

    @Pref
    KnitdroidPrefs_ prefs;

    private ProjectsAdapter adapter;

    private ProjectsFragmentListener listener;

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
        }
        adapter = new ProjectsAdapter(getActivity()) {

            @Override
            protected void onProjectClicked(final ProjectShort project) {
                listener.onProjectSelected(project.id);
            }

        };
        projectlist.setAdapter(adapter);
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
        spiceManager.shouldStop();
        super.onStop();
    }
}