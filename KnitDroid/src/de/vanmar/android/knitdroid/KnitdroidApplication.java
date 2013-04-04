package de.vanmar.android.knitdroid;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.googlecode.androidannotations.annotations.EApplication;
import com.googlecode.androidannotations.annotations.UiThread;

@EApplication
public class KnitdroidApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		AQUtility.setExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(final Thread thread,
					final Throwable ex) {
				reportException(ex);
			}
		});
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		BitmapAjaxCallback.clearCache();
	}

	@UiThread
	protected void reportException(final Throwable ex) {
		Log.e("Knitdroid", ex.getMessage(), ex);
		Toast.makeText(getApplicationContext(), ex.getMessage(),
				Toast.LENGTH_LONG).show();
	}

}
