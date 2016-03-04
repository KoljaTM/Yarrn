package de.vanmar.android.yarrn.util;

import android.content.Context;

import com.androidquery.util.AQUtility;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import de.vanmar.android.yarrn.R;

public class SslCertificateHelper {

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
