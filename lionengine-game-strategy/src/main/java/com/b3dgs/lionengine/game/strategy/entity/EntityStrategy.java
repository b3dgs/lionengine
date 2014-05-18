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
package com.b3dgs.lionengine.game.strategy.entity;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.game.strategy.map.MapTileStrategy;

/**
 * This class represent the first abstraction for any kind of entity which can be used in a strategy game. It contains a
 * set of useful functions, a specific ID, a ready to use animated sprite, and a list of skills. Entity data are
 * automatically loaded, from the file data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class EntityStrategy
        extends EntityGame
        implements Animator, Tiled
{
    /** Animation surface. */
    protected final SpriteAnimated sprite;
    /** Entity location offset x. */
    protected final int offsetX;
    /** Entity location offset y. */
    protected final int offsetY;
    /** Map reference. */
    private final MapTileStrategy<?, ?, ?> map;
    /** Current animation. */
    private Animation animationCurrent;
    /** Orientation value. */
    private Orientation orientation;
    /** Entity field of view (in tile). */
    private int fov;
    /** Entity layer. */
    private int layer;
    /** Entity old layer. */
    private int layerOld;
    /** True if layer changed. */
    private boolean layerChanged;
    /** Entity map layer. */
    private int mapLayer;
    /** Entity old map layer. */
    private int mapLayerOld;
    /** True if map layer changed. */
    private boolean mapLayerChanged;
    /** Entity over flag. */
    private boolean over;
    /** Entity selected flag. */
    private boolean selected;
    /** Entity active flag. */
    private boolean active;
    /** Entity visible flag. */
    private boolean visible;
    /** Entity alive flag. */
    private boolean alive;
    /** Entity is selectable. */
    private boolean selectable;
    /** Owner id. */
    private int ownerId;

    /**
     * Create a new entity. The entity configuration file should at least contain the following elements:
     * 
     * <pre>
     * {@code
     * <entity surface="sprite.png">
     *     <frames horizontal="" vertical=""/>
     *     <size width="" height=""/>
     *     <offset x="" y=""/>
     * </entity>
     * }
     * </pre>
     * 
     * @param setup The entity setup.
     * @param map The map reference.
     */
    public EntityStrategy(SetupSurfaceGame setup, MapTileStrategy<?, ?, ?> map)
    {
        super(setup);
        this.map = map;
        final String configFile = setup.configFile.getPath();

        // Horizontal frames
        final int hf = getDataInteger("horizontal", "lionengine:frames");
        Check.argument(hf > 0, "The horizontal frames number is missing: \"", configFile, "\"");

        // Vertical frames
        final int vf = getDataInteger("vertical", "lionengine:frames");
        Check.argument(vf > 0, "The vertical frames number is missing: \"", configFile, "\"");

        // Size
        final int width = getDataInteger("widthInTile", "lionengine:tileSize") * map.getTileWidth();
        final int height = getDataInteger("heightInTile", "lionengine:tileSize") * map.getTileHeight();
        Check.argument(width > 0 && height > 0, "The surface size is missing: \"", configFile, "\"");

        // Offset
        offsetX = getDataInteger("x", "lionengine:offset");
        offsetY = getDataInteger("y", "lionengine:offset");

        // Surface
        Check.notNull(setup.surface, "Missing surface file from the setup: \"", setup.surfaceFile.getPath(), "\"");
        sprite = Drawable.loadSpriteAnimated(setup.surface, hf, vf);

        // Setup
        setSize(width, height);
        sprite.setFrame(1);
        orientation = Orientation.SOUTH;
        setCollision(new CollisionData(0, 0, getWidth(), getHeight(), false));
        animationCurrent = null;
        active = true;
        visible = true;
        alive = true;
        selectable = true;
        mapLayer = 0;
        mapLayerOld = 0;
        mapLayerChanged = true;
        fov = 1;
        ownerId = -1;
    }

    /**
     * Stop any action.
     */
    public abstract void stop();

    /**
     * Set location in tile.
     * 
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     */
    public void setLocation(int tx, int ty)
    {
        for (int v = 0; v < getHeightInTile(); v++)
        {
            for (int h = 0; h < getWidthInTile(); h++)
            {
                if (map.getRef(getLocationInTileX() + h, getLocationInTileY() + v).equals(getId()))
                {
                    map.setRef(getLocationInTileX() + h, getLocationInTileY() + v, Integer.valueOf(0));
                }
            }
        }
        super.setLocation(tx * map.getTileWidth(), ty * map.getTileHeight());
        for (int v = 0; v < getHeightInTile(); v++)
        {
            for (int h = 0; h < getWidthInTile(); h++)
            {
                map.setRef(getLocationInTileX() + h, getLocationInTileY() + v, getId());
            }
        }
    }

    /**
     * Set the player owner id.
     * 
     * @param id The player owner id.
     */
    public void setPlayerId(int id)
    {
        ownerId = id;
    }

    /**
     * Define a layer number, used for rendering priority. Layer number has to be between 0 and
     * {@link HandlerEntityStrategy#LAYERS}. 0 is rendered firstly, last is rendered lastly.
     * 
     * @param layer The layer number.
     */
    public void setLayer(int layer)
    {
        if (!layerChanged)
        {
            layerOld = layer;
            this.layer = UtilMath.fixBetween(layer, 0, HandlerEntityStrategy.LAYERS);
            layerChanged = true;
        }
    }

    /**
     * Set specific orientation.
     * 
     * @param orientation The new orientation.
     */
    public void setOrientation(Orientation orientation)
    {
        this.orientation = orientation;
    }

    /**
     * Set field of view value (in tile).
     * 
     * @param fov The field of view.
     */
    public void setFov(int fov)
    {
        this.fov = fov;
    }

    /*
     * States
     */

    /**
     * Set selection state (used with cursor selection, to give an order).
     * 
     * @param state The state.
     */
    public void setSelection(boolean state)
    {
        selected = state;
    }

    /**
     * Set over state (used with cursor selection, to apply cursor over effect).
     * 
     * @param state The state.
     */
    public void setOver(boolean state)
    {
        over = state;
    }

    /**
     * Set active state (false to disable mouse over and selection).
     * 
     * @param state The active state.
     */
    public void setActive(boolean state)
    {
        active = state;
    }

    /**
     * Set visibility state.
     * 
     * @param state The visibility state.
     */
    public void setVisible(boolean state)
    {
        visible = state;
    }

    /**
     * Set alive state.
     * 
     * @param state The alive state.
     */
    public void setAlive(boolean state)
    {
        alive = state;
    }

    /**
     * Set the selectable state.
     * 
     * @param state The selectable state.
     */
    public void setSelectable(boolean state)
    {
        selectable = state;
    }

    /**
     * Get distance in tile between the area.
     * 
     * @param tx The tile x.
     * @param ty The tile y.
     * @param tw The width in tile.
     * @param th The height in tile.
     * @param fromCenter <code>true</code> to get distance from center only, <code>false</code> from the global area.
     * @return The number of tiles between them.
     */
    public int getDistance(int tx, int ty, int tw, int th, boolean fromCenter)
    {
        if (fromCenter)
        {
            return UtilMath.getDistance(getLocationInTileX() + getWidthInTile() / 2, getLocationInTileY()
                    + getHeightInTile() / 2, tx + tw / 2, ty + th / 2);
        }
        int min = Integer.MAX_VALUE;
        for (int h = tx; h < tx + tw; h++)
        {
            for (int v = ty; v < ty + th; v++)
            {
                final int dist = UtilMath.getDistance(getLocationInTileX(), getLocationInTileY(), h, v);
                if (dist < min)
                {
                    min = dist;
                }
            }
        }
        return min;
    }

    /**
     * Get distance in tile between the specified tiled.
     * 
     * @param tiled The tiled to check.
     * @param fromCenter <code>true</code> to get distance from center only, <code>false</code> from the global area.
     * @return The number of tiles between them.
     */
    public int getDistanceInTile(Tiled tiled, boolean fromCenter)
    {
        return getDistance(tiled.getLocationInTileX(), tiled.getLocationInTileY(), tiled.getWidthInTile(),
                tiled.getHeightInTile(), fromCenter);
    }

    /**
     * Get field of view value (in tile).
     * 
     * @return The field of view value.
     */
    public int getFov()
    {
        return fov;
    }

    /**
     * Get the player owner id.
     * 
     * @return The player owner id.
     */
    public int getPlayerId()
    {
        return ownerId;
    }

    /**
     * Get current orientation.
     * 
     * @return The current orientation.
     */
    public Orientation getOrientation()
    {
        return orientation;
    }

    /**
     * Get current layer value.
     * 
     * @return The current layer value.
     */
    public int getLayer()
    {
        return layer;
    }

    /**
     * Get the current playing animation.
     * 
     * @return current playing animation.
     */
    public Animation getAnimationCurrent()
    {
        return animationCurrent;
    }

    /**
     * Get horizontal offset (used in case of rendering).
     * 
     * @return The horizontal rendering offset.
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * Get vertical offset (used in case of rendering).
     * 
     * @return The vertical rendering offset.
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    /**
     * Check if entity is currently selected (hit by a cursor selection).
     * 
     * @return <code>true</code> if selected, <code>false</code> else.
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * Check if entity is currently over a cursor (hit by a cursor).
     * 
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver()
    {
        return over;
    }

    /**
     * Check if entity is active.
     * 
     * @return <code>true</code> if active, <code>false</code> else.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Check if entity is visible.
     * 
     * @return The visibility state.
     */
    public boolean isVisible()
    {
        return visible;
    }

    /**
     * Check if entity is alive.
     * 
     * @return <code>true</code> if alive, <code>false</code> else.
     */
    public boolean isAlive()
    {
        return alive;
    }

    /**
     * Check if entity is selectable.
     * 
     * @return <code>true</code> if selectable, <code>false</code> else.
     */
    public boolean isSelectable()
    {
        return selectable;
    }

    /**
     * Check if layer has be changed.
     * 
     * @return The changed state.
     */
    public boolean isLayerChanged()
    {
        return layerChanged;
    }

    /**
     * Render a surface by using the camera referential.
     * 
     * @param g The graphic output.
     * @param sprite The sprite reference.
     * @param camera The camera reference.
     * @param offsetX The horizontal offset.
     * @param offsetY The vertical offset.
     */
    protected void render(Graphic g, Sprite sprite, CameraGame camera, int offsetX, int offsetY)
    {
        final int x = camera.getViewpointX(getLocationIntX() - offsetX);
        final int y = camera.getViewpointY(getLocationIntY() + offsetY + getHeight());
        sprite.render(g, x, y);
    }

    /**
     * Remove any map reference for this entity, around its size.
     */
    protected void removeRef()
    {
        for (int v = getLocationInTileY() - 1; v <= getLocationInTileY() + getHeightInTile() + 1; v++)
        {
            for (int h = getLocationInTileX() - 1; h <= getLocationInTileX() + getWidthInTile() + 1; h++)
            {
                try
                {
                    if (map.getRef(h, v).equals(getId()))
                    {
                        map.setRef(h, v, Integer.valueOf(0));
                    }
                }
                catch (final ArrayIndexOutOfBoundsException exception)
                {
                    continue;
                }
            }
        }
    }

    /**
     * Set layer changes state. After a call to setLayer(), it is automatically set to true. It has to be set to false
     * when changes are done.
     * 
     * @param state The changed flag.
     */
    void setLayerChanged(boolean state)
    {
        layerChanged = state;
    }

    /**
     * Define a layer number, used for rendering priority. Layer number has to be between 0 and the number of vertical
     * map tiles included. 0 is rendered firstly, last is rendered lastly. Changes can be done only one time, until a
     * call to setLayerChanged(false).
     * 
     * @param layer The layer number.
     */
    void setMapLayer(int layer)
    {
        if (!mapLayerChanged)
        {
            mapLayerOld = layer;
            mapLayer = UtilMath.fixBetween(layer, 0, map.getHeightInTile());
            mapLayerChanged = true;
        }
    }

    /**
     * Set layer changes state. After a call to setLayer(), it is automatically set to true. It has to be set to false
     * when changes are done.
     * 
     * @param state The changed flag.
     */
    void setMapLayerChanged(boolean state)
    {
        mapLayerChanged = state;
    }

    /**
     * Get last layer value, before changes.
     * 
     * @return The last layer value.
     */
    int getLayerOld()
    {
        return layerOld;
    }

    /**
     * Get current layer value.
     * 
     * @return The current layer value.
     */
    int getMapLayer()
    {
        return mapLayer;
    }

    /**
     * Get last layer value, before changes.
     * 
     * @return The last layer value.
     */
    int getMapLayerOld()
    {
        return mapLayerOld;
    }

    /**
     * Check if layer has be changed.
     * 
     * @return The changed state.
     */
    boolean isMapLayerChanged()
    {
        return mapLayerChanged;
    }

    /*
     * EntityGame
     */

    @Override
    public void update(double extrp)
    {
        updateMirror();
        sprite.setMirror(getMirror());
        updateCollision();
        sprite.updateAnimation(extrp);
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        if (visible)
        {
            int offset = 0;
            final int frame;
            if (animationCurrent != null)
            {
                if (getMirror())
                {
                    offset = getOrientation().ordinal() - Orientation.ORIENTATIONS_NUMBER_HALF;
                }
                frame = getFrame() + (getOrientation().ordinal() - offset * 2)
                        * (animationCurrent.getLast() - animationCurrent.getFirst() + 1);
            }
            else
            {
                frame = getFrame();
            }

            final int x = camera.getViewpointX(getLocationIntX() - getOffsetX());
            final int y = camera.getViewpointY(getLocationIntY() + getOffsetY() + getHeight());
            sprite.render(g, frame, x, y);
        }
    }

    @Override
    public void destroy()
    {
        super.destroy();
        removeRef();
    }

    /*
     * Localizable
     */

    @Override
    public int getLocationInTileX()
    {
        return (int) Math.round(getLocationX() / map.getTileWidth());
    }

    @Override
    public int getLocationInTileY()
    {
        return (int) Math.round(getLocationY() / map.getTileHeight());
    }

    @Override
    public int getWidthInTile()
    {
        return getWidth() / map.getTileWidth();
    }

    @Override
    public int getHeightInTile()
    {
        return getHeight() / map.getTileHeight();
    }

    /*
     * Animator
     */

    @Override
    public void play(Animation anim)
    {
        animationCurrent = anim;
        sprite.play(anim);
    }

    @Override
    public void setAnimSpeed(double speed)
    {
        sprite.setAnimSpeed(speed);
    }

    @Override
    public void setFrame(int frame)
    {
        sprite.setFrame(frame);
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
    public AnimState getAnimState()
    {
        return sprite.getAnimState();
    }

    @Override
    public void stopAnimation()
    {
        animationCurrent = null;
        sprite.stopAnimation();
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated already called in main loop. Has to be called only on specific cases.
     */
    @Deprecated
    @Override
    public void updateAnimation(double extrp)
    {
        sprite.updateAnimation(extrp);
    }
}
