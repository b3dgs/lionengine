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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeaturableModel;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Effect implementation.
 */
class Effect extends FeaturableModel
{
    /** Explode media. */
    public static final Media EXPLODE = Medias.create("Explode.xml");

    private final Transformable transformable = addFeatureAndGet(new TransformableModel());
    private final SpriteAnimated surface;
    private final Animation animExplode;

    @Service private Viewer viewer;

    /**
     * Constructor.
     * 
     * @param setup the setup reference.
     */
    public Effect(Setup setup)
    {
        super();

        final FramesConfig config = FramesConfig.imports(setup);
        final int scale = UtilRandom.getRandomInteger(75) + 50;

        final AnimationConfig configAnimations = AnimationConfig.imports(setup);
        animExplode = configAnimations.getAnimation("explode");

        surface = Drawable.loadSpriteAnimated(setup.getSurface(), config.getHorizontal(), config.getVertical());
        surface.stretch(scale, scale);
        surface.setOrigin(Origin.MIDDLE);

        addFeature(new RefreshableModel(extrp ->
        {
            surface.update(extrp);
            if (AnimState.FINISHED == surface.getAnimState())
            {
                getFeature(Identifiable.class).destroy();
            }
        }));

        addFeature(new DisplayableModel(surface::render));
    }

    /**
     * Start the effect at the specified location.
     * 
     * @param pointer The pointer reference.
     */
    public void start(InputDevicePointer pointer)
    {
        transformable.teleport(viewer.getViewpointX(pointer.getX()), viewer.getViewpointY(pointer.getY()));
        surface.setLocation(viewer, transformable);
        surface.play(animExplode);
    }
}
