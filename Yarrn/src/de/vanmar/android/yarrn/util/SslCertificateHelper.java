package de.vanmar.android.yarrn.util;

import android.content.Context;

import com.androidquery.util.AQUtility;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.vanmar.android.yarrn.R;

public class SslCertificateHelper {

    /**
     * Trust every server - dont check for any certificate
     */
    public static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain,
                                           final String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        // Install the all-trusting trust manager
        try {
            final SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void trustGeotrustCertificate(final Context context) {
        try {
            final KeyStore trustStore = KeyStore.getInstance("BKS");
            final InputStream in = context.getResources().openRawResource(
                    R.raw.geotrust_cert);
            trustStore.load(in, null);

            final TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

            final SSLContext sslCtx = SSLContext.getInstance("TLS");
            sslCtx.init(null, tmf.getTrustManagers(),
                    new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslCtx
                    .getSocketFactory());
        } catch (final Exception e) {
            AQUtility.report(e);
            e.printStackTrace();
        }
    }
}
