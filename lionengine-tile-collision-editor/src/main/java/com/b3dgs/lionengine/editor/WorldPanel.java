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
package com.b3dgs.lionengine.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.CollisionTile;
import com.b3dgs.lionengine.game.platform.map.MapTilePlatform;
import com.b3dgs.lionengine.game.platform.map.TilePlatform;

/**
 * Represents the world scene, containing the map and the entities.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class WorldPanel<C extends Enum<C> & CollisionTile, T extends TilePlatform<C>>
        extends JPanel
        implements MouseListener, MouseMotionListener
{
    /** Uid. */
    private static final long serialVersionUID = -3609110757656654125L;
    /** Color of the grid. */
    private static final ColorRgba COLOR_GRID = new ColorRgba(128, 128, 128, 128);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(240, 240, 240, 96);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_SELECTED = new ColorRgba(192, 192, 192, 96);
    /** Color of the selection area. */
    private static final ColorRgba COLOR_GROUP_SELECTED = new ColorRgba(240, 240, 240, 96);
    /** Default width. */
    private static final int DEFAULT_WIDTH = 640;
    /** Default height. */
    private static final int DEFAULT_HEIGHT = 480;

    /**
     * Draw the grid.
     * 
     * @param g The graphic output.
     * @param tw Horizontal grid spacing (width).
     * @param th Vertical grid spacing (height).
     * @param areaX Horizontal global grid size.
     * @param areaY Vertical global grid size.
     * @param color Grid color.
     */
    private static void drawGrid(Graphic g, int tw, int th, int areaX, int areaY, ColorRgba color)
    {
        g.setColor(color);
        for (int v = 0; v <= areaY; v += tw)
        {
            g.drawLine(0, v, areaX, v);
        }
        for (int h = 0; h <= areaX; h += th)
        {
            g.drawLine(h, 0, h, areaY);
        }
    }

    /** The map reference. */
    public final MapTilePlatform<C, T> map;
    /** The camera reference. */
    public final CameraPlatform camera;
    /** The editor reference. */
    private final TileCollisionEditor<C, T> editor;
    /** Selected tile. */
    private T selectedTile;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Selecting flag. */
    private boolean selecting;
    /** Clicking flag. */
    private boolean clicking;
    /** Moving entity flag. */
    private boolean moving;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     * @param map The map reference.
     */
    public WorldPanel(TileCollisionEditor<C, T> editor, MapTilePlatform<C, T> map)
    {
        super();
        this.editor = editor;
        this.map = map;
        camera = new CameraPlatform(WorldPanel.DEFAULT_WIDTH, WorldPanel.DEFAULT_HEIGHT);
        setPreferredSize(new Dimension(WorldPanel.DEFAULT_WIDTH, WorldPanel.DEFAULT_HEIGHT));
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Get the current selected tile.
     * 
     * @return The current selected, <code>null</code> if none.
     */
    public T getSelectedTile()
    {
        return selectedTile;
    }

    /**
     * Check if mouse is clicking.
     * 
     * @return <code>true</code> if clicking, <code>false</code> else.
     */
    public boolean isClicking()
    {
        return clicking;
    }

    /**
     * Draw the cursor.
     * 
     * @param g The graphic output.
     * @param tw The tile width.
     * @param th The tile height.
     * @param areaX Maximum width.
     * @param areaY Maximum height.
     */
    private void drawCursor(Graphic g, int tw, int th, int areaX, int areaY)
    {
        if (!selecting && !moving)
        {
            if (mouseX >= 0 && mouseY >= 0 && mouseX < areaX && mouseY < areaY)
            {
                final int mx = UtilityMath.getRounded(mouseX, tw);
                final int my = UtilityMath.getRounded(mouseY, th);

                g.setColor(WorldPanel.COLOR_MOUSE_SELECTION);
                g.drawRect(mx, my, tw, th, true);
            }
        }
    }

    /**
     * Update the mouse.
     * 
     * @param mx The mouse horizontal location.
     * @param my The mouse vertical location.
     */
    private void updateMouse(int mx, int my)
    {
        mouseX = mx;
        mouseY = my;
        repaint();
    }

    /**
     * Render the world.
     * 
     * @param g The graphic output.
     */
    private void render(Graphic g)
    {
        final int width = getWidth();
        final int height = getHeight();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int areaX = UtilityMath.getRounded(width, tw);
        final int areaY = UtilityMath.getRounded(height, th);
        camera.setView(0, 0, areaX - tw, areaY);

        // Background
        g.setColor(ColorRgba.GRAY_LIGHT);
        g.drawRect(0, 0, width, height, true);

        // Map area
        g.setColor(ColorRgba.BLUE);
        g.drawRect(0, 0, areaX, areaY, true);

        // Renders
        if (!map.getPatterns().isEmpty())
        {
            map.render(g, camera);
        }

        if (selectedTile != null)
        {
            // Render selected collision
            g.setColor(WorldPanel.COLOR_GROUP_SELECTED);
            for (int ty = 0; ty < map.getHeightInTile(); ty++)
            {
                for (int tx = 0; tx < map.getWidthInTile(); tx++)
                {
                    final T tile = map.getTile(tx, ty);
                    if (tile != null)
                    {
                        if (tile.getCollision() == selectedTile.getCollision())
                        {
                            g.drawRect(camera.getViewpointX(tile.getX()), camera.getViewpointY(tile.getY()) - th,
                                    tile.getWidth(), tile.getHeight(), true);
                        }
                    }
                }
            }

            // Render selected tile
            g.setColor(WorldPanel.COLOR_SELECTED);
            g.drawRect(camera.getViewpointX(selectedTile.getX()), camera.getViewpointY(selectedTile.getY()) - th,
                    selectedTile.getWidth(), selectedTile.getHeight(), true);
        }

        drawCursor(g, tw, th, areaX, areaY);
        WorldPanel.drawGrid(g, tw, th, areaX, areaY, WorldPanel.COLOR_GRID);
    }

    /*
     * JPanel
     */

    @Override
    public void paintComponent(Graphics gd)
    {
        final Graphic g = Core.GRAPHIC.createGraphic();
        g.setGraphic((Graphics2D) gd);
        render(g);
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseEntered(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mousePressed(MouseEvent event)
    {
        final int mx = event.getX();
        final int my = event.getY();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int h = UtilityMath.getRounded(getHeight(), th) - map.getTileHeight();
        final int tx = (editor.getOffsetViewH() + UtilityMath.getRounded(mx, tw)) / tw;
        final int ty = (editor.getOffsetViewV() - UtilityMath.getRounded(my, th) + h) / th;

        if (map.isCreated())
        {
            selectedTile = map.getTile(tx, ty);
            editor.toolBar.setSelectedTile(selectedTile);
        }
        clicking = true;
        updateMouse(mx, my);
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        final int mx = event.getX();
        final int my = event.getY();

        clicking = false;
        moving = false;
        updateMouse(mx, my);
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        final int mx = event.getX();
        final int my = event.getY();
        updateMouse(mx, my);
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        final int mx = event.getX();
        final int my = event.getY();
        updateMouse(mx, my);
    }
}
