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
package com.b3dgs.lionengine.game.collision.object;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.object.ComponentUpdater;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.UtilSetup;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;

/**
 * Test the component collision model class.
 */
public class ComponentCollisionTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(config.getFile().delete());
        Graphics.setFactoryGraphic(null);
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test collidable class.
     */
    @Test
    public void testCollidable()
    {
        final Services services = new Services();
        services.add(new Camera());

        final ObjectGame object1 = CollidableModelTest.createObject(config, services);
        final ObjectGame object2 = CollidableModelTest.createObject(config, services);

        final Collidable collidable1 = object1.getTrait(Collidable.class);
        final Collidable collidable2 = object2.getTrait(Collidable.class);

        final Collision collision1 = new Collision("test1", 0, 0, 3, 3, false);
        collidable1.addCollision(collision1);

        final Collision collision2 = new Collision("test2", 0, 0, 3, 3, false);
        collidable2.addCollision(collision2);

        final ComponentCollision component = new ComponentCollision();
        final Handler handler = new Handler();
        handler.addUpdatable(new ComponentUpdater());
        handler.addUpdatable(component);
        handler.add(object1);
        handler.add(object2);

        final AtomicReference<ObjectGame> collide = new AtomicReference<ObjectGame>();
        final CollidableListener listener = new CollidableListener()
        {
            @Override
            public void notifyCollided(ObjectGame object)
            {
                collide.set(object);
            }
        };
        collidable2.addListener(listener);
        collidable1.update(1.0);
        collidable2.update(1.0);
        handler.update(1.0);

        Assert.assertEquals(object1, collide.get());

        collide.set(null);
        object1.getTrait(Transformable.class).teleport(10.0, 10.0);
        collidable1.update(1.0);
        handler.update(1.0);

        Assert.assertNull(collide.get());
    }
}
