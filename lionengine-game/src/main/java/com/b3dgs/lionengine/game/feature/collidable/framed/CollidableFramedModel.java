/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.framed;

import java.util.Collection;
import java.util.Collections;

import com.b3dgs.lionengine.AnimatorFrameListener;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.collidable.CollisionConfig;

/**
 * Collidable framed model implementation.
 */
public class CollidableFramedModel extends FeatureModel implements CollidableFramed
{
    /** Loaded collisions framed. */
    private final CollidableFramedConfig config;

    /** Last collision found. */
    private Collection<Collision> last = Collections.emptyList();

    /**
     * Create a collidable framed model.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Collidable}</li>
     * <li>{@link Animatable}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @param setup The setup reference, must provide a valid {@link CollisionConfig}.
     */
    public CollidableFramedModel(Services services, Setup setup)
    {
        super();

        config = CollidableFramedConfig.imports(setup);
    }

    /*
     * CollidableFramed
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        final Collidable collidable = provider.getFeature(Collidable.class);
        final Animatable animatable = provider.getFeature(Animatable.class);

        for (final Collision collision : config.getCollisions())
        {
            collidable.addCollision(collision);
            collidable.setEnabled(false, collision);
        }

        animatable.addListener((AnimatorFrameListener) frame ->
        {
            for (final Collision collision : last)
            {
                collidable.setEnabled(false, collision);
            }
            last = config.getCollision(Integer.valueOf(frame));
            for (final Collision collision : last)
            {
                collidable.setEnabled(true, collision);
                collidable.forceUpdate();
            }
        });
    }
}
