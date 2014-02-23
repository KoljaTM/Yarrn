package de.vanmar.android.yarrn.util;

import com.google.ads.internal.AdWebView;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowWebView;

/**
 * A shadow for ad mob so roboelectric tests work without crashing
 */
@Implements(AdWebView.class)
public class AdWebViewShadow extends ShadowWebView {
    @Implementation
    public void __constructor__(com.google.ads.n slotState, com.google.ads.AdSize adSize) {
    }

}