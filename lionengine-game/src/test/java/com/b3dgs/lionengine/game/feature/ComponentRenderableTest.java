/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Test {@link ComponentRenderable}.
 */
final class ComponentRenderableTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(ComponentRenderableTest.class.getSimpleName(), Version.DEFAULT));

        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Medias.setLoadFromJar(ComponentRenderableTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        config = UtilTransformable.createMedia(ComponentRenderableTest.class);
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);

        Engine.terminate();
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the renderable.
     */
    @Test
    void testRenderable()
    {
        final ComponentRenderable renderable = new ComponentRenderable();
        final Handler handler = new Handler(services);
        handler.addComponent(renderable);

        final Renderer object = new Renderer(services, setup);
        handler.add(object);

        assertFalse(object.isRendered());
        handler.update(1.0);

        assertFalse(object.isRendered());

        handler.render(null);

        assertTrue(object.isRendered());

        handler.removeAll();
        handler.update(1.0);
    }

    /**
     * Renderable object mock.
     */
    private static final class Renderer extends FeaturableModel implements Renderable
    {
        /**
         * Constructor.
         * 
         * @param services The services reference.
         * @param setup The setup reference.
         */
        private Renderer(Services services, Setup setup)
        {
            super(services, setup);
        }

        /** Rendered flag. */
        private boolean rendered;

        /**
         * Check if has been rendered.
         * 
         * @return <code>true</code> if rendered, <code>false</code> else.
         */
        public boolean isRendered()
        {
            return rendered;
        }

        @Override
        public void render(Graphic g)
        {
            rendered = true;
        }
    }
}
