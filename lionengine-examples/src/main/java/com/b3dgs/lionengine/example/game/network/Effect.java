/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.network;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Timing;
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
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
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

    private final Animation animExplode;
    private final Timing timing = new Timing();

    private final Transformable transformable;
    private final Animatable animatable;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup the setup reference.
     * @param identifiable The identifiable feature.
     * @param animatable The animatable feature.
     * @param rasterable The rasterable feature.
     * @param transformable The transformable feature.
     */
    public Effect(Services services,
                  Setup setup,
                  Identifiable identifiable,
                  Animatable animatable,
                  Rasterable rasterable,
                  Transformable transformable)
    {
        super(services, setup);

        this.animatable = animatable;
        this.transformable = transformable;

        final AnimationConfig configAnimations = AnimationConfig.imports(setup);
        animExplode = configAnimations.getAnimation("explode");

        animatable.addListener((AnimatorStateListener) state ->
        {
            if (animatable.is(AnimState.FINISHED))
            {
                identifiable.destroy();
            }
        });

        timing.start();
    }

    /**
     * Start the effect at the specified location.
     * 
     * @param pointer The pointer reference.
     */
    public void start(DevicePointer pointer)
    {
        start((int) pointer.getX(), (int) pointer.getY());
    }

    /**
     * Start the effect at the specified location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void start(int x, int y)
    {
        transformable.teleport(viewer.getViewpointX(x), viewer.getViewpointY(y));
        animatable.play(animExplode);
    }

    /**
     * Lock effect at.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void lock(int x, int y)
    {
        transformable.teleport(viewer.getViewpointX(x), viewer.getViewpointY(y));
    }
}
