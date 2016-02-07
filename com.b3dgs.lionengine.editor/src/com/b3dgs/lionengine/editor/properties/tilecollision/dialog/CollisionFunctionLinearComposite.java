/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunction;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunctionLinear;

/**
 * Represents the collision function linear edition.
 */
public class CollisionFunctionLinearComposite implements CollisionFunctionTypeComposite
{
    /** Default function values. */
    private static final String DEFAULT_FUNCTION_VALUES = String.valueOf(0.0);

    /** A value. */
    private Text textA;
    /** B value. */
    private Text textB;

    /**
     * Create composite.
     */
    public CollisionFunctionLinearComposite()
    {
        // Nothing to do
    }

    /*
     * CollisionFunctionTypeComposite
     */

    @Override
    public void create(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayout(new GridLayout(2, true));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        textA = UtilText.create(Messages.Dialog_TileCollision_Linear_A, composite);
        textA.setText(DEFAULT_FUNCTION_VALUES);

        textB = UtilText.create(Messages.Dialog_TileCollision_Linear_B, composite);
        textB.setText(DEFAULT_FUNCTION_VALUES);
    }

    @Override
    public void load(CollisionFunction function)
    {
        final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
        textA.setText(String.valueOf(linear.getA()));
        textB.setText(String.valueOf(linear.getB()));
    }

    @Override
    public void dispose()
    {
        textA.dispose();
        textB.dispose();
    }

    @Override
    public CollisionFunction get()
    {
        return new CollisionFunctionLinear(Double.parseDouble(textA.getText()), Double.parseDouble(textB.getText()));
    }
}
