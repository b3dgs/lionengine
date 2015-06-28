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

import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.game.map.Tile;
import com.b3dgs.lionengine.game.object.Services;

/**
 * Represents the world view, where the global map is displayed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldViewPart
        implements TileSelectionListener
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world-view";
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
    private WorldViewUpdater worldViewUpdater;
    /** Renderer. */
    private WorldViewRenderer worldViewRenderer;

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

        final Services services = WorldViewModel.INSTANCE.getServices();

        worldViewUpdater = checkUpdaterExtensionPoint(services);
        worldViewUpdater.addListenerTile(this);
        composite.addMouseListener(worldViewUpdater);
        composite.addMouseMoveListener(worldViewUpdater);
        composite.addKeyListener(worldViewUpdater);
        services.add(worldViewUpdater);

        worldViewRenderer = checkRendererExtensionPoint(services);
        composite.addPaintListener(worldViewRenderer);
        composite.addMouseListener(worldViewRenderer);
        composite.addMouseMoveListener(worldViewRenderer);
        composite.addKeyListener(worldViewRenderer);

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
        final MPart part = partService.findPart(WorldViewPart.ID);
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
        final MPart part = partService.findPart(WorldViewPart.ID);
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
     * Switch the grid enabled state.
     */
    public void switchGridEnabled()
    {
        worldViewUpdater.switchGridEnabled();
    }

    /**
     * Switch the collisions enabled state.
     */
    public void switchCollisionsEnabled()
    {
        worldViewUpdater.switchCollisionsEnabled();
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
     * Get the world renderer.
     * 
     * @return The world renderer.
     */
    public WorldViewRenderer getRenderer()
    {
        return worldViewRenderer;
    }

    /**
     * Force focus.
     */
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
    private WorldViewUpdater checkUpdaterExtensionPoint(Services services)
    {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                WorldViewUpdater.EXTENSION_ID);
        if (elements.length > 0)
        {
            final String renderer = elements[0].getAttribute(EXTENSION_UPDATER);
            if (renderer != null)
            {
                try
                {
                    return UtilEclipse.createClass(renderer, WorldViewUpdater.class, partService);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(getClass(), "checkUpdaterExtensionPoint", exception);
                }
            }
        }
        return new WorldViewUpdater(partService, services);
    }

    /**
     * Check the renderer extension point.
     * 
     * @param services The services reference.
     * @return renderer instance from extension point or default one.
     */
    private WorldViewRenderer checkRendererExtensionPoint(Services services)
    {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                WorldViewRenderer.EXTENSION_ID);
        if (elements.length > 0)
        {
            final String renderer = elements[0].getAttribute(EXTENSION_RENDERER);
            if (renderer != null)
            {
                try
                {
                    return UtilEclipse.createClass(renderer, WorldViewRenderer.class, composite, partService);
                }
                catch (final ReflectiveOperationException exception)
                {
                    Verbose.exception(getClass(), "checkRendererExtensionPoint", exception);
                }
            }
        }
        return new WorldViewRenderer(composite, partService, services);
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
