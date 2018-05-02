/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Test {@link ComponentRenderable}.
 */
public final class ComponentRenderableTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(HandlerTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the renderable.
     */
    @Test
    public void testRenderable()
    {
        final ComponentRenderable renderable = new ComponentRenderable();
        final Handler handler = new Handler(new Services());
        handler.addComponent(renderable);

        final Renderer object = new Renderer();
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
        /** Rendered flag. */
        private boolean rendered;

        /**
         * Constructor.
         */
        public Renderer()
        {
            super();

            addFeature(new IdentifiableModel());
        }

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
