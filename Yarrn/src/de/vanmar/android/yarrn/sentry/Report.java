package de.vanmar.android.yarrn.sentry;

import android.text.format.Time;
import android.util.Log;
import android.util.TimeFormatException;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Report extends BaseObject {

    public final static String TAG = SentrySender.TAG + "/Report";
    public final static String TIMESTAMP_FORMAT = "%Y-%m-%dT:%H:%M:%S";

    public final static String SENTRY_EVENT_ID = "event_id";
    public final static String SENTRY_PLATFORM = "platform";
    public final static String SENTRY_CULPRIT = "culprit";
    public final static String SENTRY_TIMESTAMP = "timestamp";
    public final static String SENTRY_MESSAGE = "message";
    public final static String SENTRY_TAGS = "tags";
    public final static String SENTRY_EXTRA = "extra";
    public final static String SENTRY_EXCEPTION = "sentry.interfaces.Exception";
    public final static String SENTRY_EXCEPTION_VALUES = "values";

    String eventId;

    Time timestamp;

    String message;

    String culprit;

    Map<String, String> extra = new HashMap<String, String>();

    Map<String, String> tags = new HashMap<String, String>();

    List<Exception> exceptions = new ArrayList<Exception>();

    public Report(CrashReportData crashReportData, ReportField[] tags) {

        // Set time of crash
        if (crashReportData.containsKey(ReportField.USER_CRASH_DATE)) {
            String crashDate = crashReportData.getProperty(ReportField.USER_CRASH_DATE);
            setCrashReportTimestamp(crashDate);
        }

        // Set report ID
        if (crashReportData.containsKey(ReportField.REPORT_ID)) {
            this.setEventId(crashReportData.getProperty(ReportField.REPORT_ID));
        }

        put(SENTRY_PLATFORM, "android");

        if (tags != null) {
            HashMap<String, String> tagsMap = new HashMap<String, String>();

            for (ReportField field : tags) {
                if (!crashReportData.containsKey(field)) {
                    continue;
                }

                tagsMap.put(field.toString(), crashReportData.getProperty(field));
            }

            setTags(tagsMap);
        }
    }

    public List<Exception> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Exception> exceptions) {
        this.exceptions = exceptions;

        JSONObject exceptionObject = new JSONObject();
        JSONArray exceptionsArray = new JSONArray();

        try {
            for (Exception exception : exceptions) {
                exceptionsArray.put(exception.getJsonObject());
            }

            exceptionObject.put(SENTRY_EXCEPTION_VALUES, exceptionsArray);
        } catch (JSONException e) {
            Log.w(getTag(), "Failed to build exception", e);
        }

        put(SENTRY_EXCEPTION, exceptionObject);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId.replace("-", "");

        put(SENTRY_EVENT_ID, this.eventId);
    }

    public Time getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Time timestamp) {
        this.timestamp = timestamp;

        put(SENTRY_TIMESTAMP, timestamp.format(TIMESTAMP_FORMAT));
    }

    public void setCrashReportTimestamp(String crashReportTimestamp) {
        Time crashTimestamp = parseCrashReportTimestamp(crashReportTimestamp);
        setTimestamp(crashTimestamp);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;

        put(SENTRY_MESSAGE, message == null ? "" : message);
    }

    public String getCulprit() {
        return culprit;
    }

    public void setCulprit(String culprit) {
        this.culprit = culprit;

        put(SENTRY_CULPRIT, culprit);
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, String> extra) {
        this.extra = extra;

        put(SENTRY_EXTRA, getMappedObject(extra));
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, String> tags) {
        this.tags = tags;

        put(SENTRY_TAGS, getMappedObject(tags));
    }

    protected Time parseCrashReportTimestamp(String crashTime) {
        Time parsedTime = new Time();

        try {
            parsedTime.parse3339(crashTime);
        } catch (TimeFormatException e) {
            ACRA.log.w(getTag(), String.format("Failed to decode timestamp %s", crashTime), e);
        }

        return parsedTime;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String toString() {
        return getJsonObject().toString();
    }
}