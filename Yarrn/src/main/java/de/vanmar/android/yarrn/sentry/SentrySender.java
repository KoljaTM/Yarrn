package de.vanmar.android.yarrn.sentry;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.HttpSender.Method;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;
import org.acra.util.HttpRequest;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SentrySender implements ReportSender {

    public final static String TAG = ACRA.LOG_TAG + "/SentrySender";

    private SentryConfig config;

    public static final ReportField[] SENTRY_TAGS_FIELDS = {
            ReportField.ANDROID_VERSION,
            ReportField.APP_VERSION_CODE,
            ReportField.APP_VERSION_NAME,
            ReportField.BRAND,
            ReportField.INSTALLATION_ID,
            ReportField.IS_SILENT,
            ReportField.PACKAGE_NAME,
            ReportField.PHONE_MODEL,
            ReportField.PRODUCT,
            ReportField.USER_EMAIL,
    };

    public static final ReportField[] SENTRY_EXTRA_FIELDS = {
            ReportField.STACK_TRACE,
            ReportField.AVAILABLE_MEM_SIZE,
            ReportField.TOTAL_MEM_SIZE,
            ReportField.USER_APP_START_DATE
    };

    public static final ReportField[] SENTRY_MAPPED_EXTRA_FIELDS = {
            ReportField.CUSTOM_DATA,
            //  ReportField.SETTINGS_GLOBAL,
            //  ReportField.SETTINGS_SECURE,
            //  ReportField.ENVIRONMENT,
            //  ReportField.DISPLAY,
            // ReportField.CRASH_CONFIGURATION,
            //  ReportField.BUILD,
            ReportField.SHARED_PREFERENCES
    };

    /**
     * Takes in a sentryDSN
     *
     * @param sentryDSN '{PROTOCOL}://{PUBLIC_KEY}:{SECRET_KEY}@{HOST}/{PATH}/{PROJECT_ID}'
     */
    public SentrySender(String sentryDSN) {
        if ((sentryDSN == null) || sentryDSN.isEmpty()) {
            return;
        }

        try {
            config = new SentryConfig(sentryDSN);
        } catch (MalformedURLException e) {
            ACRA.log.e(TAG, String.format("Failed to parse Sentry DSN %s", sentryDSN), e);
        }
    }

    /**
     * Sets up a base HttpRequest with required headers and options
     *
     * @return HttpRequest
     */
    protected HttpRequest createHttpRequest() {
        HttpRequest request = new HttpRequest();

        request.setConnectionTimeOut(ACRA.getConfig().connectionTimeout());
        request.setSocketTimeOut(ACRA.getConfig().socketTimeout());
        request.setMaxNrRetries(ACRA.getConfig().maxNumberOfRequestRetries());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("X-Sentry-Auth", buildAuthHeader());

        request.setHeaders(headers);

        return request;
    }

    @Override
    public void send(CrashReportData errorContent) throws ReportSenderException {

        if (config == null) {
            return;
        }

        final HttpRequest request = createHttpRequest();

        String jsonData;

        try {
            jsonData = buildJSON(errorContent);
        } catch (JSONException e) {
            throw new ReportSenderException("Error while compiling the output data", e);
        }

        ACRA.log.d(TAG, jsonData);

        try {
            request.send(config.getSentryURL(), Method.POST, jsonData, org.acra.sender.HttpSender.Type.JSON);
        } catch (MalformedURLException e) {
            throw new ReportSenderException("Error while sending report to Sentry.", e);
        } catch (IOException e) {
            throw new ReportSenderException("Error while sending report to Sentry.", e);
        }
    }

    /**
     * Build up the sentry auth header in the following format.
     * <p/>
     * The header is composed of the timestamp from when the message was generated, and an
     * arbitrary client version string. The client version should be something distinct to your client,
     * and is simply for reporting purposes.
     * <p/>
     * X-Sentry-Auth: Sentry sentry_version=3,
     * sentry_timestamp=<signature timestamp>[,
     * sentry_key=<public api key>,[
     * sentry_client=<client version/arbitrary>]]
     *
     * @return String version of the sentry auth header
     */
    protected String buildAuthHeader() {
        String authHeaderFormat = "Sentry sentry_version=%s, " +
                "sentry_timestamp=%d, " +
                "sentry_key=%s, " +
                "sentry_secret=%s, " +
                "sentry_client=%s/%d";


        return String.format(authHeaderFormat, 4, (new Date().getTime() / 1000L),
                config.getPublicKey(), config.getSecretKey(), this.getClass().toString(), 1);
    }

    private String buildJSON(CrashReportData crashReportData) throws JSONException {
        Report sentryReport = new Report(crashReportData, SENTRY_TAGS_FIELDS);

        Throwable throwable = SentryHandler.getLatestThrowable();

        String userComment = crashReportData.getProperty(ReportField.USER_COMMENT);
        if (userComment != null) {
            sentryReport.setMessage(userComment);
        } else if (crashReportData.getProperty(ReportField.STACK_TRACE) != null) {
            String firstStackTraceElement = crashReportData.getProperty(ReportField.STACK_TRACE).split("\\s")[0];
            sentryReport.setMessage(firstStackTraceElement);
        } else {
            sentryReport.setMessage(throwable != null ? throwable.getMessage() : "No message available");
        }

        /* Add the exceptions and determine the culprit */
        List<de.vanmar.android.yarrn.sentry.Exception> exceptions = new ArrayList<Exception>();
        String culprit = "";

        while (throwable != null) {
            Exception exception = new Exception(throwable);
            exceptions.add(exception);

            if (exception.getFrames().size() > 0) {
                culprit = exception.getFrames().get(0).toString();
            }

            throwable = throwable.getCause();
        }

        // Sentry outputs the exceptions in reversed order
        Collections.reverse(exceptions);

        sentryReport.setExceptions(exceptions);
        sentryReport.setCulprit(culprit);

        // Accumulate extra fields and values
        Map<String, String> extraValues = new HashMap<String, String>();

        for (ReportField reportField : SENTRY_MAPPED_EXTRA_FIELDS) {
            if (!crashReportData.containsKey(reportField)) {
                continue;
            }

            extraValues.putAll(parseMappedString(crashReportData.getProperty(reportField)));
        }

        for (ReportField reportField : SENTRY_EXTRA_FIELDS) {
            if (!crashReportData.containsKey(reportField)) {
                continue;
            }

            extraValues.put(reportField.toString(), crashReportData.getProperty(reportField));
        }

        sentryReport.setExtra(extraValues);

        return sentryReport.toString();
    }

    protected Map<String, String> parseMappedString(String mappedData) {
        Map<String, String> mappedValues = new HashMap<String, String>();

        for (String line : mappedData.split("\n")) {
            if (!line.contains("=")) {
                continue;
            }

            String[] components = line.split("=", 2);

            mappedValues.put(components[0], (components.length >= 2) ? components[1] : "");
        }

        return mappedValues;
    }

    private class SentryConfig {

        private String host, protocol, publicKey, secretKey, prefix;
        private Integer port, projectId;

        private final String API_FORMAT = "/api/%d/store/";

        /**
         * Takes in a sentryDSN and builds up the configuration
         *
         * @param sentryDSN '{PROTOCOL}://{PUBLIC_KEY}:{SECRET_KEY}@{HOST}/{PATH}/{PROJECT_ID}'
         */
        public SentryConfig(String sentryDSN) throws MalformedURLException {

            URL url = new URL(sentryDSN);

            String path = url.getPath();

            int lastSeparator = path.lastIndexOf("/");

            Integer projectId = Integer.parseInt(path.substring(lastSeparator + 1, path.length()));

            String prefix = "";

            if (lastSeparator > 0) {
                prefix = path.substring(0, lastSeparator);
            }

            String userInfo = url.getUserInfo();

            if (userInfo.isEmpty() || !userInfo.contains(":")) {
                throw new MalformedURLException("Missing secret or public keys");
            }

            String[] userParts = userInfo.split(":");

            setHost(url.getHost());
            setProtocol(url.getProtocol());
            setProjectId(projectId);
            setPrefix(prefix);
            setPublicKey(userParts[0]);
            setSecretKey(userParts[1]);
            setPort(url.getPort());
        }

        /**
         * The Sentry server URL that we post the message to.
         *
         * @return sentry server url
         * @throws MalformedURLException
         */
        public URL getSentryURL() throws MalformedURLException {
            String path = getPrefix() + String.format(API_FORMAT, getProjectId());

            return new URL(getProtocol(), getHost(), getPort(), path);
        }

        /**
         * The sentry server host
         *
         * @return server host
         */
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        /**
         * Sentry server protocol http https?
         *
         * @return http or https
         */
        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        /**
         * The Sentry public key
         *
         * @return Sentry public key
         */
        public String getPublicKey() {
            return publicKey;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        /**
         * The Sentry secret key
         *
         * @return Sentry secret key
         */
        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        /**
         * sentry url path
         *
         * @return url path
         */
        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * Sentry project Id
         *
         * @return project Id
         */
        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }

        /**
         * sentry server port
         *
         * @return server port
         */
        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

    }
}