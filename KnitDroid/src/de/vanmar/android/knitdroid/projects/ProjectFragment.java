package de.vanmar.android.knitdroid.projects;

import android.app.Activity;
import android.net.Uri;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
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
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.components.ImageDialog;
import de.vanmar.android.knitdroid.components.ViewEditText;
import de.vanmar.android.knitdroid.ravelry.AbstractRavelryGetRequest;
import de.vanmar.android.knitdroid.ravelry.IRavelryActivity;
import de.vanmar.android.knitdroid.ravelry.RavelryResultListener;
import de.vanmar.android.knitdroid.ravelry.dts.Project;
import de.vanmar.android.knitdroid.ravelry.dts.ProjectResult;

@EFragment(R.layout.fragment_project_detail)
@OptionsMenu(R.menu.fragment_menu)
public class ProjectFragment extends SherlockFragment {

    public static final String ARG_PROJECT_ID = "projectId";
    public static final String ARG_USERNAME = "username";
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

    @ViewById(R.id.notes)
    public ViewEditText notes;

    @ViewById(R.id.rating)
    public RatingBar rating;

    @FragmentArg(ARG_PROJECT_ID)
    int projectId;

    @FragmentArg(ARG_USERNAME)
    String username;

    private ProjectFragmentListener listener;

    private PhotoAdapter adapter;

    @Pref
    KnitdroidPrefs_ prefs;

    @AfterViews
    public void afterViews() {
        if (spiceManager == null) {
            spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
        }
        notes.setOnSaveListener(new ViewEditText.OnSaveListener() {
            @Override
            public void onSave(ViewEditText view, Editable text) {
                JsonObject updateData = new JsonObject();
                updateData.addProperty("notes", text.toString());
                executeUpdate(updateData);
            }
        });

        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    int newRating = (int) Math.floor(rating - 1);
                    JsonObject updateData = new JsonObject();
                    updateData.addProperty("rating", newRating);
                    executeUpdate(updateData);
                }
            }
        });

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
                executeUpdate(updateData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        };
    }

    private void executeUpdate(JsonObject updateData) {
        spiceManager.execute(new UpdateProjectRequest(prefs, getActivity().getApplication(), projectId, updateData), new ProjectListener(listener));
        spiceManager.removeDataFromCache(ProjectResult.class, new GetProjectRequest(getActivity().getApplication(), prefs, projectId, prefs.username().get()).getCacheKey());
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

    public void onProjectSelected(final int projectId, final String username) {
        clearProject();
        if (projectId != 0) {
            GetProjectRequest request = new GetProjectRequest(this.getActivity().getApplication(), prefs, projectId, username);
            spiceManager.execute(request, request.getCacheKey(), AbstractRavelryGetRequest.CACHE_DURATION, new ProjectListener(listener));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this.getActivity());

        onProjectSelected(projectId, username);
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
        getView().setVisibility(View.GONE);
    }

    @UiThread
    protected void displayProject(final ProjectResult projectResult) {
        Project project = projectResult.project;
        getActivity().setTitle(project.name);
        name.setText(project.name);
        rating.setRating(project.rating + 1);
        patternName.setText(project.patternName);
        status.setText(project.status);
        started.setText(getCompletedDateText(project.started, project.startedDaySet));
        completed.setText(getCompletedDateText(project.completed, project.completedDaySet));
        notes.setBodyText(project.notes);
        adapter.clear();
        adapter.addAll(project.photos);
        displayProgress(project.progress);
        progressSpinner.setOnItemSelectedListener(null);
        getView().setVisibility(View.VISIBLE);
        progressSpinner.setOnItemSelectedListener(progressListener);
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
        progressSpinner.setSelection(progress / 5, false);
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
        Toast.makeText(getActivity(), getActivity().getString(R.string.upload_started), Toast.LENGTH_LONG).show();
        spiceManager.execute(new PhotoUploadRequest(getActivity().getApplication(), prefs, photoUri, projectId), new RequestListener<String>() {

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
        spiceManager.removeDataFromCache(ProjectResult.class, new GetProjectRequest(getActivity().getApplication(), prefs, projectId, prefs.username().get()).getCacheKey());
    }

    @Click(R.id.progressBar)
    public void onProgressBarClicked() {
        progressSpinner.performClick();
    }


    @OptionsItem(R.id.menu_refresh)
    public void menuRefresh() {
        onProjectSelected(projectId, username);
    }

    class ProjectListener extends RavelryResultListener<ProjectResult> {

        protected ProjectListener(IRavelryActivity activity) {
            super(activity);
        }

        @Override
        public void onRequestSuccess(ProjectResult result) {
            displayProject(result);
        }
    }
}