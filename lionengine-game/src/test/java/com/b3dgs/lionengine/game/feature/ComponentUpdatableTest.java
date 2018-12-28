/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Medias;

/**
 * Test {@link ComponentUpdatable}.
 */
public final class ComponentUpdatableTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setLoadFromJar(ComponentUpdatableTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the updater.
     */
    @Test
    public void testUpdater()
    {
        final ComponentUpdatable updatable = new ComponentUpdatable();
        final Handler handler = new Handler(new Services());
        handler.addComponent(updatable);

        final Updater object = new Updater();
        handler.add(object);

        assertFalse(object.isUpdated());

        handler.update(1.0);

        assertTrue(object.isUpdated());

        handler.removeAll();
        handler.update(1.0);
    }
}
