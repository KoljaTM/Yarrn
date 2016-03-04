package de.vanmar.android.yarrn.util;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import de.vanmar.android.yarrn.R;

/**
 * Created by kmarkwardt on 19/08/15.
 */
public class AdHelper {

    public static void loadAds(Activity context) {
        AdView adView = (AdView) context.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}
