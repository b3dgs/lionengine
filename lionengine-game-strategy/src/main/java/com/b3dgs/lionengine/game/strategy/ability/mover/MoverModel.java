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
package com.b3dgs.lionengine.game.strategy.ability.mover;

import java.util.Set;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.game.strategy.ability.AbilityModel;
import com.b3dgs.lionengine.game.strategy.map.MapTileStrategy;

/**
 * Default and abstract mover model implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MoverModel
        extends AbilityModel<MoverListener, MoverUsedServices>
        implements MoverServices, MoverListener
{
    /** Pathfindable purview. */
    private final Pathfindable pathfindable;
    /** Already arrived flag. */
    private boolean alreadyArrived;

    /**
     * Constructor.
     * 
     * @param user The user reference.
     * @param map The map reference.
     */
    public MoverModel(MoverUsedServices user, MapTileStrategy<?, ?, ?> map)
    {
        super(user);
        pathfindable = new PathfindableModel(map, user, user.getId());
        alreadyArrived = true;
    }

    /*
     * MoverServices
     */

    @Override
    public Orientation getMovementOrientation()
    {
        final double mx = getMoveX();
        final double my = getMoveY();

        if (my < 0.0 && mx == 0.0)
        {
            return Orientation.SOUTH;
        }
        else if (my < 0.0 && mx > 0.0)
        {
            return Orientation.SOUTH_EAST;
        }
        else if (my == 0.0 && mx > 0.0)
        {
            return Orientation.EAST;
        }
        else if (my > 0.0 && mx > 0.0)
        {
            return Orientation.NORTH_EAST;
        }
        else if (my > 0.0 && mx == 0.0)
        {
            return Orientation.NORTH;
        }
        else if (my > 0.0 && mx < 0.0)
        {
            return Orientation.NORTH_WEST;
        }
        else if (my == 0.0 && mx < 0.0)
        {
            return Orientation.WEST;
        }
        else if (my < 0.0 && mx < 0.0)
        {
            return Orientation.SOUTH_WEST;
        }
        return user.getOrientation();
    }

    @Override
    public void setDestination(Tiled tiled)
    {
        setDestination(tiled.getLocationInTileX() + tiled.getWidthInTile() / 2,
                tiled.getLocationInTileY() + tiled.getHeightInTile() / 2);
    }

    @Override
    public void pointTo(int dtx, int dty)
    {
        if (getLocationInTileX() == dtx && getLocationInTileY() > dty)
        {
            user.setOrientation(Orientation.SOUTH);
        }
        else if (getLocationInTileX() > dtx && getLocationInTileY() > dty)
        {
            user.setOrientation(Orientation.SOUTH_WEST);
        }
        else if (getLocationInTileX() > dtx && getLocationInTileY() == dty)
        {
            user.setOrientation(Orientation.WEST);
        }
        else if (getLocationInTileX() > dtx && getLocationInTileY() < dty)
        {
            user.setOrientation(Orientation.NORTH_WEST);
        }
        else if (getLocationInTileX() == dtx && getLocationInTileY() < dty)
        {
            user.setOrientation(Orientation.NORTH);
        }
        else if (getLocationInTileX() < dtx && getLocationInTileY() < dty)
        {
            user.setOrientation(Orientation.NORTH_EAST);
        }
        else if (getLocationInTileX() < dtx && getLocationInTileY() == dty)
        {
            user.setOrientation(Orientation.EAST);
        }
        else if (getLocationInTileX() < dtx && getLocationInTileY() > dty)
        {
            user.setOrientation(Orientation.SOUTH_EAST);
        }
    }

    @Override
    public void pointTo(Tiled tiled)
    {
        pointTo(tiled.getLocationInTileX(), tiled.getLocationInTileY());
    }

    @Override
    public void setDestination(double extrp, double dx, double dy)
    {
        pathfindable.setDestination(extrp, dx, dy);
    }

    @Override
    public boolean setDestination(int dtx, int dty)
    {
        final boolean destinationFound = pathfindable.setDestination(dtx, dty);

        if (destinationFound)
        {
            alreadyArrived = false;
            pointTo(dtx, dty);
            notifyStartMove();
        }
        return destinationFound;
    }

    @Override
    public boolean isPathAvailable(int dtx, int dty)
    {
        return pathfindable.isPathAvailable(dtx, dty);
    }

    @Override
    public void setLocation(int dtx, int dty)
    {
        pathfindable.setLocation(dtx, dty);
    }

    @Override
    public void setIgnoreId(Integer id, boolean state)
    {
        pathfindable.setIgnoreId(id, state);
    }

    @Override
    public boolean isIgnoredId(Integer id)
    {
        return pathfindable.isIgnoredId(id);
    }

    @Override
    public void clearIgnoredId()
    {
        pathfindable.clearIgnoredId();
    }

    @Override
    public void setSharedPathIds(Set<Integer> ids)
    {
        pathfindable.setSharedPathIds(ids);
    }

    @Override
    public void clearSharedPathIds()
    {
        pathfindable.clearSharedPathIds();
    }

    @Override
    public void updateMoves(double extrp)
    {
        pathfindable.updateMoves(extrp);

        if (pathfindable.isMoving())
        {
            user.setOrientation(getMovementOrientation());
            alreadyArrived = false;
            notifyMoving();
        }

        if (!alreadyArrived && pathfindable.isDestinationReached())
        {
            alreadyArrived = true;
            notifyArrived();
        }
    }

    @Override
    public void stopMoves()
    {
        pathfindable.stopMoves();
    }

    @Override
    public void setSpeed(double speedX, double speedY)
    {
        pathfindable.setSpeed(speedX, speedY);
    }

    @Override
    public boolean isMoving()
    {
        return pathfindable.isMoving();
    }

    @Override
    public boolean isDestinationReached()
    {
        return pathfindable.isDestinationReached();
    }

    @Override
    public double getSpeedX()
    {
        return pathfindable.getSpeedX();
    }

    @Override
    public double getSpeedY()
    {
        return pathfindable.getSpeedY();
    }

    @Override
    public double getMoveX()
    {
        return pathfindable.getMoveX();
    }

    @Override
    public double getMoveY()
    {
        return pathfindable.getMoveY();
    }

    @Override
    public int getLocationInTileX()
    {
        return pathfindable.getLocationInTileX();
    }

    @Override
    public int getLocationInTileY()
    {
        return pathfindable.getLocationInTileY();
    }

    /*
     * MoverListener
     */

    @Override
    public void notifyStartMove()
    {
        for (final MoverListener listener : listeners)
        {
            listener.notifyStartMove();
        }
    }

    @Override
    public void notifyMoving()
    {
        for (final MoverListener listener : listeners)
        {
            listener.notifyMoving();
        }
    }

    @Override
    public void notifyArrived()
    {
        for (final MoverListener listener : listeners)
        {
            listener.notifyArrived();
        }
    }
}
