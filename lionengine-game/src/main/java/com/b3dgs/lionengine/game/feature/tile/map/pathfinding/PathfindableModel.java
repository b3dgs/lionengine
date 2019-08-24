/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.OrientableModel;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Text;

/**
 * Pathfindable implementation.
 */
// CHECKSTYLE IGNORE LINE: FanOutComplexity
public class PathfindableModel extends FeatureModel implements Pathfindable, Recyclable
{
    /** Category not found error. */
    private static final String ERROR_CATEGORY = "Category not found: ";
    /** Diagonal speed factor. */
    private static final double DIAGONAL_SPEED = 0.8;
    /** Debug text size. */
    private static final int TEXT_DEBUG_SIZE = 8;

    /**
     * Check if the pathfindable is arrived.
     * 
     * @param v The current value.
     * @param s The speed.
     * @param d The tile destination.
     * @return <code>true</code> if arrived, <code>false</code> else.
     */
    private static boolean checkArrived(double v, double s, double d)
    {
        return s < 0 && Double.compare(v, d) <= 0 || Double.compare(s, 0) >= 0 && Double.compare(v, d) >= 0;
    }

    /** Pathfindable listeners. */
    private final ListenableModel<PathfindableListener> listenable = new ListenableModel<>();
    /** List of shared path id. */
    private final Collection<Integer> sharedPathIds = new HashSet<>(0);
    /** List of ignored id. */
    private final Collection<Integer> ignoredIds = new HashSet<>(0);
    /** Object id. */
    private Integer id;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Map reference. */
    private final MapTile map;
    /** Map path reference. */
    private final MapTilePath mapPath;
    /** Pathfinder reference. */
    private final PathFinder pathfinder;
    /** List of categories. */
    private final Map<String, PathData> categories;
    /** Transformable model. */
    private Transformable transformable;
    /** Orientable model. */
    private final OrientableModel orientable;
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
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Identifiable}</li>
     * <li>{@link Transformable}</li>
     * </ul>
     * <p>
     * The {@link Configurer} owner must provide a valid {@link PathfindableConfig}.
     * </p>
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * If the {@link Featurable} is a {@link PathfindableListener}, it will automatically
     * {@link #addListener(PathfindableListener)} on it.
     * </p>
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public PathfindableModel(Services services, Configurer configurer)
    {
        super();

        Check.notNull(services);

        map = services.get(MapTile.class);
        viewer = services.get(Viewer.class);
        mapPath = map.getFeature(MapTilePath.class);
        categories = PathfindableConfig.imports(configurer);
        orientable = new OrientableModel(services);

        final int range = (int) Math.sqrt(map.getInTileWidth() * map.getInTileWidth()
                                          + map.getInTileHeight() * (double) map.getInTileHeight());
        pathfinder = Astar.createPathFinder(map, range, Astar.createHeuristicClosest());
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

        for (int tx = dtx; tx < dtx + tw; tx++)
        {
            for (int ty = dty; ty < dty + th; ty++)
            {
                mapPath.addObjectId(tx, ty, id);
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
     * Update orientation based on movement.
     */
    private void updateOrientation()
    {
        final Orientation orientation = Orientation.get(UtilMath.getSign(moveX), UtilMath.getSign(moveY));
        if (orientation != null)
        {
            orientable.setOrientation(orientation);
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
        if (isStepReached(sx, sy, dx, dy))
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

            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).notifyMoving(this);
            }
        }
    }

    /**
     * Check if step is reached horizontally or vertically.
     * 
     * @param sx The horizontal speed.
     * @param sy The vertical speed.
     * @param dx The horizontal destination.
     * @param dy The vertical destination.
     * @return <code>true</code> if reached, <code>false</code> else.
     */
    private boolean isStepReached(double sx, double sy, int dx, int dy)
    {
        final boolean arrivedX = checkArrived(transformable.getX(), sx, dx);
        final boolean arrivedY = checkArrived(transformable.getY(), sy, dy);
        final boolean arrivedLinear = arrivedX && arrivedY;
        final boolean arrivedDiagonal = Double.compare(sx, 0) != 0
                                        && Double.compare(sy, 0) != 0
                                        && (arrivedX || arrivedY);
        return arrivedLinear || arrivedDiagonal;
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
            if (path != null)
            {
                path.clear();
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
        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyArrived(this);
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
        if (dx < x)
        {
            sx = -getSpeedX();
        }
        else if (dx > x)
        {
            sx = getSpeedX();
        }
        // Vertical speed
        if (dy < y)
        {
            sy = -getSpeedY();
        }
        else if (dy > y)
        {
            sy = getSpeedY();
        }
        // Diagonal speed
        if (Double.compare(sx, 0) != 0 && Double.compare(sy, 0) != 0)
        {
            sx *= DIAGONAL_SPEED;
            sy *= DIAGONAL_SPEED;
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
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        id = provider.getFeature(Identifiable.class).getId();
        transformable = provider.getFeature(Transformable.class);
        orientable.prepare(provider);

        if (provider instanceof PathfindableListener)
        {
            addListener((PathfindableListener) provider);
        }
    }

    @Override
    public void addListener(PathfindableListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(PathfindableListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void clearPath()
    {
        removeObjectId(getInTileX(), getInTileY());
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
                final int dx = path.getX(currentStep);
                final int dy = path.getY(currentStep);
                updateOrientation();
                moving = false;
                moveX = 0.0;
                moveY = 0.0;
                moveTo(extrp, dx * map.getTileWidth(), dy * map.getTileHeight());
            }
            // Max step is reached, stop moves and animation
            else
            {
                onArrived();
            }
        }
    }

    @Override
    public void moveTo(double extrp, double dx, double dy)
    {
        final Force force = getMovementForce(transformable.getX(), transformable.getY(), dx, dy);
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
            // CHECKSTYLE IGNORE LINE: InnerAssignment
            if (path == null && (path = pathfinder.findPath(this, tx, ty, false)) != null)
            {
                currentStep = 0;
                pathFoundChanged = false;
                prepareDestination(tx, ty);

                for (int i = 0; i < listenable.size(); i++)
                {
                    listenable.get(i).notifyStartMove(this);
                }
                return true;
            }

            // Next path, while object is moving, change takes effect when the object reached a step point
            prepareDestination(tx, ty);
            pathFoundChanged = true;
        }
        return false;
    }

    @Override
    public void setLocation(CoordTile coord)
    {
        setLocation(coord.getX(), coord.getY());
    }

    @Override
    public void setLocation(int tx, int ty)
    {
        removeObjectId(getInTileX(), getInTileY());
        transformable.teleport(tx * (double) map.getTileWidth(), ty * (double) map.getTileHeight());
        assignObjectId(getInTileX(), getInTileY());
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
            text = Graphics.createText(TEXT_DEBUG_SIZE);
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
        throw new LionEngineException(ERROR_CATEGORY + category);
    }

    @Override
    public boolean isMovementAllowed(String category, MovementTile movement)
    {
        if (categories.containsKey(category))
        {
            return categories.get(category).isAllowedMovement(movement);
        }
        return false;
    }

    @Override
    public boolean isPathAvailable(int tx, int ty)
    {
        final Path found = pathfinder.findPath(this, tx, ty, false);
        if (found != null)
        {
            found.clear();
            return true;
        }
        return false;
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

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        speedX = 1.0;
        speedY = 1.0;
        destinationReached = true;
        renderDebug = false;
        skip = false;
        moving = false;
        pathStopped = false;
        pathStoppedRequested = true;
        pathFoundChanged = false;
        currentStep = 0;
        path = null;
        moveX = 0.0;
        moveY = 0.0;
        sharedPathIds.clear();
    }
}
