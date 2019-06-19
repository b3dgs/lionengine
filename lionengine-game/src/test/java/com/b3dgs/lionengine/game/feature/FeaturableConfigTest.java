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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;

/**
 * Test {@link FeaturableConfig}.
 */
public final class FeaturableConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final String clazz = "class";
        final String setup = "setup";
        final FeaturableConfig config = new FeaturableConfig(clazz, setup);

        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass(clazz));
        root.add(FeaturableConfig.exportSetup(setup));

        final Media media = Medias.create("object.xml");
        root.save(media);

        assertEquals(config, FeaturableConfig.imports(new Xml(media)));
        assertEquals(config, FeaturableConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test with default class.
     */
    @Test
    public void testDefaultClass()
    {
        final Xml root = new Xml("test");
        final Media media = Medias.create("object.xml");
        root.save(media);

        assertEquals(new FeaturableConfig(FeaturableModel.class.getName(), ""),
                     FeaturableConfig.imports(new Xml(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test with default setup.
     */
    @Test
    public void testDefaultSetup()
    {
        final Xml root = new Xml("test");
        root.add(FeaturableConfig.exportClass("clazz"));

        final Media media = Medias.create("object.xml");
        root.save(media);

        assertEquals(new FeaturableConfig("clazz", ""), FeaturableConfig.imports(new Xml(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test with <code>null</code> class.
     */
    @Test
    public void testNullClass()
    {
        assertThrows(() -> new FeaturableConfig(null, "setup"), "Unexpected null argument !");
    }

    /**
     * Test with <code>null</code> setup.
     */
    @Test
    public void testNullSetup()
    {
        assertThrows(() -> new FeaturableConfig("class", null), "Unexpected null argument !");
    }

    /**
     * Test with cached feature class.
     */
    @Test
    public void testGetFeaturesCache()
    {
        final Xml root = new Xml("test");
        root.createChild(FeaturableConfig.NODE_FEATURE).setText(FeatureModel.class.getName());
        final Media media = Medias.create("object.xml");
        root.save(media);

        final ClassLoader loader = ClassLoader.getSystemClassLoader();
        final Services services = new Services();
        final Setup setup = new Setup(media);

        final List<Feature> a = FeaturableConfig.getFeatures(loader, services, setup);
        final List<Feature> b = FeaturableConfig.getFeatures(loader, services, setup);

        assertNotEquals(a.get(0), b.get(0));
    }

    /**
     * Test with unknown feature class.
     */
    @Test
    public void testGetFeaturesUnknown()
    {
        final Xml root = new Xml("test");
        root.createChild(FeaturableConfig.NODE_FEATURE).setText(Feature.class.getName());
        final Media media = Medias.create("object.xml");
        root.save(media);

        assertCause(() -> FeaturableConfig.getFeatures(ClassLoader.getSystemClassLoader(),
                                                       new Services(),
                                                       new Setup(media)),
                    NoSuchMethodException.class);
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final FeaturableConfig config = new FeaturableConfig("class", "setup");

        assertEquals(config, config);
        assertEquals(config, new FeaturableConfig("class", "setup"));

        assertNotEquals(config, null);
        assertNotEquals(config, new Object());
        assertNotEquals(config, new FeaturableConfig("", "setup"));
        assertNotEquals(config, new FeaturableConfig("class", ""));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final FeaturableConfig hash = new FeaturableConfig("class", "setup");

        assertHashEquals(hash, new FeaturableConfig("class", "setup"));

        assertHashNotEquals(hash, new FeaturableConfig("", "setup"));
        assertHashNotEquals(hash, new FeaturableConfig("class", ""));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        final FeaturableConfig config = new FeaturableConfig("clazz", "setup");

        assertEquals("FeaturableConfig [clazz=clazz, setup=setup]", config.toString());
    }
}
