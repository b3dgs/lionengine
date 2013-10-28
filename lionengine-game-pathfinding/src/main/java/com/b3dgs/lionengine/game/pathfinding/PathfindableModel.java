/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.pathfinding;

import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.pathfinding.map.MapTilePath;
import com.b3dgs.lionengine.game.pathfinding.map.TilePath;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Pathfindable implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PathfindableModel
        implements Pathfindable
{
    /** Diagonal speed factor. */
    private static final double DIAGONAL_SPEED = 0.8;

    /** Map reference. */
    protected final MapTilePath<?, ? extends TilePath<?>> map;
    /** User reference. */
    private final Localizable user;
    /** Localizable model. */
    private final Localizable localizable;
    /** Pathfinder reference. */
    private final PathFinder pathfinder;
    /** List of shared path id. */
    private final Set<Integer> sharedPathIds;
    /** List of ignored id. */
    private final Set<Integer> ignoredIds;
    /** Ref id. */
    private final Integer id;
    /** Last valid path found. */
    private Path path;
    /** Steps last. */
    private int lastStep;
    /** Destination location x. */
    private int destX;
    /** Destination location y. */
    private int destY;
    /** Horizontal movement speed. */
    private double speedX;
    /** Vertical movement speed. */
    private double speedY;
    /** Horizontal movement force. */
    private double moveX;
    /** Vertical movement force. */
    private double moveY;
    /** Pathfound changes flag. */
    private boolean pathFoundChanged;
    /** Destination has been reached. */
    private boolean destinationReached;
    /** Path stopped request flag. */
    private boolean pathStoppedRequested;
    /** Path stopped flag. */
    private boolean pathStopped;
    /** Moving flag. */
    private boolean moving;
    /** Skip flag. Used to skip one loop of update. */
    private boolean skip;
    /** Rechecks ref flag. */
    private boolean reCheckRef;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param user The pathfindable user.
     * @param id The id used.
     */
    public PathfindableModel(MapTilePath<?, ? extends TilePath<?>> map, Localizable user, Integer id)
    {
        this.id = id;
        this.map = map;
        this.user = user;
        final int range = (int) Math.sqrt(map.getWidthInTile() * map.getWidthInTile() + map.getHeightInTile()
                * map.getHeightInTile());
        pathfinder = new PathFinderImpl(map, range, true);
        ignoredIds = new HashSet<>(0);
        sharedPathIds = new HashSet<>(0);
        localizable = user;
        pathStopped = false;
        pathStoppedRequested = false;
        pathFoundChanged = false;
        destinationReached = true;
        speedX = 1.0;
        speedY = 1.0;
        moving = false;
        skip = false;
        reCheckRef = false;
    }

    /**
     * Update reference by updating map id.
     * 
     * @param lastStep The last step.
     * @param nextStep The next step.
     */
    private void updateRef(int lastStep, int nextStep)
    {
        final int max = getMaxStep();
        if (nextStep < max)
        {
            // Next step is free
            if (checkRef(path.getX(nextStep), path.getY(nextStep)))
            {
                if (!pathStoppedRequested)
                {
                    removeRef(path.getX(lastStep), path.getY(lastStep));
                    assignRef(path.getX(nextStep), path.getY(nextStep));
                }
            }
            // Need to avoid new obstacle
            else
            {
                if (nextStep >= max - 1)
                {
                    pathStoppedRequested = true;
                }
                final Integer cid = map.getRef(path.getX(nextStep), path.getY(nextStep));
                if (sharedPathIds.contains(cid))
                {
                    // skip = true;
                    setDestination(destX, destY);
                }
                else
                {
                    if (!isIgnoredId(cid))
                    {
                        setDestination(destX, destY);
                    }
                }
            }
        }
    }

    /**
     * Move to destination.
     * 
     * @param extrp The extrapolation value.
     * @param dx The destination x.
     * @param dy The destination y.
     */
    private void moveTo(double extrp, int dx, int dy)
    {
        final Force force = getMovementForce(localizable.getLocationX(), localizable.getLocationY(), dx, dy);
        final double sx = force.getForceHorizontal();
        final double sy = force.getForceVertical();

        // Move entity
        moveX = sx;
        moveY = sy;
        localizable.moveLocation(extrp, force);
        moving = true;

        // Entity arrived, next step
        final boolean arrivedX = checkArrivedX(extrp, sx, dx);
        final boolean arrivedY = checkArrivedY(extrp, sy, dy);

        if (arrivedX && arrivedY)
        {
            // When entity arrived on next step, we place it on step location, in order to avoid bug
            // (to be sure entity location is correct)
            setLocation(path.getX(lastStep), path.getY(lastStep));

            // Go to next step
            final int next = lastStep + 1;

            if (lastStep < getMaxStep() - 1)
            {
                updateRef(lastStep, next);
            }
            if (!pathStoppedRequested && !skip)
            {
                lastStep = next;
            }

            // Check if a new path has been assigned (this allow the entity to change its path before finishing it)
            if (lastStep > 0 && !skip)
            {
                checkPathfinderChanges();
            }
        }
    }

    /**
     * Check if the pathfindable is horizontally arrived.
     * 
     * @param extrp The extrapolation value.
     * @param sx The horizontal speed.
     * @param dx The horizontal tile destination.
     * @return <code>true</code> if arrived, <code>false</code> else.
     */
    private boolean checkArrivedX(double extrp, double sx, double dx)
    {
        final double x = localizable.getLocationX();
        if (sx < 0 && x <= dx || sx >= 0 && x >= dx)
        {
            localizable.moveLocation(extrp, dx - x, 0);
            return true;
        }
        return false;
    }

    /**
     * Check if the pathfindable is vertically arrived.
     * 
     * @param extrp The extrapolation value.
     * @param sy The horizontal speed.
     * @param dy The horizontal tile destination.
     * @return <code>true</code> if arrived, <code>false</code> else.
     */
    private boolean checkArrivedY(double extrp, double sy, double dy)
    {
        final double y = localizable.getLocationY();
        if (sy < 0 && y <= dy || sy >= 0 && y >= dy)
        {
            localizable.moveLocation(extrp, 0, dy - y);
            return true;
        }
        return false;
    }

    /**
     * Check if pathfinder changed.
     */
    private void checkPathfinderChanges()
    {
        if (pathFoundChanged)
        {
            if (lastStep < getMaxStep())
            {
                removeRef(path.getX(lastStep), path.getY(lastStep));
            }
            path = pathfinder.findPath(this, getLocationInTileX(), getLocationInTileY(), destX, destY, false);
            pathFoundChanged = false;
            lastStep = 0;
            skip = false;
            reCheckRef = false;

            if (path == null)
            {
                pathStoppedRequested = true;
            }
        }
        if (pathStoppedRequested)
        {
            pathStopped = true;
            pathStoppedRequested = false;
            arrived();
        }
    }

    /**
     * Get total number of steps.
     * 
     * @return The total number of steps.
     */
    private int getMaxStep()
    {
        if (path != null)
        {
            if (pathStopped)
            {
                return lastStep;
            }
            return path.getLength();
        }
        return 0;
    }

    /**
     * Called when destination has been reached and any movement are done.
     */
    private void arrived()
    {
        destinationReached = true;
        moving = false;
        path = null;
        moveX = 0.0;
        moveY = 0.0;
        sharedPathIds.clear();
    }

    /**
     * Get the movement force depending of the current location and the destination location.
     * 
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @param dx The destination horizontal location.
     * @param dy The destination vertical location.
     * @return The movement force pointing to the destination.
     */
    private Force getMovementForce(double x, double y, double dx, double dy)
    {
        double sx = 0.0;
        double sy = 0.0;

        // Horizontal speed
        if (dx - x < 0)
        {
            sx = -getSpeedX();
        }
        else if (dx - x > 0)
        {
            sx = getSpeedX();
        }
        // Vertical speed
        if (dy - y < 0)
        {
            sy = -getSpeedX();
        }
        else if (dy - y > 0)
        {
            sy = getSpeedX();
        }
        // Diagonal speed
        if (sx != 0 && sy != 0)
        {
            sx *= PathfindableModel.DIAGONAL_SPEED;
            sy *= PathfindableModel.DIAGONAL_SPEED;
        }

        return new Force(sx, sy);
    }

    /**
     * Prepare the destination, store its location, and reset states.
     * 
     * @param dtx The destination horizontal tile.
     * @param dty The destination vertical tile.
     */
    private void prepareDestination(int dtx, int dty)
    {
        pathStopped = false;
        pathStoppedRequested = false;
        destX = dtx;
        destY = dty;
        destinationReached = false;
    }

    /**
     * Assign the map ref of the pathfindable.
     * 
     * @param dtx The tile horizontal destination.
     * @param dty The tile vertical destination.
     */
    private void assignRef(int dtx, int dty)
    {
        final int tw = user.getWidth() / map.getTileWidth();
        final int th = user.getHeight() / map.getTileHeight();
        if (map.isAreaAvailable(dtx, dty, tw, th, id.intValue()))
        {
            for (int tx = dtx; tx < dtx + tw; tx++)
            {
                for (int ty = dty; ty < dty + th; ty++)
                {
                    map.setRef(tx, ty, id);
                }
            }
        }
    }

    /**
     * Remove the map ref of the pathfindable.
     * 
     * @param dtx The tile horizontal destination.
     * @param dty The tile vertical destination.
     */
    private void removeRef(int dtx, int dty)
    {
        final int tw = user.getWidth() / map.getTileWidth();
        final int th = user.getHeight() / map.getTileHeight();
        for (int tx = dtx; tx < dtx + tw; tx++)
        {
            for (int ty = dty; ty < dty + th; ty++)
            {
                if (map.getRef(tx, ty).equals(id))
                {
                    map.setRef(tx, ty, Integer.valueOf(0));
                }
            }
        }
    }

    /**
     * Check if the ref location is available for the pathfindable.
     * 
     * @param dtx The tile horizontal destination.
     * @param dty The tile vertical destination.
     * @return <code>true</code> if available, <code>false</code> else.
     */
    private boolean checkRef(int dtx, int dty)
    {
        final int tw = user.getWidth() / map.getTileWidth();
        final int th = user.getHeight() / map.getTileHeight();
        final boolean areaFree = map.isAreaAvailable(dtx, dty, tw, th, id.intValue());
        for (int tx = dtx; tx < dtx + tw; tx++)
        {
            for (int ty = dty; ty < dty + th; ty++)
            {
                final Integer id = map.getRef(tx, ty);
                if (id.intValue() > 0 && !isIgnoredId(id) && !id.equals(this.id))
                {
                    return false;
                }
            }
        }
        return areaFree;
    }

    /*
     * Pathfindable
     */

    @Override
    public void setDestination(double extrp, double dx, double dy)
    {
        final Force force = getMovementForce(localizable.getLocationX(), localizable.getLocationY(), dx, dy);
        localizable.moveLocation(extrp, force);
    }

    @Override
    public boolean setDestination(int dtx, int dty)
    {
        final int tx = getLocationInTileX();
        final int ty = getLocationInTileY();

        // No need to move
        if (tx == dtx && ty == dty)
        {
            return false;
        }
        // New first path, when entity is not moving
        if (path == null)
        {
            lastStep = 0;
            path = pathfinder.findPath(this, tx, ty, dtx, dty, true);
            pathFoundChanged = false;
            prepareDestination(dtx, dty);
        }
        // Next path, while entity is moving, change takes effect when the entity reached a step point
        else
        {
            prepareDestination(dtx, dty);
            pathFoundChanged = true;
            return false;
        }
        return true;
    }

    @Override
    public boolean isPathAvailable(int dtx, int dty)
    {
        return pathfinder.findPath(this, getLocationInTileX(), getLocationInTileY(), dtx, dty, false) != null;
    }

    @Override
    public void setLocation(int dtx, int dty)
    {
        if (checkRef(dtx, dty))
        {
            removeRef(getLocationInTileX(), getLocationInTileY());
            localizable.setLocation(dtx * map.getTileWidth(), dty * map.getTileHeight());
            assignRef(getLocationInTileX(), getLocationInTileY());
        }
    }

    @Override
    public void setIgnoreId(Integer id, boolean state)
    {
        if (state)
        {
            ignoredIds.add(id);
        }
        else
        {
            ignoredIds.remove(id);
        }
    }

    @Override
    public boolean isIgnoredId(Integer id)
    {
        return ignoredIds.contains(id);
    }

    @Override
    public void clearIgnoredId()
    {
        ignoredIds.clear();
    }

    @Override
    public void setSharedPathIds(Set<Integer> ids)
    {
        sharedPathIds.clear();
        sharedPathIds.addAll(ids);
        sharedPathIds.remove(id);
    }

    @Override
    public void clearSharedPathIds()
    {
        sharedPathIds.clear();
    }

    @Override
    public void updateMoves(double extrp)
    {
        if (reCheckRef)
        {
            updateRef(lastStep, lastStep + 1);
            reCheckRef = false;
        }
        if (skip)
        {
            skip = false;
            reCheckRef = true;
            return;
        }
        if (path != null)
        {
            // Continue until max step
            if (lastStep < getMaxStep())
            {
                final int dx = path.getX(lastStep) * map.getTileWidth();
                final int dy = path.getY(lastStep) * map.getTileHeight();
                moving = false;
                moveX = 0.0;
                moveY = 0.0;
                moveTo(extrp, dx, dy);
            }
            // Max step is reached, stop moves and animation
            else
            {
                arrived();
            }
        }
    }

    @Override
    public void stopMoves()
    {
        pathStoppedRequested = true;
    }

    @Override
    public void setSpeed(double speedX, double speedY)
    {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    @Override
    public boolean isMoving()
    {
        return moving;
    }

    @Override
    public boolean isDestinationReached()
    {
        return destinationReached;
    }

    @Override
    public double getSpeedX()
    {
        return speedX;
    }

    @Override
    public double getSpeedY()
    {
        return speedY;
    }

    @Override
    public double getMoveX()
    {
        return moveX;
    }

    @Override
    public double getMoveY()
    {
        return moveY;
    }

    @Override
    public int getLocationInTileX()
    {
        return localizable.getLocationIntX() / map.getTileWidth();
    }

    @Override
    public int getLocationInTileY()
    {
        return localizable.getLocationIntY() / map.getTileHeight();
    }
}
