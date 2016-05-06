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
package com.b3dgs.lionengine.game.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.trait.TraitModel;
import com.b3dgs.lionengine.game.object.trait.orientable.Orientable;
import com.b3dgs.lionengine.game.object.trait.orientable.OrientableModel;
import com.b3dgs.lionengine.game.object.trait.transformable.Transformable;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.Tiled;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.TextStyle;
import com.b3dgs.lionengine.graphic.Viewer;

/**
 * Pathfindable implementation.
 * <p>
 * The {@link ObjectGame} owner must have the following {@link com.b3dgs.lionengine.game.object.trait.Trait}:
 * </p>
 * <ul>
 * <li>{@link Transformable}</li>
 * </ul>
 * <p>
 * The {@link ObjectGame} owner must provide a valid {@link com.b3dgs.lionengine.game.Configurer} compatible
 * with {@link PathfindableConfig}.
 * </p>
 * <p>
 * The {@link Services} must provide the following services:
 * </p>
 * <ul>
 * <li>{@link MapTile}</li>
 * <li>{@link Viewer}</li>
 * </ul>
 * <p>
 * If the {@link ObjectGame} is a {@link PathfindableListener}, it will automatically
 * {@link #addListener(PathfindableListener)} on it.
 * </p>
 */
public class PathfindableModel extends TraitModel implements Pathfindable
{
    /** Category not found error. */
    private static final String ERROR_CATEGORY = "Category not found: ";
    /** Diagonal speed factor. */
    private static final double DIAGONAL_SPEED = 0.8;
    /** Debug text size. */
    private static final int TEXT_DEBUG_SIZE = 8;

    /** Pathfindable listeners. */
    private final Collection<PathfindableListener> listeners = new ArrayList<PathfindableListener>();
    /** List of shared path id. */
    private final Collection<Integer> sharedPathIds = new HashSet<Integer>(0);
    /** List of ignored id. */
    private final Collection<Integer> ignoredIds = new HashSet<Integer>(0);
    /** Object id. */
    private Integer id;
    /** Viewer reference. */
    private Viewer viewer;
    /** Map reference. */
    private MapTile map;
    /** Map path reference. */
    private MapTilePath mapPath;
    /** Pathfinder reference. */
    private PathFinder pathfinder;
    /** List of categories. */
    private Map<String, PathData> categories;
    /** Transformable model. */
    private Transformable transformable;
    /** Orientable model. */
    private Orientable orientable;
    /** Last valid path found. */
    private Path path;
    /** Text debug rendering. */
    private Text text;
    /** Current step index on path. */
    private int currentStep;
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
    /** Render debug (draw additional path information). */
    private boolean renderDebug;

    /**
     * Create a pathfindable model.
     */
    public PathfindableModel()
    {
        super();

        destinationReached = true;
        speedX = 1.0;
        speedY = 1.0;
    }

    /**
     * Assign the map object id of the pathfindable.
     * 
     * @param dtx The tile horizontal destination.
     * @param dty The tile vertical destination.
     */
    private void assignObjectId(int dtx, int dty)
    {
        final int tw = transformable.getWidth() / map.getTileWidth();
        final int th = transformable.getHeight() / map.getTileHeight();
        if (mapPath.isAreaAvailable(this, dtx, dty, tw, th, id))
        {
            for (int tx = dtx; tx < dtx + tw; tx++)
            {
                for (int ty = dty; ty < dty + th; ty++)
                {
                    mapPath.addObjectId(tx, ty, id);
                }
            }
        }
    }

    /**
     * Remove the map object id of the pathfindable.
     * 
     * @param dtx The tile horizontal destination.
     * @param dty The tile vertical destination.
     */
    private void removeObjectId(int dtx, int dty)
    {
        final int tw = transformable.getWidth() / map.getTileWidth();
        final int th = transformable.getHeight() / map.getTileHeight();
        for (int tx = dtx; tx < dtx + tw; tx++)
        {
            for (int ty = dty; ty < dty + th; ty++)
            {
                if (mapPath.getObjectsId(tx, ty).contains(id))
                {
                    mapPath.removeObjectId(tx, ty, id);
                }
            }
        }
    }

