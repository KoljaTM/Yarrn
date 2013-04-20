package de.vanmar.android.knitdroid.projects;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.FragmentById;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.googlecode.androidannotations.annotations.OnActivityResult;
import com.googlecode.androidannotations.annotations.UiThread;

import de.vanmar.android.knitdroid.AbstractRavelryActivity;
import de.vanmar.android.knitdroid.R;
import de.vanmar.android.knitdroid.projects.ProjectFragment.ProjectFragmentListener;

@EActivity(resName = "activity_project_detail")
public class ProjectDetailActivity extends AbstractRavelryActivity implements
		ProjectFragmentListener {

	public static final String EXTRA_PROJECT_ID = "ProjectDetailActivity.extra.project_id";

	private static final int REQUEST_CODE_GALLERY = 1;
	private static final int REQUEST_CODE_CAMERA = 2;

	private static final String JPEG_FILE_PREFIX = "IMG_";

	private static final String JPEG_FILE_SUFFIX = ".jpg";

	@FragmentById(R.id.projectFragment)
	ProjectFragment projectFragment;

	@Extra(EXTRA_PROJECT_ID)
	protected int projectId;

	@Bean
	@NonConfigurationInstance
	PhotoUploadTask photoUploadTask;

	@NonConfigurationInstance
	Uri photoUri;

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
		startActivityForResult(intent, REQUEST_CODE_GALLERY);
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
			startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
		} catch (final IOException e) {
			AQUtility.report(e);
		}
	}

	@OnActivityResult(REQUEST_CODE_CAMERA)
	void onCameraResult(final int resultCode, final Intent data) {
		if (resultCode == Activity.RESULT_OK && photoUri != null) {
			uploadPhotoToProject(photoUri, projectId);
			photoUri = null;
		}
	}

	@OnActivityResult(REQUEST_CODE_GALLERY)
	void onGalleryResult(final int resultCode, final Intent data) {
		if (resultCode == Activity.RESULT_OK && photoUri != null) {
			uploadPhotoToProject(data.getData(), projectId);
		}
	}

	public void uploadPhotoToProject(final Uri photoUri, final int projectId) {
		photoUploadTask.uploadPhotoToProject(photoUri, projectId);
	}

	@UiThread
	public void onPhotoUploadSuccess() {
		Toast.makeText(this, "Upload successful", Toast.LENGTH_LONG).show();
	}
}
