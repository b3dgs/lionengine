/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine;

import java.lang.reflect.Method;
import java.security.Permission;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

import com.b3dgs.lionengine.util.UtilReflection;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the verbose class.
 */
public class VerboseTest
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
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(Verbose.class);
    }

    /**
     * Test the verbose class.
     */
    @Test
    public void testVerbose()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        testVerbose(Verbose.INFORMATION);
        testVerbose(Verbose.WARNING);
        testVerbose(Verbose.CRITICAL);
        Verbose.set(Verbose.INFORMATION, Verbose.WARNING, Verbose.CRITICAL);
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test the add file handler limit.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testAddFileHandler() throws Exception
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        final Method method = Verbose.class.getDeclaredMethod("addFileHandler", Logger.class);
        UtilReflection.setAccessible(method, true);
        final Logger logger = Logger.getAnonymousLogger();
        try
        {
            for (int i = 0; i < 101; i++)
            {
                method.invoke(Verbose.class, logger);
            }
        }
        finally
        {
            final Handler[] handlers = logger.getHandlers();
            for (final Handler handler : handlers)
            {
                logger.removeHandler(handler);
            }
            UtilReflection.setAccessible(method, false);
            Verbose.info("****************************************************************************************");
        }
    }

    /**
     * Test the set formatter without right.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testSetFormatter() throws Exception
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
