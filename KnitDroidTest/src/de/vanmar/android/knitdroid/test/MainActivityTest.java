package de.vanmar.android.knitdroid.test;

import junit.framework.Assert;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.vanmar.android.knitdroid.projects.ProjectListActivity_;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<ProjectListActivity_> {

	private Solo solo;

	public MainActivityTest() {
		super(ProjectListActivity_.class);
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