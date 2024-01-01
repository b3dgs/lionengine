/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.effect;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.io.DevicePointer;

/**
 * Effect implementation.
 */
@FeatureInterface
public final class Effect extends FeatureModel implements Routine
{
    /** Explode media. */
    public static final Media EXPLODE = Medias.create("Explode.xml");

    private final Viewer viewer = services.get(Viewer.class);

    private final Transformable transformable;
    private final Animatable animatable;

    private final Animation animExplode;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup the setup reference.
     * @param identifiable The identifiable feature.
     * @param transformable The transformable feature.
     * @param animatable The animatable feature.
     */
    public Effect(Services services,
                  Setup setup,
                  Identifiable identifiable,
                  Transformable transformable,
                  Animatable animatable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;

        final AnimationConfig configAnimations = AnimationConfig.imports(setup);
        animExplode = configAnimations.getAnimation("explode");

        animatable.addListener((AnimatorStateListener) state ->
        {
            if (animatable.is(AnimState.FINISHED))
            {
                identifiable.destroy();
            }
        });
    }

    /**
     * Start the effect at the specified location.
     * 
     * @param pointer The pointer reference.
     */
    public void start(DevicePointer pointer)
    {
        transformable.teleport(viewer.getViewpointX(pointer.getX()), viewer.getViewpointY(pointer.getY()));
        animatable.play(animExplode);
    }
}
