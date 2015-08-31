/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world.updater;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.editor.properties.tile.PropertiesTile;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.FormulaItem;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.TileSelectionListener;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Point;

/**
 * Handle the interaction with tiles.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldInteractionTile implements WorldMouseClickListener, WorldMouseMoveListener
{
    /** Tile selection listener. */
    private final Collection<TileSelectionListener> tileSelectionListeners = new ArrayList<>();

    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Selected tile. */
    private Tile selectedTile;
    /** Line collision assign. */
    private Line collLine;
    /** Start x collision assign. */
    private int startX;
    /** Start y collision assign. */
    private int startY;
    /** Starting point collision assign. */
    private Point collStart;
    /** Ending point collision assign. */
    private Point collEnd;

    /**
     * Create the interactions handler.
     * 
     * @param services The services reference.
     */
    public WorldInteractionTile(Services services)
    {
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
    }

    /**
     * Add an tile selection listener.
     * 
     * @param listener The listener reference.
     */
    public void addListener(TileSelectionListener listener)
    {
        tileSelectionListeners.add(listener);
    }

    /**
     * Remove a tile selection listener.
     * 
     * @param listener The listener reference.
     */
    public void removeListener(TileSelectionListener listener)
    {
        tileSelectionListeners.remove(listener);
    }

    /**
     * Get the current selected tile.
     * 
     * @return The current selected tile.
     */
    public Tile getSelection()
    {
        return selectedTile;
    }

    /**
     * Get the current line collision assigning.
     * 
     * @return The line collision assigning.
     */
    public Line getCollisionLine()
    {
        return collLine;
    }

    /**
     * Update the pointer in tile case.
     * 
     * @param mx The horizontal mouse location.
     * @param my The vertical mouse location.
     */
    private void updatePointerTile(int mx, int my)
    {
        if (map.isCreated())
        {
            selectedTile = UtilWorld.getTile(map, camera, mx, my);
        }
        else
        {
            selectedTile = null;
        }
        for (final TileSelectionListener listener : tileSelectionListeners)
        {
            listener.notifyTileSelected(selectedTile);
        }
    }

    /**
     * Update the pointer in collision case.
     * 
     * @param mx The horizontal mouse location.
     * @param my The vertical mouse location.
     */
    private void updatePointerCollision(int mx, int my)
    {
        final WorldPart part = UtilPart.getPart(WorldPart.ID, WorldPart.class);
        final FormulaItem item = part.getToolItem(FormulaItem.ID, FormulaItem.class);
        final CollisionFunction function = item.getFunction();
        if (function != null)
        {
            updatePointerCollision(mx, my, function);
        }
    }

    /**
     * Update the pointer in collision case.
     * 
     * @param mx The horizontal mouse location.
     * @param my The vertical mouse location.
     * @param function The function base used.
     */
    private void updatePointerCollision(int mx, int my, CollisionFunction function)
    {
        final int x = mx - startX;
        final int y = my - startY;
        final int sideX = UtilMath.getSign(x);
        final int sideY = UtilMath.getSign(y);
        if (function == FormulaItem.LINE)
        {
            if (Math.abs(x) > Math.abs(y))
            {
                collEnd = UtilWorld.getPoint(map, camera, mx, startY + (int) function.compute(x * sideX));
            }
            else
            {
                collEnd = UtilWorld.getPoint(map, camera, startX + (int) function.compute(y * sideY), my);
            }
        }
        else
        {
            if (sideY > 0)
            {
                collEnd = UtilWorld.getPoint(map, camera, mx, startY + (int) function.compute(x * sideX));
            }
            else
            {
                collEnd = UtilWorld.getPoint(map, camera, mx, startY + (int) function.compute(x * -sideX));
            }
        }
    }

    /**
     * Check if property can be past from middle click.
     */
    private void checkPastProperty()
    {
        final Object copy = PropertiesModel.INSTANCE.getCopyData();
        final String group = PropertiesModel.INSTANCE.getCopyText();
        if (copy != null && selectedTile != null && ConfigTileGroup.GROUP.equals(copy))
        {
            PropertiesTile.changeTileGroup(map, selectedTile.getGroup(), group, selectedTile);
        }
    }

    /*
     * WorldMouseClickListener
     */

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        startX = mx;
        startY = my;
        collStart = UtilWorld.getPoint(map, camera, mx, my);
        if (WorldModel.INSTANCE.isPalette(PaletteType.POINTER_TILE))
        {
            updatePointerTile(mx, my);
        }
        if (click == Mouse.MIDDLE)
        {
            checkPastProperty();
        }
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        if (WorldModel.INSTANCE.isPalette(PaletteType.POINTER_COLLISION))
        {
            updatePointerCollision(mx, my);
            collStart = null;
            collEnd = null;
            collLine = null;
        }
    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        if (WorldModel.INSTANCE.isPalette(PaletteType.POINTER_COLLISION))
        {
            if (collStart != null)
            {
                updatePointerCollision(mx, my);
                collLine = Geom.createLine(collStart.getX(), collStart.getY(), collEnd.getX(), collEnd.getY());
            }
        }
    }
}
