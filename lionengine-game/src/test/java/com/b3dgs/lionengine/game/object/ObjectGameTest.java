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
import com.b3dgs.lionengine.game.handler.Feature;
import com.b3dgs.lionengine.game.handler.IdentifiableListener;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.object.feature.body.Body;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
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
        root.add(SizeConfig.exports(new SizeConfig(16, 32)));
        Xml.save(root, media);
        return media;
    }

    /**
     * Free the object id.
     * 
     * @param object The object to free.
     */
    public static void notifyDestroyed(ObjectGame object)
    {
        object.notifyDestroyed();
    }

    /**
     * Test the config.
     */
    @Test
    public void testConfig()
    {
        final Media config = Medias.create(OBJECT_XML);
        final ObjectGame object = new ObjectGame(new Setup(config));
        Assert.assertEquals(config, object.getConfigurer().getMedia());

        object.destroy();
        object.notifyDestroyed();
    }

    /**
     * Test the features.
     */
    @Test
    public void testFeatures()
    {
        final AtomicBoolean prepared = new AtomicBoolean();
        final Services services = new Services();
        final Setup setup = new Setup(Medias.create(OBJECT_XML));
        final ObjectGame object = new ObjectGame(setup)
        {
            @Override
            protected void onPrepared()
            {
                super.onPrepared();
                prepared.set(true);
            }
        };
        final TransformableModel transformable = object.addFeatureAndGet(new TransformableModel(setup));
        object.prepareFeatures(object, services);

        Assert.assertEquals(object, transformable.getOwner());
        Assert.assertTrue(prepared.get());

        Assert.assertFalse(object.hasFeature(Body.class));
        Assert.assertTrue(object.hasFeature(Transformable.class));
        Assert.assertEquals(transformable, object.getFeature(Transformable.class));

        for (final Feature trait : object.getFeatures())
        {
            Assert.assertEquals(transformable, trait);
        }

        object.destroy();
        object.notifyDestroyed();
    }

    /**
     * Test the feature not found.
     */
    @Test(expected = LionEngineException.class)
    public void testFeatureNotFound()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        try
        {
            Assert.assertNotNull(object.getFeature(Feature.class));
        }
        finally
        {
            object.destroy();
            object.notifyDestroyed();
        }
    }

    /**
     * Test destroy.
     */
    @Test
    public void testDestroy()
    {
        final ObjectGame object = new ObjectGame(new Setup(Medias.create(OBJECT_XML)));
        final AtomicBoolean destroyed = new AtomicBoolean();
        object.addListener(new IdentifiableListener()
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

        object.notifyDestroyed();
    }
}
