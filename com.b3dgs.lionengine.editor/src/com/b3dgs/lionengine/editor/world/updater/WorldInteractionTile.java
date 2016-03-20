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
package com.b3dgs.lionengine.editor.world.updater;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.editor.dialog.map.sheets.palette.SheetPaletteType;
import com.b3dgs.lionengine.editor.dialog.map.sheets.palette.SheetsPaletteModel;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Handle the interaction with tiles.
 */
public class WorldInteractionTile implements WorldMouseClickListener, WorldMouseMoveListener
{
    /** Tile selection listener. */
    private final Collection<TileSelectionListener> tileSelectionListeners = new ArrayList<>();
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Map tile group reference. */
    private final MapTileGroup mapGroup;
    /** Palette model. */
    private final PaletteModel palette;
    /** Selected tile. */
    private Tile selectedTile;

    /**
     * Create the interactions handler.
     * 
     * @param services The services reference.
     */
    public WorldInteractionTile(Services services)
    {
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        mapGroup = services.get(MapTileGroup.class);
        palette = services.get(PaletteModel.class);
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
     * @param click The mouse click.
     */
    private void updatePointerTile(int mx, int my, int click)
    {
        if (map.isCreated())
        {
            final Tile tile = UtilWorld.getTile(map, camera, mx, my);
            final SheetPaletteType type = SheetsPaletteModel.INSTANCE.getSheetPaletteType();
            updatePointerTile(tile, type);
        }
        else
        {
            selectedTile = null;
        }
        for (final TileSelectionListener listener : tileSelectionListeners)
        {
            listener.notifyTileSelected(click, selectedTile);
            if (selectedTile != null)
            {
                listener.notifyTileGroupSelected(mapGroup.getGroup(selectedTile));
            }
            else
            {
                listener.notifyTileGroupSelected(MapTileGroupModel.NO_GROUP_NAME);
            }
        }
    }

    /**
     * Update the pointer with current pointed tile depending of the palette.
     * 
     * @param tile The pointed tile.
     * @param type The sheet palette type.
     */
    private void updatePointerTile(Tile tile, SheetPaletteType type)
    {
        switch (type)
        {
            case SELECTION:
                selectedTile = tile;
                break;
            case EDITION:
                selectedTile = null;
                updateTileEdition(tile);
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    /**
     * Update the tile edition from palette.
     * 
     * @param tile The pointed tile.
     */
    private void updateTileEdition(Tile tile)
    {
        final TileRef palette = SheetsPaletteModel.INSTANCE.getSelectedTile();
        if (tile != null && palette != null)
        {
            final Tile newTile = map.createTile(palette.getSheet(), palette.getNumber(), tile.getX(), tile.getY());
            map.setTile(newTile);

            final MapTileTransition mapTileTransition = map.getFeature(MapTileTransition.class);
            mapTileTransition.resolve(newTile);
        }
    }

    /**
     * Check if property can be past from middle click.
     */
    private void checkPastProperty()
    {
        final Object copy = PropertiesModel.INSTANCE.getCopyData();
        final String group = PropertiesModel.INSTANCE.getCopyText();
        if (selectedTile != null && TileGroupsConfig.NODE_GROUP.equals(copy))
        {
            UtilWorld.changeTileGroup(mapGroup, mapGroup.getGroup(selectedTile), group, selectedTile);
        }
    }

    /*
     * WorldMouseClickListener
     */

    @Override
    public void onMousePressed(int click, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_TILE))
        {
            updatePointerTile(mx, my, click);
        }
        if (click == Mouse.MIDDLE)
        {
            checkPastProperty();
        }
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        // Nothing to do
    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_TILE) && click > 0)
        {
            updatePointerTile(mx, my, click);
        }
    }
}
