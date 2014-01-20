package de.vanmar.android.knitdroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.androidquery.util.AQUtility;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vanmar.android.knitdroid.projects.ProjectFragment;
import de.vanmar.android.knitdroid.projects.ProjectFragment_;
import de.vanmar.android.knitdroid.projects.ProjectsFragment;
import de.vanmar.android.knitdroid.projects.ProjectsFragment.ProjectsFragmentListener;
import de.vanmar.android.knitdroid.projects.ProjectsFragment_;
import de.vanmar.android.knitdroid.util.RequestCode;

@EActivity(resName = "activity_main")
@OptionsMenu(R.menu.main)
public class MainActivity extends AbstractRavelryActivity implements
        ProjectsFragmentListener, ProjectFragment.ProjectFragmentListener {

    private static final String JPEG_FILE_PREFIX = "IMG_";

    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String PROJECT_DETAIL_TAG = "projectDetail";
    public static final String PROJECTS_TAG = "projects";

    @NonConfigurationInstance
    Uri photoUri;

    ProjectFragment projectFragment;
    ProjectsFragment projectsFragment;

    @ViewById(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onStart() {
        super.onStart();

        displayProjectsFragment();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    private void displayProjectsFragment() {
        if (projectsFragment == null) {
            projectsFragment = new ProjectsFragment_();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentManager.popBackStackImmediate(PROJECTS_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction
                .replace(R.id.content_frame, projectsFragment, PROJECTS_TAG)
                .addToBackStack(PROJECTS_TAG)
                .commit();
    }


    private void displayProjectDetailFragment(final int projectId) {
        if (projectFragment == null) {
            projectFragment = new ProjectFragment_();
        }
        Bundle args = new Bundle();
        args.putInt(ProjectFragment.ARG_PROJECT_ID, projectId);
        projectFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, projectFragment, PROJECT_DETAIL_TAG)
                .addToBackStack(PROJECT_DETAIL_TAG)
                .commit();
    }

    @Override
    public void onProjectSelected(final int projectId) {
        displayProjectDetailFragment(projectId);
    }

    @Override
    public void pickImage() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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
            takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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
            projectFragment.uploadPhotoToProject(photoUri);
            photoUri = null;
        }
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_GALLERY)
    void onGalleryResult(final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            projectFragment.uploadPhotoToProject(data.getData());
        }
    }

    @OptionsItem(R.id.menu_drawer)
    void toggleMenu() {
        View drawer = findViewById(R.id.right_drawer);
        if (drawerLayout.isDrawerOpen(drawer)) {
            drawerLayout.closeDrawers();
        } else {
            drawerLayout.openDrawer(drawer);
        }
    }

    @Click(R.id.menu_my_projects)
    public void menuMyProjectsClicked() {
        displayProjectsFragment();
    }

    @Click(R.id.menu_change_user)
    public void menuChangeUserClicked() {
        requestToken();
    }
}
