package de.vanmar.android.knitdroid.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

import junit.framework.Assert;

import de.vanmar.android.knitdroid.MainActivity_;
import de.vanmar.android.knitdroid.R;

public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity_> {

    private Solo solo;

    public MainActivityTest() {
        super(MainActivity_.class);
    }

    @Override
    public void setUp() {
        solo = new Solo(getInstrumentation(), getActivity());
        final String ravelryUrl = getActivity().getString(R.string.ravelry_url);
        System.out.println("RavelryUrl is: " + ravelryUrl);
        assertFalse("Must not be configured for live environment to run tests! Run target <config-dev>",
                ravelryUrl.contains("ravelry.com"));
        assertFalse("Replace localhost with IP address running the mock server",
                ravelryUrl.contains("localhost"));
    }

    @Override
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    public void testStartPage() {
        assertText("Welthasen Viajante");
        assertText("aqua diva");
    }

    public void testProjectDetails() {
        // when
        solo.clickOnText("aqua diva");

        // then
        assertText("Started:");
        assertText("Finished:");
        assertText("Notizfeld");
        assertText("15%");
    }

    public void testFavorites() {
        // when
        solo.clickOnActionBarItem(R.id.menu_drawer);
        selectMyFavoritesMenuEntry();

        // then
        assertText("Trines Sleeves");

        // when
        solo.clickOnText("Trines Sleeves");

        // then
        assertText("Trines Sleeves");
        assertText("Started:");
        assertText("Finished:");
    }

    public void testBackAndForthNavigation() {
        // start page
        assertText("Welthasen Viajante");

        // open detail page
        solo.clickOnText("aqua diva");
        assertText("Started:");

        // navigate to my projects by back button and to project again
        solo.goBack();
        assertText("Welthasen Viajante");
        solo.clickOnText("aqua diva");
        assertText("Started:");

        // navigate to faqvorites from navigation drawer
        solo.clickOnActionBarItem(R.id.menu_drawer);
        selectMyFavoritesMenuEntry();
        assertText("Trines Sleeves");

        // call favorite
        solo.clickOnText("Trines Sleeves");
        assertText("Trines Sleeves");
        assertText("Finished:");

        // back to favorites list
        solo.goBack();
        assertText("Trines Sleeves");
        assertText("Strata Sphere");

        // back to previous project details
        solo.goBack();
        assertText("aqua diva");

        // navigate to my projects from navigation drawer
        solo.clickOnActionBarItem(R.id.menu_drawer);
        selectMyProjectsMenuEntry();

        // my projects page is shown
        assertText("Welthasen Viajante");

        // navigate to my projects from navigation drawer again (check for duplicate on stack)
        solo.clickOnActionBarItem(R.id.menu_drawer);
        selectMyProjectsMenuEntry();

        // back button closes the app
        solo.goBack();
        assertTrue(solo.getCurrentActivity() instanceof MainActivity_);
        assertTrue(solo.getCurrentActivity().isFinishing());
    }

    private void selectMyProjectsMenuEntry() {
        View menuItem = solo.getCurrentActivity().findViewById(R.id.menu_my_projects);
        solo.clickOnView(menuItem);
    }

    private void selectMyFavoritesMenuEntry() {
        View menuItem = solo.getCurrentActivity().findViewById(R.id.menu_my_favorites);
        solo.clickOnView(menuItem);
    }

    private void assertText(String text) {
        Assert.assertTrue(solo.searchText(text));
    }
}
