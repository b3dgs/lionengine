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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Test {@link ActionConfig}.
 */
final class ActionConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final ActionConfig action = new ActionConfig("name", "description", 0, 1, 16, 32);
        final Xml root = new Xml("test");
        root.add(ActionConfig.exports(action));

        final Media media = Medias.create("action.xml");
        root.save(media);

        assertEquals(action, ActionConfig.imports(new Xml(media)));
        assertEquals(action, ActionConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final ActionConfig action = new ActionConfig("a", "b", 0, 1, 2, 3);

        assertEquals(action, action);

        assertNotEquals(action, null);
        assertNotEquals(action, new Object());
        assertNotEquals(action, new ActionConfig("", "b", 0, 1, 2, 3));
        assertNotEquals(action, new ActionConfig("a", "", 0, 1, 2, 3));
        assertNotEquals(action, new ActionConfig("a", "b", -1, 1, 2, 3));
        assertNotEquals(action, new ActionConfig("a", "b", 0, -1, 2, 3));
        assertNotEquals(action, new ActionConfig("a", "b", 0, 1, -1, 3));
        assertNotEquals(action, new ActionConfig("a", "b", 0, 1, 2, -1));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final ActionConfig action = new ActionConfig("a", "b", 0, 1, 2, 3);

        assertHashEquals(action, new ActionConfig("a", "b", 0, 1, 2, 3));

        assertHashNotEquals(action, new ActionConfig("", "b", 0, 1, 2, 3));
        assertHashNotEquals(action, new ActionConfig("a", "", 0, 1, 2, 3));
        assertHashNotEquals(action, new ActionConfig("a", "b", -1, 1, 2, 3));
        assertHashNotEquals(action, new ActionConfig("a", "b", 0, -1, 2, 3));
        assertHashNotEquals(action, new ActionConfig("a", "b", 0, 1, -1, 3));
        assertHashNotEquals(action, new ActionConfig("a", "b", 0, 1, 2, -1));
    }

    /**
     * Test the to string.
     */
    @Test
    void testToString()
    {
        final ActionConfig action = new ActionConfig("a", "b", 0, 1, 2, 3);

        assertEquals("ActionConfig [name=a, description=b, x=0, y=1, width=2, height=3]", action.toString());
    }
}
