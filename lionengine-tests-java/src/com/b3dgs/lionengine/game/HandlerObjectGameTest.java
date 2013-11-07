/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.core.Media;

/**
 * Test the handler object game.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class HandlerObjectGameTest
{
    /**
     * Test the handler object game.
     */
    @Test
    public void testHandlerObjectGame()
    {
        final SetupGame setup = new SetupGame(Media.create(Media.getPath("resources", "type.xml")));

        final Handler handler = new Handler();
        Assert.assertTrue(handler.size() == 0);
        final TestObject objectA = new TestObject(setup);
        handler.add(objectA);
        final TestObject object = new TestObject(setup);
        handler.add(object);
        Assert.assertTrue(handler.size() == 0);

        Assert.assertNull(handler.get(objectA.getId()));

        handler.update(0);

        Assert.assertNotNull(handler.get(objectA.getId()));

        Assert.assertTrue(handler.size() == 1);
        handler.render(null);
        objectA.destroy();
        handler.remove(object);
        Assert.assertTrue(handler.size() == 1);
        handler.update(0);
        Assert.assertTrue(handler.size() == 0);
        handler.update(0);
        handler.removeAll();
        handler.update(0);
        Assert.assertTrue(handler.size() == 0);

        handler.add(objectA);
        handler.add(object);
        Assert.assertTrue(handler.size() == 0);
    }
}
