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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.handler.FeatureModel;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.layer.Layerable;
import com.b3dgs.lionengine.game.object.FramesConfig;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.body.Body;

/**
 * Entity model implementation.
 */
class EntityModel extends FeatureModel
{
    private final Force movement = new Force();
    private final Force jump = new Force();
    private final SpriteAnimated surface;
    private final Configurer configurer;

    @Service private Layerable layerable;
    @Service private Body body;
    @Service private Collidable collidable;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public EntityModel(SetupSurface setup)
    {
        super();

        configurer = setup.getConfigurer();

        final FramesConfig frames = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());
        surface.setOrigin(Origin.CENTER_BOTTOM);
        surface.setFrameOffsets(-1, 0);
    }

    @Override
    public void prepare(Handlable owner, Services services)
    {
        super.prepare(owner, services);

        layerable.setLayer(1);

        body.setVectors(movement, jump);
        body.setDesiredFps(60);
        body.setMass(2.0);

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

    /**
     * Get the configurer.
     * 
     * @return The configurer reference.
     */
    public Configurer getConfigurer()
    {
        return configurer;
    }
}
