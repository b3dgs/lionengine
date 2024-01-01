/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.world.view;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.utility.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilToolbar;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderer;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.game.feature.Services;

/**
 * Represents the world, where the global map is displayed.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class WorldPart implements WorldView, Focusable
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world";
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
    public static Canvas createPart(Composite parent, WorldUpdater updater, PaintListener renderer)
    {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 1;
        layout.verticalSpacing = 1;
        parent.setLayout(layout);

        final Canvas composite = new Canvas(parent, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        composite.addMouseListener(updater);
        composite.addMouseMoveListener(updater);
        composite.addMouseWheelListener(updater);
        composite.addKeyListener(updater);

        composite.addPaintListener(renderer);
        composite.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseUp(MouseEvent event)
            {
                update(composite);
            }

            @Override
            public void mouseDown(MouseEvent event)
            {
                update(composite);
            }
        });
        composite.addMouseMoveListener(event -> update(composite));
        composite.addMouseWheelListener(event -> update(composite));
        composite.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(org.eclipse.swt.events.KeyEvent event)
            {
                update(composite);
            }
        });

        return composite;
    }

    /**
     * Update and redraw composite if not disposed.
     * 
     * @param composite The composite to redraw.
     */
    private static void update(Composite composite)
    {
        if (!composite.isDisposed())
        {
            composite.redraw();
        }
    }

    /** Part service. */
    private final EPartService partService;
    /** Composite. */
    private Canvas composite;
    /** Updater. */
    private WorldUpdater updater;
    /** Renderer. */
    private WorldRenderer renderer;

    /**
     * Create part.
     * 
     * @param partService The injected part service.
     */
    @Inject
    public WorldPart(EPartService partService)
    {
        super();

        this.partService = partService;
    }

    /**
     * Add a mouse wheel listener.
     * 
     * @param listener The listener reference.
     */
    public void addMouseWheelListener(MouseWheelListener listener)
    {
        composite.addMouseWheelListener(listener);
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

        updater = new WorldUpdater(partService, services);
        services.add(updater);
        renderer = new WorldRenderer(partService, services);

        composite = createPart(parent, updater, renderer);
        composite.addMouseTrackListener(UtilSwt.createFocusListener(this));

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
     * Get the view area.
     * 
     * @return The view area.
     */
    public Rectangle getView()
    {
        return composite.getClientArea();
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
        throw new LionEngineException(ERROR_ITEM + item);
    }
}
