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
package com.b3dgs.lionengine.game.feature.collidable;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertIterableEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link CollidableConfig}.
 */
final class CollidableConfigTest
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
        final int group = 1;
        final Media media = Medias.create("Object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild(CollidableConfig.NODE_COLLIDABLE);
        node.writeInteger(CollidableConfig.ATT_GROUP, group);
        node.writeString(CollidableConfig.ATT_ACCEPTED, "1%2");
        root.save(media);

        final CollidableConfig config = CollidableConfig.imports(new Configurer(media));
        assertEquals(1, config.getGroup().intValue());
        assertIterableEquals(Arrays.asList(Integer.valueOf(1), Integer.valueOf(2)), config.getAccepted());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test with invalid group.
     */
    @Test
    void testInvalidGroup()
    {
        final Media media = Medias.create("Object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild(CollidableConfig.NODE_COLLIDABLE);
        node.writeString(CollidableConfig.ATT_GROUP, "a");
        root.save(media);

        assertCause(() -> CollidableConfig.imports(new Configurer(media)), NumberFormatException.class);
        assertTrue(media.getFile().delete());
    }

    /**
     * Test with default group.
     */
    @Test
    void testDefaultGroup()
    {
        final Media media = Medias.create("Object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        assertEquals(CollidableConfig.DEFAULT_GROUP, CollidableConfig.imports(new Configurer(media)).getGroup());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test with empty accepted.
     */
    @Test
    void testEmptyAccepted()
    {
        final Media media = Medias.create("Object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        final Services services = new Services();
        services.add(new ViewerMock());

        final Collidable collidable = new CollidableModel(services, new Setup(media));
        collidable.setGroup(Integer.valueOf(1));
        CollidableConfig.exports(root, collidable);

        root.save(media);

        final CollidableConfig config = CollidableConfig.imports(new Configurer(media));
        assertEquals(Integer.valueOf(1), config.getGroup());
        assertTrue(config.getAccepted().isEmpty());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test export.
     */
    @Test
    void testExport()
    {
        final Media media = Medias.create("Object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        final Services services = new Services();
        services.add(new ViewerMock());

        final Collidable collidable = new CollidableModel(services, new Setup(media));
        collidable.setGroup(Integer.valueOf(1));
        collidable.addAccept(Integer.valueOf(2));
        collidable.addAccept(Integer.valueOf(3));
        CollidableConfig.exports(root, collidable);

        root.save(media);

        final CollidableConfig config = CollidableConfig.imports(new Configurer(media));
        assertEquals(Integer.valueOf(1), config.getGroup());
        assertIterableEquals(Arrays.asList(Integer.valueOf(2), Integer.valueOf(3)), config.getAccepted());
        assertTrue(media.getFile().delete());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final CollidableConfig config = new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(2)));

        assertEquals(config, config);
        assertEquals(config, new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(2))));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new CollidableConfig(Integer.valueOf(0), Arrays.asList(Integer.valueOf(2))));
        assertNotEquals(config, new CollidableConfig(Integer.valueOf(1), Collections.emptyList()));
        assertNotEquals(config, new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(0))));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final CollidableConfig config = new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(2)));

        assertHashEquals(config, config);
        assertHashEquals(config, new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(2))));

        assertHashNotEquals(config, new Object());
        assertHashNotEquals(config, new CollidableConfig(Integer.valueOf(0), Arrays.asList(Integer.valueOf(2))));
        assertHashNotEquals(config, new CollidableConfig(Integer.valueOf(1), Collections.emptyList()));
        assertHashNotEquals(config, new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(0))));
    }

    /**
     * Test the to string.
     */
    @Test
    void testToString()
    {
        final CollidableConfig config = new CollidableConfig(Integer.valueOf(1), Arrays.asList(Integer.valueOf(2)));

        assertEquals(CollidableConfig.class.getSimpleName() + " [group=1, accepted=[2]]", config.toString());
    }
}
