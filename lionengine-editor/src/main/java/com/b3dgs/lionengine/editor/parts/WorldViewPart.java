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
package com.b3dgs.lionengine.editor.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.editor.world.WorldViewRenderer;

/**
 * Represents the world view, where the global map is displayed.
 * 
 * @author Pierre-Alexandre
 */
public class WorldViewPart
{
    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));
        final Composite composite = new Composite(parent, SWT.DOUBLE_BUFFERED);
        final WorldViewRenderer worldViewRenderer = new WorldViewRenderer();
        composite.addPaintListener(worldViewRenderer);
        composite.addMouseListener(worldViewRenderer);
        composite.addMouseMoveListener(worldViewRenderer);
    }
}
