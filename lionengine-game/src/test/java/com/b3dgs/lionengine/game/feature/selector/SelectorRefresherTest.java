/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.selector;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.ContextMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.MouseMock;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilTransformable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectorListener;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectorModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectorRefresher;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Test {@link SelectorRefresher}.
 */
final class SelectorRefresherTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilTransformable.createMedia(SelectorRefresherTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);
    private final SelectorModel model = new SelectorModel();
    private final MouseMock mouse = new MouseMock();
    private final AtomicReference<Area> started = new AtomicReference<>();
    private final AtomicReference<Area> done = new AtomicReference<>();
    private final SelectorListener listener = new SelectorListener()
    {
        @Override
        public void notifySelectionStarted(Area selection)
        {
            started.set(selection);
        }

        @Override
        public void notifySelectionDone(Area selection)
        {
            done.set(selection);
        }
    };
    private Cursor cursor;
    private SelectorRefresher refresher;

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {

        services.add(new ContextMock());
        services.add(new Camera());
        services.add(new ViewerMock());

        cursor = services.create(Cursor.class);
        cursor.setSync(mouse);

        final Featurable featurable = new FeaturableModel(services, setup);
        featurable.addFeature(LayerableModel.class, services, setup);
        featurable.addFeature(TransformableModel.class, services, setup);
        featurable.addFeature(CollidableModel.class, services, setup);

        refresher = new SelectorRefresher(services, model);
        refresher.addListener(listener);
        refresher.prepare(featurable);
    }

    /**
     * Test the selecting flag.
     */
    @Test
    void testSelecting()
    {
        assertFalse(model.isSelecting());

        refresher.update(1.0);
        mouse.setClick(Integer.valueOf(1));
        refresher.update(1.0);

        assertFalse(model.isSelecting());

        model.setClickSelection(Integer.valueOf(1));
        refresher.update(1.0);

        assertTrue(model.isSelecting());
    }

    /**
     * Test the selection.
     */
    @Test
    void testSelection()
    {
        assertNull(started.get());
        assertNull(done.get());

        model.setClickSelection(Integer.valueOf(1));

        mouse.move(1, 1);
        cursor.update(1.0);
        refresher.update(1.0);

        assertNull(started.get());

        mouse.setClick(Integer.valueOf(1));
        cursor.update(1.0);
        refresher.update(1.0);

        assertEquals(Geom.createArea(1.0, 1.0, 0.0, 0.0), started.get());
        assertNull(done.get());

        mouse.move(10, 20);
        cursor.update(1.0);
        refresher.update(1.0);

        assertEquals(Geom.createArea(1.0, 1.0, 0.0, 0.0), started.get());
        assertNull(done.get());

        mouse.setClick(Integer.valueOf(0));
        cursor.update(1.0);
        refresher.update(1.0);

        assertEquals(Geom.createArea(1.0, 1.0, 0.0, 0.0), started.get());
        assertEquals(Geom.createArea(1.0, 1.0, 10.0, 20.0), done.get());
    }

    /**
     * Test the listener.
     */
    @Test
    void testListener()
    {
        model.setClickSelection(Integer.valueOf(1));

        mouse.move(1, 1);
        cursor.update(1.0);
        refresher.update(1.0);

        assertNull(started.get());

        mouse.setClick(Integer.valueOf(1));
        cursor.update(1.0);
        refresher.update(1.0);

        assertEquals(Geom.createArea(1.0, 1.0, 0.0, 0.0), started.get());

        refresher.removeListener(listener);
        started.set(null);

        mouse.setClick(Integer.valueOf(0));
        mouse.move(-1, -1);
        cursor.update(1.0);
        refresher.update(1.0);

        mouse.move(1, 1);
        cursor.update(1.0);
        refresher.update(1.0);

        assertNull(started.get());

        mouse.setClick(Integer.valueOf(1));
        cursor.update(1.0);
        refresher.update(1.0);

        assertNull(started.get());
    }
}
