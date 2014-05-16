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

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the world view, where the global map is displayed.
 * 
 * @author Pierre-Alexandre
 */
public class WorldViewPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.world-view";

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
        parent.setLayout(new GridLayout(1, false));
        composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        worldViewRenderer = new WorldViewRenderer(composite);
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
     * Force focus.
     */
    @Focus
    public void focus()
    {
        composite.forceFocus();
    }
}
