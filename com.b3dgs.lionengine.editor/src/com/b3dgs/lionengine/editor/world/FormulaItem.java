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
package com.b3dgs.lionengine.editor.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.b3dgs.lionengine.editor.utility.UtilCombo;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionFunctionLinear;

/**
 * Represents the quick formula assignment area.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FormulaItem
{
    /** Element ID. */
    public static final String ID = "formulas";
    /** Line function. */
    public static final CollisionFunction LINE = new CollisionFunctionLinear(0.0, 0.0);
    /** Slope function. */
    public static final CollisionFunction SLOPE = new CollisionFunctionLinear(0.5, 0.0);
    /** Text height. */
    private static final int TEXT_HEIGHT = 8;
    /** Minimum combo width. */
    private static final int COMBO_MIN_WIDTH = 128;

    /** Loaded data. */
    private final Map<String, CollisionFunction> values = new HashMap<>();
    /** The combo item. */
    private Combo combo;

    /**
     * Create item.
     */
    public FormulaItem()
    {
        // Nothing to do
    }

    /**
     * Get the selected formula name.
     * 
     * @return The selected formula name.
     */
    public String getName()
    {
        return combo.getText();
    }

    /**
     * Get the current collision function selection.
     * 
     * @return The collision function selection (<code>null</code> if no function).
     */
    public CollisionFunction getFunction()
    {
        return (CollisionFunction) combo.getData();
    }

    /**
     * Get the parent composite.
     * 
     * @return The parent composite.
     */
    public Composite getParent()
    {
        return combo.getParent();
    }

    /**
     * Construct the item.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void create(Composite parent)
    {
        final FontDescriptor boldDescriptor = FontDescriptor.createFrom(parent.getFont()).setHeight(TEXT_HEIGHT);
        final Font font = boldDescriptor.createFont(parent.getDisplay());

        final GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        parent.setLayout(layout);

        final Label label = new Label(parent, SWT.NONE);
        label.setFont(font);
        label.setText(Messages.Toolbar_Formula);

        combo = new Combo(parent, SWT.SINGLE | SWT.READ_ONLY);
        combo.setFont(font);
        combo.setLayoutData(new GridData(COMBO_MIN_WIDTH, TEXT_HEIGHT));
        UtilCombo.setAction(combo, () -> combo.setData(values.get(combo.getText())));
        values.clear();
        values.put("Line", LINE);
        values.put("Slope", SLOPE);

        final String[] formulas = values.keySet().toArray(new String[values.size()]);
        Arrays.sort(formulas);
        combo.setItems(formulas);
    }
}
