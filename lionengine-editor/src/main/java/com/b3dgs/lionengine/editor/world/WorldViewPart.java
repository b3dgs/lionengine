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
package com.b3dgs.lionengine.editor.world;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.collision.TileCollisionPart;
import com.b3dgs.lionengine.editor.dialogs.ImportMapDialog;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Represents the world view, where the global map is displayed.
 * 
 * @author Pierre-Alexandre
 */
public class WorldViewPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world-view";
    /** Import map icon. */
    private static final Image ICON_IMPORT_MAP = UtilEclipse.getIcon("import-map.png");
    /** Import level verbose. */
    private static final String VERBOSE_IMPORT_LEVEL = "Importing map from level rip: ";
    /** Using tile sheet verbose. */
    private static final String VERBOSE_USING_TILESHEETS = " using the following sheets: ";
    /** Extension point attribute renderer. */
    private static final String EXTENSION_RENDERER = "renderer";

    /** Part service. */
    @Inject
    EPartService partService;
    /** Composite. */
    private Composite composite;
    /** Renderer. */
    private WorldViewRenderer worldViewRenderer;
    /** Tool bar. */
    private ToolBar toolBar;

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

        toolBar = createToolBar(parent);
        toolBar.setEnabled(false);

        composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        worldViewRenderer = checkRendererExtensionPoint();
        composite.addPaintListener(worldViewRenderer);
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
        toolBar.setEnabled(enabled);
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
     * Import map from its level rip and tile sheets.
     * 
     * @param level The level rip.
     * @param pattern The tile sheets directory.
     */
    void importMap(Media level, Media pattern)
    {
        Verbose.info(WorldViewPart.VERBOSE_IMPORT_LEVEL, level.getPath(), WorldViewPart.VERBOSE_USING_TILESHEETS,
                pattern.getPath());

        final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
        map.load(level, pattern);
        map.createCollisionDraw();

        final TileCollisionPart part = UtilEclipse.getPart(partService, TileCollisionPart.ID, TileCollisionPart.class);
        part.setSaveEnabled(true);

        update();
        focus();
    }

    /**
     * Create the tool bar.
     * 
     * @param parent The parent reference.
     * @return The created tool bar.
     */
    private ToolBar createToolBar(final Composite parent)
    {
        final ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

        final ToolItem importMap = new ToolItem(toolBar, SWT.PUSH);
        importMap.setImage(WorldViewPart.ICON_IMPORT_MAP);
        importMap.setText(Messages.WorldView_ImportMap);
        importMap.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final ImportMapDialog importMapDialog = new ImportMapDialog(parent.getShell());
                importMapDialog.open();

                if (importMapDialog.isFound())
                {
                    final Media level = importMapDialog.getLevelRipLocation();
                    final Media pattern = importMapDialog.getPatternsLocation();
                    importMap(level, pattern);
                }
            }
        });
        return toolBar;
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
