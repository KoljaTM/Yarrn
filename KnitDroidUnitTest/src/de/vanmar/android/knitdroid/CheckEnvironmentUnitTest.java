package de.vanmar.android.knitdroid;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ActivityController;

import java.net.URL;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

@RunWith(RobolectricTestRunner.class)
public class CheckEnvironmentUnitTest {

    private MainActivity activity;

    @Before
    public void prepare() {
        ActivityController<MainActivity_> activityController = Robolectric.buildActivity(MainActivity_.class);
        activityController.create();
        activity = activityController.get();
    }

    @Test
    public void shouldBeConfiguredForTestServer() throws Exception {
        final String ravelryUrl = activity.getString(R.string.ravelry_url);
        System.out.println("RavelryUrl is: " + ravelryUrl);
        assertThat(
                "Must not be configured for live environment to run tests! Run target <config-dev>",
                ravelryUrl, not(containsString("ravelry.com")));
    }

    @Test
    public void httpMockShouldBeRunning() throws Exception {
        try {
            final URL mockUrl = new URL("http://localhost:8888");
            mockUrl.openStream();
        } catch (final Exception e) {
            e.printStackTrace();
            fail("Please check that the mock HttpServer is running.");
        }

    }

}
