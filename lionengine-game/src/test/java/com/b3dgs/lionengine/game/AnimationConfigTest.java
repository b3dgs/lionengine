/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link AnimationConfig}.
 */
final class AnimationConfigTest
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
     * Test with no node.
     */
    @Test
    void testNoNode()
    {
        final Xml root = new Xml("test");

        assertTrue(AnimationConfig.imports(root).getAnimations().isEmpty());
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Xml root = new Xml("test");
        final Animation animation1 = new Animation("anim1", 1, 2, 3.0, false, true);
        final Animation animation2 = new Animation("anim2", 4, 5, 6.0, true, false);
        AnimationConfig.exports(root, animation1);
        AnimationConfig.exports(root, animation2);

        final Media media = Medias.create("animations.xml");
        root.save(media);

        final AnimationConfig imported = AnimationConfig.imports(new Setup(media));

        assertEquals(animation1, imported.getAnimation("anim1"));
        assertEquals(animation2, imported.getAnimation("anim2"));
        assertTrue(imported.getAnimations().containsAll(Arrays.asList(animation1, animation2)));

        assertFalse(imported.hasAnimation("anim"));
        assertTrue(imported.hasAnimation("anim1"));
        assertTrue(imported.hasAnimation("anim2"));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test get unknown animation.
     */
    @Test
    void testGetUnknownAnimation()
    {
        final AnimationConfig config = new AnimationConfig(new HashMap<>());

        assertThrows(() -> config.getAnimation("void"), AnimationConfig.ERROR_NOT_FOUND + "void");
    }
}
