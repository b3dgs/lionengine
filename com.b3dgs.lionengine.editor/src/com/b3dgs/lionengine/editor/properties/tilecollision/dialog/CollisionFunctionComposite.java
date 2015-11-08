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
package com.b3dgs.lionengine.editor.properties.tilecollision.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.utility.UtilCombo;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunction;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunctionType;

/**
 * Represents the collision function edition.
 */
public class CollisionFunctionComposite
{
    /**
     * Create composite for function type.
     * 
     * @param type The function type.
     * @return The function composite.
     */
    private static CollisionFunctionTypeComposite createComposite(CollisionFunctionType type)
    {
        switch (type)
        {
            case LINEAR:
                return new CollisionFunctionLinearComposite();
            default:
                throw new LionEngineException(type);
        }
    }

    /** Function type. */
    private Combo comboFunction;
    /** Current function composite. */
    private CollisionFunctionTypeComposite current;
    /** Old function. */
    private CollisionFunction old;

    /**
     * Create composite.
     */
    public CollisionFunctionComposite()
    {
        // Nothing to do
    }

    /**
     * Load an existing function and fill fields.
     * 
     * @param function The function to load.
     */
    public void load(CollisionFunction function)
    {
        old = function;
        comboFunction.setText(function.getType().name());
        comboFunction.setData(function.getType());
        current.load(function);
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent composite.
     */
    public void create(Composite parent)
    {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(1, true));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        group.setText(Messages.Dialog_TileCollision_Function);

        comboFunction = UtilCombo.create(group, CollisionFunctionType.values());
        UtilCombo.setAction(comboFunction, () ->
        {
            if (current != null)
            {
                current.dispose();
            }
            current = createComposite((CollisionFunctionType) comboFunction.getData());
            current.create(group);
            current.load(old);
        });
        current = createComposite((CollisionFunctionType) comboFunction.getData());
        current.create(group);
    }

    /**
     * Get the configured collision function.
     * 
     * @return The configured collision function.
     */
    public CollisionFunction get()
    {
        return current.get();
    }
}