    /**
     * Update reference by updating map object Id.
     * 
     * @param lastStep The last step.
     * @param nextStep The next step.
     */
    private void updateObjectId(int lastStep, int nextStep)
    {
        final int max = getMaxStep();
        if (nextStep < max)
        {
            // Next step is free
            if (checkObjectId(path.getX(nextStep), path.getY(nextStep)))
            {
                takeNextStep(lastStep, nextStep);
            }
            else
            {
                avoidObstacle(nextStep, max);
            }
        }
    }

    /**
     * Update the next step has it is free.
     * 
     * @param lastStep The last step.
     * @param nextStep The next step.
     */
    private void takeNextStep(int lastStep, int nextStep)
    {
        if (!pathStoppedRequested)
        {
            removeObjectId(path.getX(lastStep), path.getY(lastStep));
            assignObjectId(path.getX(nextStep), path.getY(nextStep));
        }
    }

    /**
     * Update to avoid obstacle because next step is not free.
     * 
     * @param nextStep The next step.
     * @param max The maximum steps.
     */
    private void avoidObstacle(int nextStep, int max)
    {
        if (nextStep >= max - 1)
        {
            pathStoppedRequested = true;
        }
        final Collection<Integer> cid = mapPath.getObjectsId(path.getX(nextStep), path.getY(nextStep));
        if (sharedPathIds.containsAll(cid))
        {
            setDestination(destX, destY);
        }
        else
        {
            if (!ignoredIds.containsAll(cid))
            {
                setDestination(destX, destY);
            }
        }
    }

