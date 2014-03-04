package de.vanmar.android.yarrn.projects;

import android.widget.RatingBar;
import android.widget.Spinner;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import de.vanmar.android.yarrn.FragmentFactory;
import de.vanmar.android.yarrn.MainActivity;
import de.vanmar.android.yarrn.MainActivity_;
import de.vanmar.android.yarrn.R;
import de.vanmar.android.yarrn.components.ViewEditText;
import de.vanmar.android.yarrn.ravelry.dts.Photo;
import de.vanmar.android.yarrn.ravelry.dts.PhotoResult;
import de.vanmar.android.yarrn.ravelry.dts.Project;
import de.vanmar.android.yarrn.ravelry.dts.ProjectResult;
import de.vanmar.android.yarrn.ravelry.dts.User;
import de.vanmar.android.yarrn.requests.ReorderProjectPhotosRequest;
import de.vanmar.android.yarrn.requests.UpdateProjectRequest;
import de.vanmar.android.yarrn.util.MyRobolectricTestRunner;
import de.vanmar.android.yarrn.util.TestUtil;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;

@RunWith(MyRobolectricTestRunner.class)
public class ProjectFragmentUnitTest {

    public static final int PROJECT_ID = 10014463;
    public static final String USERNAME = "Jillda";

    @Mock
    private FragmentFactory fragmentFactory;
    @Mock
    private SpiceManager spiceManager;
    @Mock
    private ProjectsFragment projectsFragment;

    private ProjectFragment_ projectFragment;
    private SpiceRequest request;
    private ReorderProjectPhotosRequest reorderRequest;

    @Before
    public void prepare() {
        MockitoAnnotations.initMocks(this);
        TestUtil.mockBackgroundExecutor();

        projectFragment = new ProjectFragment_();
        projectFragment.spiceManager = this.spiceManager;
        ActivityController<MainActivity_> activityController = Robolectric.buildActivity(MainActivity_.class).create();
        MainActivity activity = activityController.get();
        TestUtil.mockValidUser(activity.prefs);
        activity.fragmentFactory = this.fragmentFactory;
        given(fragmentFactory.getProjectsFragment()).willReturn(projectsFragment);
        given(fragmentFactory.getProjectFragment()).willReturn(projectFragment);
        activityController.start().resume();

        activity.onProjectSelected(PROJECT_ID, USERNAME);
    }

    @Test
    public void shouldGetProjectDetails() throws Exception {
        // given
        mockSpiceCall(createProjectResult());

        // when
        projectFragment.onProjectSelected(PROJECT_ID, USERNAME);

        // then
        assertThat(projectFragment.name.getText().toString(), is("aqua diva"));
        assertThat(projectFragment.status.getText().toString(), is("Started"));
        assertThat(projectFragment.progressBar.getProgress(), is(5));
        assertThat((String) projectFragment.progressSpinner.getSelectedItem(), is("5%"));
        assertThat(projectFragment.started.getText().toString(), is("17.05.2013")); // testing with German Locale
        assertThat(projectFragment.completed.getText().toString(), is("Juni 2013"));
    }

    @Test
    public void shouldRefreshProjectDetails() throws Exception {
        // given
        assertThat(projectFragment.name.getText().toString(), is(""));
        mockSpiceCall(createProjectResult());

        // when
        projectFragment.menuRefresh();

        // then
        assertThat(projectFragment.name.getText().toString(), is("aqua diva"));
        assertThat(projectFragment.status.getText().toString(), is("Started"));
        assertThat(projectFragment.progressBar.getProgress(), is(5));
        assertThat((String) projectFragment.progressSpinner.getSelectedItem(), is("5%"));
        assertThat(projectFragment.started.getText().toString(), is("17.05.2013")); // testing with German Locale
        assertThat(projectFragment.completed.getText().toString(), is("Juni 2013"));
    }

    @Test
    public void shouldUpdateProgress() {
        // given
        Spinner progressSpinner = projectFragment.progressSpinner;
        assertThat((String) progressSpinner.getItemAtPosition(7), is("35%"));
        mockSpiceCall(createProjectResult());
        projectFragment.onProjectSelected(PROJECT_ID, USERNAME);

        // when
        progressSpinner.getOnItemSelectedListener().onItemSelected(progressSpinner, null, 7, 0);

        // then
        assertThat(request, is(UpdateProjectRequest.class));
        UpdateProjectRequest updateProjectRequest = (UpdateProjectRequest) request;
        assertThat(updateProjectRequest.getProjectId(), is(PROJECT_ID));
        assertThat(updateProjectRequest.getUpdateData().get("progress").getAsInt(), is(35));
    }

