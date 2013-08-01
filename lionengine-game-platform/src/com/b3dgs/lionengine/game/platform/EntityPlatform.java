package com.b3dgs.lionengine.game.platform;

import java.util.List;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.SetupEntityGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.maptile.MapTile;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;

/**
 * Abstract and standard entity used for platform games. It already supports gravity, animation and collisions.
 * 
 * @param <C> Tile collision type used.
 * @param <T> Tile type used.
 */
public abstract class EntityPlatform<C extends Enum<C>, T extends TilePlatform<C>>
        extends EntityGame
        implements Animator
{
    /** Animation surface. */
    protected final SpriteAnimated sprite;
    /** Map reference. */
    final MapTile<C, T> map;
    /** Collisions special offsets x. */
    private int collOffX;
    /** Collisions special offsets y. */
    private int collOffY;
    /** Frame offsets x. */
    private int frameOffsetX;
    /** Frame offsets y. */
    private int frameOffsetY;

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
     * @param map The map reference.
     */
    public EntityPlatform(SetupEntityGame setup, MapTile<C, T> map)
    {
        super(setup.configurable);
        this.map = map;
        final int hf = setup.configurable.getDataInteger("horizontal", "frames");
        final int vf = setup.configurable.getDataInteger("vertical", "frames");
        final int width = setup.configurable.getDataInteger("width", "size");
        final int height = setup.configurable.getDataInteger("height", "size");
        sprite = Drawable.loadSpriteAnimated(setup.surface, hf, vf);
        frameOffsetX = 0;
        frameOffsetY = 0;
        setSize(width, height);
        invertAxisY(true);
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
     * Update collisions, after movements. Should be used to call {@link #collisionCheck(int, int, List)} for each
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
        updateCollision(-frameOffsetX, frameOffsetY, getWidth(), getHeight());
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
        final int x = camera.getViewpointX(getLocationIntX() - frameOffsetX);
        final int y = camera.getViewpointY(getLocationIntY() + frameOffsetY + getHeight());
        sprite.render(g, x, y);
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
     * Get location x relative to map referential as tile.
     * 
     * @return The location x relative to map referential as tile.
     */
    public int getInTileX()
    {
        return (int) Math.floor(getLocationX() / map.getTileWidth());
    }

    /**
     * Get location y relative to map referential as tile.
     * 
     * @return The location y relative to map referential as tile.
     */
    public int getInTileY()
    {
        return (int) Math.floor(getLocationY() / map.getTileHeight());
    }

    /**
     * Get old location x relative to map referential as tile.
     * 
     * @return The old location x relative to map referential as tile.
     */
    public int getInTileOldX()
    {
        return (int) Math.floor(getLocationOldX() / map.getTileWidth());
    }

    /**
     * Get old location y relative to map referential as tile.
     * 
     * @return The old location y relative to map referential as tile.
     */
    public int getInTileOldY()
    {
        return (int) Math.floor(getLocationOldY() / map.getTileHeight());
    }

    /**
     * Get x value on tile referential (between 0 and tile width).
     * 
     * @param tile The tile referential.
     * @return The x value.
     */
    public int getOnTileX(T tile)
    {
        return getLocationIntX() - tile.getX();
    }

    /**
     * Get y value on tile referential (between 0 and tile height).
     * 
     * @param tile The tile referential.
     * @return The y value.
     */
    public int getOnTileY(T tile)
    {
        return getLocationIntY() - tile.getY();
    }

    /**
     * Get old x value on tile referential (between 0 and tile width).
     * 
     * @param tile The tile referential.
     * @return The old x value.
     */
    public int getOnTileOldX(T tile)
    {
        return (int) getLocationOldX() - tile.getX();
    }

    /**
     * Get old y value on tile referential (between 0 and tile height).
     * 
     * @param tile The tile referential.
     * @return The old y value.
     */
    public int getOnTileOldY(T tile)
    {
        return (int) getLocationOldY() - tile.getY();
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
            setLocationX(x.doubleValue());
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
            setLocationY(y.doubleValue());
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
        sprite.render(g, getLocationIntX() - camera.getLocationIntX() + rx,
                -getLocationIntY() + camera.getLocationIntY() + ry);
    }

    /**
     * Return the first hit tile. Any call to this function should be done last in {@link #handleCollisions(double)}.
     * Multiple call can be done consecutively. Any other code should be done before the first call to this function.
     * 
     * <pre>
     * do something...
     * //...
     * collisionCheck(...)
     * // last
     * 
     * </pre>
     * 
     * @param offsetX The offset value.
     * @param offsetY The offset value.
     * @param collisions The accepted collisions.
     * @return The first hit tile (ray cast).
     */
    protected T collisionCheck(int offsetX, int offsetY, List<C> collisions)
    {
        // Collision offset storage
        this.collOffX = offsetX;
        this.collOffY = offsetY;

        // Starting location
        final int sv = (int) Math.floor(getLocationOldY());
        final int sh = (int) Math.floor(getLocationOldX());

        // Ending location
        final int ev = (int) Math.ceil(getLocationY());
        final int eh = (int) Math.ceil(getLocationX());

        // Distance calculation
        final int dv = sv - ev;
        final int dh = eh - sh;

        // Search vector and number of search steps
        final double sx, sy;
        final int stepMax;
        if (Math.abs(dv) >= Math.abs(dh))
        {
            sy = EntityPlatform.getTileSearchSpeed(dv);
            sx = EntityPlatform.getTileSearchSpeed(dv, dh);
            stepMax = Math.abs(dv);
        }
        else
        {
            sx = EntityPlatform.getTileSearchSpeed(dh);
            sy = EntityPlatform.getTileSearchSpeed(dh, dv);
            stepMax = Math.abs(dh);
        }

        // Check each potential tile from first to last and search first collision
        return getFirstTileHit(sv, sh, sy, sx, stepMax, collisions);
    }

    /**
     * Get the first tile hit on the search area.
     * 
     * @param sv Starting vertical location.
     * @param sh Starting horizontal location.
     * @param sy Search vertical speed.
     * @param sx Search horizontal speed.
     * @param stepMax Maximum number of search steps.
     * @param collisions Collisions list to search for.
     * @return The first tile hit, <code>null</code> if none found.
     */
    private T getFirstTileHit(int sv, int sh, double sy, double sx, int stepMax, List<C> collisions)
    {
        int step = 0;
        for (double v = sv, h = sh; step <= stepMax; v -= sy, h += sx)
        {
            final T tile = map.getTile((int) Math.floor(h / map.getTileWidth()),
                    (int) Math.floor(v / map.getTileHeight()));
            if (tile != null && collisionTest(tile, collisions) && tile.hasCollision(this))
            {
                return tile;
            }
            step++;
        }
        return null;
    }

    /**
     * Check if tile fill condition
     * 
     * @param tile The tile to check.
     * @param collisions The collisions list.
     * @return <code>true</code> if collision is allowed, <code>false</code> else.
     */
    private boolean collisionTest(T tile, List<C> collisions)
    {
        if (collisions.isEmpty())
        {
            return true;
        }
        for (final C collision : collisions)
        {
            if (collision == tile.getCollision())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the tile search speed value.
     * 
     * @param d The distance value.
     * @return The speed value.
     */
    private static double getTileSearchSpeed(int d)
    {
        if (d < 0)
        {
            return -1.0;
        }
        else if (d > 0)
        {
            return 1.0;
        }
        return 0.0;
    }

    /**
     * Get the tile search speed value.
     * 
     * @param dsup The distance superior value.
     * @param dinf The distance inferior value.
     * @return The speed value.
     */
    private static double getTileSearchSpeed(int dsup, int dinf)
    {
        if (0 == dsup)
        {
            return EntityPlatform.getTileSearchSpeed(dinf);
        }
        return dinf / (double) dsup;
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
    public double getFrameReal()
    {
        return sprite.getFrameReal();
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
