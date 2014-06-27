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
package com.b3dgs.lionengine.editor.collision;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the tile collision view, where the tile collision can be edited.
 * 
 * @author Pierre-Alexandre
 */
public class TileCollisionPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.tile-collision";

    /** Parent reference. */
    private Composite parent;
    /** Formula list. */
    private List<TileCollisionComposite> formulas;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        this.parent = parent;
        formulas = new ArrayList<>(1);
        parent.setLayout(new GridLayout(1, false));
        parent.setEnabled(false);

        final TileCollisionComposite tileCollisionComposite = new TileCollisionComposite(parent);
        formulas.add(tileCollisionComposite);
    }

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        parent.setEnabled(enabled);
    }
}
