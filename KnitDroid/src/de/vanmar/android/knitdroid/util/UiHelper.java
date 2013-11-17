package de.vanmar.android.knitdroid.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

@EBean
public class UiHelper {

	@RootContext
	Context context;
	private ProgressDialog progressDialog = null;

	@UiThread
	public void displayError(final Exception e) {
		Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		Log.e("UiHelper", e.getMessage(), e);
	}

	@UiThread
	public void displayError(final int resId) {
		Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT)
				.show();
	}

	@UiThread
	public void startProgress(String title, String message, boolean indeterminate, boolean cancelable) {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(context, title, message, indeterminate, cancelable);
		}
	}

	@UiThread
	public void stopProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
