package de.vanmar.android.yarrn.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

import junit.framework.Assert;

import de.vanmar.android.yarrn.MainActivity_;
import de.vanmar.android.yarrn.R;

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
        openNavigationDrawer();
        selectMyFavoritesMenuEntry();

        // then
        assertText("Trines Sleeves");
        assertText("Martian Boy");

        // when
        solo.clickOnText("Martian Boy");

        // then
        assertText("Kati Galusz");
        assertText("Tenth Doctor");
    }

    public void testStashes() {
        // when
        openNavigationDrawer();
        selectMyStashesMenuEntry();

        // then
        assertText("Wollmeise \"Pure\" 100% Merino");
        assertText("Wollmeise Lace-Garn");

        // when
        solo.clickOnText("Wollmeise \"Pure\" 100% Merino");

        // then
        assertText("Fingering 4ply 100% Merino");
        assertText("Flaschenpost");
        assertText("dunkler als ich dachte");
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
        openNavigationDrawer();
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
        openNavigationDrawer();
        selectMyProjectsMenuEntry();

        // my projects page is shown
        assertText("Welthasen Viajante");

        // navigate to my projects from navigation drawer again (check for duplicate on stack)
        openNavigationDrawer();
        selectMyProjectsMenuEntry();

        // back button closes the app
        solo.goBack();
        assertTrue(solo.getCurrentActivity() instanceof MainActivity_);
        assertTrue(solo.getCurrentActivity().isFinishing());
    }

    private void openNavigationDrawer() {
        Activity activity = solo.getCurrentActivity();
        View homeButton = activity.findViewById(android.R.id.home);
        if (homeButton == null) {
            homeButton = activity.findViewById(R.id.abs__home);
        }
        solo.clickOnView(homeButton);
    }

    private void selectMyProjectsMenuEntry() {
        View menuItem = solo.getCurrentActivity().findViewById(R.id.menu_my_projects);
        solo.clickOnView(menuItem);
    }

    private void selectMyFavoritesMenuEntry() {
        View menuItem = solo.getCurrentActivity().findViewById(R.id.menu_my_favorites);
        solo.clickOnView(menuItem);
    }

    private void selectMyStashesMenuEntry() {
        View menuItem = solo.getCurrentActivity().findViewById(R.id.menu_my_stashes);
        solo.clickOnView(menuItem);
    }

    private void assertText(String text) {
        Assert.assertTrue(solo.waitForText(text, 1, 20000));
    }
}