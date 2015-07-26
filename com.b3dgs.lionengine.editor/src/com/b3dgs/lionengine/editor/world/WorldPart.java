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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderer;
import com.b3dgs.lionengine.editor.world.updater.WorldInteractionTile;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Represents the world, where the global map is displayed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldPart implements Focusable, TileSelectionListener
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world";
    /** Extension point attribute updater. */
    private static final String EXTENSION_UPDATER = "updater";
    /** Extension point attribute renderer. */
    private static final String EXTENSION_RENDERER = "renderer";

    /** Part service. */
    @Inject
    EPartService partService;
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

        composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.addMouseTrackListener(UtilSwt.createFocusListener(this));

        final Services services = WorldModel.INSTANCE.getServices();

        updater = checkUpdaterExtensionPoint(services);
        composite.addMouseListener(updater);
        composite.addMouseMoveListener(updater);
        composite.addMouseWheelListener(updater);
        composite.addKeyListener(updater);
        services.add(updater);

        final WorldInteractionTile tileInteraction = services.get(WorldInteractionTile.class);
        tileInteraction.addListener(this);

        renderer = checkRendererExtensionPoint(services);
        composite.addPaintListener(renderer);
        composite.addMouseListener(renderer);
        composite.addMouseMoveListener(renderer);
        composite.addMouseWheelListener(renderer);
        composite.addKeyListener(renderer);

        Display.getDefault().asyncExec(new Runnable()
        {
            @Override
            public void run()
            {
                setToolBarEnabled(false);
            }
        });
    }

    /**
     * Update the view.
     */
    public void update()
    {
        if (!composite.isDisposed())
        {
            composite.redraw();
        }
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
                UtilEclipse.setToolItemEnabled(toolBar, enabled);
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
                UtilEclipse.setToolItemEnabled(toolBar, enabled, item);
            }
        }
    }

    /**
     * Set the tool item text.
     * 
     * @param item The item id extract.
     * @param text The item text.
     */
    public void setToolItemText(String item, String text)
    {
        final MPart part = partService.findPart(WorldPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                UtilEclipse.setToolItemText(toolBar, item, text);
            }
        }
    }

    /**
     * Get the tool item.
     * 
     * @param <T> The element type.
     * @param item The item id extract.
     * @param clazz The element class.
     * @return The composite found.
     * @throws LionEngineException If not found.
     */
    public <T> T getToolItem(String item, Class<T> clazz) throws LionEngineException
    {
        final MPart part = partService.findPart(WorldPart.ID);
        if (part != null)
        {
            final MToolBar toolBar = part.getToolbar();
            if (toolBar != null)
            {
                return UtilEclipse.getToolItem(toolBar, item, clazz);
            }
        }
        throw new LionEngineException(UtilEclipse.ERROR_ITEM, item);
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
            final String renderer = elements[0].getAttribute(EXTENSION_UPDATER);
            if (renderer != null)
            {
                try
                {
                    return UtilEclipse.createClass(renderer, WorldUpdater.class, partService);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(getClass(), "checkUpdaterExtensionPoint", exception);
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
            final String renderer = elements[0].getAttribute(EXTENSION_RENDERER);
            if (renderer != null)
            {
                try
                {
                    return UtilEclipse.createClass(renderer, WorldRenderer.class, composite, partService);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(getClass(), "checkRendererExtensionPoint", exception);
                }
            }
        }
        return new WorldRenderer(composite, partService, services);
    }

    @Override
    public void notifyTileSelected(Tile tile)
    {
        final PropertiesPart part = UtilEclipse.getPart(PropertiesPart.ID, PropertiesPart.class);
        if (tile != null)
        {
            part.setInput(part.getTree(), tile);
        }
        else
        {
            part.clear();
        }
    }
}
