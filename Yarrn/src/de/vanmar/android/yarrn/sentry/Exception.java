package de.vanmar.android.yarrn.sentry;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exception extends BaseObject {

    public final static String TAG = SentrySender.TAG + "/Exception";

    public final static String SENTRY_EXCEPTION_TYPE = "type";
    public final static String SENTRY_EXCEPTION_VALUE = "value";
    public final static String SENTRY_EXCEPTION_MODULE = "module";
    public final static String SENTRY_EXCEPTION_STACKTRACE = "stacktrace";
    public final static String SENTRY_STACKTRACE_FRAMES = "frames";

    String type;

    String value;

    String module;

    List<Frame> frames = new ArrayList<Frame>();

    public Exception(Throwable throwable) {
        // Set exception type
        setType(throwable.getClass().getSimpleName());

        // Set exception value (emptry string if none exists)
        String message = throwable.getMessage();

        if (message == null) {
            message = "";
        }

        setValue(message);

        // Set module
        setModule(throwable.getClass().getPackage().getName());

        // Create the stacktrace frames and set them
        List<Frame> stacktraceFrames = new ArrayList<Frame>();

        ArrayList<StackTraceElement> stackTraceElements = new ArrayList<StackTraceElement>();

        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            stacktraceFrames.add(new Frame(stackTraceElement));
        }

        // Sentry outputs the frames in reverse order
        Collections.reverse(stacktraceFrames);

        setFrames(stacktraceFrames);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

        put(SENTRY_EXCEPTION_TYPE, type);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;

        put(SENTRY_EXCEPTION_VALUE, value);
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;

        put(SENTRY_EXCEPTION_MODULE, module);
    }

    public List<Frame> getFrames() {
        // Always make sure that this is not null!
        if (frames == null) {
            frames = new ArrayList<Frame>();
        }

        return frames;
    }

    public void setFrames(List<Frame> frames) {
        this.frames = frames;

        JSONObject stacktrace = new JSONObject();
        JSONArray framesJsonArray = new JSONArray();

        try {
            for (Frame frame : frames) {
                framesJsonArray.put(frame.getJsonObject());
            }

            stacktrace.put(SENTRY_STACKTRACE_FRAMES, framesJsonArray);
        } catch (JSONException e) {
            ACRA.log.w(getTag(), "Failed to create the stacktrace", e);
        }

        put(SENTRY_EXCEPTION_STACKTRACE, stacktrace);
    }

    @Override
    public String getTag() {
        return TAG;
    }
}