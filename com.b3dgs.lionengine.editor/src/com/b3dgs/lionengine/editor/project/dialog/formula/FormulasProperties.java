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
package com.b3dgs.lionengine.editor.project.dialog.formula;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectProperties;
import com.b3dgs.lionengine.editor.UtilSwt;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.collision.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.collision.CollisionFunctionType;
import com.b3dgs.lionengine.game.collision.CollisionRange;

/**
 * Represents the formulas properties edition view.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FormulasProperties
        extends ObjectProperties<CollisionFormula>
        implements ObjectListListener<CollisionFormula>
{
    /** Unknown function type. */
    private static final String ERROR_TYPE = "Unknown collision function type: ";

    /**
     * Get the constraint value.
     * 
     * @param constraint The constraint value.
     * @return The same constraint value, empty string if <code>null</code>.
     */
    private static String getConstraintValue(String constraint)
    {
        return constraint != null ? constraint : "";
    }

    /** Last collision function panel. */
    private Composite lastFunctionPanel;
    /** Function type. */
    private Combo type;
    /** Last type data. */
    private CollisionFunctionType lastType;
    /** Output type. */
    private Combo output;
    /** Minimum X. */
    private Text minX;
    /** Maximum X. */
    private Text maxX;
    /** Minimum Y. */
    private Text minY;
    /** Maximum Y. */
    private Text maxY;
    /** Function linear A. */
    private Text linearA;
    /** Function linear B. */
    private Text linearB;
    /** Constraint top. */
    private Text constraintTop;
    /** Constraint bottom. */
    private Text constraintBottom;
    /** Constraint left. */
    private Text constraintLeft;
    /** Constraint right. */
    private Text constraintRight;

    /**
     * Create formulas properties.
     * 
     * @param list The list reference.
     */
    public FormulasProperties(FormulaList list)
    {
        super(list);
    }

    /**
     * Select the function type.
     * 
     * @param parent The composite parent.
     */
    void selectFunctionType(Composite parent)
    {
        final CollisionFunctionType current = (CollisionFunctionType) type.getData();
        if (lastType != current)
        {
            if (lastFunctionPanel != null)
            {
                lastFunctionPanel.dispose();
                lastFunctionPanel = null;
            }
            selectFunctionPanel(current, parent);
            parent.getShell().pack(true);
        }
        lastType = current;
    }

    /**
     * Select the function panel to display depending of the type.
     * 
     * @param type The function type.
     * @param parent The parent composite.
     */
    void selectFunctionPanel(CollisionFunctionType type, Composite parent)
    {
        switch (type)
        {
            case LINEAR:
                createCollisionFunctionLinear(parent);
                break;
            default:
                throw new LionEngineException(ERROR_TYPE, type.name());
        }
    }

    /**
     * Create the collision function linear area.
     * 
     * @param parent The parent composite.
     */
    private void createCollisionFunctionLinear(Composite parent)
    {
        final Composite linearArea = new Composite(parent, SWT.NONE);
        linearArea.setLayout(new GridLayout(2, false));
        linearA = UtilSwt.createText(Messages.EditFormulasDialog_FunctionLinearA, linearArea);
        linearA.addVerifyListener(UtilSwt.createVerify(linearA, InputValidator.DOUBLE_MATCH));
        linearB = UtilSwt.createText(Messages.EditFormulasDialog_FunctionLinearB, linearArea);
        linearB.addVerifyListener(UtilSwt.createVerify(linearB, InputValidator.DOUBLE_MATCH));

        lastFunctionPanel = linearArea;
    }

    /**
     * Create the range properties.
     * 
     * @param parent The parent composite.
     */
    private void createTextRange(Composite parent)
    {
        final Composite outputArea = new Composite(parent, SWT.NONE);
        final GridLayout outputAreaLayout = new GridLayout(1, false);
        outputAreaLayout.marginHeight = 0;
        outputArea.setLayout(outputAreaLayout);
        output = UtilSwt.createCombo(Messages.EditFormulasDialog_RangeOutput, outputArea, Axis.values());

        final Composite xArea = new Composite(parent, SWT.NONE);
        final GridLayout xAreaLayout = new GridLayout(2, false);
        xAreaLayout.marginHeight = 0;
        xArea.setLayout(xAreaLayout);
        minX = UtilSwt.createText(Messages.EditFormulasDialog_RangeMinX, xArea);
        minX.addVerifyListener(UtilSwt.createVerify(minX, InputValidator.INTEGER_POSITIVE_MATCH));
        maxX = UtilSwt.createText(Messages.EditFormulasDialog_RangeMaxX, xArea);
        maxX.addVerifyListener(UtilSwt.createVerify(maxX, InputValidator.INTEGER_POSITIVE_MATCH));

        final Composite yArea = new Composite(parent, SWT.NONE);
        final GridLayout yAreaLayout = new GridLayout(2, false);
        yAreaLayout.marginHeight = 0;
        yArea.setLayout(yAreaLayout);
        minY = UtilSwt.createText(Messages.EditFormulasDialog_RangeMinY, yArea);
        minY.addVerifyListener(UtilSwt.createVerify(minY, InputValidator.INTEGER_POSITIVE_MATCH));
        maxY = UtilSwt.createText(Messages.EditFormulasDialog_RangeMaxY, yArea);
        maxY.addVerifyListener(UtilSwt.createVerify(maxY, InputValidator.INTEGER_POSITIVE_MATCH));
    }

    /**
     * Create the function properties.
     * 
     * @param parent The parent composite.
     */
    private void createTextFunction(final Composite parent)
    {
        final Composite typeArea = new Composite(parent, SWT.NONE);
        final GridLayout typeAreaLayout = new GridLayout(1, false);
        typeAreaLayout.marginHeight = 0;
        typeArea.setLayout(typeAreaLayout);
        type = UtilSwt.createCombo(Messages.EditFormulasDialog_FunctionType, typeArea, CollisionFunctionType.values());
        selectFunctionType(parent);
        type.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                selectFunctionType(parent);
            }
        });
    }

    /**
     * Create the constraint properties.
     * 
     * @param parent The parent composite.
     */
    private void createTextConstraint(Composite parent)
    {
        final Composite constraintArea = new Composite(parent, SWT.NONE);
        constraintArea.setLayout(new GridLayout(2, true));
        constraintTop = UtilSwt.createText(Messages.EditFormulasDialog_ConstraintTop, constraintArea);
        constraintBottom = UtilSwt.createText(Messages.EditFormulasDialog_ConstraintBottom, constraintArea);
        constraintLeft = UtilSwt.createText(Messages.EditFormulasDialog_ConstraintLeft, constraintArea);
        constraintRight = UtilSwt.createText(Messages.EditFormulasDialog_ConstraintRight, constraintArea);
    }

    /**
     * Create the selected function.
     * 
     * @param type The function type.
     * @return The function instance.
     */
    private CollisionFunction createFunction(CollisionFunctionType type)
    {
        switch (type)
        {
            case LINEAR:
                final double a = Double.parseDouble(linearA.getText());
                final double b = Double.parseDouble(linearB.getText());
                return new CollisionFunctionLinear(a, b);
            default:
                throw new LionEngineException(ERROR_TYPE, type.name());
        }
    }

    /**
     * Load the function and fill fields.
     * 
     * @param function The function to load.
     */
    private void loadFunction(CollisionFunction function)
    {
        switch (function.getType())
        {
            case LINEAR:
                final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
                linearA.setText(Double.toString(linear.getA()));
                linearB.setText(Double.toString(linear.getB()));
                break;
            default:
                throw new LionEngineException(ERROR_TYPE, function.getType().name());
        }
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Group range = new Group(parent, SWT.NONE);
        range.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        range.setLayout(new GridLayout(1, false));
        range.setText(Messages.EditFormulasDialog_Range);
        createTextRange(range);

        final Group function = new Group(parent, SWT.NONE);
        function.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        function.setLayout(new GridLayout(1, false));
        function.setText(Messages.EditFormulasDialog_Function);
        createTextFunction(function);

        final Group constraint = new Group(parent, SWT.NONE);
        constraint.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        constraint.setLayout(new GridLayout(1, false));
        constraint.setText(Messages.EditFormulasDialog_Constraint);
        createTextConstraint(constraint);
    }

    @Override
    protected CollisionFormula createObject(String name)
    {
        final Axis output = (Axis) this.output.getData();
        final int minX = Integer.parseInt(this.minX.getText());
        final int maxX = Integer.parseInt(this.maxX.getText());
        final int minY = Integer.parseInt(this.minY.getText());
        final int maxY = Integer.parseInt(this.maxY.getText());
        final CollisionFunctionType type = (CollisionFunctionType) this.type.getData();

        final CollisionRange range = new CollisionRange(output, minX, maxX, minY, maxY);
        final CollisionFunction function = createFunction(type);
        final CollisionConstraint constraint = new CollisionConstraint(constraintTop.getText(),
                constraintBottom.getText(), constraintLeft.getText(), constraintRight.getText());
        final CollisionFormula formula = new CollisionFormula(name, range, function, constraint);
        return formula;
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(CollisionFormula formula)
    {
        final CollisionRange range = formula.getRange();
        output.setText(range.getOutput().name());
        output.setData(range.getOutput());
        minX.setText(Integer.toString(range.getMinX()));
        maxX.setText(Integer.toString(range.getMaxX()));
        minY.setText(Integer.toString(range.getMinY()));
        maxY.setText(Integer.toString(range.getMaxY()));

        final CollisionFunction function = formula.getFunction();
        type.setText(function.getType().name());
        type.setData(function.getType());
        loadFunction(function);

        final CollisionConstraint constraint = formula.getConstraint();
        constraintTop.setText(getConstraintValue(constraint.getTop()));
        constraintBottom.setText(getConstraintValue(constraint.getBottom()));
        constraintLeft.setText(getConstraintValue(constraint.getLeft()));
        constraintRight.setText(getConstraintValue(constraint.getRight()));
    }

    @Override
    public void notifyObjectDeleted(CollisionFormula formula)
    {
        output.setText("");
        output.setData(null);
        minX.setText("");
        maxX.setText("");
        minY.setText("");
        maxY.setText("");
        type.setText("");
        type.setData(null);
        linearA.setText("");
        linearB.setText("");
        constraintTop.setText("");
        constraintBottom.setText("");
        constraintLeft.setText("");
        constraintRight.setText("");
    }
}
