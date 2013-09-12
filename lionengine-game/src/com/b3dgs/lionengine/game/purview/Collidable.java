package com.b3dgs.lionengine.game.purview;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;

/**
 * Purview representing something which can enter in collision with another. Based on a ray casting collision from a
 * bounding box area.
 */
public interface Collidable
{
    /**
     * Update collision with specified area.
     * 
     * @param mirror The mirror flag.
     */
    void updateCollision(boolean mirror);

    /**
     * Set the collision to use.
     * 
     * @param collision The collision to use (<code>null</code> if none).
     */
    void setCollision(CollisionData collision);

    /**
     * Get the current collision used.
     * 
     * @return The collision data.
     */
    CollisionData getCollisionData();

    /**
     * Check if the entity entered in collision with another one.
     * 
     * @param entity The opponent.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    boolean collide(Collidable entity);

    /**
     * Check if the entity entered in collision with a specified area.
     * 
     * @param area The area to check.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    boolean collide(Rectangle2D area);

    /**
     * Render collision bounding box.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    void renderCollision(Graphic g, CameraGame camera);

    /**
     * Get collision representation.
     * 
     * @return The collision representation.
     */
    Rectangle2D getCollisionBounds();

    /**
     * Get collision ray cast.
     * 
     * @return The collision ray cast.
     */
    Line2D getCollisionRay();
}
