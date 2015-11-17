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
package com.b3dgs.lionengine.editor.world;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.utility.UtilClass;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.utility.UtilToolbar;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderer;
import com.b3dgs.lionengine.editor.world.renderer.WorldSelectedTiles;
import com.b3dgs.lionengine.editor.world.updater.WorldInteractionTile;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Represents the world, where the global map is displayed.
 */
public class WorldPart implements WorldView, Focusable, TileSelectionListener
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world";
    /** Extension point attribute updater. */
    private static final String EXTENSION_UPDATER = "updater";
    /** Extension point attribute renderer. */
    private static final String EXTENSION_RENDERER = "renderer";
    /** Item not found. */
    private static final String ERROR_ITEM = "Item not found: ";

    /**
     * Create the world part.
     * 
     * @param parent The composite parent.
     * @param updater The updater reference.
     * @param renderer The renderer reference.
     * @return The created part.
     */
    public static Composite createPart(Composite parent, WorldUpdater updater, WorldRenderer renderer)
    {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 1;
        layout.verticalSpacing = 1;
        parent.setLayout(layout);

        final Composite composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        composite.addMouseListener(updater);
        composite.addMouseMoveListener(updater);
        composite.addMouseWheelListener(updater);
        composite.addKeyListener(updater);

        composite.addPaintListener(renderer);
        composite.addMouseListener(renderer);
        composite.addMouseMoveListener(renderer);
        composite.addMouseWheelListener(renderer);
        composite.addKeyListener(renderer);

        return composite;
    }

    /** Part service. */
    @Inject
    private EPartService partService;
    /** Composite. */
    private Composite composite;
    /** Updater. */
    private WorldUpdater updater;
    /** Renderer. */
    private WorldRenderer renderer;

    /**
     * Create part.
     */
    public WorldPart()
    {
        // Nothing to do
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 1;
        layout.verticalSpacing = 1;
        parent.setLayout(layout);

        final Services services = WorldModel.INSTANCE.getServices();
        services.add(this);

        updater = checkUpdaterExtensionPoint(services);
        services.add(updater);
        renderer = checkRendererExtensionPoint(services);

        composite = createPart(parent, updater, renderer);
        composite.addMouseTrackListener(UtilSwt.createFocusListener(this));

        final WorldInteractionTile tileInteraction = services.get(WorldInteractionTile.class);
        tileInteraction.addListener(this);
        tileInteraction.addListener(services.get(WorldSelectedTiles.class));

        Display.getDefault().asyncExec(() -> setToolBarEnabled(false));
    }

    /**
     * Set the tool bar enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setToolBarEnabled(boolean enabled)
    {
        final MPart part = partService.findPart(WorldPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                UtilToolbar.setToolItemEnabled(toolBar, enabled);
            }
        }
    }

    /**
     * Set the tool item enabled state.
     * 
     * @param item The item id extract.
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setToolItemEnabled(String item, boolean enabled)
    {
        final MPart part = partService.findPart(WorldPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                UtilToolbar.setToolItemEnabled(toolBar, enabled, item);
            }
        }
    }

    /**
     * Set the current cursor.
     * 
     * @param cursor The cursor to set.
     */
    public void setCursor(Cursor cursor)
    {
        composite.setCursor(cursor);
    }

    /**
     * Get the view location.
     * 
     * @return The view location.
     */
    public Point getLocation()
    {
        return composite.toDisplay(composite.getClientArea().x, composite.getClientArea().y);
    }

    /**
     * Get the world updater.
     * 
     * @return The world updater.
     */
    public WorldUpdater getUpdater()
    {
        return updater;
    }

    /**
     * Get the world renderer.
     * 
     * @return The world renderer.
     */
    public WorldRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Force focus.
     */
    @Override
    @Focus
    public void focus()
    {
        composite.forceFocus();
    }

    /**
     * Check the updater extension point.
     * 
     * @param services The services reference.
     * @return renderer instance from extension point or default one.
     */
    private WorldUpdater checkUpdaterExtensionPoint(Services services)
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IConfigurationElement[] elements = registry.getConfigurationElementsFor(WorldUpdater.EXTENSION_ID);
        if (elements.length > 0)
        {
            final String extensionUpdater = elements[0].getAttribute(EXTENSION_UPDATER);
            if (extensionUpdater != null)
            {
                try
                {
                    return UtilClass.createClass(extensionUpdater, WorldUpdater.class, partService, services);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(exception);
                }
            }
        }
        return new WorldUpdater(partService, services);
    }

    /**
     * Check the renderer extension point.
     * 
     * @param services The services reference.
     * @return renderer instance from extension point or default one.
     */
    private WorldRenderer checkRendererExtensionPoint(Services services)
    {
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        final IConfigurationElement[] elements = registry.getConfigurationElementsFor(WorldRenderer.EXTENSION_ID);
        if (elements.length > 0)
        {
            final String extensionRenderer = elements[0].getAttribute(EXTENSION_RENDERER);
            if (extensionRenderer != null)
            {
                try
                {
                    return UtilClass.createClass(extensionRenderer, WorldRenderer.class, partService, services);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(exception);
                }
            }
        }
        return new WorldRenderer(partService, services);
    }

    /*
     * WorldView
     */

    @Override
    public void update()
    {
        if (!composite.isDisposed())
        {
            composite.redraw();
        }
    }

    @Override
    public void setToolItemText(String item, String text)
    {
        final MPart part = partService.findPart(WorldPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                UtilToolbar.setToolItemText(toolBar, item, text);
            }
        }
    }

    @Override
    public <T> T getToolItem(String item, Class<T> clazz)
    {
        final MPart part = partService.findPart(WorldPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                return UtilToolbar.getToolItem(toolBar, item, clazz);
            }
        }
        throw new LionEngineException(ERROR_ITEM, item);
    }

    /*
     * TileSelectionListener
     */

    @Override
    public void notifyTileSelected(Tile tile)
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
