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

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.utility.UtilExtension;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Services;

/**
 * World updater, update the current world.
 */
public class WorldUpdater implements KeyListener, MouseListener, MouseMoveListener, MouseWheelListener
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".worldUpdater";

    /** Part service. */
    protected final EPartService partService;
    /** World mouse click listeners. */
    private final Collection<WorldMouseClickListener> clickListeners = new ArrayList<>();
    /** World mouse move listeners. */
    private final Collection<WorldMouseMoveListener> moveListeners = new ArrayList<>();
    /** World mouse scroll listeners. */
    private final Collection<WorldMouseScrollListener> scrollListeners = new ArrayList<>();
    /** World keyboard listeners. */
    private final Collection<WorldKeyboardListener> keyListeners = new ArrayList<>();
    /** Zoom handler. */
    private final WorldZoom zoom;
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
     * Create a world updater with grid enabled.
     * 
     * @param partService The part services reference.
     * @param services The services reference.
     */
    public WorldUpdater(EPartService partService, Services services)
    {
        this.partService = partService;
        zoom = services.create(WorldZoom.class);
        final WorldNavigation navigation = services.create(WorldNavigation.class);
        final WorldInteractionObject interactionObject = services.create(WorldInteractionObject.class);
        final WorldInteractionTile interactionTile = services.create(WorldInteractionTile.class);
        map = services.get(MapTile.class);
        gridEnabled = true;

        addMouseClickListener(interactionObject);
        addMouseMoveListener(interactionObject);

        addMouseClickListener(interactionTile);
        addMouseMoveListener(interactionTile);

        addMouseClickListener(zoom);
        addMouseScrollListener(zoom);

        addMouseMoveListener(navigation);
        addKeyboardListener(navigation);

        for (final WorldMouseClickListener listener : UtilExtension.get(WorldMouseClickListener.class,
                                                                        WorldMouseClickListener.EXTENSION_ID,
                                                                        services))
        {
            services.add(listener);
            addMouseClickListener(listener);
        }
        for (final WorldMouseMoveListener listener : UtilExtension.get(WorldMouseMoveListener.class,
                                                                       WorldMouseMoveListener.EXTENSION_ID,
                                                                       services))
        {
            services.add(listener);
            addMouseMoveListener(listener);
        }
    }

    /**
     * Add a mouse click listener.
     * 
     * @param listener The listener reference.
     */
    public final void addMouseClickListener(WorldMouseClickListener listener)
    {
        clickListeners.add(listener);
    }

    /**
     * Add a mouse move listener.
     * 
     * @param listener The listener reference.
     */
    public final void addMouseMoveListener(WorldMouseMoveListener listener)
    {
        moveListeners.add(listener);
    }

    /**
     * Add a mouse scroll listener.
     * 
     * @param listener The listener reference.
     */
    public final void addMouseScrollListener(WorldMouseScrollListener listener)
    {
        scrollListeners.add(listener);
    }

    /**
     * Add a key listener.
     * 
     * @param listener The listener reference.
     */
    public final void addKeyboardListener(WorldKeyboardListener listener)
    {
        keyListeners.add(listener);
    }

    /**
     * Remove a listener from all events listening.
     * 
     * @param listener The listener to remove.
     */
    public void removeListeners(Object listener)
    {
        clickListeners.remove(listener);
        moveListeners.remove(listener);
        scrollListeners.remove(listener);
        keyListeners.remove(listener);
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

        for (final WorldMouseClickListener listener : clickListeners)
        {
            listener.onMousePressed(click, mx, my);
        }
    }

    @Override
    public void mouseUp(MouseEvent mouseEvent)
    {
        mouseX = mouseEvent.x;
        mouseY = mouseEvent.y;

        final int mx = getMouseX();
        final int my = getMouseY();

        for (final WorldMouseClickListener listener : clickListeners)
        {
            listener.onMouseReleased(click, mx, my);
        }

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

        for (final WorldMouseMoveListener listener : moveListeners)
        {
            listener.onMouseMoved(click, oldMx, oldMy, mx, my);
        }
    }

    /*
     * MouseWheelListener
     */

    @Override
    public void mouseScrolled(MouseEvent mouseEvent)
    {
        for (final WorldMouseScrollListener listener : scrollListeners)
        {
            listener.onMouseScroll(mouseEvent.count, getMouseX(), getMouseY());
        }
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        for (final WorldKeyboardListener listener : keyListeners)
        {
            listener.onKeyPushed(Integer.valueOf(keyEvent.keyCode));
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
        // Nothing to do
    }
}
