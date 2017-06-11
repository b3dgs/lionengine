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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FeatureGet;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Entity model implementation.
 */
class EntityModel extends FeatureModel
{
    private static final double GRAVITY = 7.0;

    private final Force movement = new Force();
    private final Force jump = new Force();
    private final SpriteAnimated surface;
    private final Context context;

    @FeatureGet private Body body;
    @FeatureGet private Collidable collidable;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityModel(Services services, Setup setup)
    {
        super();

        context = services.get(Context.class);

        final FramesConfig frames = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        body.setVectors(movement, jump);
        body.setGravity(GRAVITY);
        body.setDesiredFps(context.getConfig().getSource().getRate());

        collidable.setOrigin(Origin.CENTER_TOP);
    }

    /**
     * Get the movement force.
     * 
     * @return The movement force.
     */
    public Force getMovement()
    {
        return movement;
    }

    /**
     * Get the jump force.
     * 
     * @return The jump force.
     */
    public Force getJump()
    {
        return jump;
    }

    /**
     * Get the surface representation.
     * 
     * @return The surface representation.
     */
    public SpriteAnimated getSurface()
    {
        return surface;
    }
}
