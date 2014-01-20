package de.vanmar.android.knitdroid.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import junit.framework.Assert;

import de.vanmar.android.knitdroid.MainActivity_;

public class MainActivityTest extends
        ActivityInstrumentationTestCase2<MainActivity_> {

    private Solo solo;

    public MainActivityTest() {
        super(MainActivity_.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testHelloWorld() throws Exception {
        Assert.assertTrue(solo.searchText("Knitdroid"));
    }
}