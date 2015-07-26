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

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.object.Services;

/**
 * World view updater, update the current world.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldViewUpdater implements KeyListener, MouseListener, MouseMoveListener, MouseWheelListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldViewUpdater";

    /** Part service. */
    protected final EPartService partService;
    /** Zoom handler. */
    private final WorldZoom zoom;
    /** World navigation. */
    private final WorldNavigation navigation;
    /** Objects interaction. */
    private final WorldInteractionObject interactionObject;
    /** Tiles interaction. */
    private final WorldInteractionTile interactionTile;
    /** Map reference. */
    private final MapTile map;
    /** Grid enabled. */
    private boolean gridEnabled;
    /** Collisions enabled. */
    private boolean collisionsEnabled;
    /** Current horizontal mouse location. */
    private int mouseX;
    /** Current vertical mouse location. */
    private int mouseY;
    /** Mouse click. */
    private int click;

    /**
     * Create a world view renderer with grid enabled.
     * 
     * @param partService The part services reference.
     * @param services The services reference.
     */
    public WorldViewUpdater(EPartService partService, Services services)
    {
        this.partService = partService;
        zoom = services.create(WorldZoom.class);
        navigation = services.create(WorldNavigation.class);
        interactionObject = services.create(WorldInteractionObject.class);
        interactionTile = services.create(WorldInteractionTile.class);
        map = services.get(MapTile.class);
        gridEnabled = true;
    }

    /**
     * Set the grid enabled state.
     */
    public void switchGridEnabled()
    {
        gridEnabled = !gridEnabled;
    }

    /**
     * Set the collisions enabled state.
     */
    public void switchCollisionsEnabled()
    {
        collisionsEnabled = !collisionsEnabled;

        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapTileCollision = map.getFeature(MapTileCollision.class);
            if (collisionsEnabled)
            {
                mapTileCollision.createCollisionDraw();
            }
            else
            {
                mapTileCollision.clearCollisionDraw();
            }
        }
    }

    /**
     * Get the mouse click.
     * 
     * @return The mouse click.
     */
    public int getClick()
    {
        return click;
    }

    /**
     * Get the horizontal mouse location.
     * 
     * @return The horizontal mouse location.
     */
    public int getMouseX()
    {
        return (int) (mouseX / zoom.getScale());
    }

    /**
     * Get the vertical mouse location.
     * 
     * @return The vertical mouse location.
     */
    public int getMouseY()
    {
        return (int) (mouseY / zoom.getScale());
    }

    /**
     * Get the real horizontal mouse location.
     * 
     * @return The real horizontal mouse location.
     */
    public int getMx()
    {
        return mouseX;
    }

    /**
     * Get the real vertical mouse location.
     * 
     * @return The real vertical mouse location.
     */
    public int getMy()
    {
        return mouseY;
    }

    /**
     * Check if grid is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isGridEnabled()
    {
        return gridEnabled;
    }

    /**
     * Check if collisions is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isCollisionsEnabled()
    {
        return collisionsEnabled;
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent mouseEvent)
    {
        mouseX = mouseEvent.x;
        mouseY = mouseEvent.y;
        click = mouseEvent.button;

        final int mx = getMouseX();
        final int my = getMouseY();
        interactionObject.onMousePressed(click, mx, my);
        interactionTile.onMousePressed(click, mx, my);
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        mouseX = mouseEvent.x;
        mouseY = mouseEvent.y;

        final int mx = getMouseX();
        final int my = getMouseY();
        interactionObject.onMouseReleased(click, mx, my);
        interactionTile.onMouseReleased(click, mx, my);

        click = 0;
    }

    @Override
    public void mouseDoubleClick(MouseEvent mouseEvent)
    {
        // Nothing to do
    }

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent mouseEvent)
    {
        final int oldMx = getMouseX();
        final int oldMy = getMouseY();

        mouseX = mouseEvent.x;
        mouseY = mouseEvent.y;

        final int mx = getMouseX();
        final int my = getMouseY();
        navigation.onMouseMoved(click, oldMx, oldMy, mx, my);
        interactionObject.onMouseMoved(click, oldMx, oldMy, mx, my);
        interactionTile.onMouseMoved(click, oldMx, oldMy, mx, my);
    }

    /*
     * MouseWheelListener
     */

    @Override
    public void mouseScrolled(MouseEvent mouseEvent)
    {
        zoom.onMouseScroll(mouseEvent.count, getMouseX(), getMouseY());
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        navigation.onKeyPushed(Integer.valueOf(keyEvent.keyCode));
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
