/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.trait.transformable.TransformableModel;

/**
 * Object representation of any user object. This allows to avoid constructor error, especially with traits.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ObjectRepresentation extends ObjectGame implements Updatable, Renderable
{
    /**
     * Get the sprite depending of the configuration.
     * 
     * @param configurer The configurer reference.
     * @param surface The surface reference.
     * @return The sprite instance.
     */
    private static SpriteAnimated getSprite(Configurer configurer, ImageBuffer surface)
    {
        try
        {
            final ConfigFrames frames = ConfigFrames.create(configurer);
            return Drawable.loadSpriteAnimated(surface, frames.getHorizontal(), frames.getVertical());
        }
        catch (final LionEngineException exception)
        {
            return Drawable.loadSpriteAnimated(surface, 1, 1);
        }
    }

    /** Transformable trait. */
    private final Transformable transformable = addTrait(new TransformableModel());
    /** Surface reference. */
    private final SpriteAnimated surface;
    /** Camera reference. */
    private final Camera camera;

    /**
     * Create the object.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     * @throws LionEngineException If error.
     */
    public ObjectRepresentation(SetupSurface setup, Services services) throws LionEngineException
    {
        super(setup, services);
        surface = getSprite(setup.getConfigurer(), setup.getSurface());
        surface.setOrigin(Origin.BOTTOM_LEFT);
        surface.prepare();
        transformable.setSize(surface.getFrameWidth(), surface.getFrameHeight());
        camera = services.get(Camera.class);
    }

    /**
     * Set object location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void place(int x, int y)
    {
        transformable.teleport(x, y);
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        surface.setLocation(camera, transformable);
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        surface.render(g);
    }
}
