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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.object.trait.body.Body;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.object.trait.transformable.TransformableModel;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

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
     * Create the object media.
     * 
     * @param clazz The class type.
     * @return The object media.
     */
    public static Media createMedia(Class<?> clazz)
    {
        final Media media = Medias.create(clazz.getName() + ".xml");
        final XmlNode root = Xml.create("test");
        root.add(ObjectConfig.exportClass(clazz.getName()));
        root.add(ObjectConfig.exportSetup("com.b3dgs.lionengine.game.object.Setup"));
        Xml.save(root, media);
        return media;
    }

    /**
     * Free the object id.
     * 
     * @param object The object to free.
     */
    public static void freeId(ObjectGame object)
    {
        object.freeId();
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

        object.destroy();
        object.freeId();
    }

    /**
     * Test the id.
     * 
     * @throws NoSuchFieldException If error.
     * @throws IllegalAccessException If error.
     */
    @Test
    public void testId() throws NoSuchFieldException, IllegalAccessException
    {
        final Collection<Integer> ids = UtilReflection.getField(ObjectGame.class, "IDS");
        ids.clear();
        final Collection<Integer> recycle = UtilReflection.getField(ObjectGame.class, "RECYCLE");
        recycle.clear();
        final Field field = ObjectGame.class.getDeclaredField("lastId");
        UtilReflection.setAccessible(field, true);
        field.set(ObjectGame.class, Integer.valueOf(0));

        final Collection<ObjectGame> objects = new ArrayList<ObjectGame>();
        for (int i = 0; i < 10; i++)
        {
            final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
            objects.add(object);
            Assert.assertEquals(Integer.valueOf(i), object.getId());
        }
        for (final ObjectGame object : objects)
        {
            object.destroy();
            object.freeId();
            Assert.assertNull(object.getId());
        }
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        Assert.assertEquals(Integer.valueOf(0), object.getId());
        object.destroy();
        object.freeId();
        Assert.assertNull(object.getId());
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

        object.destroy();
        object.freeId();
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

        object.destroy();
        object.freeId();
    }

    /**
     * Test the trait not found.
     */
    @Test(expected = LionEngineException.class)
    public void testTraitNotFound()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)), new Services());
        try
        {
            Assert.assertNotNull(object.getTrait(Trait.class));
        }
        finally
        {
            object.destroy();
            object.freeId();
        }
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

        object.freeId();
    }
}
