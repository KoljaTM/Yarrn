package de.vanmar.android.yarrn.ravelry;

import com.androidquery.util.AQUtility;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

public abstract class RavelryResultListener<T> implements RequestListener<T> {

    private final IRavelryActivity activity;

    protected RavelryResultListener(IRavelryActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        if (spiceException.getCause() instanceof RavelryException) {
            RavelryException ravelryException = (RavelryException) spiceException.getCause();
            if (ravelryException.getStatusCode() == 401 || ravelryException.getStatusCode() == 403) {
                activity.requestToken();
                return;
            }
        }
        AQUtility.report(spiceException);
    }
}
