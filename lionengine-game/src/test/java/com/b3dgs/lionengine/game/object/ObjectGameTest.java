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
package com.b3dgs.lionengine.game.object;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.trait.body.Body;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;

/**
 * Test the object game class.
 */
public class ObjectGameTest
{
    /** Object configuration file name. */
    private static final String OBJECT_XML = "object.xml";

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(ObjectGameTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        final Media config = Medias.create(OBJECT_XML);
        final ObjectGame object = new ObjectGame(new Setup(config), new Services());
        Assert.assertEquals(config, object.getConfigurer().getMedia());
        Assert.assertEquals(config, object.getMedia());
    }

    /**
     * Test the id.
     */
    @Test
    public void testId()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        Assert.assertNull(object.getId());

        object.setId(Integer.valueOf(0));
        Assert.assertEquals(Integer.valueOf(0), object.getId());
    }

    /**
     * Test the trait.
     */
    @Test
    public void testTrait()
    {
        final AtomicBoolean prepared = new AtomicBoolean();
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services())
        {
            @Override
            protected void onPrepared()
            {
                super.onPrepared();
                prepared.set(true);
            }
        };
        final TransformableModel transformable = object.addTrait(new TransformableModel());
        object.prepareTraits(new Services());

        Assert.assertEquals(object, transformable.getOwner());
        Assert.assertTrue(prepared.get());

        Assert.assertTrue(object.hasTrait(ObjectGame.class));
        Assert.assertNotNull(object.getTrait(ObjectGame.class));

        Assert.assertFalse(object.hasTrait(Body.class));
        Assert.assertTrue(object.hasTrait(Transformable.class));
        Assert.assertEquals(transformable, object.getTrait(Transformable.class));

        for (final Trait trait : object.getTraits())
        {
            Assert.assertEquals(transformable, trait);
        }

        for (final Class<? extends Trait> trait : object.getTraitsType())
        {
            Assert.assertEquals(Transformable.class, trait);
        }
    }

    /**
     * Test the types.
     */
    @Test
    public void testTypes()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        object.addType(new TransformableModel());
        object.addType(Boolean.TRUE);

        Assert.assertFalse(object.hasTrait(Body.class));
        Assert.assertFalse(object.hasTrait(Integer.class));
        Assert.assertTrue(object.hasTrait(Boolean.class));
        Assert.assertEquals(Boolean.TRUE, object.getTrait(Boolean.class));
    }

    /**
     * Test the trait not found.
     */
    @Test(expected = LionEngineException.class)
    public void testTraitNotFound()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        object.getTrait(Trait.class);
    }

    /**
     * Test destroy.
     */
    @Test
    public void testDestroy()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        final AtomicBoolean destroyed = new AtomicBoolean();
        object.addListener(new ObjectGameListener()
        {
            @Override
            public void notifyDestroyed(Integer objectId)
            {
                destroyed.set(true);
            }
        });
        Assert.assertFalse(destroyed.get());
        object.destroy();

        Assert.assertTrue(destroyed.get());

        destroyed.set(false);
        object.destroy();
        Assert.assertFalse(destroyed.get());
    }
}
