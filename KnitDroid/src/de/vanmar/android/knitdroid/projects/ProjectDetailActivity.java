package de.vanmar.android.knitdroid.projects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vanmar.android.knitdroid.AbstractRavelryActivity;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.projects.ProjectFragment.ProjectFragmentListener;
import de.vanmar.android.knitdroid.util.RequestCode;
import de.vanmar.android.knitdroid.util.UiHelper;

@EActivity(resName = "activity_project_detail")
public class ProjectDetailActivity extends AbstractRavelryActivity implements
        ProjectFragmentListener {

    SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    public static final String EXTRA_PROJECT_ID = "ProjectDetailActivity.extra.project_id";

    private static final String JPEG_FILE_PREFIX = "IMG_";

    private static final String JPEG_FILE_SUFFIX = ".jpg";

    @FragmentById(R.id.projectFragment)
    ProjectFragment projectFragment;

    @Extra(EXTRA_PROJECT_ID)
    protected int projectId;

    @NonConfigurationInstance
    Uri photoUri;

    @Bean
    UiHelper uiHelper;

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(this);

    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        onProjectSelected(projectId);
    }

    public void onProjectSelected(final int projectId) {
        projectFragment.onProjectSelected(projectId);
    }

    @Override
    public void pickImage() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RequestCode.REQUEST_CODE_GALLERY);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    public void takePhoto() {
        final File storageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Knitdroid");
        storageDir.mkdirs();

        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        final String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File image;
        try {
            image = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX,
                    storageDir);
            final Intent takePictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            photoUri = Uri.fromFile(image);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent,
                    RequestCode.REQUEST_CODE_CAMERA);
        } catch (final IOException e) {
            AQUtility.report(e);
        }
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_CAMERA)
    void onCameraResult(final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK && photoUri != null) {
            uploadPhotoToProject(photoUri, projectId);
            photoUri = null;
        }
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_GALLERY)
    void onGalleryResult(final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            uploadPhotoToProject(data.getData(), projectId);
        }
    }

    public void uploadPhotoToProject(final Uri photoUri, final int projectId) {
        spiceManager.execute(new PhotoUploadRequest(this, prefs, photoUri, projectId), new RequestListener<String>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                uiHelper.displayError(spiceException);
            }

            @Override
            public void onRequestSuccess(String s) {
                onPhotoUploadSuccess();
            }
        });
    }

    @UiThread
    public void onPhotoUploadSuccess() {
        Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show();
    }
}
