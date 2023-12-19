/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.world.updater;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.map.sheet.palette.SheetPaletteType;
import com.b3dgs.lionengine.editor.map.sheet.palette.SheetsPaletteModel;
import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseClickListener;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseMoveListener;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.MapTileCircuit;
import com.b3dgs.lionengine.swt.graphic.MouseSwt;

/**
 * Handle the interaction with tiles.
 */
public class WorldInteractionTile implements WorldMouseClickListener, WorldMouseMoveListener
{
    /** Tile selection listener. */
    private final Collection<TileSelectionListener> tileSelectionListeners = new ArrayList<>();
    /** Tile selection listener. */
    private final TileSelectionListener listener = new TileSelectionListenerImpl();
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Map tile group reference. */
    private final MapTileGroup mapGroup;
    /** Map tile transition reference. */
    private final MapTileTransition mapTransition;
    /** Map tile circuit reference. */
    private final MapTileCircuit mapCircuit;
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
        palette = services.get(PaletteModel.class);
        map = services.get(MapTile.class);
        mapGroup = map.getFeature(MapTileGroup.class);
        mapTransition = map.getFeature(MapTileTransition.class);
        mapCircuit = map.getFeature(MapTileCircuit.class);
        tileSelectionListeners.add(listener);
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
     * Get the tile listener.
     * 
     * @return The tile listener.
     */
    public TileSelectionListener getListener()
    {
        return listener;
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
            updatePointerTile(tile);
        }
        else
        {
            selectedTile = null;
        }
        for (final TileSelectionListener current : tileSelectionListeners)
        {
            if (selectedTile != null)
            {
                current.notifyTileSelected(click, selectedTile);
                current.notifyTileGroupSelected(mapGroup.getGroup(selectedTile));
            }
            else
            {
                current.notifyTileGroupSelected(MapTileGroupModel.NO_GROUP_NAME);
            }
        }
    }

    /**
     * Update the pointer with current pointed tile depending of the palette.
     * 
     * @param tile The pointed tile.
     */
    private void updatePointerTile(Tile tile)
    {
        final SheetPaletteType type = SheetsPaletteModel.INSTANCE.getSheetPaletteType();
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
        final int tilePalette = SheetsPaletteModel.INSTANCE.getSelectedTile();
        if (tile != null)
        {
            map.setTile(tile.getInTileX(), tile.getInTileY(), tilePalette);

            final Tile newTile = map.getTile(tile.getInTileX(), tile.getInTileY());
            final TileGroupType groupType = mapGroup.getType(newTile);
            if (TileGroupType.PLAIN == groupType)
            {
                final Collection<Tile> resolved = mapTransition.resolve(newTile);
                fixCircuits(resolved);
            }
            if (TileGroupType.CIRCUIT == groupType)
            {
                mapCircuit.resolve(newTile);
            }
        }
    }

    /**
     * Fix circuits due to transition resolution close to existing circuits.
     * 
     * @param resolved The list of updated tiles.
     */
    private void fixCircuits(Collection<Tile> resolved)
    {
        for (final Tile resolvedTile : resolved)
        {
            for (final Tile neighbor : map.getNeighbors(resolvedTile))
            {
                if (TileGroupType.CIRCUIT == mapGroup.getType(neighbor))
                {
                    mapCircuit.resolve(neighbor);
                }
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
        if (click == MouseSwt.MIDDLE.intValue())
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
        if (click > 0 && palette.isPalette(PaletteType.POINTER_TILE))
        {
            updatePointerTile(mx, my, click);
        }
    }

    /**
     * Listen to tile selection to show tile properties.
     */
    private static final class TileSelectionListenerImpl implements TileSelectionListener
    {
        /**
         * Create listener.
         */
        TileSelectionListenerImpl()
        {
            super();
        }

        @Override
        public void notifyTileSelected(int click, Tile tile)
        {
            final PropertiesPart part = UtilPart.getPart(PropertiesPart.ID, PropertiesPart.class);
            if (tile != null)
            {
                part.setInput(part.getTree(), tile);
            }
            else
            {
                part.clear();
            }
        }

        @Override
        public void notifyTileGroupSelected(String group)
        {
            // Nothing to do
        }
    }
}
