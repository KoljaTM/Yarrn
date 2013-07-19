package de.vanmar.android.knitdroid;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.bugsense.trace.BugSenseHandler;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.UiThread;

import de.vanmar.android.knitdroid.util.SslCertificateHelper;
import de.vanmar.android.knitdroid.util.UiHelper;

@EApplication
public class KnitdroidApplication extends Application {

	private boolean useBugSense = false;

	@Bean
	UiHelper uiHelper;

	@Override
	public void onCreate() {
		super.onCreate();

		final String bugSenseKey = getString(R.string.bugsense_key);
		if (bugSenseKey != null && bugSenseKey.length() > 0) {
			useBugSense = true;
		}
		if (useBugSense) {
			BugSenseHandler.initAndStartSession(this, bugSenseKey);
		}

		SslCertificateHelper.trustGeotrustCertificate(this);

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
		if (useBugSense) {
			BugSenseHandler.sendException(ex);
			BugSenseHandler.flush(this);
		}
	}
}
