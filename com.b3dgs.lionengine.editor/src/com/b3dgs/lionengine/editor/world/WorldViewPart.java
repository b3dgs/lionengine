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

import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilEclipse;

/**
 * Represents the world view, where the global map is displayed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class WorldViewPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world-view";
    /** Extension point attribute renderer. */
    private static final String EXTENSION_RENDERER = "renderer";

    /** Part service. */
    @Inject
    EPartService partService;
    /** Composite. */
    private Composite composite;
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

        worldViewRenderer = checkRendererExtensionPoint();
        composite.addPaintListener(worldViewRenderer);
        addListeners();
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
     * Add mouse and keyboard listeners.
     */
    public void addListeners()
    {
        composite.addMouseListener(worldViewRenderer);
        composite.addMouseMoveListener(worldViewRenderer);
        composite.addKeyListener(worldViewRenderer);
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
     * Switch the grid enabled state.
     */
    public void switchGridEnabled()
    {
        worldViewRenderer.switchGridEnabled();
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
     * Check the renderer extension point.
     * 
     * @return renderer instance from extension point or default one.
     */
    private WorldViewRenderer checkRendererExtensionPoint()
    {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                WorldViewRenderer.EXTENSION_ID);
        if (elements.length > 0)
        {
            final String renderer = elements[0].getAttribute(WorldViewPart.EXTENSION_RENDERER);
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
        return new WorldViewRenderer(composite, partService);
    }
}
