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
package com.b3dgs.lionengine.game.platform.entity;

import java.util.HashMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.CollisionTileCategory;
import com.b3dgs.lionengine.game.platform.map.MapTilePlatform;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;

/**
 * Abstract and standard entity used for platform games. It already supports gravity, animation and collisions.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityPlatform
        extends EntityGame
        implements Animator
{
    /** Animation surface. */
    protected SpriteAnimated sprite;
    /** List of declared tile collision point. */
    private final HashMap<CollisionTileCategory<?>, CoordTile> tileCollisions;
    /** Collisions special offsets x. */
    private int collOffX;
    /** Collisions special offsets y. */
    private int collOffY;
    /** Frame offsets x. */
    private int frameOffsetX;
    /** Frame offsets y. */
    private int frameOffsetY;
    /** Old collision y. */
    private double locationBeforeCollisionOldY;
    /** Last collision y. */
    private double locationBeforeCollisionY;

    /**
     * Constructor.
     * <p>
     * It needs in its config file the frame description:
     * </p>
     * 
     * <pre>
     * {@code
     * <entity>
     *     <frames horizontal="" vertical=""/>
     *     <size width="" height=""/>
     * </entity>
     * }
     * </pre>
     * 
     * @param setup The entity setup.
     */
    public EntityPlatform(SetupSurfaceGame setup)
    {
        super(setup);
        tileCollisions = new HashMap<>(1);
        final int hf = setup.configurable.getDataInteger("horizontal", "frames");
        final int vf = setup.configurable.getDataInteger("vertical", "frames");
        final int width = setup.configurable.getDataInteger("width", "size");
        final int height = setup.configurable.getDataInteger("height", "size");
        sprite = Drawable.loadSpriteAnimated(setup.surface, hf, vf);
        frameOffsetX = 0;
        frameOffsetY = 0;
        setSize(width, height);
    }

    /**
     * Update actions, such as movements and attacks.
     * 
     * @param extrp The extrapolation value.
     */
    protected abstract void handleActions(final double extrp);

    /**
     * Update movement, depending of actions.
     * 
     * @param extrp The extrapolation value.
     */
    protected abstract void handleMovements(final double extrp);

    /**
     * Update collisions, after movements. Should be used to call
     * {@link #getCollisionTile(MapTilePlatform, CollisionTileCategory)} for each collision test.
     * <p>
     * Example:
     * </p>
     * 
     * <pre>
     * &#064;Override
     * protected void handleCollisions(double extrp)
     * {
     *     // Check something here
     *     // ...
     * 
     *     // Horizontal collision
     *     if (getDiffHorizontal() &lt; 0)
     *     {
     *         checkHorizontal(EntityCollisionTileCategory.KNEE_LEFT);
     *     }
     *     else if (getDiffHorizontal() &gt; 0)
     *     {
     *         checkHorizontal(EntityCollisionTileCategory.KNEE_RIGHT);
     *     }
     * 
     *     // Vertical collision
     *     if (getDiffVertical() &lt; 0 || isOnGround())
     *     {
     *         checkVertical(EntityCollisionTileCategory.LEG_LEFT);
     *         checkVertical(EntityCollisionTileCategory.LEG_RIGHT);
     *         checkVertical(EntityCollisionTileCategory.GROUND_CENTER);
     *     }
     * }
     * </pre>
     * 
     * @param extrp The extrapolation value.
     */
    protected abstract void handleCollisions(final double extrp);

    /**
     * Update animations, corresponding to a movement.
     * 
     * @param extrp The extrapolation value.
     */
    protected abstract void handleAnimations(final double extrp);

    /**
     * Render on screen.
     * 
     * @param g The graphic output.
     * @param camera The camera viewpoint.
     */
    public void render(Graphic g, CameraPlatform camera)
    {
        renderAnim(g, sprite, camera);
    }

    /**
     * Set frame offsets (offsets on rendering).
     * 
     * @param frameOffsetX The horizontal offset.
     * @param frameOffsetY The vertical offset.
     */
    public void setFrameOffsets(int frameOffsetX, int frameOffsetY)
    {
        this.frameOffsetX = frameOffsetX;
        this.frameOffsetY = frameOffsetY;
    }

    /**
     * Get real horizontal speed (calculated on differential location x).
     * 
     * @return The real speed.
     */
    public double getDiffHorizontal()
    {
        return getLocationX() - getLocationOldX();
    }

    /**
     * Get real vertical speed (calculated on differential location y).
     * 
     * @return The real speed.
     */
    public double getDiffVertical()
    {
        return getLocationY() - getLocationOldY();
    }

    /**
     * Check if entity is going up.
     * 
     * @return <code>true</code> if going up, <code>false</code> else.
     */
    public boolean isGoingUp()
    {
        return locationBeforeCollisionY > locationBeforeCollisionOldY;
    }

    /**
     * Check if entity is going down.
     * 
     * @return <code>true</code> if going down, <code>false</code> else.
     */
    public boolean isGoingDown()
    {
        return locationBeforeCollisionY < locationBeforeCollisionOldY;
    }

    /**
     * Apply an horizontal collision using the specified blocking x value.
     * 
     * @param x The blocking x value.
     * @return <code>true</code> if collision where applied.
     */
    public boolean applyHorizontalCollision(Double x)
    {
        if (x != null)
        {
            teleportX(x.doubleValue());
            return true;
        }
        return false;
    }

    /**
     * Apply a vertical collision using the specified blocking y value.
     * 
     * @param y The blocking y value.
     * @return <code>true</code> if collision where applied.
     */
    public boolean applyVerticalCollision(Double y)
    {
        if (y != null)
        {
            locationBeforeCollisionOldY = locationBeforeCollisionY;
            locationBeforeCollisionY = getLocationY();
            teleportY(y.doubleValue());
            return true;
        }
        return false;
    }

    /**
     * Render an animated sprite from the entity location, following camera view point.
     * 
     * @param g The graphics output.
     * @param sprite The sprite to render.
     * @param camera The camera reference.
     */
    public void renderAnim(Graphic g, SpriteAnimated sprite, CameraPlatform camera)
    {
        renderAnim(g, sprite, camera, 0, 0);
    }

    /**
     * Render an animated sprite from the entity location, following camera view point.
     * 
     * @param g The graphics output.
     * @param sprite The sprite to render.
     * @param camera The camera reference.
     * @param rx The horizontal rendering offset.
     * @param ry The vertical rendering offset.
     */
    public void renderAnim(Graphic g, SpriteAnimated sprite, CameraPlatform camera, int rx, int ry)
    {
        final int x = camera.getViewpointX(getLocationIntX() - sprite.getFrameWidth() / 2 - frameOffsetX);
        final int y = camera.getViewpointY(getLocationIntY() + sprite.getFrameHeight() + frameOffsetY);
        sprite.render(g, x + rx, y + ry);
    }

    /**
     * Define a tile collision at a specific offset from the entity referential.
     * 
     * @param type The collision tile type.
     * @param offsetX The horizontal offset value.
     * @param offsetY The vertical offset value.
     */
    protected <C extends Enum<C>> void addCollisionTile(CollisionTileCategory<C> type, int offsetX, int offsetY)
    {
        tileCollisions.put(type, new CoordTile(offsetX, offsetY));
    }

    /**
     * Get the collision offset.
     * 
     * @param type The collision category.
     * @return The collision offset.
     */
    protected <C extends Enum<C>> CoordTile getCollisionTileOffset(CollisionTileCategory<C> type)
    {
        return tileCollisions.get(type);
    }

    /**
     * Get the first tile hit for the specified collision tile category matching the collision list.
     * 
     * @param map The map reference.
     * @param category The collision tile category.
     * @return The first tile hit, <code>null</code> if none.
     */
    public <C extends Enum<C>, T extends TilePlatform<C>, M extends MapTilePlatform<C, T>> T getCollisionTile(M map,
            CollisionTileCategory<C> category)
    {
        final CoordTile offsets = tileCollisions.get(category);
        collOffX = offsets.getX();
        collOffY = offsets.getY();
        final T tile = map.getFirstTileHit(this, category.getCollisions());
        return tile;
    }

    /*
     * EntityGame
     */

    /**
     * Main update routine. By default it calls theses functions in this order:
     * <ul>
     * <li>{@link #handleActions(double extrp)}</li>
     * <li>{@link #handleMovements(double extrp)}</li>
     * <li>{@link #handleCollisions(double extrp)}</li>
     * <li>{@link #handleAnimations(double extrp)}</li>
     * </ul>
     * 
     * @param extrp The extrapolation value.
     */
    @Override
    public void update(double extrp)
    {
        handleActions(extrp);
        handleMovements(extrp);
        handleCollisions(extrp);
        collOffX = 0;
        collOffY = 0;
        updateCollision();
        handleAnimations(extrp);
    }

    @Override
    public void updateMirror()
    {
        super.updateMirror();
        sprite.setMirror(getMirror());
    }

    @Override
    public double getLocationX()
    {
        return super.getLocationX() + collOffX;
    }

    @Override
    public double getLocationY()
    {
        return super.getLocationY() + collOffY;
    }

    @Override
    public double getLocationOldX()
    {
        return super.getLocationOldX() + collOffX;
    }

    @Override
    public double getLocationOldY()
    {
        return super.getLocationOldY() + collOffY;
    }

    @Override
    public int getLocationIntX()
    {
        return super.getLocationIntX() + collOffX;
    }

    @Override
    public int getLocationIntY()
    {
        return super.getLocationIntY() + collOffY;
    }

    @Override
    public void teleport(double x, double y)
    {
        super.teleport(x - collOffX, y - collOffY);
    }

    @Override
    public void teleportX(double x)
    {
        super.teleportX(x - collOffX);
    }

    @Override
    public void teleportY(double y)
    {
        super.teleportY(y - collOffY);
    }

    /*
     * Animator
     */

    @Override
    public void play(Animation anim)
    {
        sprite.play(anim);
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        sprite.setAnimSpeed(speed);
    }

    @Override
    public void updateAnimation(double extrp)
    {
        sprite.updateAnimation(extrp);
    }

    @Override
    public int getFrame()
    {
        return sprite.getFrame();
    }

    @Override
    public int getFrameAnim()
    {
        return sprite.getFrameAnim();
    }

    @Override
    public void stopAnimation()
    {
        sprite.stopAnimation();
    }

    @Override
    public AnimState getAnimState()
    {
        return sprite.getAnimState();
    }

    @Override
    public void setFrame(int frame)
    {
        sprite.setFrame(frame);
    }
}
