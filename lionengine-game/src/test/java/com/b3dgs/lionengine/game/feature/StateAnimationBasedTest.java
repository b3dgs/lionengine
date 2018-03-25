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
package com.b3dgs.lionengine.game.feature;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.StateFactory;
import com.b3dgs.lionengine.game.state.InputDirectionalMock;
import com.b3dgs.lionengine.game.state.InputPointerMock;
import com.b3dgs.lionengine.game.state.StateHandler;
import com.b3dgs.lionengine.game.state.StateType;
import com.b3dgs.lionengine.game.state.StateTypeError;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilTests;

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

        final Xml root = new Xml("test");

        final Animation idle = new Animation("idle", 1, 2, 3.0, false, true);
        AnimationConfig.exports(root, idle);

        final Animation walk = new Animation("walk", 1, 2, 3.0, false, true);
        AnimationConfig.exports(root, walk);

        final Animation jump = new Animation("jump", 1, 2, 3.0, false, true);
        AnimationConfig.exports(root, jump);

        root.save(media);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(media.getFile().delete());
        Medias.setResourcesDirectory(null);
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
     * Test the utility function.
     */
    @Test
    public void testUtil()
    {
        final StateFactory factory = new StateFactory();
        final StateHandler handler = new StateHandler(factory);
        handler.addInput(new InputDirectionalMock());
        handler.addInput(new InputPointerMock());

        final Featurable featurable = new FeaturableModel();
        StateAnimationBased.Util.loadStates(StateType.values(), factory, featurable, new Setup(media));

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
        final Featurable featurable = new FeaturableModel();
        StateAnimationBased.Util.loadStates(StateTypeError.values(), new StateFactory(), featurable, new Setup(media));
    }
}
