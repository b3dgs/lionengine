/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.lang.reflect.Method;
import java.security.Permission;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Verbose}.
 */
final class VerboseTest
{
    /**
     * Test the verbose level.
     * 
     * @param level The verbose level.
     */
    private static void testVerbose(Verbose level)
    {
        Verbose.set(level);
        Verbose.info("info");
        Verbose.warning("warning");
        Verbose.warning(VerboseTest.class, "testVerbose", "warning");
        Verbose.critical(VerboseTest.class, "testVerbose", "critical");
        Verbose.exception(new LionEngineException("exception"), "exception");
    }

    /**
     * Test enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(Verbose.class);
    }

    /**
     * Test verbose.
     */
    @Test
    void testVerbose()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        testVerbose(Verbose.INFORMATION);
        testVerbose(Verbose.WARNING);
        testVerbose(Verbose.CRITICAL);
        Verbose.set(Verbose.INFORMATION, Verbose.WARNING, Verbose.CRITICAL);
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test <code>null</code> class warning.
     */
    @Test
    void testNullClassWarning()
    {
        assertThrows(() -> Verbose.warning((Class<?>) null, "test", "test"), Check.ERROR_NULL);
    }

    /**
     * Test <code>null</code> function warning.
     */
    @Test
    void testNullFunctionWarning()
    {
        assertThrows(() -> Verbose.warning(Object.class, (String) null, "test"), Check.ERROR_NULL);
    }

    /**
     * Test <code>null</code> class warning.
     */
    @Test
    void testNullClassCritical()
    {
        assertThrows(() -> Verbose.critical(null, "test", "test"), Check.ERROR_NULL);
    }

    /**
     * Test <code>null</code> function warning.
     */
    @Test
    void testNullFunctionCritical()
    {
        assertThrows(() -> Verbose.critical(Object.class, null, "test"), Check.ERROR_NULL);
    }

    /**
     * Test <code>null</code> exception.
     */
    @Test
    void testNullException()
    {
        assertThrows(() -> Verbose.exception(null, "test"), Check.ERROR_NULL);
    }

    /**
     * Test add file handler limit.
     * 
     * @throws Exception If error.
     */
    @Test
    void testAddFileHandler() throws Exception
    {
        final Method method = Verbose.class.getDeclaredMethod("addFileHandler", Logger.class);
        UtilReflection.setAccessible(method, true);
        final Logger logger = Logger.getAnonymousLogger();
        try
        {
            Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
            for (int i = 0; i < 101; i++)
            {
                method.invoke(Verbose.class, logger);
            }
        }
        finally
        {
            Verbose.info("****************************************************************************************");
            final Handler[] handlers = logger.getHandlers();
            for (final Handler handler : handlers)
            {
                logger.removeHandler(handler);
            }
            UtilReflection.setAccessible(method, false);
        }
    }

    /**
     * Test set formatter without right.
     * 
     * @throws Exception If error.
     */
    @Test
    void testSetFormatter() throws Exception
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        final SecurityManager old = System.getSecurityManager();
        try
        {
            System.setSecurityManager(new SecurityManager()
            {
                @Override
                public void checkPermission(Permission perm)
                {
                    if (perm instanceof java.util.logging.LoggingPermission)
                    {
                        throw new SecurityException();
                    }
                }
            });
            final Method method = Verbose.class.getDeclaredMethod("setFormatter", Logger.class, Formatter.class);
            UtilReflection.setAccessible(method, true);
            final Logger logger = Logger.getAnonymousLogger();
            logger.addHandler(new ConsoleHandler());
            method.invoke(Verbose.class, logger, new Mock());
            UtilReflection.setAccessible(method, false);
        }
        finally
        {
            System.setSecurityManager(old);
        }
        Verbose.info("****************************************************************************************");
    }

    /**
     * Mock
     */
    private static final class Mock extends Formatter
    {
        /**
         * Constructor.
         */
        Mock()
        {
            super();
        }

        @Override
        public String format(LogRecord record)
        {
            return null;
        }
    }
}
