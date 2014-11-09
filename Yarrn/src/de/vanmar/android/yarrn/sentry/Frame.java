package de.vanmar.android.yarrn.sentry;


public class Frame extends BaseObject {

    public final static String TAG = SentrySender.TAG + "/Frame";

    public final static String SENTRY_STACKTRACE_FRAME_FILENAME = "filename";
    public final static String SENTRY_STACKTRACE_FRAME_LINENO = "lineno";
    public final static String SENTRY_STACKTRACE_FRAME_MODULE = "module";
    public final static String SENTRY_STACKTRACE_FRAME_FUNCTION = "function";
    public final static String SENTRY_STACKTRACE_FRAME_IN_APP = "in_app";

    String filename;

    Integer linenumber;

    String module;

    String function;

    Boolean inApp;

    public Frame(StackTraceElement stackTraceElement) {
        // Set filename and line number if available
        String filename = stackTraceElement.getFileName();

        if (filename != null && !filename.isEmpty()) {
            setFilename(filename);
            setLinenumber(stackTraceElement.getLineNumber());
        }

        // Set module and function
        setModule(stackTraceElement.getClassName());
        setFunction(stackTraceElement.getMethodName());

        // Set if this frame is in app or native
        setInApp(!isNative(stackTraceElement));
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;

        put(SENTRY_STACKTRACE_FRAME_FILENAME, filename);
    }

    public Integer getLinenumber() {
        return linenumber;
    }

    public void setLinenumber(Integer linenumber) {
        this.linenumber = linenumber;

        put(SENTRY_STACKTRACE_FRAME_LINENO, linenumber);
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;

        put(SENTRY_STACKTRACE_FRAME_MODULE, module);
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;

        put(SENTRY_STACKTRACE_FRAME_FUNCTION, function);
    }

    public Boolean getInApp() {
        return inApp;
    }

    public void setInApp(Boolean inApp) {
        this.inApp = inApp;

        put(SENTRY_STACKTRACE_FRAME_IN_APP, inApp);
    }

    protected boolean isNative(StackTraceElement stackTraceElement) {
        if (stackTraceElement.isNativeMethod()) {
            return true;
        }

        return false;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public String toString() {
        return String.format("%s.%s", getModule(), getFunction());
    }
}