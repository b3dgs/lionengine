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

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
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
    private static final Image ICON_IMPORT_MAP = Tools.getIcon("import-map.png");

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
        parent.setLayout(new GridLayout(1, false));

        toolBar = createToolBar(parent);
        toolBar.setEnabled(false);

        final Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        worldViewRenderer = new WorldViewRenderer(composite, partService);
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
        importMap.setText("Import Map From Level Rip");
        importMap.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final ImportMapDialog importMapDialog = new ImportMapDialog(parent.getShell());
                importMapDialog.open();

                if (importMapDialog.isFound())
                {
                    final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
                    map.load(importMapDialog.getLevelRipLocation(), importMapDialog.getPatternsLocation());

                    final WorldViewPart part = Tools.getPart(partService, WorldViewPart.ID, WorldViewPart.class);
                    part.update();
                    part.focus();
                }
            }
        });
        return toolBar;
    }
}
