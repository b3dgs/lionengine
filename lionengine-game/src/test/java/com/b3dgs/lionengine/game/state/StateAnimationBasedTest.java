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
package com.b3dgs.lionengine.game.state;

import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Setup;

/**
 * Test {@link StateAnimationBased}.
 */
public final class StateAnimationBasedTest
{
    /** Test configuration. */
    private static Media media;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
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
    @AfterAll
    public static void afterTests()
    {
        assertTrue(media.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the constructor.
     */
    @Test
    public void testConstructor()
    {
        assertPrivateConstructor(StateAnimationUtil.class);
    }

    /**
     * Test the utility function.
     */
    @Test
    public void testUtil()
    {
        final StateFactory factory = new StateFactory();
        final StateHandler handler = new StateHandler(factory);

        final Featurable featurable = new FeaturableModel();
        StateAnimationUtil.loadStates(StateType.values(), factory, featurable, new Setup(media));

        handler.changeState(StateType.IDLE);

        assertTrue(handler.isState(StateType.IDLE));

        handler.update(1.0);

        assertTrue(handler.isState(StateType.WALK));
    }

    /**
     * Test the utility function with wrong constructor.
     */
    @Test
    public void testUtilError()
    {
        final Featurable featurable = new FeaturableModel();
        final String message = NoSuchMethodException.class.getName()
                               + ": No compatible constructor found for "
                               + StateJump.class.getName()
                               + " with: "
                               + Arrays.asList(FeaturableModel.class, Animation.class);

        assertThrows(() -> StateAnimationUtil.loadStates(StateTypeError.values(),
                                                         new StateFactory(),
                                                         featurable,
                                                         new Setup(media)),
                     message);
    }
}
