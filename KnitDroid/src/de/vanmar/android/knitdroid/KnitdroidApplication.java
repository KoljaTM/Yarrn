package de.vanmar.android.knitdroid;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import de.vanmar.android.knitdroid.util.UiHelper;

@EApplication
public class KnitdroidApplication extends Application {

	@Bean
	UiHelper uiHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		//SslCertificateHelper.trustGeotrustCertificate(this);

		AQUtility.setExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(final Thread thread,
					final Throwable ex) {
				if (ex instanceof Exception) {
					reportException((Exception) ex);
				}
			}
		});

		final File ext = Environment.getExternalStorageDirectory();
		final File cacheDir = new File(ext, "KnitDroid");
		AQUtility.setCacheDir(cacheDir);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		BitmapAjaxCallback.clearCache();
	}

	@UiThread
	protected void reportException(final Exception ex) {
		Log.e("Knitdroid", ex.getMessage(), ex);
		uiHelper.displayError(ex);
		Toast.makeText(getApplicationContext(), ex.getMessage(),
				Toast.LENGTH_LONG).show();
	}
}
