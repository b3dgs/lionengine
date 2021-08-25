/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.ContextMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link Hud}.
 */
final class HudTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(HudTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        config = Medias.create("Object.xml");
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
        Medias.setLoadFromJar(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Prepare test.
     */
    @BeforeEach
    public void before()
    {
        services.add(new ContextMock());
        services.add(new Handler(services));
        services.add(new Factory(services));
        services.add(new ViewerMock());
        services.add(new Cursor(services));
        services.add(new ComponentCollision());
    }

    /**
     * Test cancel with listener.
     */
    @Test
    void testListenerCancel()
    {
        final AtomicBoolean canceled = new AtomicBoolean();
        final HudListener listener = new HudListener()
        {
            @Override
            public void notifyCreated(List<Selectable> selection, Actionable actionable)
            {
                // Nothing to do
            }

            @Override
            public void notifyCanceled()
            {
                canceled.set(true);
            }
        };
        final Hud hud = new Hud(services, setup);
        hud.addListener(listener);

        final AtomicBoolean cancel = new AtomicBoolean();
        hud.setCancelShortcut(cancel::get);

        hud.getFeature(Refreshable.class).update(1.0);

        assertFalse(canceled.get());

        cancel.set(true);
        hud.getFeature(Refreshable.class).update(1.0);

        assertTrue(canceled.get());
        assertEquals(0, hud.getActive().size());

        hud.removeListener(listener);
    }
}
