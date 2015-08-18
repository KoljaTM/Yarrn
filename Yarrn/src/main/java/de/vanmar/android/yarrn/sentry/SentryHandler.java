package de.vanmar.android.yarrn.sentry;

import android.app.Application;

import org.acra.ACRA;

public class SentryHandler implements Thread.UncaughtExceptionHandler {

    public static SentryHandler instance;

    private static Throwable latestThrowable;

    public static void init(Application application) {
        instance = new SentryHandler(application);
    }

    public SentryHandler(Application application) {
        ACRA.init(application);

        String dsn = ACRA.getConfig().formUri();

        ACRA.getErrorReporter().setReportSender(new SentrySender(dsn));

        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        latestThrowable = throwable;

        ACRA.getErrorReporter().uncaughtException(thread, throwable);
    }

    public static Throwable getLatestThrowable() {
        return latestThrowable;
    }
}