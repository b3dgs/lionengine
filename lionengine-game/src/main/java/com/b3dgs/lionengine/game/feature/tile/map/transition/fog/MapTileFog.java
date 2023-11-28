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
package com.b3dgs.lionengine.game.feature.tile.map.transition.fog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroup;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Designed to handle a fog of war (discovering tile and hiding tile).
 */
public class MapTileFog implements Listenable<RevealedListener>
{
    /** No fog tile. */
    public static final int FOG = 16;
    /** Fog tile. */
    public static final int NO_FOG = 17;
    /** Fog group. */
    private static final String FOG_GROUP = "fog";
    /** Transition group. */
    private static final String TRANSITION_GROUP = "transition";

    /**
     * Check if is in angle.
     * 
     * @param x The current x.
     * @param y The current y.
     * @param sx The starting x.
     * @param sy The starting y.
     * @param ex The ending y.
     * @param ey The ending y.
     * @return <code>true</code> if angle, <code>false</code> else.
     */
    private static boolean isAngle(int x, int y, int sx, int sy, int ex, int ey)
    {
        // CHECKSTYLE IGNORE LINE: BooleanExpressionComplexity
        return x == sx && y == sy || x == ex - 1 && y == ey - 1 || x == sx && y == ey - 1 || x == ey - 1 && y == sy;
    }

    /** Listener. */
    private final ListenableModel<RevealedListener> listenable = new ListenableModel<>();
    /** Hidden map. */
    private final MapTileGame map;
    /** Map group. */
    private final MapTileGroup mapGroup;
    /** Transitions. */
    private final MapTileTransition transition;
    private final List<Set<Fovable>> fog = new ArrayList<>();
    /** Angle flag. */
    private boolean allowAngle = true;

    /**
     * Create a fog of war.
     * 
     * @param fog <code>true</code> if fog, <code>false</code> if hide.
     */
    public MapTileFog(boolean fog)
    {
        super();

        map = new MapTileGame();
        mapGroup = map.addFeatureAndGet(new MapTileGroupModel());
        transition = map.addFeatureAndGet(new MapTileTransitionModel());
        map.addListener(tile ->
        {
            if (fog)
            {
                final int n = listenable.size();
                for (int i = 0; i < n; i++)
                {
                    listenable.get(i).notifyFogged(tile.getInTileX(), tile.getInTileY(), tile.getNumber() == FOG);
                }
            }
            else if (NO_FOG == tile.getNumber())
            {
                final int n = listenable.size();
                for (int i = 0; i < n; i++)
                {
                    listenable.get(i).notifyVisited(tile.getInTileX(), tile.getInTileY());
                }
            }
        });
    }

    /**
     * Create a fog of war from a map.
     * 
     * @param map The map reference.
     * @param config The fog configuration.
     * @param sheet The sheet used (can be <code>null</code>).
     */
    public void create(MapTile map, Media config, SpriteTiled sheet)
    {
        this.map.create(map.getTileWidth(), map.getTileHeight(), map.getInTileWidth(), map.getInTileHeight());
        if (sheet != null)
        {
            this.map.loadSheets(Arrays.asList(sheet));
        }
        for (int i = 0; i < NO_FOG; i++)
        {
            final String group;
            if (i == FOG)
            {
                group = FOG_GROUP;
            }
            else
            {
                group = TRANSITION_GROUP;
            }
            mapGroup.changeGroup(new TileGame(i, 0, 0, map.getTileWidth(), map.getTileHeight()), group);
        }
        mapGroup.changeGroup(new TileGame(NO_FOG, 0, 0, map.getTileWidth(), map.getTileHeight()),
                             MapTileGroupModel.NO_GROUP_NAME);
        transition.loadTransitions(config);

        for (int tx = 0; tx < map.getInTileWidth(); tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                this.map.setTile(tx, ty, FOG);
                fog.add(new HashSet<>());
            }
        }
    }

    /**
     * Update fovable field of view (fog of war).
     * 
     * @param fovable The fovable reference.
     * @param nx The new horizontal location.
     * @param ny The new vertical location.
     */
    public void updateFov(Fovable fovable, int nx, int ny)
    {
        final int ray = fovable.getInTileFov();
        final int sx = UtilMath.clamp(nx - ray, 0, map.getInTileWidth());
        int ex = UtilMath.clamp(nx + ray + fovable.getInTileWidth(), 0, map.getInTileWidth());
        if (ex == sx)
        {
            ex++;
        }
        final int sy = UtilMath.clamp(ny - ray, 0, map.getInTileHeight());
        int ey = UtilMath.clamp(ny + ray + fovable.getInTileHeight(), 0, map.getInTileHeight());
        if (ey == sy)
        {
            ey++;
        }

        for (int x = sx; x < ex; x++)
        {
            for (int y = sy; y < ey; y++)
            {
                if (allowAngle || !isAngle(x, y, sx, sy, ex, ey))
                {
                    if (map.getTile(x, y).getNumber() != NO_FOG)
                    {
                        map.setTile(x, y, NO_FOG);
                        transition.resolve(map.getTile(x, y));
                    }

                    final Set<Fovable> set = fog.get(y * map.getInTileWidth() + x);
                    set.add(fovable);
                }
            }
        }
    }

    /**
     * Reset the revealed tiles to fogged.
     * 
     * @param fovable The fovable reference.
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     */
    public void reset(Fovable fovable, int ox, int oy)
    {
        final int ray = fovable.getInTileFov();
        final int sx = UtilMath.clamp(ox - ray, 0, map.getInTileWidth());
        int ex = UtilMath.clamp(ox + ray + fovable.getInTileWidth(), 0, map.getInTileWidth());
        if (ex == sx)
        {
            ex++;
        }
        final int sy = UtilMath.clamp(oy - ray, 0, map.getInTileHeight());
        int ey = UtilMath.clamp(oy + ray + fovable.getInTileHeight(), 0, map.getInTileHeight());
        if (ey == sy)
        {
            ey++;
        }

        for (int x = sx; x < ex; x++)
        {
            for (int y = sy; y < ey; y++)
            {
                if (allowAngle || !isAngle(x, y, sx, sy, ex, ey))
                {
                    final Set<Fovable> set = fog.get(y * map.getInTileWidth() + x);
                    set.remove(fovable);

                    if (set.isEmpty())
                    {
                        map.setTile(x, y, FOG);
                        transition.resolve(map.getTile(x, y));
                    }
                }
            }
        }
    }

    /**
     * Set allow angle flag.
     * 
     * @param allow <code>true</code> to allow angle, <code>false</code> else.
     */
    public void setAllowAngle(boolean allow)
    {
        allowAngle = allow;
    }

    /**
     * Get the tile.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     * @return The tile reference (<code>null</code> if none).
     */
    Tile getTile(int tx, int ty)
    {
        return map.getTile(tx, ty);
    }

    /*
     * Listenable
     */

    @Override
    public void addListener(RevealedListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(RevealedListener listener)
    {
        listenable.removeListener(listener);
    }
}
