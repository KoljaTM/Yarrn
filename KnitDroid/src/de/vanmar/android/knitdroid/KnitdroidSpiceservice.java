package de.vanmar.android.knitdroid;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;

/**
 * Created by Kolja on 05.02.14.
 */
public class KnitDroidSpiceService extends GsonSpringAndroidSpiceService {
    @Override
    public int getThreadCount() {
        return 3;
    }
}
