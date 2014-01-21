package de.vanmar.android.knitdroid.test;

import android.test.ActivityInstrumentationTestCase2;

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
        assertText("Upload photo");
    }

    public void testBackAndForthNavigation() {
        // start page
        assertText("Welthasen Viajante");

        // open detail page
        solo.clickOnText("aqua diva");
        assertText("Upload photo");

        // navigate to my projects by back button and to project again
        solo.goBack();
        assertText("Welthasen Viajante");
        solo.clickOnText("aqua diva");

        // navigate to my projects from navigation drawer
        solo.clickOnActionBarItem(R.id.menu_drawer);
        solo.clickOnText("My Projects");

        // my projects page is shown
        assertText("Welthasen Viajante");

        // navigate to my projects from navigation drawer again (check for duplicate on stack)
        solo.clickOnActionBarItem(R.id.menu_drawer);
        solo.clickOnText("My Projects");

        // back button closes the app
        solo.goBack();
        assertTrue(solo.getCurrentActivity().isFinishing());
    }

    private void assertText(String text) {
        Assert.assertTrue(solo.searchText(text));
    }
}
