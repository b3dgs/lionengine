package com.b3dgs.lionengine.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test all classes.
 */
@RunWith(Suite.class)
@SuiteClasses(
{
        TestEngine.class, TestDrawable.class, TestFile.class, TestAudio.class, TestGame.class, TestUtility.class
})
public class TestAll
{
    // All test
}
