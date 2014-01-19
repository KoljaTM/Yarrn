package org.github.gist.hborders;

import android.os.Looper;

import org.robolectric.Robolectric;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;

/**
 * Workaround for a problem in Robolectric: https://gist.github.com/hborders/8285441
 * <p/>
 * To automatically configure this Shadow for all tests, create a top-level configuration file named: org.robolectric.Config.properties with the following contents:
 * <p/>
 * shadows=org.github.gist.hborders.Fixed895ShadowLooper
 */
@SuppressWarnings({"UnusedDeclaration"})
@Implements(Looper.class)
public class Fixed895ShadowLooper extends ShadowLooper {
    private static final Thread MAIN_THREAD = Thread.currentThread();

    public void __constructor__() {
    }

    @Implementation
    public static Looper getMainLooper() {
        ShadowApplication shadowApplication = Robolectric.getShadowApplication();
        if ((shadowApplication == null) && (Thread.currentThread() == MAIN_THREAD)) {
            Looper mainLooper = myLooper();
            return mainLooper;
        } else {
            // might still throw NullPointerException
            // better than returning null because this fails early.
            return shadowApplication.getMainLooper();
        }
    }

}
