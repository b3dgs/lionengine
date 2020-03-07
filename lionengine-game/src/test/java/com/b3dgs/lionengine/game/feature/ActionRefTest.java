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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;

/**
 * Test {@link ActionRef}.
 */
public final class ActionRefTest
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
        config = UtilTransformable.createMedia(ActionRefTest.class);
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

    private final boolean cancel = true;
    private final FeaturableModel model = new FeaturableModel(services, setup);
    private final ActionRef actionRef1 = new ActionRef("path", !cancel, new ArrayList<ActionRef>());
    private final ActionRef actionRef2 = new ActionRef("path2", cancel, Arrays.asList(actionRef1), model::getFeature);

    /**
     * Test constructor with <code>null</code> path.
     */
    @Test
    public void testConstructorNullPath()
    {
        assertThrows(() -> new ActionRef(null, false, Collections.emptyList()), "Unexpected null argument !");
    }

    /**
     * Test constructor with <code>null</code> refs.
     */
    @Test
    public void testConstructorNullRefs()
    {
        assertThrows(() -> assertNull(new ActionRef(Constant.EMPTY_STRING, false, null)), "Unexpected null argument !");
    }

    /**
     * Test getter.
     */
    @Test
    public void testGetter()
    {
        assertEquals("path", actionRef1.getPath());
        assertEquals("path2", actionRef2.getPath());

        assertFalse(actionRef1.hasCancel());
        assertTrue(actionRef2.hasCancel());

        assertFalse(actionRef1.isUnique());
        assertTrue(actionRef2.isUnique());

        assertTrue(actionRef1.getRefs().isEmpty());
        assertEquals(actionRef1, actionRef2.getRefs().iterator().next());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final FeaturableModel featurable = new FeaturableModel(services, setup);

        assertEquals(actionRef1, actionRef1);
        assertEquals(actionRef1, new ActionRef("path", !cancel, new ArrayList<ActionRef>()));

        assertEquals(actionRef2, actionRef2);
        assertEquals(actionRef2, new ActionRef("path2", cancel, Arrays.asList(actionRef1), model::getFeature));
        assertEquals(new ActionRef("path2", cancel, Arrays.asList(actionRef1), featurable::getFeature),
                     new ActionRef("path2", cancel, Arrays.asList(actionRef1), featurable::getFeature));

        assertNotEquals(actionRef1, null);
        assertNotEquals(actionRef1, new Object());
        assertNotEquals(actionRef1, actionRef2);
        assertNotEquals(actionRef2, actionRef1);

        assertNotEquals(actionRef1, new ActionRef("", !cancel, new ArrayList<ActionRef>()));
        assertNotEquals(actionRef1, new ActionRef("path", cancel, new ArrayList<ActionRef>()));
        assertNotEquals(actionRef1, new ActionRef("path", cancel, Arrays.asList(actionRef1)));
        assertNotEquals(actionRef1, new ActionRef("path", !cancel, Arrays.asList(actionRef1)));
        assertNotEquals(new ActionRef("path2", cancel, Arrays.asList(actionRef1), featurable::getFeature),
                        new ActionRef("path2",
                                      cancel,
                                      Arrays.asList(actionRef1),
                                      new FeaturableModel(services, setup)::getFeature));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final ActionRef hash = actionRef1;
        final FeaturableModel featurable = new FeaturableModel(services, setup);

        assertHashEquals(hash, new ActionRef("path", !cancel, new ArrayList<ActionRef>()));

        assertHashEquals(actionRef2, actionRef2);
        assertHashEquals(actionRef2, new ActionRef("path2", cancel, Arrays.asList(actionRef1), model::getFeature));
        assertHashEquals(new ActionRef("path2", cancel, Arrays.asList(actionRef1), featurable::getFeature),
                         new ActionRef("path2", cancel, Arrays.asList(actionRef1), featurable::getFeature));

        assertHashNotEquals(hash, new Object());
        assertHashNotEquals(hash, actionRef2);
        assertHashNotEquals(actionRef2, hash);

        assertHashNotEquals(hash, new ActionRef("", !cancel, new ArrayList<ActionRef>()));
        assertHashNotEquals(hash, new ActionRef("path", cancel, new ArrayList<ActionRef>()));
        assertHashNotEquals(hash, new ActionRef("path", cancel, Arrays.asList(actionRef1)));
        assertHashNotEquals(hash, new ActionRef("path", !cancel, Arrays.asList(actionRef1)));
        assertHashNotEquals(new ActionRef("path2", cancel, Arrays.asList(actionRef1), featurable::getFeature),
                            new ActionRef("path2",
                                          cancel,
                                          Arrays.asList(actionRef1),
                                          new FeaturableModel(services, setup)::getFeature));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("ActionRef [path=path2, cancel=true, refs=[ActionRef [path=path, cancel=false, refs=[]]]]",
                     actionRef2.toString());
    }
}
