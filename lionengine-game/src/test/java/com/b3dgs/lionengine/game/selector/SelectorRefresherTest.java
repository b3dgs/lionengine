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
package com.b3dgs.lionengine.game.selector;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.MouseMock;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Test the selector refresher class.
 */
public class SelectorRefresherTest
{
    private final Services services = new Services();
    private final Cursor cursor = services.create(Cursor.class);
    private final SelectorModel model = new SelectorModel();
    private final SelectorRefresher refresher = new SelectorRefresher(model);
    private final MouseMock mouse = new MouseMock();
    private final AtomicReference<Rectangle> started = new AtomicReference<Rectangle>();
    private final AtomicReference<Rectangle> done = new AtomicReference<Rectangle>();

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        refresher.addListener(new SelectorListener()
        {
            @Override
            public void notifySelectionStarted(Rectangle selection)
            {
                started.set(selection);
            }

            @Override
            public void notifySelectionDone(Rectangle selection)
            {
                done.set(selection);
            }
        });
        cursor.setInputDevice(mouse);
        cursor.setSyncMode(true);

        services.add(new Camera());
        refresher.prepare(new FeaturableModel(), services);
    }

    /**
     * Test the selecting flag.
     */
    @Test
    public void testSelecting()
    {
        Assert.assertFalse(model.isSelecting());

        mouse.setClick(1);
        refresher.update(1.0);

        Assert.assertFalse(model.isSelecting());

        model.setClickSelection(1);
        refresher.update(1.0);

        Assert.assertTrue(model.isSelecting());
    }

    /**
     * Test the selection.
     */
    @Test
    public void testSelection()
    {
        Assert.assertNull(started.get());
        Assert.assertNull(done.get());

        model.setClickSelection(1);

        mouse.move(1, 1);
        cursor.update(1.0);
        refresher.update(1.0);

        Assert.assertNull(started.get());

        mouse.setClick(1);
        cursor.update(1.0);
        refresher.update(1.0);

        Assert.assertEquals(Geom.createRectangle(1.0, -1.0, 0, 0.0), started.get());

        mouse.move(10, -20);
        cursor.update(1.0);
        refresher.update(1.0);

        Assert.assertNull(done.get());

        mouse.setClick(0);
        cursor.update(1.0);
        refresher.update(1.0);

        Assert.assertEquals(Geom.createRectangle(1.0, -1.0, 10, 20.0), done.get());
    }
}
