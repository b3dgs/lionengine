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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Represents a tile collision composite, allowing to edit the tile collision data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollisionComposite
{
    /**
     * Create the buttons area.
     * 
     * @param parent The composite parent.
     */
    private static void createButtonsArea(Composite parent)
    {
        final Composite buttons = new Composite(parent, SWT.NONE);
        buttons.setLayout(new GridLayout(2, true));

        final Button apply = new Button(buttons, SWT.PUSH);
        apply.setText("Apply");

        final Button delete = new Button(buttons, SWT.PUSH);
        delete.setText("Delete");
    }

    /** Axis combo. */
    private Combo axis;
    /** Input combo. */
    private Combo input;
    /** Value field. */
    private Text value;
    /** Offset field. */
    private Text offset;
    /** Range min. */
    private Text min;
    /** Range max. */
    private Text max;

    /**
     * Constructor.
     * 
     * @param parent The composite parent.
     */
    public TileCollisionComposite(Composite parent)
    {
        final Group referential = new Group(parent, SWT.NONE);
        referential.setLayout(new GridLayout(1, false));
        referential.setText("Formula");

        createAxisChooser(referential);
        createFormulaEdition(referential);
        createButtonsArea(referential);
    }

    /**
     * Create the axis chooser area.
     * 
     * @param parent The composite parent.
     */
    private void createAxisChooser(Composite parent)
    {
        final Composite axisComposite = new Composite(parent, SWT.NONE);
        axisComposite.setLayout(new GridLayout(2, false));

        final Label axisLabel = new Label(axisComposite, SWT.NONE);
        axisLabel.setText("Axis: ");

        axis = new Combo(axisComposite, SWT.READ_ONLY);
        axis.setTextLimit(2);
    }

    /**
     * Create the formula edition area.
     * 
     * @param parent The composite parent.
     */
    private void createFormulaEdition(Composite parent)
    {
        final Composite formula = new Composite(parent, SWT.NONE);
        formula.setLayout(new GridLayout(5, false));

        input = new Combo(formula, SWT.READ_ONLY);
        input.setTextLimit(2);

        final Label multiplicate = new Label(formula, SWT.NONE);
        multiplicate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        multiplicate.setText("*");

        value = new Text(formula, SWT.SINGLE);
        value.setTextLimit(4);

        final Label add = new Label(formula, SWT.NONE);
        add.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        add.setText("+");

        offset = new Text(formula, SWT.SINGLE);
        offset.setTextLimit(4);

        createRangeEdition(formula);
    }

    /**
     * Create the range edition area.
     * 
     * @param parent The composite parent.
     */
    private void createRangeEdition(Composite parent)
    {
        final Label range = new Label(parent, SWT.NONE);
        range.setText("range");

        final Label rangeMin = new Label(parent, SWT.NONE);
        rangeMin.setText("min");

        min = new Text(parent, SWT.SINGLE);
        min.setTextLimit(4);

        final Label rangeMax = new Label(parent, SWT.NONE);
        rangeMax.setText("max");

        max = new Text(parent, SWT.SINGLE);
        max.setTextLimit(4);
    }
}
