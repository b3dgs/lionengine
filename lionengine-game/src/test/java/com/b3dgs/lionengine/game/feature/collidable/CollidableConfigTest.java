/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

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
public final class CollidableConfigTest
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
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(CollidableConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final String group = "1";
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild("lionengine:group");
        node.setText(group);
        root.save(media);

        assertEquals(Integer.valueOf(group), CollidableConfig.imports(new Configurer(media)));
        assertTrue(media.getFile().delete());
    }

    /**
     * Test with invalid group.
     */
    @Test
    public void testInvalidGroup()
    {
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        final Xml node = root.createChild("lionengine:group");
        node.setText("a");
        root.save(media);

        assertThrows(() -> CollidableConfig.imports(new Configurer(media)), CollidableConfig.ERROR_INVALID_GROUP + "a");
        assertTrue(media.getFile().delete());
    }

    /**
     * Test with default group.
     */
    @Test
    public void testDefaultGroup()
    {
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        assertEquals(CollidableConfig.DEFAULT_GROUP, CollidableConfig.imports(new Configurer(media)));
        assertTrue(media.getFile().delete());
    }

    /**
     * Test export.
     */
    @Test
    public void testExport()
    {
        final Media media = Medias.create("object.xml");
        final Xml root = new Xml("test");
        root.save(media);

        final Services services = new Services();
        services.add(new ViewerMock());

        final Collidable collidable = new CollidableModel(services, new Setup(media));
        collidable.setGroup(Integer.valueOf(1));
        CollidableConfig.exports(root, collidable);

        root.save(media);

        assertEquals(Integer.valueOf(1), CollidableConfig.imports(new Configurer(media)));
        assertTrue(media.getFile().delete());
    }
}