    @Test
    public void shouldUpdateNotes() {
        // given
        ViewEditText notes = projectFragment.notes;
        mockSpiceCall(createProjectResult());
        projectFragment.onProjectSelected(PROJECT_ID, USERNAME);
        assertThat(notes.getBodyText().toString(), is("Notizfeld"));
        assertFalse(notes.isEditMode());

        // when
        notes.findViewById(R.id.toggleButton).performClick();
        assertTrue(notes.isEditMode());
        notes.setBodyText("Neue Notiz");
        notes.findViewById(R.id.toggleButton).performClick();
        assertFalse(notes.isEditMode());

        // then
        assertThat(request, is(UpdateProjectRequest.class));
        UpdateProjectRequest updateProjectRequest = (UpdateProjectRequest) request;
        assertThat(updateProjectRequest.getProjectId(), is(PROJECT_ID));
        assertThat(updateProjectRequest.getUpdateData().get("notes").getAsString(), is("Neue Notiz"));
    }

    @Test
    public void shouldUpdateRating() {
        // given
        RatingBar rating = projectFragment.rating;
        mockSpiceCall(createProjectResult());
        projectFragment.onProjectSelected(PROJECT_ID, USERNAME);
        assertThat(rating.getRating(), is(3.0f));

        // when
        rating.getOnRatingBarChangeListener().onRatingChanged(rating, 5, true);

        // then
        assertThat(request, is(UpdateProjectRequest.class));
        UpdateProjectRequest updateProjectRequest = (UpdateProjectRequest) request;
        assertThat(updateProjectRequest.getProjectId(), is(PROJECT_ID));
        assertThat(updateProjectRequest.getUpdateData().get("rating").getAsInt(), is(4));
    }

    @Test
    public void shouldRearrangePhotos() {
        // given
        mockSpiceCall(createProjectResult());
        projectFragment.onProjectSelected(PROJECT_ID, USERNAME);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                reorderRequest = (ReorderProjectPhotosRequest) invocationOnMock.getArguments()[0];
                RequestListener<PhotoResult> listener = (RequestListener<PhotoResult>) invocationOnMock.getArguments()[1];
                PhotoResult photoResult = new PhotoResult();
                photoResult.photos = new ArrayList<Photo>();
                listener.onRequestSuccess(photoResult);
                return null;
            }
        }).when(spiceManager).execute(any(SpiceRequest.class), any(RequestListener.class));

        // when
        HorizontalListView gallery = projectFragment.gallery;
        gallery.getOnItemLongClickListener().onItemLongClick(gallery, null, 0, 0);
        PhotoAdapter adapter = (PhotoAdapter) gallery.getAdapter();
        PhotoAdapter.PhotoAdapterListener adapterListener = adapter.getPhotoAdapterListener();
        adapterListener.onMoveLeft(1); // 2 1 3
        adapterListener.onMoveRight(1); // 2 3 1
        adapterListener.onMoveAllRight(0); // 3 1 2
        adapterListener.onMoveAllLeft(1); // 1 3 2
        projectFragment.galleryEditDone.performClick();

        // then
        List<Photo> photos = reorderRequest.getPhotos();
        assertThat(photos.size(), is(3));
        assertThat(photos.get(0).id, is("1"));
        assertThat(photos.get(1).id, is("3"));
        assertThat(photos.get(2).id, is("2"));
    }

    private void mockSpiceCall(final ProjectResult projectResult) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                request = (SpiceRequest) invocationOnMock.getArguments()[0];
                RequestListener<ProjectResult> listener = (RequestListener<ProjectResult>) invocationOnMock.getArguments()[3];
                listener.onRequestSuccess(projectResult);
                return null;
            }
        }).when(spiceManager).execute(any(SpiceRequest.class), any(), anyLong(), any(RequestListener.class));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                request = (SpiceRequest) invocationOnMock.getArguments()[0];
                RequestListener<ProjectResult> listener = (RequestListener<ProjectResult>) invocationOnMock.getArguments()[1];
                listener.onRequestSuccess(projectResult);
                return null;
            }
        }).when(spiceManager).execute(any(SpiceRequest.class), any(RequestListener.class));
    }

    private ProjectResult createProjectResult() {
        ProjectResult projectResult = new ProjectResult();
        Project project = new Project();
        project.name = "aqua diva";
        project.progress = 5;
        project.rating = 2;
        project.status = "Started";
        project.notes = "Notizfeld";
        GregorianCalendar started = new GregorianCalendar();
        started.set(2013, Calendar.MAY, 17);
        project.started = started.getTime();
        project.startedDaySet = true;
        GregorianCalendar completed = new GregorianCalendar();
        completed.set(2013, Calendar.JUNE, 21);
        project.completed = completed.getTime();
        project.completedDaySet = false;
        User user = new User();
        user.username = TestUtil.USERNAME;
        project.user = user;
        project.photos = new ArrayList<Photo>();
        project.photos.add(createPhoto("1"));
        project.photos.add(createPhoto("2"));
        project.photos.add(createPhoto("3"));
        projectResult.project = project;
        return projectResult;
    }

    private Photo createPhoto(String id) {
        Photo photo = new Photo();
        photo.id = id;
        return photo;
    }
}
