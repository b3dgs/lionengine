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
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.game.platform.CameraPlatform;

/**
 * Represents the world scene, containing the map and the entities.
 * 
 * @param <C> The collision type used.
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class WorldPanel<C extends Enum<C>, T extends TileGame<C>>
        extends JPanel
        implements MouseListener, MouseMotionListener
{
    /** Uid. */
    private static final long serialVersionUID = -3609110757656654125L;
    /** Color of the selection area. */
    private static final ColorRgba COLOR_MOUSE_SELECTION = new ColorRgba(128, 128, 192, 192);
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
    public final MapTileGame<C, T> map;
    /** The camera reference. */
    public final CameraPlatform camera;
    /** The editor reference. */
    private final TileCollisionEditor<C, T> editor;
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
    /** Moving offset x. */
    private int movingOffsetX;
    /** Moving offset y. */
    private int movingOffsetY;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     * @param map The map reference.
     */
    public WorldPanel(final TileCollisionEditor<C, T> editor, MapTileGame<C, T> map)
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

        drawCursor(g, tw, th, areaX, areaY);
        WorldPanel.drawGrid(g, tw, th, areaX, areaY, ColorRgba.GRAY);
    }

    /*
     * JPanel
     */

    @Override
    public void paintComponent(Graphics gd)
    {
        final Graphic g = UtilityImage.createGraphic();
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
        final int x = editor.getOffsetViewH() + UtilityMath.getRounded(mx, tw);
        final int y = editor.getOffsetViewV() - UtilityMath.getRounded(my, th) + h;

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
        final int th = map.getTileHeight();
        final int mx = event.getX();
        final int my = event.getY();
        final int areaY = UtilityMath.getRounded(getHeight(), th);
        if (!moving)
        {
            movingOffsetX = UtilityMath.getRounded(mouseX, map.getTileWidth()) - mx;
            movingOffsetY = my - UtilityMath.getRounded(mouseY, th) - th;
            moving = true;
        }
        final int ox = mouseX + editor.getOffsetViewH() + movingOffsetX;
        final int oy = areaY - mouseY + editor.getOffsetViewV() - 1 + movingOffsetY;
        final int x = mx + editor.getOffsetViewH() + movingOffsetX;
        final int y = areaY - my + editor.getOffsetViewV() - 1 + movingOffsetY;

        updateMouse(mx, my);
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        final int th = map.getTileHeight();
        final int mx = event.getX();
        final int my = event.getY();
        final int areaY = UtilityMath.getRounded(getHeight(), th);
        final int x = UtilityMath.getRounded(mx, map.getTileWidth());
        final int y = UtilityMath.getRounded(areaY - my - 1, th);

        updateMouse(mx, my);
    }
}
