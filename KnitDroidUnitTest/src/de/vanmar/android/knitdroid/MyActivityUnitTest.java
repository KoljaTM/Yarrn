package de.vanmar.android.knitdroid;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MyActivityUnitTest {

	@Test
	public void shouldHaveHappySmiles() throws Exception {
		final String hello = new ProjectListActivity().getResources().getString(
				R.string.hello_world);
		assertThat(hello, equalTo("Hello World!"));
	}
}
