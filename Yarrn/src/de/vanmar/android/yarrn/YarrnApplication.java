package de.vanmar.android.yarrn;

import android.app.Application;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import de.vanmar.android.yarrn.util.SslCertificateHelper;
import de.vanmar.android.yarrn.util.UiHelper;

@EApplication
@ReportsCrashes(formUri = "https://vanmar-acra.appspot.com/acrareport", formKey = "", mode = ReportingInteractionMode.DIALOG,
        formUriBasicAuthLogin = "yarrn",
        formUriBasicAuthPassword = "crash",
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast,// optional. displays a Toast message when the user accepts to send a report.
        excludeMatchingSharedPreferencesKeys = {"^accessToken", "^accessSecret", "^requestToken"}
)
public class YarrnApplication extends Application {

    @Bean
    UiHelper uiHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        SslCertificateHelper.trustGeotrustCertificate(this);
        ACRA.init(this);

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
        final File cacheDir = new File(ext, "Yarrn");
        AQUtility.setCacheDir(cacheDir);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        BitmapAjaxCallback.clearCache();
    }

    @UiThread
    protected void reportException(final Exception ex) {
        Log.e("Yarrn", ex.getMessage(), ex);
        uiHelper.displayError(ex);
        Toast.makeText(getApplicationContext(), ex.getMessage(),
                Toast.LENGTH_LONG).show();
    }
}
