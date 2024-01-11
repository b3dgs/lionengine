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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Mirror;

/**
 * Test {@link MirrorableModel}.
 */
final class MirrorableModelTest
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
        config = UtilSetup.createConfig(MirrorableModelTest.class);
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

    /**
     * Test the mirror.
     */
    @Test
    void testMirror()
    {
        final AtomicReference<Mirror> o = new AtomicReference<>();
        final AtomicReference<Mirror> n = new AtomicReference<>();
        final MirrorableModel mirrorable = new MirrorableModel(services, setup);
        mirrorable.addListener((om, nm) ->
        {
            o.set(om);
            n.set(nm);
        });

        assertEquals(Mirror.NONE, mirrorable.getMirror());
        assertNull(o.get());
        assertNull(n.get());

        mirrorable.recycle();

        assertEquals(Mirror.NONE, mirrorable.getMirror());
        assertTrue(mirrorable.is(Mirror.NONE));
        assertNull(o.get());
        assertNull(n.get());

        mirrorable.updateBefore();
        mirrorable.update(1.0);
        mirrorable.updateAfter();

        assertEquals(Mirror.NONE, mirrorable.getMirror());
        assertNull(o.get());
        assertNull(n.get());

        mirrorable.mirror(Mirror.HORIZONTAL);

        assertEquals(Mirror.NONE, mirrorable.getMirror());
        assertNull(o.get());
        assertNull(n.get());

        mirrorable.updateBefore();
        mirrorable.update(1.0);
        mirrorable.updateAfter();

        assertEquals(Mirror.HORIZONTAL, mirrorable.getMirror());
        assertTrue(mirrorable.is(Mirror.HORIZONTAL));
        assertEquals(Mirror.NONE, o.get());
        assertEquals(Mirror.HORIZONTAL, n.get());

        mirrorable.mirror(Mirror.VERTICAL);

        assertEquals(Mirror.HORIZONTAL, mirrorable.getMirror());
        assertEquals(Mirror.NONE, o.get());
        assertEquals(Mirror.HORIZONTAL, n.get());

        mirrorable.updateBefore();
        mirrorable.update(1.0);
        mirrorable.updateAfter();

        assertEquals(Mirror.VERTICAL, mirrorable.getMirror());
        assertTrue(mirrorable.is(Mirror.VERTICAL));
        assertFalse(mirrorable.is(Mirror.HORIZONTAL));
        assertEquals(Mirror.HORIZONTAL, o.get());
        assertEquals(Mirror.VERTICAL, n.get());
    }
}
