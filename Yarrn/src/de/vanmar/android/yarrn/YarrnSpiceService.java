package de.vanmar.android.yarrn;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;

/**
 * Created by Kolja on 05.02.14.
 */
public class YarrnSpiceService extends GsonSpringAndroidSpiceService {
    @Override
    public int getThreadCount() {
        return 3;
    }
}
