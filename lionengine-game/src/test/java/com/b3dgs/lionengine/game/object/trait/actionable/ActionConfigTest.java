/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object.trait.actionable;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.trait.actionable.ActionConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Test the action config class.
 */
public class ActionConfigTest
{
    /** Temp file name. */
    private static final String ACTION_XML = "action.xml";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the action configuration.
     */
    @Test
    public void testConfig()
    {
        final Media media = Medias.create(ACTION_XML);
        final ActionConfig action = new ActionConfig("name", "description", 0, 1, 16, 32);
        try
        {
            final XmlNode root = Xml.create("test");
            root.add(ActionConfig.exports(action));
            Xml.save(root, media);

            final ActionConfig loaded = ActionConfig.imports(Xml.load(media));
            Assert.assertEquals(action, loaded);
        }
        finally
        {
            Assert.assertTrue(media.getFile().delete());
        }
    }

    /**
     * Test the actionnable hash code.
     */
    @Test
    public void testHashCode()
    {
        final int action = new ActionConfig("a", "b", 0, 1, 2, 3).hashCode();

        Assert.assertNotEquals(action, new ActionConfig("", "b", 0, 1, 2, 3).hashCode());
        Assert.assertNotEquals(action, new ActionConfig("a", "", 0, 1, 2, 3).hashCode());
        Assert.assertNotEquals(action, new ActionConfig("a", "b", -1, 1, 2, 3).hashCode());
        Assert.assertNotEquals(action, new ActionConfig("a", "b", 0, -1, 2, 3).hashCode());
        Assert.assertNotEquals(action, new ActionConfig("a", "b", 0, 1, -1, 3).hashCode());
        Assert.assertNotEquals(action, new ActionConfig("a", "b", 0, 1, 2, -1).hashCode());
    }

    /**
     * Test the actionnable equality.
     */
    @Test
    public void testEquals()
    {
        final ActionConfig action = new ActionConfig("a", "b", 0, 1, 2, 3);

        Assert.assertEquals(action, action);
        Assert.assertNotEquals(action, null);
        Assert.assertNotEquals(action, new Object());
        Assert.assertNotEquals(action, new ActionConfig("", "b", 0, 1, 2, 3));
        Assert.assertNotEquals(action, new ActionConfig("a", "", 0, 1, 2, 3));
        Assert.assertNotEquals(action, new ActionConfig("a", "b", -1, 1, 2, 3));
        Assert.assertNotEquals(action, new ActionConfig("a", "b", 0, -1, 2, 3));
        Assert.assertNotEquals(action, new ActionConfig("a", "b", 0, 1, -1, 3));
        Assert.assertNotEquals(action, new ActionConfig("a", "b", 0, 1, 2, -1));
    }
}
