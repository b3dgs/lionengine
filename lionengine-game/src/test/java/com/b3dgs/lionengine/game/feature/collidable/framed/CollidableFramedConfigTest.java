/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.framed;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.collidable.Collision;

/**
 * Test {@link CollidableFramedConfig}.
 */
final class CollidableFramedConfigTest
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
     * Test no node.
     */
    @Test
    void testNoNode()
    {
        final Xml root = new Xml("test");

        assertTrue(CollidableFramedConfig.imports(root).getCollisions().isEmpty());
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Map<Integer, List<Collision>> collisions = new HashMap<>();
        collisions.put(Integer.valueOf(1), Arrays.asList(new Collision("anim%1", 0, 1, 2, 3, true)));

        final CollidableFramedConfig config = new CollidableFramedConfig(collisions);

        final Xml root = new Xml("test");
        final Animation animation = new Animation("anim", 1, 2, 3.0, false, true);
        AnimationConfig.exports(root, animation);

        final Xml framed = root.getChildXml(AnimationConfig.NODE_ANIMATIONS)
                               .getChildXml(AnimationConfig.NODE_ANIMATION);
        CollidableFramedConfig.exports(framed, collisions);

        final Media media = Medias.create("Object.xml");
        root.save(media);

        assertEquals(config, CollidableFramedConfig.imports(new Configurer(media)));
        assertEquals(collisions.values().iterator().next(), config.getCollisions());
        assertEquals(collisions.get(Integer.valueOf(1)), config.getCollision(Integer.valueOf(1)));
        assertTrue(config.getCollision(Integer.valueOf(2)).isEmpty());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test exports imports with number.
     */
    @Test
    void testExportsImportsNumber()
    {
        final Map<Integer, List<Collision>> collisions = new HashMap<>();
        collisions.put(Integer.valueOf(1), Arrays.asList(new Collision("coll%anim%1", 0, 1, 2, 3, true)));

        final Xml root = new Xml("test");
        final Animation animation = new Animation("anim", 1, 2, 3.0, false, true);
        AnimationConfig.exports(root, animation);

        final Xml framed = root.getChildXml(AnimationConfig.NODE_ANIMATIONS)
                               .getChildXml(AnimationConfig.NODE_ANIMATION);
        CollidableFramedConfig.exports(framed, collisions);
        framed.getChildXml(CollidableFramedConfig.NODE_COLLISION_FRAMED)
              .removeAttribute(CollidableFramedConfig.ATT_NUMBER);
        framed.getChildXml(CollidableFramedConfig.NODE_COLLISION_FRAMED)
              .writeString(CollidableFramedConfig.ATT_PREFIX, "coll");

        final Media media = Medias.create("Object.xml");
        root.save(media);

        final CollidableFramedConfig imported = CollidableFramedConfig.imports(new Configurer(media));

        assertEquals(collisions.get(Integer.valueOf(1)), imported.getCollision(Integer.valueOf(1)));
        assertFalse(imported.getCollision(Integer.valueOf(2)).isEmpty());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Map<Integer, List<Collision>> collisions = new HashMap<>();
        collisions.put(Integer.valueOf(1), Arrays.asList(new Collision("anim%1", 0, 1, 2, 3, true)));
        collisions.put(Integer.valueOf(2), Arrays.asList(new Collision("anim%2", 3, 2, 1, 0, false)));

        final CollidableFramedConfig config = new CollidableFramedConfig(collisions);

        assertEquals(config, config);
        assertEquals(config, new CollidableFramedConfig(collisions));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new CollidableFramedConfig(new HashMap<>()));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final Map<Integer, List<Collision>> collisions = new HashMap<>();
        collisions.put(Integer.valueOf(1), Arrays.asList(new Collision("anim%1", 0, 1, 2, 3, true)));
        collisions.put(Integer.valueOf(2), Arrays.asList(new Collision("anim%2", 3, 2, 1, 0, false)));

        final CollidableFramedConfig config = new CollidableFramedConfig(collisions);

        assertHashEquals(config, config);
        assertHashEquals(config, new CollidableFramedConfig(collisions));

        assertHashNotEquals(config, new Object());
        assertHashNotEquals(config, new CollidableFramedConfig(new HashMap<>()));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        final Map<Integer, List<Collision>> collisions = new HashMap<>();
        collisions.put(Integer.valueOf(1), Arrays.asList(new Collision("anim%1", 0, 1, 2, 3, true)));

        final CollidableFramedConfig config = new CollidableFramedConfig(collisions);

        assertEquals("CollidableFramedConfig"
                     + "{1=[Collision [name=anim%1, offsetX=0, offsetY=1, width=2, height=3, mirror=true]]}",
                     config.toString());
    }
}
