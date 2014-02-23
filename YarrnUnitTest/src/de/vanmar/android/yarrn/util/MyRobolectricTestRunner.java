package de.vanmar.android.yarrn.util;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.bytecode.ClassInfo;
import org.robolectric.bytecode.Setup;

/**
 * see https://github.com/robolectric/robolectric/issues/540
 */
public class MyRobolectricTestRunner extends RobolectricTestRunner {
    public MyRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public Setup createSetup() {
        return new Setup() {
            @Override
            public boolean isFromAndroidSdk(ClassInfo classInfo) {
                return super.isFromAndroidSdk(classInfo) || classInfo.getName().startsWith("com.google.ads.")
                        || classInfo.getName().startsWith("com.google.analytics.");
            }
        };
    }
}
