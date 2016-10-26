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
package com.b3dgs.lionengine.game.state;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the animation configuration class.
 */
public class AnimationConfigTest
{
    /** Test configuration. */
    private static Media media;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        media = Medias.create("animations.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(media.getFile().delete());
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(AnimationConfig.class);
    }

    /**
     * Test the export and import.
     */
    @Test
    public void testExportsImports()
    {
        final XmlNode root = Xml.create("test");
        final Animation animation1 = Anim.createAnimation("anim1", 1, 2, 3.0, false, true);
        final Animation animation2 = Anim.createAnimation("anim2", 4, 5, 6.0, true, false);
        AnimationConfig.exports(root, animation1);
        AnimationConfig.exports(root, animation2);
        Xml.save(root, media);

        final AnimationConfig imported = AnimationConfig.imports(new Setup(media));

        Assert.assertEquals(animation1, imported.getAnimation("anim1"));
        Assert.assertEquals(animation2, imported.getAnimation("anim2"));
        Assert.assertTrue(imported.getAnimations().containsAll(Arrays.asList(animation1, animation2)));

        imported.clear();

        Assert.assertFalse(imported.getAnimations().containsAll(Arrays.asList(animation1, animation2)));

        try
        {
            Assert.assertNull(imported.getAnimation("void"));
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }
}
