/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.collision_tile;

import java.util.Iterator;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionResult;
import com.b3dgs.lionengine.game.factory.Setup;
import com.b3dgs.lionengine.game.handler.ObjectGame;
import com.b3dgs.lionengine.game.trait.TileCollidable;
import com.b3dgs.lionengine.game.trait.TileCollidableModel;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.game.trait.TransformableModel;

/**
 * Entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.entity
 */
class Entity
        extends ObjectGame
        implements Updatable, Renderable
{
    /** Setup reference. */
    private static final Setup SETUP = new Setup(Core.MEDIA.create("object.xml"));

    /** Transformable model. */
    private final Transformable transformable;
    /** Tile collidable model. */
    private final TileCollidable tileCollidable;
    /** Map reference. */
    private final Map map;
    /** Camera reference. */
    private final Camera camera;
    /** Mouse click x. */
    private double mouseX;
    /** Mouse click y. */
    private double mouseY;
    /** Collision result. */
    private CollisionResult<Tile> resultX;
    /** Collision result. */
    private CollisionResult<Tile> resultY;

    /**
     * Constructor.
     * 
     * @param services The services reference.
     */
    public Entity(Services services)
    {
        super(SETUP, services);
        transformable = new TransformableModel(this);
        addTrait(transformable);
        tileCollidable = new TileCollidableModel(this, SETUP.getConfigurer(), services);
        addTrait(tileCollidable);

        map = services.get(Map.class);
        camera = services.get(Camera.class);
        transformable.setSize(8, 8);
        mouseX = 256;
        mouseY = 192;
    }

    /**
     * Update the mouse.
     * 
     * @param mouse The mouse.
     */
    public void updateMouse(Mouse mouse)
    {
        if (mouse.hasClicked(Mouse.LEFT))
        {
            mouseX = camera.getX() + mouse.getX();
            mouseY = camera.getY() - mouse.getY() + 240;
        }
    }

    /**
     * Teleport.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void teleport(double x, double y)
    {
        transformable.teleport(x, y);
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getX()
    {
        return (int) transformable.getX();
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public int getY()
    {
        return (int) transformable.getY();
    }

    @Override
    public void update(double extrp)
    {
        transformable.setLocation(mouseX, mouseY);
        final Iterator<CollisionCategory> iterator = tileCollidable.getCategories().iterator();
        resultX = map.computeCollision(transformable, iterator.next());
        resultY = map.computeCollision(transformable, iterator.next());
    }

    @Override
    public void render(Graphic g)
    {
        if (resultX != null)
        {
            g.setColor(ColorRgba.RED);
            resultX.getTile().renderCollision(g, camera);

            g.setColor(ColorRgba.GREEN);
            g.drawOval(camera, Origin.MIDDLE, resultX.getX() != null ? resultX.getX().doubleValue() : getX(),
                    resultX.getY() != null ? resultX.getY().doubleValue() : getY(), transformable.getWidth() / 2,
                    transformable.getHeight() / 2, false);
        }
        if (resultY != null)
        {
            g.setColor(ColorRgba.RED);
            resultY.getTile().renderCollision(g, camera);

            g.setColor(ColorRgba.GREEN);
            g.drawOval(camera, Origin.MIDDLE, resultY.getX() != null ? resultY.getX().doubleValue() : getX(),
                    resultY.getY() != null ? resultY.getY().doubleValue() : getY(), transformable.getWidth() / 2,
                    transformable.getHeight() / 2, false);
        }
        g.setColor(ColorRgba.BLUE);
        g.drawOval(camera, Origin.MIDDLE, transformable.getX(), transformable.getY(), transformable.getWidth(),
                transformable.getHeight(), true);
    }
}
