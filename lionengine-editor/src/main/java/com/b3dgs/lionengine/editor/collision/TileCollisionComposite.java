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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.ObjectList;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.editor.dialogs.AbstractDialog;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.CollisionRefential;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Represents a tile collision composite, allowing to edit the tile collision data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileCollisionComposite
{
    /** Multiplication. */
    private static final String MULT = "*";
    /** Multiplication. */
    private static final String ADD = "+";

    /** Collision part. */
    final TileCollisionView tileCollisionView;
    /** Referential. */
    private final Group referential;
    /** Minimum composite width. */
    private final int minWidth;
    /** Minimum composite height. */
    private final int minHeight;
    /** Axis combo. */
    Combo axis;
    /** Input combo. */
    Combo input;
    /** Value field. */
    Text value;
    /** Offset field. */
    Text offset;
    /** Range min. */
    Text min;
    /** Range max. */
    Text max;
    /** Selected function. */
    CollisionFunction selectedFunction;

    /**
     * Constructor.
     * 
     * @param tileCollisionView The collision view reference.
     * @param parent The composite parent.
     */
    public TileCollisionComposite(TileCollisionView tileCollisionView, Composite parent)
    {
        this.tileCollisionView = tileCollisionView;

        referential = new Group(parent, SWT.NONE);
        referential.setLayout(new GridLayout(1, false));
        referential.setText(Messages.TileCollisionComposite_Formula);

        createAxisChooser(referential);
        createFormulaEdition(referential);
        createButtonsArea(referential);

        referential.pack(true);

        final Point size = referential.getSize();
        minWidth = size.x;
        minHeight = size.y;
    }

    /**
     * Set the selected tile function.
     * 
     * @param function The selected function.
     */
    public void setSelectedFunction(CollisionFunction function)
    {
        if (function != null)
        {
            referential.setText(Messages.TileCollisionComposite_Formula + function.getName());
            axis.setText(function.getAxis().name());
            input.setText(function.getInput().name());
            value.setText(String.valueOf(function.getValue()));
            offset.setText(String.valueOf(function.getOffset()));
            min.setText(String.valueOf(function.getRange().getMin()));
            max.setText(String.valueOf(function.getRange().getMax()));
        }
        selectedFunction = function;
    }

    /**
     * Dispose the composite.
     */
    public void dispose()
    {
        referential.dispose();
    }

    /**
     * Get the minimum width.
     * 
     * @return The minimum width.
     */
    public int getMinWidth()
    {
        return minWidth;
    }

    /**
     * Get the minimum height.
     * 
     * @return The minimum height.
     */
    public int getMinHeight()
    {
        return minHeight;
    }

    /**
     * Get the collision function associated to the composite.
     * 
     * @return The collision function.
     */
    public CollisionFunction getCollisionFunction()
    {
        return selectedFunction;
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
        axisLabel.setText(Messages.TileCollisionComposite_Axis);

        axis = UtilSwt.createCombo(axisComposite, CollisionRefential.values());
        axis.setLayoutData(new GridData(16, 16));
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

        input = UtilSwt.createCombo(formula, CollisionRefential.values());
        input.setLayoutData(new GridData(16, 16));
        input.setTextLimit(2);

        final Label multiplicate = new Label(formula, SWT.NONE);
        multiplicate.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        multiplicate.setText(TileCollisionComposite.MULT);

        value = new Text(formula, SWT.SINGLE);
        value.setLayoutData(new GridData(32, 16));
        value.setTextLimit(4);

        final Label add = new Label(formula, SWT.NONE);
        add.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        add.setText(TileCollisionComposite.ADD);

        offset = new Text(formula, SWT.SINGLE);
        offset.setLayoutData(new GridData(32, 16));
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
        range.setText(Messages.TileCollisionComposite_Range);

        final Label rangeMin = new Label(parent, SWT.NONE);
        rangeMin.setText(Messages.TileCollisionComposite_Min);

        min = new Text(parent, SWT.SINGLE);
        min.setLayoutData(new GridData(32, 16));
        min.setTextLimit(4);

        final Label rangeMax = new Label(parent, SWT.NONE);
        rangeMax.setText(Messages.TileCollisionComposite_Max);

        max = new Text(parent, SWT.SINGLE);
        max.setLayoutData(new GridData(32, 16));
        max.setTextLimit(4);
    }

    /**
     * Create the buttons area.
     * 
     * @param parent The composite parent.
     */
    private void createButtonsArea(Composite parent)
    {
        final Composite buttons = new Composite(parent, SWT.NONE);
        buttons.setLayout(new GridLayout(2, true));

        final Button apply = new Button(buttons, SWT.PUSH);
        apply.setText(Messages.TileCollisionComposite_Apply);
        apply.setImage(AbstractDialog.ICON_OK);
        apply.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                selectedFunction.setAxis(CollisionRefential.valueOf(axis.getText()));
                selectedFunction.setInput(CollisionRefential.valueOf(input.getText()));
                selectedFunction.setValue(Double.parseDouble(value.getText()));
                selectedFunction.setOffset(Integer.parseInt(offset.getText()));
                selectedFunction.setRange(Integer.parseInt(min.getText()), Integer.parseInt(max.getText()));

                final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
                map.createCollisionDraw();
                tileCollisionView.updateWorldView();
            }
        });

        final Button delete = new Button(buttons, SWT.PUSH);
        delete.setText(Messages.TileCollisionComposite_Delete);
        delete.setImage(ObjectList.ICON_REMOVE);
        delete.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                tileCollisionView.removeFormula(TileCollisionComposite.this);
            }
        });
    }
}
