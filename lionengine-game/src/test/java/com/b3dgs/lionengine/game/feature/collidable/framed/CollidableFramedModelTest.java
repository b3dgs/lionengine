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
package com.b3dgs.lionengine.game.feature.collidable.framed;

import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.UtilSetup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;

/**
 * Test {@link CollidableFramedModel}.
 */
public final class CollidableFramedModelTest
{
    /** Test configuration. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(CollidableFramedModelTest.class);

        final Map<Integer, Collection<Collision>> collisions = new HashMap<>();
        collisions.put(Integer.valueOf(1), Arrays.asList(new Collision("anim%1", 0, 0, 2, 2, false)));

        final Xml root = new Xml(config);
        AnimationConfig.exports(root, new Animation("anim", 1, 2, 1.0, false, false));

        final Xml framed = root.getChild(AnimationConfig.ANIMATION);
        CollidableFramedConfig.exports(framed, collisions);

        root.save(config);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Graphics.setFactoryGraphic(null);
        Medias.setResourcesDirectory(null);
    }

    /**
     * Create a featurable test.
     * 
     * @param config The configuration reference.
     * @param services The services reference.
     * @return The featurable test.
     */
    public static FeaturableModel createFeaturable(Media config, Services services)
    {
        final Setup setup = new Setup(config);
        final FeaturableModel featurable = new FeaturableModel(services, setup);

        final Transformable transformable = featurable.addFeatureAndGet(new TransformableModel(services, setup));
        transformable.setLocation(1.0, 2.0);

        featurable.addFeature(new AnimatableModel(services, setup));
        featurable.addFeatureAndGet(new CollidableModel(services, setup));
        featurable.addFeature(new CollidableFramedModel(services, setup));

        return featurable;
    }

    private final Services services = new Services();
    @SuppressWarnings("unused") private final Camera camera = services.add(new Camera());

    private final Featurable featurable1 = createFeaturable(config, services);
    private final Transformable transformable1 = featurable1.getFeature(Transformable.class);
    private final Collidable collidable1 = featurable1.getFeature(Collidable.class);

    private final Featurable featurable2 = createFeaturable(config, services);
    private final Transformable transformable2 = featurable2.getFeature(Transformable.class);
    private final Collidable collidable2 = featurable2.getFeature(Collidable.class);

    /**
     * Prepare test.
     */
    @BeforeEach
    public void prepare()
    {
        collidable1.setGroup(Integer.valueOf(0));
        collidable2.setGroup(Integer.valueOf(1));

        collidable1.addAccept(collidable2.getGroup());
        collidable2.addAccept(collidable1.getGroup());
    }

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        collidable1.getFeature(Identifiable.class).destroy();
        collidable2.getFeature(Identifiable.class).destroy();
    }

    /**
     * Test framed collision with animation.
     */
    @Test
    public void testFramed()
    {
        assertTrue(collidable1.collide(collidable1).isEmpty());
        assertTrue(collidable2.collide(collidable1).isEmpty());

        transformable1.moveLocation(1.0, 0.5, 1.0);
        transformable2.moveLocation(1.0, 0.0, 1.0);

        collidable1.getFeature(Animatable.class).setFrame(1);
        collidable2.getFeature(Animatable.class).setFrame(1);

        assertFalse(collidable1.collide(collidable2).isEmpty());

        collidable1.getFeature(Animatable.class).setFrame(3);

        assertTrue(collidable1.collide(collidable2).isEmpty());

        collidable1.getFeature(Animatable.class).setFrame(1);

        assertFalse(collidable1.collide(collidable2).isEmpty());
    }
}
