package com.b3dgs.lionengine.game.platform;

import java.util.List;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.entity.SetupEntityGame;
import com.b3dgs.lionengine.game.platform.map.MapTilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Abstract and standard entity used for platform games. It already supports gravity, animation and collisions.
 */
public abstract class EntityPlatform
        extends EntityGame
        implements Animator
{
    /** Animation surface. */
    protected final SpriteAnimated sprite;
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
     * Create a new platform entity from an existing, sharing the same surface.
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
    public EntityPlatform(SetupEntityGame setup)
    {
        super(setup.configurable);
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
     * Update collisions, after movements. Should be used to call {@link #setCollisionOffset(int, int)} for each
     * collision test.
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
     *     // Respawn when fall at the bottom of the map
     *     if (getLocationY() &lt; 0)
     *     {
     *         // respawn
     *     }
     * 
     *     // Horizontal collision
     *     Tile tile = collisionCheck(0, 1, TileCollision.COLLISION_HORIZONTAL);
     *     if (tile != null)
     *     {
     *         final Double x = tile.getCollisionX(this);
     *         if (applyHorizontalCollision(x))
     *         {
     *             // collision occurred
     *         }
     *     }
     * 
     *     // Vertical collision
     *     tile = collisionCheck(0, 0, TileCollision.COLLISION_VERTICAL);
     *     if (tile != null)
     *     {
     *         final Double y = tile.getCollisionY(this);
     *         if (applyVerticalCollision(y))
     *         {
     *             // collision occurred
     *         }
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
    public void update(final double extrp)
    {
        handleActions(extrp);
        handleMovements(extrp);
        handleCollisions(extrp);
        collOffX = 0;
        collOffY = 0;
        updateCollision();
        handleAnimations(extrp);
    }

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
    protected boolean applyHorizontalCollision(Double x)
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
    protected boolean applyVerticalCollision(Double y)
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
    protected void renderAnim(Graphic g, SpriteAnimated sprite, CameraPlatform camera)
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
    protected void renderAnim(Graphic g, SpriteAnimated sprite, CameraPlatform camera, int rx, int ry)
    {
        final int x = camera.getViewpointX(getLocationIntX() - sprite.getFrameWidth() / 2 - frameOffsetX);
        final int y = camera.getViewpointY(getLocationIntY() + sprite.getFrameHeight() + frameOffsetY);
        sprite.render(g, x + rx, y + ry);
    }

    /**
     * Set the collision offset. Should be called only for special collision, such as leg left & right, and more
     * generally for any collision that are not at the center. Thus
     * {@link MapTilePlatform#getFirstTileHit(Localizable, List)} can be called just after.
     * <p>
     * Example:
     * </p>
     * 
     * <pre>
     * setCollisionOffset(-10, 0); // Left leg
     * final Tile tile = map.getFirstTileHit(this, List);
     * if (tile != null)
     * {
     *     // Apply collision
     * }
     * 
     * setCollisionOffset(10, 0); // Right leg
     * final Tile tile = map.getFirstTileHit(this, List);
     * if (tile != null)
     * {
     *     // Apply collision
     * }
     * </pre>
     * 
     * @param offsetX The horizontal offset value.
     * @param offsetY The vertical offset value.
     */
    protected void setCollisionOffset(int offsetX, int offsetY)
    {
        collOffX = offsetX;
        collOffY = offsetY;
        // TODO: find a better solution
    }

    /*
     * EntityGame
     */

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

    /*
     * Animator
     */

    @Override
    public void play(Animation anim)
    {
        sprite.play(anim);
    }

    @Override
    public void play(int start, int end, double speed, boolean reverse, boolean repeat)
    {
        sprite.play(start, end, speed, reverse, repeat);
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
