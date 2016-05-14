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
package com.b3dgs.lionengine.game.object;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.handler.HandlerTest;
import com.b3dgs.lionengine.game.object.feature.Refreshable;

/**
 * Test the component refresher.
 */
public class ComponentRefresherTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(HandlerTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the refresher.
     */
    @Test
    public void testRefresher()
    {
        final ComponentRefresher refresher = new ComponentRefresher();
        final Handler handler = new Handler(new Services());
        handler.addUpdatable(refresher);

        final Refresher object = new Refresher(new Setup(Medias.create("object.xml")), new Services());
        handler.add(object);
        Assert.assertFalse(object.isRefreshed());
        handler.update(1.0);
        Assert.assertTrue(object.isRefreshed());

        handler.removeAll();
        handler.update(1.0);
    }

    /**
     * Refreshable object mock.
     */
    private static class Refresher extends ObjectGame implements Refreshable
    {
        /** Refreshed flag. */
        private boolean refreshed;

        /**
         * Constructor.
         * 
         * @param setup The setup reference.
         * @param services The services reference.
         */
        public Refresher(Setup setup, Services services)
        {
            super(setup, services);
        }

        /**
         * Check if has been refreshed.
         * 
         * @return <code>true</code> if refreshed, <code>false</code> else.
         */
        public boolean isRefreshed()
        {
            return refreshed;
        }

        @Override
        public void prepare(Handlable owner, Services services)
        {
            // Mock
        }

        @Override
        public void update(double extrp)
        {
            refreshed = true;
        }

        @Override
        public <O extends Handlable> O getOwner()
        {
            return null;
        }
    }
}
