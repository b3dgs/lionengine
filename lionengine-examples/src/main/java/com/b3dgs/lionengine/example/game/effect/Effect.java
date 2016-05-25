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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.object.FramesConfig;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.state.AnimationConfig;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilRandom;

/**
 * Effect base implementation.
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
    public Effect(SetupSurface setup)
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
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void start(double x, double y)
    {
        transformable.teleport(x, y);
        surface.setLocation(viewer.getViewpointX(transformable.getX()), viewer.getViewpointY(transformable.getY()));
        surface.play(animExplode);
    }
}
