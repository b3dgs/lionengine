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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.collision.CollisionVerifier;
import com.b3dgs.lionengine.editor.collision.map.assign.MapCollisionAssignDialog;
import com.b3dgs.lionengine.editor.toolbar.FormulaItem;
import com.b3dgs.lionengine.editor.utility.UtilWorld;
import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.view.WorldView;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.collision.tile.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormula;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunction;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroup;
import com.b3dgs.lionengine.game.collision.tile.CollisionGroupConfig;
import com.b3dgs.lionengine.game.collision.tile.CollisionRange;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Line;
import com.b3dgs.lionengine.geom.Point;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Handle the interaction with tiles collision.
 */
public class WorldInteractionTileCollision implements CollisionVerifier, WorldMouseClickListener, WorldMouseMoveListener
{
    /**
     * Check the current marker already exists or not.
     * 
     * @param markers The markers found.
     * @param marker The new marker found.
     * @return Key if exists, <code>null</code> else.
     */
    private static Integer containsMarker(Map<Integer, Marker> markers, Marker marker)
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
     * Check the current marker and add it to the markers found.
     * 
     * @param markers The markers found.
     * @param tile The current tile.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    private static void checkMarker(Map<Integer, Marker> markers, Tile tile, int x, int y)
    {
        final Marker marker = new Marker(x - (int) tile.getX(), y - (int) tile.getY());
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
     * Save the collision group.
     * 
     * @param collision The collision feature.
     * @param name The collision name.
     * @param formula The generated formula.
     * @return The saved group.
     */
    private static CollisionGroup saveCollisionGroup(MapTileCollision collision, String name, CollisionFormula formula)
    {
        final Media config = collision.getCollisionsConfig();
        final XmlNode root = Xml.load(config);
        if (CollisionGroupConfig.has(root, name))
        {
            CollisionGroupConfig.remove(root, name);
        }

        final CollisionGroup group = new CollisionGroup(name, Arrays.asList(formula));
        CollisionGroupConfig.exports(root, group);
        Xml.save(root, config);

        return group;
    }

    /** World part. */
    private final WorldView view;
    /** Camera reference. */
    private final Camera camera;
    /** Map reference. */
    private final MapTile map;
    /** Map tile group reference. */
    private final MapTileGroup mapGroup;
    /** Palette model. */
    private final PaletteModel palette;
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
    public WorldInteractionTileCollision(Services services)
    {
        view = services.get(WorldView.class);
        camera = services.get(Camera.class);
        map = services.get(MapTile.class);
        mapGroup = services.get(MapTileGroup.class);
        palette = services.get(PaletteModel.class);
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

        final Map<Integer, Marker> markersFound = new TreeMap<>();
        for (final Tile tile : map.getTilesHit(collStart.getX(), collStart.getY(), collEnd.getX(), collEnd.getY()))
        {
            final int x = (int) UtilMath.getRound(sx, h);
            final int y = (int) UtilMath.getRound(sy, v);
            checkMarker(markersFound, tile, x, y);

            if (tile.equals(map.getTileAt(x, y)))
            {
                h += sx;
                v += sy;
            }
        }
        return markersFound;
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
        final FormulaItem item = view.getToolItem(FormulaItem.ID, FormulaItem.class);
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

        if (FormulaItem.LINE.equals(function))
        {
            if (Math.abs(x) > Math.abs(y))
            {
                side = sideX;
                collEnd = UtilWorld.getPoint(camera, startX + x, startY + (int) function.compute(x * (double) side));
            }
            else
            {
                side = sideY;
                collEnd = UtilWorld.getPoint(camera, startX + (int) function.compute(y * (double) side), startY + y);
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
            collEnd = UtilWorld.getPoint(camera, startX + rx, startY + (int) function.compute(rx * (double) side));
        }
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

        final String tileGroup = mapGroup.getGroup(tile);
        if (!group.getName().equals(tileGroup))
        {
            UtilWorld.changeTileGroup(groupNode, tileGroup, group.getName(), tile);
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
        final XmlNode root = Xml.load(config);
        if (CollisionFormulaConfig.has(root, name))
        {
            CollisionFormulaConfig.remove(root, name);
        }

        final CollisionRange range = new CollisionRange(Axis.Y, 0, map.getTileWidth() - 1, 0, map.getTileHeight() - 1);
        final CollisionFunction updatedFunction = updateFunction(function, index);
        final CollisionFormula formula = new CollisionFormula(name, range, updatedFunction, new CollisionConstraint());
        CollisionFormulaConfig.exports(root, formula);
        Xml.save(root, config);

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
            final double b = (index + offset) * map.getTileHeight() / 2.0;
            return new CollisionFunctionLinear(linear.getA() * -side, b);
        }
        return function;
    }

    /*
     * CollisionVerifier
     */

    @Override
    public void verifyCollision(int offset)
    {
        final Media config = mapGroup.getGroupsConfig();
        final XmlNode groupNode = Xml.load(config);
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
            Xml.save(groupNode, config);
            mapGroup.loadGroups(config);
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(mapCollision.getFormulasConfig(), mapCollision.getCollisionsConfig());
            mapCollision.createCollisionDraw();
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
        collStart = UtilWorld.getPoint(camera, mx, my);
    }

    @Override
    public void onMouseReleased(int click, int mx, int my)
    {
        if (palette.isPalette(PaletteType.POINTER_COLLISION) && map.isCreated())
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
        if (palette.isPalette(PaletteType.POINTER_COLLISION) && map.isCreated() && collStart != null)
        {
            updatePointerCollision(mx, my, false);
            if (collEnd != null)
            {
                collLine = Geom.createLine(collStart.getX(), collStart.getY(), collEnd.getX(), collEnd.getY());
            }
        }
    }
}
