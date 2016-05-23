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
package com.b3dgs.lionengine.editor;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.handler.DisplayableModel;
import com.b3dgs.lionengine.game.handler.Refreshable;
import com.b3dgs.lionengine.game.handler.RefreshableModel;
import com.b3dgs.lionengine.game.handler.Service;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Configurer;
import com.b3dgs.lionengine.game.object.FramesConfig;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.object.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Object representation of any user object. This allows to avoid constructor error, especially with features.
 */
public class ObjectRepresentation extends ObjectGame
{
    /** Error animation. */
    private static final String ERROR_ANIMATION = "Unable to get animation data from: ";

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
            final FramesConfig frames = FramesConfig.imports(configurer);
            return Drawable.loadSpriteAnimated(surface, frames.getHorizontal(), frames.getVertical());
        }
        catch (final LionEngineException exception)
        {
            Verbose.exception(exception, ERROR_ANIMATION, configurer.getMedia().getPath());
            return Drawable.loadSpriteAnimated(surface, 1, 1);
        }
    }

    /** Rectangle. */
    private final Rectangle rectangle = Geom.createRectangle();
    /** Transformable feature. */
    private final Transformable transformable;

    @Service private Camera camera;
    @Service private MapTile map;

    /**
     * Create the object.
     * 
     * @param setup The setup reference.
     * @throws LionEngineException If error.
     */
    public ObjectRepresentation(SetupSurface setup)
    {
        super(setup);

        final Configurer configurer = setup.getConfigurer();
        final SpriteAnimated surface = getSprite(configurer, setup.getSurface());
        surface.setOrigin(Origin.BOTTOM_LEFT);
        surface.prepare();

        transformable = addFeatureAndGet(new TransformableModel(setup));
        transformable.setSize(surface.getFrameWidth(), surface.getFrameHeight());

        addFeature(new RefreshableModel(extrp ->
        {
            rectangle.set(camera.getViewpointX(transformable.getX()),
                          camera.getViewpointY(transformable.getY()) - transformable.getHeight(),
                          transformable.getWidth(),
                          transformable.getHeight());
            surface.setLocation(camera, transformable);
        }));

        addFeature(new DisplayableModel(surface::render));
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
        getFeature(Refreshable.class).update(1.0);
    }

    /**
     * Move the object.
     * 
     * @param vx The horizontal vector.
     * @param vy The vertical vector.
     */
    public void move(double vx, double vy)
    {
        transformable.moveLocation(1.0, vx, -vy);
        getFeature(Refreshable.class).update(1.0);
    }

    /**
     * Align position to grid.
     */
    public void alignToGrid()
    {
        place(UtilMath.getRounded(transformable.getX() + transformable.getWidth() / 2.0, map.getTileWidth()),
              UtilMath.getRounded(transformable.getY() + transformable.getHeight() / 2.0, map.getTileHeight()));
    }

    /**
     * Get the rectangle representation on screen.
     * 
     * @return The rectangle representation on screen.
     */
    public Rectangle getRectangle()
    {
        return rectangle;
    }
}
