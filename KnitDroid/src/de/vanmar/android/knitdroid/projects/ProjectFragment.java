package de.vanmar.android.knitdroid.projects;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.google.gson.JsonObject;
import com.meetme.android.horizontallistview.HorizontalListView;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.dialogs.ImageDialog;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryResultListener;
import de.vanmar.android.knitdroid.ravelry.dts.Project;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;

@EFragment(R.layout.fragment_project_detail)
public class ProjectFragment extends Fragment {

    public static final String ARG_PROJECT_ID = "projectId";
    protected SpiceManager spiceManager;
    private AdapterView.OnItemSelectedListener progressListener;

    public interface ProjectFragmentListener extends IRavelryActivity {

        void takePhoto();

        void pickImage();
    }

    @ViewById(R.id.name)
    TextView name;

    @ViewById(R.id.pattern_name)
    TextView patternName;

    @ViewById(R.id.status)
    TextView status;

    @ViewById(R.id.gallery)
    HorizontalListView gallery;

    @ViewById(R.id.progressBar)
    ProgressBar progressBar;

    @ViewById(R.id.progressSpinner)
    Spinner progressSpinner;

    @ViewById(R.id.started)
    public TextView started;

    @ViewById(R.id.completed)
    public TextView completed;

    @FragmentArg(ARG_PROJECT_ID)
    int projectId;

    private ProjectFragmentListener listener;

    private PhotoAdapter adapter;

    @Pref
    KnitdroidPrefs_ prefs;

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
        }

        adapter = new PhotoAdapter(getActivity());
        gallery.setAdapter(adapter);
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new ImageDialog(getActivity(), adapter.getItem(position).mediumUrl).show();
            }
        });

        progressListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int newProgress = position * 5;
                displayProgress(newProgress);
                JsonObject updateData = new JsonObject();
                updateData.addProperty("progress", newProgress);
                spiceManager.execute(new UpdateProjectRequest(prefs, getActivity(), projectId, updateData), new RavelryResultListener<ProjectResult>(listener) {
                    @Override
                    public void onRequestSuccess(ProjectResult projectResult) {
                        // nothing to do
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ProjectFragmentListener) {
            listener = (ProjectFragmentListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ProjectFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void onProjectSelected(final int projectId) {
        clearProject();
        if (projectId != 0) {
            spiceManager.execute(new GetProjectRequest(this.getActivity(), prefs, projectId), new ProjectsListener(listener));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());

        onProjectSelected(projectId);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @UiThread
    protected void clearProject() {
        progressSpinner.setOnItemSelectedListener(null);
        progressSpinner.setSelection(0);
        progressBar.setProgress(0);
        name.setText(null);
        patternName.setText(null);
        status.setText(null);
        status.setText(null);
        started.setText(null);
        completed.setText(null);
        adapter.clear();
    }

    @UiThread
    protected void displayProject(final ProjectResult projectResult) {
        Project project = projectResult.project;
        getActivity().setTitle(project.name);
        name.setText(project.name);
        patternName.setText(project.patternName);
        status.setText(project.status);
        started.setText(getCompletedDateText(project.started, project.startedDaySet));
        completed.setText(getCompletedDateText(project.completed, project.completedDaySet));
        adapter.clear();
        adapter.addAll(project.photos);
        displayProgress(project.progress);
    }

    private String getCompletedDateText(Date date, boolean withDay) {
        if (date == null) {
            return getActivity().getString(R.string.date_unknown);
        }
        DateFormat dateFormat;
        if (withDay) {
            dateFormat = SimpleDateFormat.getDateInstance();
        } else {
            dateFormat = new SimpleDateFormat("MMMM yyyy");
        }
        return dateFormat.format(date);
    }

    private void displayProgress(int progress) {
        progressBar.setProgress(progress);
        progressSpinner.setOnItemSelectedListener(null);
        progressSpinner.setSelection(progress / 5);
        progressSpinner.setOnItemSelectedListener(progressListener);
    }

    @Click(R.id.addPhoto)
    public void onAddPhotoClicked() {
        listener.pickImage();
    }

    @Click(R.id.takePhoto)
    public void onTakePhotoClicked() {
        listener.takePhoto();
    }

    public void uploadPhotoToProject(final Uri photoUri) {
        spiceManager.execute(new PhotoUploadRequest(getActivity(), prefs, photoUri, projectId), new RequestListener<String>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                AQUtility.report(spiceException);
            }

            @Override
            public void onRequestSuccess(String s) {
                onPhotoUploadSuccess();
            }
        });
    }

    @UiThread
    public void onPhotoUploadSuccess() {
        Toast.makeText(getActivity(), getActivity().getString(R.string.upload_successful), Toast.LENGTH_LONG).show();
        onProjectSelected(projectId);
    }

    @Click(R.id.progressBar)
    public void onProgressBarClicked() {
        progressSpinner.performClick();
    }

    class ProjectsListener extends RavelryResultListener<ProjectResult> {

        protected ProjectsListener(IRavelryActivity activity) {
            super(activity);
        }

        @Override
        public void onRequestSuccess(ProjectResult result) {
            displayProject(result);
        }
    }
}