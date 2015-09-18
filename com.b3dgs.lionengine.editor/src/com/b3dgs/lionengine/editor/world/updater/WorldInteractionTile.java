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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Media;
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
import com.b3dgs.lionengine.editor.world.dialog.MapCollisionAssignDialog;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.collision.CollisionRange;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionFormula;
import com.b3dgs.lionengine.game.configurer.ConfigCollisionGroup;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

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
    /** Function side (<code>-1</code> for left or <code>1</code> for right). */
    private int side;
    /** Selected function. */
    private CollisionFunction function;
    /** Selected function name. */
    private String functionName;
    /** Last markers computed. */
    private Map<Integer, Marker> markers;

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
     * Apply and verify computed collision.
     * 
     * @param offset The marker offset.
     */
    public void verifyCollision(int offset)
    {
        final Media config = map.getGroupsConfig();
        final XmlNode groupNode = Stream.loadXml(config);
        final List<Integer> keys = new ArrayList<>(markers.keySet());
        Collections.sort(keys);
        final int max = keys.size();

        for (final Integer key : keys)
        {
            final int offsetKey = (key.intValue() + offset) % max;
            final Marker marker = markers.get(key);
            for (final Tile tile : marker.getTiles())
            {
                applyCollision(groupNode, offsetKey, tile);
            }
        }
        if (!markers.isEmpty())
        {
            Stream.saveXml(groupNode, config);
            map.loadGroups(config);
            final MapTileCollision collision = map.getFeature(MapTileCollision.class);
            collision.loadCollisions(collision.getFormulasConfig(), collision.getCollisionsConfig());
            collision.createCollisionDraw();
        }
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
     * Update all markers found on selected tiles.
     * 
     * @return The markers found.
     */
    public Map<Integer, Marker> getMarkers()
    {
        final Force force = Force.fromVector(collStart.getX(), collStart.getY(), collEnd.getX(), collEnd.getY());
        final double sx = force.getDirectionHorizontal() * map.getTileWidth();
        final double sy = force.getDirectionVertical() * map.getTileHeight();

        double h = collStart.getX();
        double v = collStart.getY();

        final Map<Integer, Marker> markers = new TreeMap<>();
        for (final Tile tile : map.getTilesHit(collStart.getX(), collStart.getY(), collEnd.getX(), collEnd.getY()))
        {
            final int x = (int) UtilMath.getRound(sx, h);
            final int y = (int) UtilMath.getRound(sy, v);
            checkMarker(markers, tile, x, y);

            if (tile == map.getTileAt(x, y))
            {
                h += sx;
                v += sy;
            }
        }
        return markers;
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
     * @param apply <code>true</code> to apply collision, <code>false</code> else.
     */
    private void updatePointerCollision(int mx, int my, boolean apply)
    {
        final WorldPart part = UtilPart.getPart(WorldPart.ID, WorldPart.class);
        final FormulaItem item = part.getToolItem(FormulaItem.ID, FormulaItem.class);
        function = item.getFunction();
        if (function != null && collStart != null)
        {
            updatePointerCollision(mx, my);
            if (apply)
            {
                markers = getMarkers();
                functionName = item.getName().toLowerCase(Locale.ENGLISH);
                collStart = null;
                collEnd = null;
                collLine = null;

                final MapCollisionAssignDialog dialog = new MapCollisionAssignDialog(item.getParent().getShell(), this);
                dialog.open();

                markers.clear();
            }
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
        final int x = mx - startX;
        final int y = my - startY;
        final int sideX = UtilMath.getSign(x);
        final int sideY = UtilMath.getSign(y);

        if (function == FormulaItem.LINE)
        {
            if (Math.abs(x) > Math.abs(y))
            {
                side = sideX;
                collEnd = UtilWorld.getPoint(map, camera, startX + x, startY + (int) function.compute(x * side));
            }
            else
            {
                side = sideY;
                collEnd = UtilWorld.getPoint(map, camera, startX + (int) function.compute(y * side), startY + y);
            }
        }
        else
        {
            if (sideY > 0)
            {
                side = sideX;
            }
            else
            {
                side = -sideX;
            }
            final int rx = UtilMath.getRounded(x, (int) (1 / function.compute(1)));
            collEnd = UtilWorld.getPoint(map, camera, startX + rx, startY + (int) function.compute(rx * side));
        }
    }

    /**
     * Check the current marker and add it to the markers found.
     * 
     * @param markers The markers found.
     * @param tile The current tile.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    private void checkMarker(Map<Integer, Marker> markers, Tile tile, int x, int y)
    {
        final Marker marker = new Marker(x - tile.getX(), y - tile.getY());
        final Integer key = containsMarker(markers, marker);

        if (key != null)
        {
            markers.get(key).addTile(tile);
        }
        else
        {
            marker.addTile(tile);
            markers.put(Integer.valueOf(markers.keySet().size()), marker);
        }
    }

    /**
     * Check the current marker already exists or not.
     * 
     * @param markers The markers found.
     * @param marker The new marker found.
     * @return Key if exists, <code>null</code> else.
     */
    private Integer containsMarker(Map<Integer, Marker> markers, Marker marker)
    {
        for (final Map.Entry<Integer, Marker> entry : markers.entrySet())
        {
            if (entry.getValue().equals(marker))
            {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Apply the collision from its short name and function, depending on the index for the specified tile.
     * 
     * @param groupNode The group node reference.
     * @param index The current collision index (in case of multiple sub collision).
     * @param tile The current tile.
     */
    private void applyCollision(XmlNode groupNode, int index, Tile tile)
    {
        final MapTileCollision collision = map.getFeature(MapTileCollision.class);
        final String fullName;
        if (FormulaItem.LINE.equals(function))
        {
            fullName = functionName + Constant.UNDERSCORE + index;
        }
        else if (side == -1)
        {
            fullName = functionName + Constant.UNDERSCORE + "left" + Constant.UNDERSCORE + index;
        }
        else
        {
            fullName = functionName + Constant.UNDERSCORE + "right" + Constant.UNDERSCORE + index;
        }

        final CollisionFormula formula = saveCollisionFormula(collision, fullName, function, index);
        final CollisionGroup group = saveCollisionGroup(collision, fullName, formula);

        if (!group.getName().equals(tile.getGroup()))
        {
            PropertiesTile.changeTileGroup(groupNode, tile.getGroup(), group.getName(), tile);
        }
    }

    /**
     * Save the selected formula.
     * 
     * @param collision The collision feature.
     * @param name The collision name.
     * @param function The collision function used.
     * @param index The current collision index (in case of multiple sub collision).
     * @return The formula saved.
     */
    private CollisionFormula saveCollisionFormula(MapTileCollision collision,
                                                  String name,
                                                  CollisionFunction function,
                                                  int index)
    {
        final Media config = collision.getFormulasConfig();
        final XmlNode root = Stream.loadXml(config);
        if (ConfigCollisionFormula.has(root, name))
        {
            ConfigCollisionFormula.remove(root, name);
        }

        final CollisionRange range = new CollisionRange(Axis.Y, 0, map.getTileWidth() - 1, 0, map.getTileHeight() - 1);
        final CollisionFunction updatedFunction = updateFunction(function, index);
        final CollisionFormula formula = new CollisionFormula(name, range, updatedFunction, new CollisionConstraint());
        ConfigCollisionFormula.export(root, formula);
        Stream.saveXml(root, config);

        return formula;
    }

    /**
     * Update the function if needed.
     * 
     * @param function The function to update.
     * @param index The current formula index.
     * @return The updated function.
     */
    private CollisionFunction updateFunction(CollisionFunction function, int index)
    {
        if (function instanceof CollisionFunctionLinear)
        {
            final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
            final int offset;
            if (side < 0)
            {
                offset = -1;
            }
            else
            {
                offset = 0;
            }
            final double b = (index + offset) * map.getTileHeight() / 2;
            return new CollisionFunctionLinear(linear.getA() * -side, b);
        }
        return function;
    }

    /**
     * Save the collision group.
     * 
     * @param collision The collision feature.
     * @param name The collision name.
     * @param formula The generated formula.
     * @return The saved group.
     */
    private CollisionGroup saveCollisionGroup(MapTileCollision collision, String name, CollisionFormula formula)
    {
        final Media config = collision.getCollisionsConfig();
        final XmlNode root = Stream.loadXml(config);
        if (ConfigCollisionGroup.has(root, name))
        {
            ConfigCollisionGroup.remove(root, name);
        }

        final CollisionGroup group = new CollisionGroup(name, Arrays.asList(formula));
        ConfigCollisionGroup.export(root, group);
        Stream.saveXml(root, config);

        return group;
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
            updatePointerCollision(mx, my, true);
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
        if (WorldModel.INSTANCE.isPalette(PaletteType.POINTER_COLLISION) && collStart != null)
        {
            updatePointerCollision(mx, my, false);
            if (collEnd != null)
            {
                collLine = Geom.createLine(collStart.getX(), collStart.getY(), collEnd.getX(), collEnd.getY());
            }
        }
    }
}
