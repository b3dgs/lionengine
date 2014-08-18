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
package com.b3dgs.lionengine.example.pong;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Collision;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.EntityGame;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Ball implementation using EntityGame base.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Ball
        extends EntityGame
{
    /** Ball size. */
    private static final int SIZE = 6;
    /** Initial speed. */
    private final double speedInit;
    /** Ball force. */
    private final Force force;
    /** Speed. */
    private double speed;

    /**
     * Constructor.
     * 
     * @param screenWidth The screen width.
     * @param screenHeight The screen height.
     */
    Ball(int screenWidth, int screenHeight)
    {
        super(new SetupGame(Core.MEDIA.create("sample.xml")));
        force = new Force();
        speedInit = 2.5;
        setSize(Ball.SIZE, Ball.SIZE);
        setCollision(new Collision(0, -getHeight() / 2, getWidth() / 2, getHeight() / 2, false));
    }

    /**
     * Set the ball speed.
     * 
     * @param speed The ball speed.
     */
    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    /**
     * Get the ball speed.
     * 
     * @return The ball speed.
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * Get the initial speed.
     * 
     * @return The initial speed.
     */
    public double getSpeedInit()
    {
        return speedInit;
    }

    /**
     * Get the current force.
     * 
     * @return The current force.
     */
    public Force getForce()
    {
        return force;
    }

    /**
     * Set forces.
     * 
     * @param fh The horizontal force.
     * @param fv The vertical force.
     */
    public void setForces(double fh, double fv)
    {
        force.setForce(fh, fv);
    }

    /*
     * EntityGame
     */

    @Override
    public void prepare(ContextGame context)
    {
        // Nothing to do
    }

    @Override
    public void update(double extrp)
    {
        moveLocation(extrp, force);
        updateCollision();
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX(getLocationIntX() - Ball.SIZE / 2);
        final int y = camera.getViewpointY(getLocationIntY() + getHeight() - Ball.SIZE / 2);
        g.setColor(ColorRgba.YELLOW);
        g.drawOval(x, y, Ball.SIZE, Ball.SIZE, true);
    }
}
