package de.vanmar.android.knitdroid.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.concurrent.Executor;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.googlecode.androidannotations.api.BackgroundExecutor;

public class TestUtil {

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

}
