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

import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.editor.properties.tile.PropertiesTile;
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
    /** First x. */
    private int firstX;
    /** First y. */
    private int firstY;

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
     * Update the pointer in tile case.
     * 
     * @param mx The horizontal mouse location.
     * @param my The vertical mouse location.
     */
    private void updatePointerTile(int mx, int my)
    {
        if (map.isCreated())
        {
            selectedTile = Tools.getTile(map, camera, mx, my);
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
        final WorldPart part = UtilEclipse.getPart(WorldPart.ID, WorldPart.class);
        final FormulaItem item = part.getToolItem(FormulaItem.ID, FormulaItem.class);
        final CollisionFunction function = item.getFunction();
        if (function != null)
        {
            // TODO apply function depending of the axis
            final Point old = Tools.getPoint(map, camera, firstX, firstY);
            final Point point = Tools.getPoint(map, camera, mx, my);
            final Collection<Tile> tiles = map.getTilesHit(old.getX(), old.getY(), point.getX(), point.getY());
            // TODO assign collision to tiles
            // TODO render current line
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
        firstX = mx;
        firstY = my;
        final Enum<?> palette = WorldModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.POINTER_TILE)
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
        final Enum<?> palette = WorldModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.POINTER_COLLISION)
        {
            updatePointerCollision(mx, my);
        }
    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        final Enum<?> palette = WorldModel.INSTANCE.getSelectedPalette();
        if (palette == PaletteType.POINTER_COLLISION)
        {
            updatePointerCollision(mx, my);
        }
    }
}
