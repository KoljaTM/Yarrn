package de.vanmar.android.yarrn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.androidquery.util.AQUtility;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.vanmar.android.yarrn.favorites.FavoritesFragment;
import de.vanmar.android.yarrn.patterns.PatternFragment;
import de.vanmar.android.yarrn.patterns.PatternSearchFragment;
import de.vanmar.android.yarrn.projects.ProjectFragment;
import de.vanmar.android.yarrn.projects.ProjectSearchFragment;
import de.vanmar.android.yarrn.projects.ProjectsFragment;
import de.vanmar.android.yarrn.projects.ProjectsFragment.ProjectsFragmentListener;
import de.vanmar.android.yarrn.queues.QueuedProjectFragment;
import de.vanmar.android.yarrn.queues.QueuesFragment;
import de.vanmar.android.yarrn.stashes.StashFragment;
import de.vanmar.android.yarrn.stashes.StashSearchFragment;
import de.vanmar.android.yarrn.stashes.StashesFragment;
import de.vanmar.android.yarrn.util.RequestCode;

@EActivity(resName = "activity_main")
public class MainActivity extends AbstractRavelryActivity implements
        ProjectsFragmentListener,
        ProjectSearchFragment.ProjectSearchFragmentListener,
        ProjectFragment.ProjectFragmentListener,
        FavoritesFragment.FavoritesFragmentListener,
        PatternSearchFragment.PatternSearchFragmentListener,
        PatternFragment.PatternFragmentListener,
        StashesFragment.StashesFragmentListener,
        StashSearchFragment.StashSearchFragmentListener,
        StashFragment.StashFragmentListener,
        SettingsFragment.SettingsFragmentListener,
        QueuesFragment.QueuesFragmentListener,
        QueuedProjectFragment.QueuedProjectFragmentListener {

    private static final String JPEG_FILE_PREFIX = "IMG_";

    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String PROJECT_DETAIL_TAG = "projectDetail";
    public static final String PATTERN_DETAIL_TAG = "patternDetail";
    public static final String PROJECTS_TAG = "projects";
    public static final String PROJECT_SEARCH_TAG = "project_search";
    public static final String FAVORITES_TAG = "favorites";
    public static final String PATTERN_SEARCH_TAG = "pattern_search";
    public static final String STASHES_TAG = "stashes";
    public static final String QUEUES_TAG = "queues";
    public static final String QUEUE_DETAIL_TAG = "queueDetail";
    public static final String STASH_SEARCH_TAG = "stash_search";
    public static final String STASH_DETAIL_TAG = "stashDetail";
    public static final String SETTINGS_TAG = "settings";

    @NonConfigurationInstance
    Uri photoUri;

    public ProjectsFragment projectsFragment;
    public ProjectSearchFragment projectSearchFragment;
    public FavoritesFragment favoritesFragment;
    public PatternSearchFragment patternSearchFragment;
    public StashesFragment stashesFragment;
    public QueuesFragment queuesFragment;
    public StashSearchFragment stashSearchFragment;

    @Bean
    public
    FragmentFactory fragmentFactory;

    @ViewById(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // get fragments from backstack if available
        projectsFragment = (ProjectsFragment) getSupportFragmentManager().findFragmentByTag(PROJECTS_TAG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            // if nothing else known, start projects list
            displayProjectsFragment();
        }
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
            projectsFragment = fragmentFactory.getProjectsFragment();
        }
        ensureFragment(PROJECTS_TAG, projectsFragment);
    }

    private void displayProjectSearchFragment() {
        if (projectSearchFragment == null) {
            projectSearchFragment = fragmentFactory.getProjectSearchFragment();
        }
        ensureFragment(PROJECT_SEARCH_TAG, projectSearchFragment);
    }

    private void displayFavoritesFragment() {
        if (favoritesFragment == null) {
            favoritesFragment = fragmentFactory.getFavoritesFragment();
        }
        ensureFragment(FAVORITES_TAG, favoritesFragment);
    }

    private void displayPatternSearchFragment() {
        if (patternSearchFragment == null) {
            patternSearchFragment = fragmentFactory.getPatternSearchFragment();
        }
        ensureFragment(PATTERN_SEARCH_TAG, patternSearchFragment);
    }

    private void displayStashesFragment() {
        if (stashesFragment == null) {
            stashesFragment = fragmentFactory.getStashesFragment();
        }
        ensureFragment(STASHES_TAG, stashesFragment);
    }

    private void displayQueuesFragment() {
        if (queuesFragment == null) {
            queuesFragment = fragmentFactory.getQueuesFragment();
        }
        ensureFragment(QUEUES_TAG, queuesFragment);
    }

    private void displayStashSearchFragment() {
        if (stashSearchFragment == null) {
            stashSearchFragment = fragmentFactory.getStashSearchFragment();
        }
        ensureFragment(STASH_SEARCH_TAG, stashSearchFragment);
    }

    private void displaySettingsFragment() {
        SettingsFragment settingsFragment = fragmentFactory.getSettingsFragment();
        ensureFragment(SETTINGS_TAG, settingsFragment);
    }

    private void ensureFragment(String tag, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            FragmentManager.BackStackEntry lastBackStackEntry = fragmentManager.getBackStackEntryAt(backStackEntryCount - 1);
            if (lastBackStackEntry != null && tag.equals(lastBackStackEntry.getName())) {
                // already at correct fragment
                return;
            }
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        boolean wasPopped = fragmentManager.popBackStackImmediate(tag, 0);
        if (!wasPopped) {
            fragmentTransaction
                    .replace(R.id.content_frame, fragment, tag)
                    .addToBackStack(tag)
                    .commit();
        }
    }


    private void displayProjectDetailFragment(final int projectId, String username) {
        ProjectFragment projectFragment = fragmentFactory.getProjectFragment();
        Bundle args = new Bundle();
        args.putInt(ProjectFragment.ARG_PROJECT_ID, projectId);
        args.putString(ProjectFragment.ARG_USERNAME, username);
        projectFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, projectFragment, PROJECT_DETAIL_TAG)
                .addToBackStack(PROJECT_DETAIL_TAG)
                .commit();
    }

    @Override
    public void onProjectSelected(final int projectId, String username) {
        displayProjectDetailFragment(projectId, username);
    }

    @Override
    public void onQueuedProjectSelected(final int projectId, String username) {
        displayQueuedProjectDetailFragment(projectId, username);
    }

    private void displayPatternDetailFragment(final int patternId) {
        PatternFragment patternFragment = fragmentFactory.getPatternFragment();
        Bundle args = new Bundle();
        args.putInt(PatternFragment.ARG_PATTERN_ID, patternId);
        patternFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, patternFragment, PATTERN_DETAIL_TAG)
                .addToBackStack(PATTERN_DETAIL_TAG)
                .commit();
    }

    private void displayQueuedProjectDetailFragment(final int queuedProjectId, final String username) {
        QueuedProjectFragment queuedProjectFragment = fragmentFactory.getQueuedProjectFragment();
        Bundle args = new Bundle();
        args.putInt(QueuedProjectFragment.ARG_QUEUED_PROJECT_ID, queuedProjectId);
        args.putString(QueuedProjectFragment.ARG_USERNAME, username);
        queuedProjectFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, queuedProjectFragment, QUEUE_DETAIL_TAG)
                .addToBackStack(QUEUE_DETAIL_TAG)
                .commit();
    }

    @Override
    public void onPatternSelected(final int patternId) {
        displayPatternDetailFragment(patternId);
    }

    private void displayStashDetailFragment(final int stashId, String username) {
        StashFragment stashFragment = fragmentFactory.getStashFragment();
        Bundle args = new Bundle();
        args.putInt(StashFragment.ARG_STASH_ID, stashId);
        args.putString(StashFragment.ARG_USERNAME, username);
        stashFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, stashFragment, STASH_DETAIL_TAG)
                .addToBackStack(STASH_DETAIL_TAG)
                .commit();
    }


    @Override
    public void onStashSelected(final int stashId, String username) {
        displayStashDetailFragment(stashId, username);
    }

    @Override
    public void pickImage() {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("image/*");
        startActivityForResult(intent, RequestCode.REQUEST_CODE_GALLERY);
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    public void takePhoto() {
        final File storageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Yarrn"
        );
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
            //takePictureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
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
            ProjectFragment projectFragment = (ProjectFragment) getSupportFragmentManager().findFragmentByTag(PROJECT_DETAIL_TAG);
            projectFragment.uploadPhotoToProject(photoUri);
            photoUri = null;
        }
    }

    @OnActivityResult(RequestCode.REQUEST_CODE_GALLERY)
    void onGalleryResult(final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            ProjectFragment projectFragment = (ProjectFragment) getSupportFragmentManager().findFragmentByTag(PROJECT_DETAIL_TAG);
            projectFragment.uploadPhotoToProject(data.getData());
        }
    }

    @SuppressLint("InlinedApi")
    @OptionsItem(android.R.id.home)
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
        drawerLayout.closeDrawers();
        displayProjectsFragment();
    }

    @Click(R.id.menu_my_favorites)
    public void menuMyFavoritesClicked() {
        drawerLayout.closeDrawers();
        displayFavoritesFragment();
    }

    @Click(R.id.menu_my_stashes)
    public void menuMyStashesClicked() {
        drawerLayout.closeDrawers();
        displayStashesFragment();
    }

    @Click(R.id.menu_my_queues)
    public void menuMyQueuesClicked() {
        drawerLayout.closeDrawers();
        displayQueuesFragment();
    }

    @Click(R.id.menu_project_search)
    public void menuProjectSearchClicked() {
        drawerLayout.closeDrawers();
        displayProjectSearchFragment();
    }

    @Click(R.id.menu_pattern_search)
    public void menuPatternSearchClicked() {
        drawerLayout.closeDrawers();
        displayPatternSearchFragment();
    }

    @Click(R.id.menu_stash_search)
    public void menuStashSearchClicked() {
        drawerLayout.closeDrawers();
        displayStashSearchFragment();
    }

    @Click(R.id.menu_open_ravelry)
    public void menuOpenRavelryClicked() {
        drawerLayout.closeDrawers();
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.open_ravelry_url))));
    }

    @Click(R.id.menu_settings)
    public void menuSettingsClicked() {
        drawerLayout.closeDrawers();
        displaySettingsFragment();
    }
}
