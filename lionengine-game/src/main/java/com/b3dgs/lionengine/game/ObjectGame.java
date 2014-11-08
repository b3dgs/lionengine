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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.purview.Fabricable;
import com.b3dgs.lionengine.game.purview.Handlable;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.model.HandlableModel;
import com.b3dgs.lionengine.game.purview.model.LocalizableModel;

/**
 * Game object minimal representation. Defined by a unique ID, the object is designed to be handled by a
 * {@link HandlerGame}. To remove it from the handler, a simple call to {@link #destroy()} is needed.
 * <p>
 * An object can also be externally configured by using a {@link Configurer}, filled by an XML file.
 * </p>
 * <p>
 * Objects are also designed to be created by a {@link FactoryGame}. In that case, they must have at least a constructor
 * with a single argument, which must be type of {@link SetupGame}.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see HandlerGame
 * @see FactoryGame
 * @see FactoryObjectGame
 */
public abstract class ObjectGame
        implements Handlable, Fabricable, Localizable
{
    /** Handlable model. */
    private final Handlable handlableModel;
    /** Localizable model. */
    private final Localizable localizable;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} objects at the same time.
     */
    public ObjectGame(SetupGame setup) throws LionEngineException
    {
        Check.notNull(setup);

        handlableModel = new HandlableModel();
        localizable = new LocalizableModel();
    }

    /**
     * Update the object.
     * 
     * @param extrp The extrapolation value.
     */
    public abstract void update(double extrp);

    /**
     * Render the object.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public abstract void render(Graphic g, CameraGame camera);

    /**
     * Called when object is destroyed. Can be overridden. Does nothing by default.
     */
    protected void onDestroy()
    {
        // Nothing by default
    }

    /*
     * Handlable
     */

    @Override
    public final Integer getId()
    {
        return handlableModel.getId();
    }

    @Override
    public final void destroy()
    {
        handlableModel.destroy();
        onDestroy();
    }

    @Override
    public final boolean isDestroyed()
    {
        return handlableModel.isDestroyed();
    }

    /*
     * Localizable
     */

    @Override
    public void teleport(double x, double y)
    {
        localizable.teleport(x, y);
    }

    @Override
    public void teleportX(double x)
    {
        localizable.teleportX(x);
    }

    @Override
    public void teleportY(double y)
    {
        localizable.teleportY(y);
    }

    @Override
    public void moveLocation(double extrp, Force force, Force... forces)
    {
        localizable.moveLocation(extrp, force, forces);
    }

    @Override
    public void moveLocation(double extrp, double vx, double vy)
    {
        localizable.moveLocation(extrp, vx, vy);
    }

    @Override
    public void setLocation(double x, double y)
    {
        localizable.setLocation(x, y);
    }

    @Override
    public void setLocationX(double x)
    {
        localizable.setLocationX(x);
    }

    @Override
    public void setLocationY(double y)
    {
        localizable.setLocationY(y);
    }

    @Override
    public final void setSize(int width, int height)
    {
        localizable.setSize(width, height);
    }

    @Override
    public double getLocationX()
    {
        return localizable.getLocationX();
    }

    @Override
    public double getLocationY()
    {
        return localizable.getLocationY();
    }

    @Override
    public int getLocationIntX()
    {
        return localizable.getLocationIntX();
    }

    @Override
    public int getLocationIntY()
    {
        return localizable.getLocationIntY();
    }

    @Override
    public double getLocationOldX()
    {
        return localizable.getLocationOldX();
    }

    @Override
    public double getLocationOldY()
    {
        return localizable.getLocationOldY();
    }

    @Override
    public int getWidth()
    {
        return localizable.getWidth();
    }

    @Override
    public int getHeight()
    {
        return localizable.getHeight();
    }
}
