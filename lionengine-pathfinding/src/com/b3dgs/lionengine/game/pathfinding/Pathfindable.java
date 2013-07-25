package com.b3dgs.lionengine.game.pathfinding;

import java.util.Set;

/**
 * Describe an object which can move on a tile path based map.
 */
public interface Pathfindable
{
    /**
     * Move to specified destination only on call.
     * 
     * @param extrp The extrapolation value.
     * @param x The destination x.
     * @param y The destination y.
     */
    void setDestination(double extrp, double x, double y);

    /**
     * Assign a specified location; will move automatically until reach it. Location is as tile (not in real value).
     * 
     * @param dtx The destination x (in tile map).
     * @param dty The destination y (in tile map).
     * @return <code>true</code> if target found and valid, <code>false</code> else.
     */
    boolean setDestination(int dtx, int dty);

    /**
     * Check if a path exists between entity and destination.
     * 
     * @param dtx The destination x (in tile map).
     * @param dty The destination y (in tile map).
     * @return <code>true</code> if path exists, <code>false</code> else.
     */
    boolean isPathAvailable(int dtx, int dty);

    /**
     * Set specified location in tile.
     * 
     * @param dtx The location x in tile.
     * @param dty The location y in tile.
     */
    void setLocation(int dtx, int dty);

    /**
     * Ignore an id while searching pathfinding. It allows to not be blocked by this id.
     * 
     * @param id The id to ignore.
     * @param state <code>true</code> to ignore, <code>false</code> else.
     */
    void setIgnoreId(Integer id, boolean state);

    /**
     * Check if id is ignored.
     * 
     * @param id The id to check.
     * @return <code>true</code> if ignored, <code>false</code> else.
     */
    boolean isIgnoredId(Integer id);

    /**
     * Clear all ignored id.
     */
    void clearIgnoredId();

    /**
     * Set the id list that shares the same path (this is used in grouped movement).
     * 
     * @param ids The id list to add.
     */
    void setSharedPathIds(Set<Integer> ids);

    /**
     * Clear the list of id that share the same path.
     */
    void clearSharedPathIds();

    /**
     * Update automatic moves if has.
     * 
     * @param extrp The extrapolation value.
     */
    void updateMoves(double extrp);

    /**
     * Stop any pathfinding movements.
     */
    void stopMoves();

    /**
     * Set move speed.
     * 
     * @param speedX The horizontal speed.
     * @param speedY The vertical speed.
     */
    void setSpeed(double speedX, double speedY);

    /**
     * Check is its moving.
     * 
     * @return <code>true</code> if moving, <code>false</code> else.
     */
    boolean isMoving();

    /**
     * Check if has reached destination.
     * 
     * @return <code>true</code> if destination has been reached, <code>false</code> else.
     */
    boolean isDestinationReached();

    /**
     * Get horizontal speed.
     * 
     * @return The horizontal speed.
     */
    double getSpeedX();

    /**
     * Get vertical speed.
     * 
     * @return The vertical speed.
     */
    double getSpeedY();

    /**
     * Get horizontal current speed.
     * 
     * @return The horizontal current speed.
     */
    double getMoveX();

    /**
     * Get vertical current speed.
     * 
     * @return The vertical current speed.
     */
    double getMoveY();

    /**
     * Get horizontal location in tile (location on map).
     * 
     * @return The horizontal location in tile (location on map).
     */
    int getLocationInTileX();

    /**
     * Get vertical location in tile (location on map).
     * 
     * @return The vertical location in tile (location on map).
     */
    int getLocationInTileY();
}
