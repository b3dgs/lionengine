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
package com.b3dgs.lionengine.game.feature.producible;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.ActionRef;
import com.b3dgs.lionengine.game.ActionsConfig;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Test {@link ActionsConfig}.
 */
public final class ProducerConfigTest // TODO rename to ActionsConfig
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
        assertPrivateConstructor(ActionsConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    public void testExportsImports()
    {
        final ActionRef ref = new ActionRef("ref", false, new ArrayList<ActionRef>());
        final ActionRef ref2 = new ActionRef("ref", false, Arrays.asList(ref));
        final Collection<ActionRef> refs = Arrays.asList(new ActionRef("test", true, Arrays.asList(ref2)));

        final Xml root = new Xml("test");
        root.add(ActionsConfig.exports(refs));

        final Media media = Medias.create("producer.xml");
        root.save(media);

        assertEquals(refs, ActionsConfig.imports(new Xml(media)));
        assertEquals(refs, ActionsConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test cancel flag is not used on child reference.
     */
    @Test
    public void testCancelOnRef()
    {
        final ActionRef ref = new ActionRef("ref", true, new ArrayList<ActionRef>());
        final Collection<ActionRef> refs = Arrays.asList(new ActionRef("test", false, Arrays.asList(ref)));

        final Xml root = new Xml("test");
        root.add(ActionsConfig.exports(refs));

        final Media media = Medias.create("producer.xml");
        root.save(media);

        assertNotEquals(refs, ActionsConfig.imports(new Xml(media)));
        assertNotEquals(refs, ActionsConfig.imports(new Configurer(media)));

        assertTrue(media.getFile().delete());
    }

    /**
     * Test import without node.
     */
    @Test
    public void testImportNoNode()
    {
        final Media media = Medias.create("producer.xml");
        final Xml root = new Xml("test");
        root.save(media);

        assertTrue(ActionsConfig.imports(new Xml(media)).isEmpty());

        assertTrue(media.getFile().delete());
    }
}