    /**
     * Render the current path.
     * 
     * @param g The graphic output.
     */
    private void renderPath(Graphic g)
    {
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        for (int i = 0; i < path.getLength(); i++)
        {
            final int x = (int) viewer.getViewpointX(path.getX(i) * (double) tw);
            final int y = (int) viewer.getViewpointY(path.getY(i) * (double) th);
            g.drawRect(x, y - th, tw, th, true);
            if (renderDebug)
            {
                final Tile tile = map.getTile(path.getX(i), path.getY(i));
                if (tile != null)
                {
                    final TilePath tilePath = tile.getFeature(TilePath.class);
                    text.draw(g, x + 2, y - th + 2, String.valueOf(getCost(tilePath.getCategory())));
                }
            }
        }
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
     * Move to destination.
     * 
     * @param extrp The extrapolation value.
     * @param dx The destination horizontal location.
     * @param dy The destination vertical location.
     */
    private void moveTo(double extrp, int dx, int dy)
    {
        final Force force = getMovementForce(transformable.getX(), transformable.getY(), dx, dy);
        final double sx = force.getDirectionHorizontal();
        final double sy = force.getDirectionVertical();

        // Move object
        moveX = sx;
        moveY = sy;
        transformable.moveLocation(extrp, force);
        moving = true;

        // Object arrived, next step
        final boolean arrivedX = checkArrivedX(extrp, sx, dx);
        final boolean arrivedY = checkArrivedY(extrp, sy, dy);

        if (arrivedX && arrivedY)
        {
            // When object arrived on next step, we place it on step location, in order to avoid bug
            // (to be sure object location is correct)
            setLocation(path.getX(currentStep), path.getY(currentStep));

            // Go to next step
            final int next = currentStep + 1;
            if (currentStep < getMaxStep() - 1)
            {
                updateObjectId(currentStep, next);
            }
            if (!pathStoppedRequested && !skip)
            {
                currentStep = next;
            }
            // Check if a new path has been assigned (this allow the object to change its path before finishing it)
            if (currentStep > 0 && !skip)
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
        final double x = transformable.getX();
        if (sx < 0 && x <= dx || sx >= 0 && x >= dx)
        {
            transformable.moveLocation(extrp, dx - x, 0);
            return true;
        }
        return false;
    }

    /**
     * Check if the pathfindable is vertically arrived.
     * 
     * @param extrp The extrapolation value.
     * @param sy The vertical speed.
     * @param dy The vertical tile destination.
     * @return <code>true</code> if arrived, <code>false</code> else.
     */
    private boolean checkArrivedY(double extrp, double sy, double dy)
    {
        final double y = transformable.getY();
        if (sy < 0 && y <= dy || sy >= 0 && y >= dy)
        {
            transformable.moveLocation(extrp, 0, dy - y);
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
            if (currentStep < getMaxStep())
            {
                removeObjectId(path.getX(currentStep), path.getY(currentStep));
            }
            path = pathfinder.findPath(this, destX, destY, false);
            pathFoundChanged = false;
            currentStep = 0;
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
            onArrived();
        }
    }

    /**
     * Check if the object id location is available for the pathfindable.
     * 
     * @param dtx The tile horizontal destination.
     * @param dty The tile vertical destination.
     * @return <code>true</code> if available, <code>false</code> else.
     */
    private boolean checkObjectId(int dtx, int dty)
    {
        final int tw = transformable.getWidth() / map.getTileWidth();
        final int th = transformable.getHeight() / map.getTileHeight();
        for (int tx = dtx; tx < dtx + tw; tx++)
        {
            for (int ty = dty; ty < dty + th; ty++)
            {
                final Collection<Integer> ids = mapPath.getObjectsId(tx, ty);
                if (!ids.isEmpty() && !ids.contains(id))
                {
                    return false;
                }
            }
        }
        return mapPath.isAreaAvailable(this, dtx, dty, tw, th, id);
    }

    /**
     * Called when destination has been reached and any movement are done.
     */
    private void onArrived()
    {
        destinationReached = true;
        moving = false;
        path = null;
        moveX = 0.0;
        moveY = 0.0;
        sharedPathIds.clear();
        for (final PathfindableListener listener : listeners)
        {
            listener.notifyArrived();
        }
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
        if (Double.compare(sx, 0) != 0 && Double.compare(sy, 0) != 0)
        {
            sx *= PathfindableModel.DIAGONAL_SPEED;
            sy *= PathfindableModel.DIAGONAL_SPEED;
        }

        return new Force(sx, sy);
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
            final int steps;
            if (pathStopped)
            {
                steps = currentStep;
            }
            else
            {
                steps = path.getLength();
            }
            return steps;
        }
        return 0;
    }

    /*
     * Pathfindable
     */

    @Override
    public void prepare(ObjectGame owner, Services services)
    {
        super.prepare(owner, services);

        map = services.get(MapTile.class);
        viewer = services.get(Viewer.class);
        mapPath = map.getFeature(MapTilePath.class);
        id = owner.getId();
        final int range = (int) Math.sqrt(map.getInTileWidth() * map.getInTileWidth()
                                          + map.getInTileHeight() * (double) map.getInTileHeight());
        pathfinder = Astar.createPathFinder(map, range, Astar.createHeuristicClosest());
        categories = PathfindableConfig.create(owner.getConfigurer());

        transformable = owner.getTrait(Transformable.class);
        final OrientableModel orientableModel = new OrientableModel();
        orientableModel.prepare(owner, services);
        orientable = orientableModel;

        if (owner instanceof PathfindableListener)
        {
            addListener((PathfindableListener) owner);
        }
    }

    @Override
    public void addListener(PathfindableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void clearSharedPathIds()
    {
        sharedPathIds.clear();
    }

    @Override
    public void clearIgnoredId()
    {
        ignoredIds.clear();
    }

    @Override
    public void stopMoves()
    {
        pathStoppedRequested = true;
    }

    @Override
    public void update(double extrp)
    {
        if (reCheckRef)
        {
            updateObjectId(currentStep, currentStep + 1);
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
            if (currentStep < getMaxStep())
            {
                final int dx = path.getX(currentStep) * map.getTileWidth();
                final int dy = path.getY(currentStep) * map.getTileHeight();
                orientable.pointTo(path.getX(currentStep), path.getY(currentStep));
                moving = false;
                moveX = 0.0;
                moveY = 0.0;
                moveTo(extrp, dx, dy);
                for (final PathfindableListener listener : listeners)
                {
                    listener.notifyMoving();
                }
            }
            // Max step is reached, stop moves and animation
            else
            {
                onArrived();
            }
        }
    }

    @Override
    public void moveTo(double extrp, double x, double y)
    {
        final Force force = getMovementForce(transformable.getX(), transformable.getY(), x, y);
        transformable.moveLocation(extrp, force);
    }

    @Override
    public void pointTo(int tx, int ty)
    {
        orientable.pointTo(tx, ty);
    }

    @Override
    public void pointTo(Tiled tiled)
    {
        orientable.pointTo(tiled);
    }

    @Override
    public void render(Graphic g)
    {
        if (path != null)
        {
            final ColorRgba oldColor = g.getColor();
            g.setColor(ColorRgba.GREEN);
            renderPath(g);
            g.setColor(oldColor);
        }
    }

    @Override
    public void setSpeed(double speedX, double speedY)
    {
        this.speedX = speedX;
        this.speedY = speedY;
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
    public void setSharedPathIds(Collection<Integer> ids)
    {
        sharedPathIds.clear();
        sharedPathIds.addAll(ids);
        sharedPathIds.remove(id);
    }

    @Override
    public boolean setDestination(Localizable localizable)
    {
        return setDestination(map.getInTileX(localizable), map.getInTileY(localizable));
    }

    @Override
    public boolean setDestination(Tiled tiled)
    {
        return setDestination(tiled.getInTileX(), tiled.getInTileY());
    }

    @Override
    public boolean setDestination(int tx, int ty)
    {
        if (getInTileX() != tx || getInTileY() != ty)
        {
            // New first path, when object is not moving
            if (path == null)
            {
                currentStep = 0;
                path = pathfinder.findPath(this, tx, ty, true);
                pathFoundChanged = false;
                for (final PathfindableListener listener : listeners)
                {
                    listener.notifyStartMove();
                }
                prepareDestination(tx, ty);
                return true;
            }
            // Next path, while object is moving, change takes effect when the object reached a step point
            prepareDestination(tx, ty);
            pathFoundChanged = true;
        }
        return false;
    }

    @Override
    public void setLocation(int tx, int ty)
    {
        if (checkObjectId(tx, ty))
        {
            removeObjectId(getInTileX(), getInTileY());
            transformable.setLocation(tx * (double) map.getTileWidth(), ty * (double) map.getTileHeight());
            assignObjectId(getInTileX(), getInTileY());
        }
    }

    @Override
    public void setOrientation(Orientation orientation)
    {
        orientable.setOrientation(orientation);
    }

    @Override
    public void setRenderDebug(boolean debug)
    {
        renderDebug = debug;
        if (text == null)
        {
            text = Graphics.createText(Text.SANS_SERIF, TEXT_DEBUG_SIZE, TextStyle.NORMAL);
            text.setColor(ColorRgba.BLACK);
        }
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
    public Orientation getOrientation()
    {
        return orientable.getOrientation();
    }

    @Override
    public int getInTileX()
    {
        return map.getInTileX(transformable);
    }

    @Override
    public int getInTileY()
    {
        return map.getInTileY(transformable);
    }

    @Override
    public int getInTileWidth()
    {
        return transformable.getWidth() / map.getTileWidth();
    }

    @Override
    public int getInTileHeight()
    {
        return transformable.getHeight() / map.getTileHeight();
    }

    @Override
    public double getCost(String category)
    {
        if (categories.containsKey(category))
        {
            return categories.get(category).getCost();
        }
        throw new LionEngineException(ERROR_CATEGORY, category);
    }

    @Override
    public boolean isMovementAllowed(String category, MovementTile movement)
    {
        if (categories.containsKey(category))
        {
            return categories.get(category).getAllowedMovements().contains(movement);
        }
        throw new LionEngineException(ERROR_CATEGORY, category);
    }

    @Override
    public boolean isPathAvailable(int tx, int ty)
    {
        return pathfinder.findPath(this, tx, ty, false) != null;
    }

    @Override
    public boolean isBlocking(String category)
    {
        if (categories.containsKey(category))
        {
            return categories.get(category).isBlocking();
        }
        return false;
    }

    @Override
    public boolean isDestinationReached()
    {
        return destinationReached;
    }

    @Override
    public boolean isIgnoredId(Integer id)
    {
        return ignoredIds.contains(id);
    }

    @Override
    public boolean isMoving()
    {
        return moving;
    }
}
