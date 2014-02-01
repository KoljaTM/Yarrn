package de.vanmar.android.knitdroid.util;

import org.androidannotations.api.BackgroundExecutor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Executor;

import de.vanmar.android.knitdroid.KnitdroidPrefs_;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class TestUtil {

    public static final String USERNAME = "username";

    public static void mockBackgroundExecutor() {
        final Executor executor = mock(Executor.class);

		BackgroundExecutor.setExecutor(executor);
		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(final InvocationOnMock invocation)
					throws Throwable {
				final Runnable runnable = (Runnable) invocation.getArguments()[0];
				runnable.run();
				return null;
			}
		}).when(executor).execute(any(Runnable.class));
	}

	public static void mockValidUser(final KnitdroidPrefs_ prefs) {
        prefs.username().put(USERNAME);
        prefs.accessToken().put("accesstoken");
		prefs.accessSecret().put("accesssecret");
	}
}
