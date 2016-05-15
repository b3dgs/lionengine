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
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the animation based state.
 */
public class StateAnimationBasedTest
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
        createConfig();
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
        UtilTests.testPrivateConstructor(StateAnimationBased.Util.class);
    }

    /**
     * Create animation configuration.
     */
    private static void createConfig()
    {
        final XmlNode root = Xml.create("test");
        final Animation idle = Anim.createAnimation("idle", 1, 2, 3.0, false, true);
        final Animation walk = Anim.createAnimation("walk", 1, 2, 3.0, false, true);
        final Animation jump = Anim.createAnimation("jump", 1, 2, 3.0, false, true);
        AnimationConfig.exports(root, idle);
        AnimationConfig.exports(root, walk);
        AnimationConfig.exports(root, jump);
        Xml.save(root, media);
    }

    /**
     * Test the utility function.
     */
    @Test
    public void testUtil()
    {
        final StateFactory factory = new StateFactory();
        final StateHandler handler = new StateHandler(factory);
        handler.addInput(new InputDirectionalMock());
        handler.addInput(new InputPointerMock());

        final ObjectGame object = new ObjectGame(new Setup(media));
        StateAnimationBased.Util.loadStates(StateType.values(), factory, object);

        handler.changeState(StateType.IDLE);

        Assert.assertTrue(handler.isState(StateType.IDLE));

        handler.update(1.0);

        Assert.assertTrue(handler.isState(StateType.WALK));
    }

    /**
     * Test the utility function with wrong constructor.
     */
    @Test(expected = LionEngineException.class)
    public void testUtilError()
    {
        final ObjectGame object = new ObjectGame(new Setup(media));
        StateAnimationBased.Util.loadStates(StateTypeError.values(), new StateFactory(), object);
    }
}
