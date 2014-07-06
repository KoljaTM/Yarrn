package de.vanmar.android.yarrn;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;

/**
 * Created by Kolja on 05.02.14.
 */
public class YarrnSpiceService extends GsonSpringAndroidSpiceService {
    @Override
    public int getThreadCount() {
        return 3;
    }

    @Override
    public Notification createDefaultNotification() {
        Notification notification = super.createDefaultNotification();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            //temporary fix https://github.com/octo-online/robospice/issues/200
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
            notification.setLatestEventInfo(this, "", "", pendingIntent);
        }
        return notification;
    }
}
